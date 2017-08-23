package com.lj.cameracontroller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lj.cameracontroller.R;
import com.lj.cameracontroller.base.BaseActivity;
import com.lj.cameracontroller.base.BaseApplication;
import com.lj.cameracontroller.entity.UpdateEntity;
import com.lj.cameracontroller.entity.UserInfo;
import com.lj.cameracontroller.utils.NetworkUtils;
import com.lj.cameracontroller.view.MyAlertDialog;
import com.lj.cameracontroller.view.MyDialog;
import com.lj.cameracontroller.view.SampleListFragment2;
import com.lj.cameracontroller.view.SampleListFragment3;
import com.lj.cameracontroller.view.TitleView;

/**
 * Created by lenovo on 2017/8/23.
 */

public class SettingWebActivity extends BaseActivity {
    public static final String TAG = "SettingWebActivity";
    private TitleView titleView;
    private WebView webViewHome = null;
    private String url = null;
    private UserInfo userInfo = null;
    private String userId = "";
    private String accessToken = "";
    private UpdateEntity data;
    private MyAlertDialog myAlertDialog;
    private MyDialog dialog;
    private final int UPDATE_VERSION = 0;
    private SlidingMenu menu;
    private SampleListFragment3 fragment;
    public static Activity mActiviry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
    }

    private void initView(){
        mActiviry = this;
        titleView = (TitleView) findViewById(R.id.tv_top);
        titleView.setTv_title(getResources().getString(R.string.str_setting));
        webViewHome = (WebView) findViewById(R.id.webViewHome);
        fragment = new SampleListFragment3();
        // 设置滑动菜单的属性值
        menu = new SlidingMenu(this);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        // 设置滑动菜单的视图界面
        menu.setMenu(R.layout.menu_frame);
//        menu.showMenu();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, fragment).commit();

        //TODO 点击右滑菜单中的item
        fragment.setOnItemClickListener(new SampleListFragment3.OnItemClickListener() {
            @Override
            public void Onclick(int position) {
                menu.showContent();
                switch (position) {
                    case 0: //主页
                        finish();
                        break;
                    case 1: //注销
                        Intent intent = new Intent(SettingWebActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 2: //退出
                        System.exit(0);
                        break;
                    case 3:  //版本信息
                        Intent intent3 = new Intent(SettingWebActivity.this, VersionInforActivity.class);
                        startActivity(intent3);
                        break;
                    default:
                        break;
                }

            }
        });
        //TODO 点击左上角按钮显示右滑菜单
        titleView.setOnBackClickListener(new TitleView.backOnclickListener() {
            @Override
            public void onClick() {
                menu.showMenu();
            }
        });
    }
    private void initData(){
        myAlertDialog = new MyAlertDialog(this);
        dialog = new MyDialog(this, 0);
        this.url = getIntent().getStringExtra("url");
        if (url == null) {
            return;
        }
        userInfo = BaseApplication.userInfo;

        if (null != userInfo && null != userInfo.getResult()) {
            userId = userInfo.getResult().getUser_id();
            accessToken = userInfo.getResult().getAccess_token();
        } else {

        }
        // 设置WebView属性，能够执行Javascript脚本
        webViewHome.getSettings().setJavaScriptEnabled(true);
        webViewHome.getSettings().setDomStorageEnabled(true);
        webViewHome.getSettings().setAppCacheMaxSize(1024 * 1024 * 10);
        String appCachePath = getApplicationContext().getCacheDir()
                .getAbsolutePath();
        webViewHome.getSettings().setAppCachePath(appCachePath);
        webViewHome.getSettings().setAllowFileAccess(true);
        webViewHome.getSettings().setAppCacheEnabled(true);
        webViewHome.getSettings().setBuiltInZoomControls(true);
        /**
         * 判断是否有网络，有的话，使用LOAD_DEFAULT<br>
         * 无网络时，使用LOAD_CACHE_ELSE_NETWORK<br>
         * LOAD_DEFAULT: 根据cache-control决定是否从网络上取数据<br>
         * LOAD_CACHE_ELSE_NETWORK:只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         * **/
        if (NetworkUtils.isNetworkConnected(BaseApplication.getAppContext())) {
            webViewHome.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webViewHome.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
//        webViewHome.addJavascriptInterface(new MainWebViewActivity.ClickButton(MainWebViewActivity.this), "clickButton");
//        if(null != userInfo){
//            url = UserApi.MAINWEBURL+"?access_token="+userInfo.getResult().getAccess_token()+"&user_id="+userInfo.getResult().getUser_id();
//        }else{
//            url = UserApi.MAINWEBURL;
//        }
        webViewHome.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        // 点击返回键关闭滑动菜单
        if (menu.isMenuShowing()) {
            menu.showContent();
        } else {
            super.onBackPressed();
        }
    }
}
