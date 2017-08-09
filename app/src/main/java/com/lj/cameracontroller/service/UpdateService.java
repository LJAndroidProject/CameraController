package com.lj.cameracontroller.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lj.cameracontroller.R;
import com.lj.cameracontroller.activity.VersionInforActivity;
import com.lj.cameracontroller.constant.UserApi;
import com.lj.cameracontroller.utils.NotificationUtils;
import com.lj.cameracontroller.utils.StorageFactory;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class UpdateService extends Service {
//    private NotificationManager nm;
//    private Notification        notification;
    private File tempFile     = null;
    private boolean cancelUpdate = false;
    private MyHandler myHandler;
    private int download_precent = 0;
//    private RemoteViews views;
//    private int notificationId = 0;
    private NotificationUtils notificationUtil=new NotificationUtils();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationUtil.setUpNotification(this);
        myHandler = new MyHandler(Looper.myLooper(), this);
        // 初始化下载任务内容views
        Message message = myHandler.obtainMessage(3, 0);
        myHandler.sendMessage(message);
        // 启动线程开始执行下载任务
        downFile(StorageFactory.getInstance().getSharedPreference(this).getString("apkurl"));
        //AppSettings.getPrefString(this, "apkurl", "")
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }




    // 下载更新文件
    private void downFile(final String url) {
        new Thread() {
            public void run() {
                try {
                    HttpClient client = new DefaultHttpClient();
                    // params[0]代表连接的url
                    HttpGet get = new HttpGet(url);
                    HttpResponse response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    if (is != null) {
                        File rootFile = new File(
                                Environment.getExternalStorageDirectory(),
                                "/Cameracontroller");
                        if (!rootFile.exists() && !rootFile.isDirectory())
                            rootFile.mkdir();
                        tempFile = new File(
                                Environment.getExternalStorageDirectory(),
                                "/Cameracontroller/"
                                        + url.substring(url.lastIndexOf("/") + 1));
                        if (tempFile.exists())
                            tempFile.delete();
                        tempFile.createNewFile();
                        // 已读出流作为参数创建一个带有缓冲的输出流
                        BufferedInputStream bis = new BufferedInputStream(is);
                        // 创建一个新的写入流，讲读取到的图像数据写入到文件中
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        // 已写入流作为参数创建一个带有缓冲的写入流
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        int read;
                        long count = 0;
                        int precent = 0;
                        byte[] buffer = new byte[1024];
                        while ((read = bis.read(buffer)) != -1 && !cancelUpdate) {
                            bos.write(buffer, 0, read);
                            count += read;
                            precent = (int) (((double) count / length) * 100);
                            // 每下载完成5%就通知任务栏进行修改下载进度
                            if (precent - download_precent >= 5) {
                                download_precent = precent;
                                Message message = myHandler.obtainMessage(3,
                                        precent);
                                myHandler.sendMessage(message);
                            }
                        }
                        bos.flush();
                        bos.close();
                        fos.flush();
                        fos.close();
                        is.close();
                        bis.close();
                    }
                    if (!cancelUpdate) {
                        Message message = myHandler.obtainMessage(2, tempFile);
                        myHandler.sendMessage(message);
                    } else {
                        tempFile.delete();
                    }
                } catch (ClientProtocolException e) {
                    Message message = myHandler.obtainMessage(4, getResources().getString(R.string
                            .updata_service_error_tip));
                    myHandler.sendMessage(message);
                } catch (IOException e) {
                    Message message = myHandler.obtainMessage(4, getResources().getString(R.string
                            .updata_service_error_tip));
                    myHandler.sendMessage(message);
                } catch (Exception e) {
                    Message message = myHandler.obtainMessage(4, getResources().getString(R.string
                            .updata_service_error_tip));
                    myHandler.sendMessage(message);
                }
            }
        }.start();
    }

    // 安装下载后的apk文件
    private void Instanll(File file, Context context) {
        notificationUtil.setAnZhuangNotification(this);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    class MyHandler extends Handler {
        private Context context;

        public MyHandler(Looper looper, Context c) {
            super(looper);
            this.context = c;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                Log.e("下载时信息","msg.what="+msg.what);
                switch (msg.what) {
                    case 0:
                        Toast.makeText(context, msg.obj.toString(),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        break;
                    case 2:
                        // 下载完成后清除所有下载信息，执行安装提示
                        download_precent = 0;
                        notificationUtil.nm.cancel(UserApi.notificationId);
                        UserApi.file=(File) msg.obj;
                        Instanll((File) msg.obj, context);

//                        // 停止掉当前的服务
                        stopSelf();
                        break;
                    case 3:
                        // 更新状态栏上的下载进度信息
                        Log.e("下载百分比","download_precent="+download_precent);
                        notificationUtil.views.setTextViewText(R.id.tvProcess, "已下载"
                                + download_precent + "%");
                        notificationUtil.views.setProgressBar(R.id.pbDownload, 100,
                                download_precent, false);
                        notificationUtil.notification.contentView = notificationUtil.views;
                        notificationUtil.nm.notify(UserApi.notificationId, notificationUtil.notification);
                        break;
                    case 4:
                        notificationUtil.nm.cancel(UserApi.notificationId);
                        break;
                }
            }
        }
    }

}
