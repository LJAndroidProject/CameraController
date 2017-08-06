package com.lj.cameracontroller.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;


import com.lj.cameracontroller.R;
import com.lj.cameracontroller.activity.MainWebViewActivity;
import com.lj.cameracontroller.constant.UserApi;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Administrator on 2017/4/17.
 * 通知状态工具
 */

public class NotificationUtils {

    public static NotificationManager nm;
    public static Notification notification;
    public static RemoteViews views;
    public static Context mcontext;

    /**
     * 创建通知
     */
    public static void setUpNotification(Context context) {
        mcontext=context;
        nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = android.R.drawable.stat_sys_download;
        // notification.icon=android.R.drawable.stat_sys_download_done;
        // 放置在"正在运行"栏目中
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.tickerText = context.getString(R.string.app_name) + "更新";
        notification.when = System.currentTimeMillis();
        notification.defaults = Notification.DEFAULT_LIGHTS;
        // 设置任务栏中下载进程显示的views
        views = new RemoteViews(context.getPackageName(), R.layout.update_service);
        notification.contentView = views;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainWebViewActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentIntent;
        // 将下载任务添加到任务栏中
        nm.notify(UserApi.notificationId, notification);
    }

    public static  void stopNotification(Context context){
        if(null!=nm){
            nm.cancel(UserApi.notificationId);
        }else{
            nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(UserApi.notificationId);
        }

    }

    /**
     * 创建安装通知
     */
    public static void setAnZhuangNotification(Context context) {
        nm = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;
        // notification.icon=android.R.drawable.stat_sys_download_done;
        // 放置在"正在运行"栏目中
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.tickerText = context.getString(R.string.app_name) + "安装";
        notification.when = System.currentTimeMillis();
        notification.defaults = Notification.DEFAULT_LIGHTS;
        // 设置任务栏中下载进程显示的views
        views = new RemoteViews(context.getPackageName(), R.layout.anzhuang_notification);
        notification.contentView = views;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        if(null!=UserApi.file){
            intent.setDataAndType(Uri.fromFile(UserApi.file),
                    "application/vnd.android.package-archive");
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentIntent;
        // 将下载任务添加到任务栏中
        nm.notify(UserApi.notificationId, notification);
    }
}
