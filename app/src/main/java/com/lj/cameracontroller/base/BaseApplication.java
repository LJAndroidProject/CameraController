package com.lj.cameracontroller.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by 刘劲松 on 2017/6/28.
 * App的基类
 */

public class BaseApplication extends Application {

    //全局静态应用上下文对象
    private static Context AppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = getApplicationContext();
    }

    //TODO 静态全局获取应用上下文对象
    public static Context getAppContext(){
        return AppContext;
    }
}
