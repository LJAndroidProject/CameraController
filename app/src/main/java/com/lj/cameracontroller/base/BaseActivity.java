package com.lj.cameracontroller.base;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.lj.cameracontroller.utils.ImmerseHelper;
import com.lj.cameracontroller.utils.permissions.PermissionListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 刘劲松 on 2017/6/28.
 * Activity的基类，实现项目中Activity的通用功能和虚函数等的定义
 */

public abstract class BaseActivity extends FragmentActivity {
    private static PermissionListener mListener = null;
    private final static int REQUEST_PERMISSION_ID = 0x120;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(ThemeUtils.getCurrentThemmId());//用来实现动态主题更换的
    }

    /**
     * 设置侵入式状态栏
     *
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ImmerseHelper.setSystemBarTransparent(this);
        }
    }



//    public static void requestRuntimePermission(Activity activitie, String[] permissions, PermissionListener listener) {
//        mListener = listener;
//        List<String> permissionList = new ArrayList<>();
//        for (String permission : permissions) {
//            if (ContextCompat.checkSelfPermission(activitie, permission) != PackageManager.PERMISSION_GRANTED) {
//                permissionList.add(permission);
//            }
//        }
//        if (!permissionList.isEmpty()) {
//            ActivityCompat.requestPermissions(activitie, permissionList.toArray(new String[permissionList.size
//                    ()]), REQUEST_PERMISSION_ID);
//        } else {
//            mListener.onGranted();
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_PERMISSION_ID:
//                if (grantResults.length > 0) {
//                    List<String> deniedPermissions = new ArrayList<>();
//                    for (int i = 0; i < grantResults.length; i++) {
//                        int grantResult = grantResults[i];
//                        String permission = permissions[i];
//                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
//                            deniedPermissions.add(permission);
//                        }
//                    }
//                    if (deniedPermissions.isEmpty()) {
//                        mListener.onGranted();
//                    } else {
//                        mListener.onDenied(deniedPermissions);
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//    }
}
