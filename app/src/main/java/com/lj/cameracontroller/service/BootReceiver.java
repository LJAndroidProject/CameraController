package com.lj.cameracontroller.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lj.cameracontroller.utils.NotificationUtils;


/**
 * Created by Administrator on 2017/4/17.
 * 注册一个广播用来接收升级是否成功
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
//                String packageName = intent.getDataString();
//                System.out.println("安装了:" +packageName + "包名的程序");
            NotificationUtils.stopNotification(context);
        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
//                String packageName = intent.getDataString();
//                System.out.println("卸载了:"  + packageName + "包名的程序");

        }
    }
}
