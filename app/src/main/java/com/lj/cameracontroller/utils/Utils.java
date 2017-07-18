package com.lj.cameracontroller.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.lj.cameracontroller.base.IGeneralCallBack;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;


/**
 * Created by 侯晓戬 on 2017/7/10.
 * 通用性接口
 */

public class Utils {

    private final static String SH_APP_NAME_UTILS = "COM_QILOO_SMARTCARD_SH_UTILS";
    private final static String SH_KEY_SAVE_VER_NUMBER = "SH_KEY_VER_NUM";//本地存储的版本号
    //////////////////////////////////////
    // 异步加载网络图片
    /////////////////////////////////////
    public static void loadDrawable(final Context context, final String url, final IGeneralCallBack cb){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Resources rs = context.getResources();
                try {
                    Drawable bmp = Drawable.createFromResourceStream(rs, null,
                            new URL(url).openStream(),"src");
                    if(null != cb){
                        cb.OnResult(bmp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    ///////////////////////////////////
    // 获取当前手机语言
    ////////////////////////////////////
    public static String getLanguage(){
        return Locale.getDefault().getLanguage();
    }

    ///////////////////////////////////////////////////
    // 本地存储的版本号
    //////////////////////////////////////////////////
    public static void setShKeySaveVerNumber(Context context ,int version){
        SharedPreferences sp = context.getSharedPreferences(SH_APP_NAME_UTILS,
                Context.MODE_PRIVATE);
        sp.edit().putInt(SH_KEY_SAVE_VER_NUMBER, version).commit();
    }

    public static int getShKeySaveVerNumber(Context context){
        int  ver = 0;
        SharedPreferences sp = context.getSharedPreferences(SH_APP_NAME_UTILS,
                Context.MODE_PRIVATE);
        ver = sp.getInt(SH_KEY_SAVE_VER_NUMBER, 100);
        return ver;
    }

    /////////////////////////////////////////////////
    // 获取当前程序版本号
    /////////////////////////////////////////////////
    public static int getAppVersion(Context context){
        int ver = 0;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            ver = info.versionCode;
        }catch (Exception e){
            Logger.exception(e);
        }
        return ver;
    }

}
