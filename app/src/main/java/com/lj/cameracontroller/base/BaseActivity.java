package com.lj.cameracontroller.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.lj.cameracontroller.utils.ImmerseHelper;


/**
 * Created by 刘劲松 on 2017/6/28.
 * Activity的基类，实现项目中Activity的通用功能和虚函数等的定义
 */

public abstract class BaseActivity extends Activity {

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
