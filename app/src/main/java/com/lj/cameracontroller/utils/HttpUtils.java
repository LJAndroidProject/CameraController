package com.lj.cameracontroller.utils;

import android.util.Log;
import android.util.SparseArray;


import com.lj.cameracontroller.entity.HttpRequest;
import com.lj.cameracontroller.interfacecallback.IHttpUtilsCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 2017/7/7
 * wangxiaoer
 * 功能描述：Okhttp原始封装
 **/
public class HttpUtils {
    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");

    /**
     * okhttpclient实例
     */
    private static OkHttpClient mOkHttpClient;
    /**
     * 获取取消请求的Call对象
     */
    protected static Class<?> tag;
    /**
     * 请求集合: key=Activity value=Call集合
     */
    private static Map<Class<?>, List<Call>> callsMap = new ConcurrentHashMap<Class<?>, List<Call>>();
    /**
     * 超时.未设置时，默认为零
     */
    private static long timeconnect = 0;
    private static long timeread = 0;
    private static long timewrite = 0;
    private static SparseArray<Call> callsList = new SparseArray<>();

    /**
     * 构造方法
     */
    private static OkHttpClient getHttpClient() {
        if (null == mOkHttpClient) {
            mOkHttpClient = new OkHttpClient();
        }
        /**
         * 在这里直接设置连接超时.读取超时，写入超时
         * 设置了超时时间（即超时时间补为零），则按设置使用
         * 否则默认为2000L
         */
        OkHttpClient.Builder builder = mOkHttpClient.newBuilder();
        if (timeconnect == 0) {
            builder.connectTimeout(2000L, TimeUnit.SECONDS);
        } else {
            builder.connectTimeout(timeconnect, TimeUnit.SECONDS);
        }
        if (timeread == 0) {
            builder.readTimeout(2000L, TimeUnit.SECONDS);
        } else {
            builder.readTimeout(timeread, TimeUnit.SECONDS);
        }
        if (timewrite == 0) {
            builder.writeTimeout(2000L, TimeUnit.SECONDS);
        } else {
            builder.writeTimeout(timewrite, TimeUnit.SECONDS);
        }
        mOkHttpClient = builder.build();
        return mOkHttpClient;
    }

    /**
     * 超时请求时间设置
     *
     * @param timeconnectout 连接超时
     * @param timereadout    读取超时
     * @param timewriteout   写入超时
     */
    public static void setTimeOut(long timeconnectout, long timereadout, long timewriteout) {
        timeconnect = timeconnectout;
        timeread = timereadout;
        timewrite = timewriteout;
    }

    //-------------------------get请求数据--------------------------

    /**
     * @param url      请求地址
     * @param callBack 回调接口
     */
    public static int get(String url, final IHttpUtilsCallBack callBack) {
        Log.e("请求参数:","url="+url);
        final HttpRequest httpRequest = new HttpRequest();
        final Request request = new Request.Builder().url(url).method("GET", null).build();
        httpRequest.setRequest(request);
        setCall(callsList.size(), getHttpClient().newCall(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onFailure(httpRequest, e);
                }
                int count = callsList.indexOfValue(call);
                Logger.e("HttpUtils", "count:" + count);
                callsList.remove(count);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    callBack.onSuccess(result);
                } catch (IOException e) {
                    callBack.onFailure(httpRequest, e);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("HttpUtils", e.toString());
                }
                int count = callsList.indexOfValue(call);
                Logger.e("HttpUtils", "count:" + count);
                callsList.remove(count);
            }
        });
        //因为添加后数组会增加，不是添加前的值
        return callsList.size() - 1;
    }

    //-------------------------post请求--------------------------

    /**
     * @param url      请求地址
     * @param params   请求参数集合
     * @param callBack 回调接口
     */

    public static void post(String url, Map<String, String> params,
                            final IHttpUtilsCallBack callBack) {
        Map<String, String> header = new HashMap<>();
        header.put("Accept-Language", Utils.getLanguage());
        post(url, header, params, callBack);
    }

    /**
     * @param url      请求地址
     * @param params   请求参数集合
     * @param callBack 回调接口
     */

    public static int post(String url, Map<String, String> headparam, Map<String, String> params,
                           final IHttpUtilsCallBack callBack) {
        final HttpRequest httpRequest = new HttpRequest();
        if (params == null) {
            params = new HashMap<>();
        }
        //请求头处理
        Headers headers = null;
        Headers.Builder headersbuilder = new Headers.Builder();
        if (null != headparam) {
            for (Map.Entry<String, String> header : headparam.entrySet()) {
                headersbuilder.add(header.getKey(), header.getValue());
            }
            headers = headersbuilder.build();
        }
        Logger.i("hou", "post(headers) " + headers);
        //参数处理
        RequestBody requestBody = null;
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> map : params.entrySet()) {
            //把key和value添加到formbody中
            builder.add(map.getKey(), map.getValue() == null ? "" : map.getValue());
        }
        requestBody = builder.build();
        Logger.i("hou", "post(requestBody) " + requestBody);

        // 请求对象
        final Request request = new Request
                .Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();
        httpRequest.setRequest(request);
        setCall(callsList.size(), getHttpClient().newCall(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onFailure(httpRequest, e);
                }
                int count = callsList.indexOfValue(call);
                Logger.e("HttpUtils", "count:" + count);
                callsList.remove(count);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String result = response.body().string();
                try {
                    callBack.onSuccess(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int count = callsList.indexOfValue(call);
                Logger.e("HttpUtils", "count:" + count);
                callsList.remove(count);
            }
        });
        //因为添加后数组会增加，不是添加前的值
        return callsList.size() - 1;
    }


    //-------------------------文件上传--------------------------

    /**
     * @param URL      上传地址
     * @param FileUrl  文件地址（需要上传的）
     * @param callBack 回调接口
     */
    public static int uppost(String URL, String FileUrl, final IHttpUtilsCallBack callBack) {
        //根据文件路径创建File对象
        final HttpRequest httpRequest = new HttpRequest();
        File file = new File(FileUrl);
        final Request request = new Request.Builder()
                .url(URL)
                .post(RequestBody.create(MEDIA_TYPE_PLAIN, file))
                .build();
        httpRequest.setRequest(request);
        //上传请求
        setCall(callsList.size(), getHttpClient().newCall(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onFailure(httpRequest, e);
                }
                int count = callsList.indexOfValue(call);
                Logger.e("HttpUtils", "count:" + count);
                callsList.remove(count);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    callBack.onSuccess(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int count = callsList.indexOfValue(call);
                Logger.e("HttpUtils", "count:" + count);
                callsList.remove(count);
            }

        });
        //因为添加后数组会增加，不是添加前的值
        return callsList.size() - 1;
    }
    //-------------------------文件下载--------------------------

    /**
     * @param url      下载地址
     * @param desDir   目标地址
     * @param callBack
     */
    public static int download(final String url, final String desDir, final IHttpUtilsCallBack callBack) {
        final HttpRequest httpRequest = new HttpRequest();
        final Request request = new Request.Builder().url(url).method("GET", null).build();
        httpRequest.setRequest(request);
        setCall(callsList.size(), getHttpClient().newCall(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (callBack != null) {
                    callBack.onFailure(httpRequest, e);
                }
                int count = callsList.indexOfValue(call);
                Logger.e("HttpUtils", "count:" + count);
                callsList.remove(count);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                /**
                 * 在这里进行文件的下载处理
                 */
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                /**
                 * 文件流总长度
                 */
                long total = response.body().contentLength();
                /**
                 * 文件现长度
                 */
                long sum = 0;
                try {
                    //文件名和目标地址
                    File file = new File(desDir, getFileName(url));
                    //把请求回来的response对象装换为字节流
                    inputStream = response.body().byteStream();
                    fileOutputStream = new FileOutputStream(file);
                    int len = 0;
                    byte[] bytes = new byte[2048];
                    //循环读取数据
                    while ((len = inputStream.read(bytes)) != -1) {
                        sum += len;
                        fileOutputStream.write(bytes, 0, len);
                        long finalSum = sum;
                        try {
                            callBack.onProgress(finalSum, total);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //关闭文件输出流
                    fileOutputStream.flush();
                    //调用分发数据成功的方法
                    try {
                        callBack.onSuccess(file.getAbsolutePath());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //调用
                } catch (IOException e) {
                    //如果失败，调用此方法
                    if (callBack != null) {
                        callBack.onFailure(httpRequest, e);
                    }
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }

                }
                int count = callsList.indexOfValue(call);
                Logger.e("HttpUtils", "count:" + count);
                callsList.remove(count);
            }

        });
        //因为添加后数组会增加，不是添加前的值
        return callsList.size() - 1;
    }

    //-------------------------设置Call--------------------------

    public static Call setCall(int index, Call call) {
        callsList.put(index, call);
        Logger.e("HttpUtils", "index:" + index);
        return call;
    }

    //-------------------------取消请求--------------------------
    public static void CancelHttp(int index) {
        if (callsList != null) {
            callsList.get(index).cancel();
        }
    }

    //-------------------------其他方法--------------------------

    /**
     * 根据文件url获取文件的路径名字
     *
     * @param url
     * @return
     */
    private static String getFileName(String url) {
        int separatorIndex = url.lastIndexOf("/");
        String path = (separatorIndex < 0) ? url : url.substring(separatorIndex + 1, url.length());
        return path;
    }

}
