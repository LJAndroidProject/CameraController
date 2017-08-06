package com.lj.cameracontroller.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

/**
 * 轻量级存储
 *
 * @author Administrator
 */
public class AppSettings {


    public static String getPrefString(Context context, String key,
                                       final String defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }

    public static Boolean getPrefString(Context context, String key,
                                        final Boolean defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getBoolean(key, defaultValue);
    }

    public static int getPrefString(Context context, String key,
                                    final int defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getInt(key, defaultValue);
    }


    public static void setPrefString(Context context, final String key,
                                     final String value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putString(key, value).commit();
    }

    public static void setPrefString(Context context, final String key,
                                     final Boolean value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putBoolean(key, value).commit();
    }


    public static void setPrefString(Context context, final String key,
                                     final int value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putInt(key, value).commit();
    }


    public static void clearPreference(Context context,
                                       final SharedPreferences p) {
        final Editor editor = p.edit();

        editor.clear();
        editor.commit();
    }


    public static void removePreference(Context context, final String key) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().remove(key).commit();
    }

    public static int getAppVersionNumber(Context context) {
        int nRet = 100;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            nRet = info.versionCode;
        } catch (Exception e) {
            Log.e("AppSettings", "getAppVersionNumber() Error : " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return nRet;
    }

    public static String getAppVersion(Context context) {
        String stRet = "1.0.0";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            stRet = info.versionName;
        } catch (Exception e) {
            Log.e("AppSettings", "getAppVersionNumber() Error : " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return stRet;
    }

    public static String getAppPackegName(Context context) {
        String stRet = "com.lj.cameracontroller";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            stRet = info.packageName;
        } catch (Exception e) {
            Log.e("AppSettings", "getAppPackegName() Error : " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return stRet;
    }

    /**
     *  判断微信是否可用
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        // 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

}
