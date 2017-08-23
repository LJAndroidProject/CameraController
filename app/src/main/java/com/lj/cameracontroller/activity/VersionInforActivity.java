package com.lj.cameracontroller.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import com.lj.cameracontroller.utils.StorageFactory;
import com.lj.cameracontroller.utils.VersionUtils;
import com.lj.cameracontroller.utils.permissions.PermissionListener;
import com.lj.cameracontroller.view.MyAlertDialog;
import com.lj.cameracontroller.view.MyDialog;
import com.lj.cameracontroller.view.SampleListFragment;

import java.io.IOException;
import java.util.List;

/**
 * Created by 刘劲松 on 2017/7/23.
 * 版本信息
 */

public class VersionInforActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "VersionInforActivity";
    private TextView tv_version, tv_neibu_version;
    private Button bt_update;
    private String versionName = "";
    private int versionCode = 0;
    private MyDialog dialog;
    private UserInfo userInfo;
    private String userId = "";
    private String accessToken = "";
    private UpdateEntity data;
    private MyAlertDialog myAlertDialog;
    private final int UPDATE_VERSION = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_versioninfor);
        initView();
        initData();

    }

    public void initView() {
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_neibu_version = (TextView) findViewById(R.id.tv_neibu_version);
        bt_update = (Button) findViewById(R.id.bt_update);
        bt_update.setOnClickListener(this);
    }

    public void initData() {
        myAlertDialog = new MyAlertDialog(this);
        userInfo = StorageFactory.getInstance().getSharedPreference(VersionInforActivity.this).getDao(UserApi.USERINFOR, UserInfo.class);
        if (null != userInfo && null != userInfo.getResult()) {
            userId = userInfo.getResult().getUser_id();
            accessToken = userInfo.getResult().getAccess_token();
        } else {

        }
        dialog = new MyDialog(this, 0);
        versionCode = VersionUtils.getVersionCode(this);
        versionName = VersionUtils.getVersionName(this);
        tv_version.setText("v" + versionName);
        tv_neibu_version.setText("" + versionCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_update: //检查更新
                detectionUpdate();
                break;
            default:
                break;
        }

    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
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
                        Logger.e(TAG,"myAlertDialog:"+myAlertDialog);
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
                        Logger.e(TAG,"抛异常？？？？？？？:"+myAlertDialog);
                        myAlertDialog.show();
                    }
                    break;
                case 1:
                    myAlertDialog.ValidationOperationDialog().setMsg("已是最新版本")
                            .setNoButtonGone(true)
                            .setPositiveButton(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myAlertDialog.dismiss();
                                }
                            });
                    myAlertDialog.show();
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
                        StorageFactory.getInstance().getSharedPreference(VersionInforActivity.this).saveString("apkurl", data.getResult().getPath()); //存储下载地址
                        Log.e("","本地版本信息"+AppSettings.getAppVersionNumber(VersionInforActivity.this));
                        if (Version > AppSettings.getAppVersionNumber(VersionInforActivity.this)) {
                            Message message = Message.obtain();
                            message.what = UPDATE_VERSION;

                            myHandler.sendMessage(message);
                        }else{
                            Message message = Message.obtain();
                            message.what = 1;

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

    private void entryOnlineUpdate(){
        String[] pers = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        requestRuntimePermission(VersionInforActivity.this,pers, new PermissionListener() {
//            @Override
//            public void onGranted() {
                Intent it = new Intent(VersionInforActivity.this,UpdateService.class);
                startService(it);
                bindService(it, conn, Context.BIND_AUTO_CREATE);
//            }
//
//            @Override
//            public void onDenied(List<String> deniedPermission) {
//                Toast.makeText(VersionInforActivity.this,R.string.str_no_permision,Toast.LENGTH_SHORT).show();
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
