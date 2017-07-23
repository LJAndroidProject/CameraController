package com.lj.cameracontroller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lj.cameracontroller.R;
import com.lj.cameracontroller.base.BaseActivity;
import com.lj.cameracontroller.base.BaseApplication;
import com.lj.cameracontroller.utils.NetworkUtils;
import com.lj.cameracontroller.view.TitleView;

/**
 * Created by 刘劲松 on 2017/7/19.
 * 登录之后的主web页面
 */

public class MainWebViewActivity extends BaseActivity{

    private TitleView titleView;
    private WebView webViewHome = null;
    private String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainwebview);
        initView();
        initData();
    }


    private void initView(){
        titleView=(TitleView) findViewById(R.id.tv_top);
        titleView.setTv_title(getResources().getString(R.string.str_mainHome));
        webViewHome=(WebView) findViewById(R.id.webViewHome);
    }

    private void initData(){
        this.url = getIntent().getStringExtra("url");
        if (url == null) {
            return;
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
        webViewHome.addJavascriptInterface(new ClickButton(MainWebViewActivity.this), "clickButton");
        webViewHome.loadUrl(url);
    }


    public class ClickButton{
        private Activity act;
        public ClickButton(Activity activity){
            act=activity;
        }
        @android.webkit.JavascriptInterface
        public void ClickBtn(){
            Intent intent =new Intent(MainWebViewActivity.this,DeviceListActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
