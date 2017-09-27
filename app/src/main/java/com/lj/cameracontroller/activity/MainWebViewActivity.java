package com.lj.cameracontroller.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lj.cameracontroller.R;
import com.lj.cameracontroller.base.BaseActivity;
import com.lj.cameracontroller.base.BaseApplication;
import com.lj.cameracontroller.base.HDateGsonAdapter;
import com.lj.cameracontroller.constant.UserApi;
import com.lj.cameracontroller.entity.HttpRequest;
import com.lj.cameracontroller.entity.UpdateEntity;
import com.lj.cameracontroller.entity.UserInfo;
import com.lj.cameracontroller.interfacecallback.IHttpUtilsCallBack;
import com.lj.cameracontroller.service.UpdateService;
import com.lj.cameracontroller.utils.AppSettings;
import com.lj.cameracontroller.utils.HttpUtils;
import com.lj.cameracontroller.utils.Logger;
import com.lj.cameracontroller.utils.NetworkUtils;
import com.lj.cameracontroller.utils.StorageFactory;
import com.lj.cameracontroller.utils.permissions.PermissionListener;
import com.lj.cameracontroller.view.MyAlertDialog;
import com.lj.cameracontroller.view.MyDialog;
import com.lj.cameracontroller.view.SampleListFragment;
import com.lj.cameracontroller.view.SampleListFragment2;
import com.lj.cameracontroller.view.TitleView;

import java.io.IOException;
import java.util.List;

/**
 * Created by 刘劲松 on 2017/7/19.
 * 登录之后的主web页面
 */

public class MainWebViewActivity extends BaseActivity {

    public static final String TAG = "MainWebViewActivity";
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
    private SampleListFragment2 fragment;
    public static Activity mActiviry;
    // 按一次退出按钮后等待2000毫秒如果再按即程序退出
    private long waitTime = 2000;
    private long touchTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainwebview);
        initView();
        initData();
    }

    private void initView() {
        mActiviry = this;
        titleView = (TitleView) findViewById(R.id.tv_top);
        titleView.setTv_title(getResources().getString(R.string.str_mainHome));
        webViewHome = (WebView) findViewById(R.id.webViewHome);
        fragment = new SampleListFragment2();
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
        fragment.setOnItemClickListener(new SampleListFragment2.OnItemClickListener() {
            @Override
            public void Onclick(int position) {
                menu.showContent();
                switch (position) {
                    case 0: //设置
                        Intent intentSet =new Intent(MainWebViewActivity.this,SettingWebActivity.class);
                        intentSet.putExtra("url",UserApi.SETTINGWEBURL+"?access_token="+accessToken+"&user_id="+userId
                                +"&mobile="+ StorageFactory.getInstance().getSharedPreference(MainWebViewActivity.this).getString(UserApi.LOGIN_USERNAME));
                        startActivity(intentSet);
                        break;
                    case 1: //注销
                        Intent intent = new Intent(MainWebViewActivity.this, LoginActivity.class);
                        startActivity(intent);
                        StorageFactory.getInstance().getSharedPreference(MainWebViewActivity.this).saveBoolean(UserApi.ISFORGETPWD,false);
                        finish();
                        break;
                    case 2: //退出
                        System.exit(0);
                        break;
                    case 3:  //版本信息
                        Intent intent3 = new Intent(MainWebViewActivity.this, VersionInforActivity.class);
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

    private void initData() {
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
            detectionUpdate();
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
        webViewHome.addJavascriptInterface(new ClickButton(MainWebViewActivity.this), "clickButton");
//        if(null != userInfo){
//            url = UserApi.MAINWEBURL+"?access_token="+userInfo.getResult().getAccess_token()+"&user_id="+userInfo.getResult().getUser_id();
//        }else{
//            url = UserApi.MAINWEBURL;
//        }
        webViewHome.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
                touchTime = currentTime;
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return false;
        }
        return false;
    }

    public class ClickButton {
        private Activity act;

        public ClickButton(Activity activity) {
            act = activity;
        }

        @android.webkit.JavascriptInterface
        public void ClickBtn() {
            Logger.e("aaaa", "进来了吗");
            Intent intent = new Intent(MainWebViewActivity.this, DeviceListActivity.class);
            startActivity(intent);
//            finish();
        }

    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (data.getResult().getForce_update() == 1) { //强制更新
                        myAlertDialog.GeneralTipsDialog()
                                .setButtonContent("确认", "取消")
                                .setTitleTip("升级提示")
                                .setMsg(data.getResult().getDescription())
                                .setNoButtonGone(true)
                                .setPositiveButton(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myAlertDialog.dismiss();
                                        entryOnlineUpdate();
                                    }
                                });
                        myAlertDialog.show();
                    } else {
                        Logger.e(TAG, "myAlertDialog:" + myAlertDialog);
                        myAlertDialog.GeneralTipsDialog()
                                .setButtonContent("确认", "取消")
                                .setTitleTip("升级提示")
                                .setMsg(data.getResult().getDescription())
                                .setPositiveButton(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myAlertDialog.dismiss();
                                        entryOnlineUpdate();
                                    }
                                })
                                .setNegativeButton(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myAlertDialog.dismiss();
                                    }
                                });
                        Logger.e(TAG, "抛异常？？？？？？？:" + myAlertDialog);
                        myAlertDialog.show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 检测是否需要更新
     */
    public void detectionUpdate() {
        dialog.show();
        HttpUtils.get(UserApi.UPDATA + "?access_token=" + accessToken + "&model=get_app_latest_version&type=1", new IHttpUtilsCallBack() {
            @Override
            public void onFailure(HttpRequest request, IOException e) {
                dialog.dismiss();
            }

            @Override
            public void onSuccess(String result) throws Exception {
                dialog.dismiss();
                Logger.e(TAG, result);
                Log.e("检测更新：", "result=" + result);
                if (!"".equals(result) && !"null".equals(result)) {
                    Gson gson = HDateGsonAdapter.createGson();
                    data = gson.fromJson(result, UpdateEntity.class);
                    if (null != data && data.getCode() == 1 && null != data.getResult()) { //获取数据成功
                        int Version = data.getResult().getVersion();
                        StorageFactory.getInstance().getSharedPreference(MainWebViewActivity.this).saveString("apkurl", data.getResult().getPath()); //存储下载地址
                        Log.e("", "本地版本信息" + AppSettings.getAppVersionNumber(MainWebViewActivity.this));
                        if (Version > AppSettings.getAppVersionNumber(MainWebViewActivity.this)) {
                            Message message = Message.obtain();
                            message.what = UPDATE_VERSION;
                            myHandler.sendMessage(message);

                        }
                    }
                }
            }

            @Override
            public void onProgress(float progress, long total) {
                dialog.dismiss();
            }
        });
    }

    private boolean isBinded;
    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBinded = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBinded = true;
        }
    };

    private void entryOnlineUpdate() {
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        requestRuntimePermission(MainWebViewActivity.this,pers, new PermissionListener() {
//            @Override
//            public void onGranted() {
        Intent it = new Intent(MainWebViewActivity.this, UpdateService.class);
        startService(it);
        bindService(it, conn, Context.BIND_AUTO_CREATE);
//            }
//
//            @Override
//            public void onDenied(List<String> deniedPermission) {
//                Toast.makeText(MainWebViewActivity.this,R.string.str_no_permision,Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        if (isBinded) {
            unbindService(conn);
        }
        Intent it = new Intent(this, UpdateService.class);
        stopService(it);
        super.onDestroy();
    }
}
