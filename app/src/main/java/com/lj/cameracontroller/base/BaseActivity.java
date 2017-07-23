package com.lj.cameracontroller.base;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.lj.cameracontroller.utils.FileUtils;
import com.lj.cameracontroller.utils.ImmerseHelper;
import com.lj.cameracontroller.utils.permissions.PermissionListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
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

}
