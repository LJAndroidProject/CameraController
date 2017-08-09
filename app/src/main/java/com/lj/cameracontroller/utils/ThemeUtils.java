package com.lj.cameracontroller.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;

import com.lj.cameracontroller.R;
import com.lj.cameracontroller.base.BaseApplication;


/**
 * Created by Administrator on 2017/6/28.
 * 主题相关的工具类，实现主题获取，设定
 * 从主题项中获取资源等功能
 */

public class ThemeUtils {

    private static int CurrentThemmId = 0;
    private static final int DEF_THEME_RES_ID = R.style.QLTheme;
    private static final String THEME_APP = "SMART_CARD_THEM";
    private static final String KEY_THEME_ID = "KEY_THEME_ID";

    //TODO 获取当前应用的主题
    public static int getCurrentThemmId(){
        if(0 == CurrentThemmId){
            SharedPreferences sp = BaseApplication.getAppContext()
                    .getSharedPreferences(THEME_APP, Context.MODE_PRIVATE);
            CurrentThemmId = sp.getInt(KEY_THEME_ID, DEF_THEME_RES_ID);
        }
        return CurrentThemmId;
    }

    //TODO 设置新的主题
    public static boolean setCurrentThemmId(int resid){
        CurrentThemmId = resid;
        SharedPreferences sp = BaseApplication.getAppContext()
                .getSharedPreferences(THEME_APP, Context.MODE_PRIVATE);
        return sp.edit().putInt(KEY_THEME_ID, CurrentThemmId).commit();
    }

    //TODO 重启App
    public static void reStartApplication(){
        //定时执行
        Context context = BaseApplication.getAppContext();
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(context, 0,intent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + 680, restartIntent);

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //TODO 获取主题的颜色值
    public static int getThemeColor(Context context, int id){
        int color = 0x000000;
        try {
            int[] attrsArray = {id};
            TypedArray typedArray = context.obtainStyledAttributes(attrsArray);
            color = typedArray.getColor(0, 0x000000);
            typedArray.recycle();
        }catch (Exception e){
//            Log.exception(e);
        }
        return color;
    }

    //TODO 获取主题的字体大小
    public static float getThemeTextSize(Context context, int id){
        float size = 0.0f;
        try {
            int[] attrsArray = {id};
            TypedArray typedArray = context.obtainStyledAttributes(attrsArray);
            size = typedArray.getDimension(0, 0.0f);
            size = DensityUtils.px2dip(context, size);
            typedArray.recycle();
        }catch (Exception e){
//            Logger.exception(e);
        }
        return size;
    }

}
