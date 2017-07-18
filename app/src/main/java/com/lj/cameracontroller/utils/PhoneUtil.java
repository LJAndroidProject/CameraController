package com.lj.cameracontroller.utils;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @功能：获取本机相关信息的工具类
 */
public class PhoneUtil {

    /**
     * 获取本机Android-SDK版本号
     */
    @SuppressWarnings("deprecation")
    public static String getPhoneSDKVersion() {
        return Build.VERSION.SDK;
    }

    /**
     * 获取本机系统版本号
     */
    public static String getPhoneSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取本机主板信息
     */
    public static String getPhoneBoardInfo() {
        return Build.BOARD;
    }

    /**
     * 获取本机当前网络ip
     */
    public static String getPhoneNetIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIp = intf.getInetAddresses(); enumIp
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = enumIp.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            return null;
        }
        return null;
    }

    /**
     * 获取本机物理网卡ip地址
     */
    public static String getPhoneMacIp(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取本机号码
     */
    public static String getPhoneNumber(Context context) {
        String phoneNum = "";
        try {
            TelephonyManager phoneMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            phoneNum = phoneMgr.getLine1Number();

            int length = phoneNum.length();
            if (length < 11) {
                return "";
            }

            phoneNum = phoneNum.substring(length - 11, length);
            return phoneNum;
        } catch (Exception e) {
            // Toast.makeText(context, "获取手机号码失败~", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    /**
     * 监测手机号码是否有效
     */
    public static boolean isGloblePhoneNumber(String phone) {
//		Pattern p = Pattern.compile("[1][3587]\\d{9}");
        Pattern p = Pattern.compile("[1]\\d{10}");
        Matcher m = p.matcher(phone);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    /**
     * 获取本机型号
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机服务商信息 IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
     */
    public static String getProvidersName(Context context) {
        String ProvidersName = "N/A";
        String IMSI = "";
        try {
            TelephonyManager phoneMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            IMSI = phoneMgr.getSubscriberId();
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProvidersName;
    }

    /**
     * 获取手机所有信息
     *
     * @param context
     * @return
     */
    public static String getPhoneInfo(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();
        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        sb.append("\nNetworkType = " + tm.getNetworkType());
        sb.append("\nPhoneType = " + tm.getPhoneType());
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSimState = " + tm.getSimState());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        return sb.toString();
    }

    /**
     * 获取手机IMEI
     */
    public static String getIMEIinfo(Context context) {
        String imei = "";
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        return imei;
    }

    /**
     * 获取手机MAC地址
     */
    public static String getMACadress(Context context) {
        // 获取 MAC 地址
        WifiManager manager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        if (null != macAddress) {
            String mac = macAddress.replace(".", "").replace(":", "")
                    .replace("-", "").replace("_", "");

            return mac;
        }
        return "";
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    /**
     * 获取屏幕密度（0.75 / 1.0 / 1.5）
     */
    public static float getScreenDensity(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.density;
    }
}
