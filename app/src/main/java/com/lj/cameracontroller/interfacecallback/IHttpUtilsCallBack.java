package com.lj.cameracontroller.interfacecallback;

import com.lj.cameracontroller.entity.HttpRequest;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 2017
 * 07
 * 2017/7/7
 * 刘劲松
 * 功能描述：
 **/
public interface IHttpUtilsCallBack {
    /**
     * 请求失败
     *
     * @param request
     * @param e
     */
    void onFailure(HttpRequest request, IOException e);

    /**
     * 请求成功(需转换)
     * JSONObject jsonObject = new JSONObject(result)
     *
     * @param result
     * @throws Exception
     */
    void onSuccess(String result) throws Exception;

    /**
     * 请求进度显示
     *
     * @param progress 目前进度值
     * @param total    总进度值
     */
    void onProgress(float progress, long total);
}
