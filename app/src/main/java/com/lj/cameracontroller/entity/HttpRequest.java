package com.lj.cameracontroller.entity;

import okhttp3.Request;

/**
 * 2017
 * 07
 * 2017/7/18
 * wangxiaoer
 * 功能描述：
 **/
public class HttpRequest {
    private static Request request;

    public static Request getRequest() {
        return request;
    }

    public static void setRequest(Request request_s) {
        request = request_s;
    }
}
