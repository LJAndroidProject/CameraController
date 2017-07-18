package com.lj.cameracontroller.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.lj.cameracontroller.base.BaseApplication;


/**
 * 功能：log工具类
 *
 * @author 刘劲松
 */
public class Logger {

    public static void exception(Exception e) {
        e.printStackTrace();
    }

    public static void v(String tag, String msg) {
        if (isDebugMode())
            Log.v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable t) {
        if (isDebugMode())
            Log.v(tag, msg, t);
    }

    public static void d(String tag, String msg) {
        if (isDebugMode())
            Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable t) {
        if (isDebugMode())
            Log.d(tag, msg, t);
    }

    public static void i(String tag, String msg) {
        if (isDebugMode())
            Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable t) {
        if (isDebugMode())
            Log.i(tag, msg, t);
    }

    public static void w(String tag, String msg) {
        if (isDebugMode())
            Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable t) {
        if (isDebugMode())
            Log.w(tag, msg, t);
    }

    public static void e(String tag, String msg) {
        if (isDebugMode())
            Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable t) {
        if (isDebugMode())
            Log.e(tag, msg, t);
    }

    /**
     * 判断apk当前是否是debug模式
     * @return
     */
    public static boolean isDebugMode() {
        try {
            Context context = BaseApplication.getAppContext();
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
