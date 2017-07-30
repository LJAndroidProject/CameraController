package com.lj.cameracontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lj.cameracontroller.R;
import com.lj.cameracontroller.base.BaseActivity;
import com.lj.cameracontroller.base.BaseApplication;
import com.lj.cameracontroller.base.HDateGsonAdapter;
import com.lj.cameracontroller.constant.UserApi;
import com.lj.cameracontroller.entity.HttpRequest;
import com.lj.cameracontroller.entity.UserInfo;
import com.lj.cameracontroller.interfacecallback.IHttpUtilsCallBack;
import com.lj.cameracontroller.utils.HttpUtils;
import com.lj.cameracontroller.utils.Logger;
import com.lj.cameracontroller.utils.StorageFactory;
import com.lj.cameracontroller.utils.StringUtils;
import com.lj.cameracontroller.view.MyDialog;
import com.lj.cameracontroller.view.TitleView;

import java.io.IOException;

/**
 * Created by 刘劲松 on 2017/7/16.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_UserName, et_Password;
    private Button bt_Login;
    private CheckBox cb_Forgetpwd;
    private TitleView titleView;
    private String userName = "";
    private String passWord = "";
    private boolean isforgetPwd=false;//是否记住密码
    private static final String TAG="LoginActivity";
    private MyDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setListener();
        init();
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.tv_top);
        titleView.setTv_title(getResources().getString(R.string.str_login));
        et_UserName = (EditText) findViewById(R.id.et_user_login_user_no);
        et_Password = (EditText) findViewById(R.id.et_user_login_pswd);
        bt_Login = (Button) findViewById(R.id.but_user_login);
        cb_Forgetpwd = (CheckBox) findViewById(R.id.remindPwd);
    }

    private void init(){
        dialog=new MyDialog(this,0);
        userName=StorageFactory.getInstance().getSharedPreference(LoginActivity.this).getString(UserApi.LOGIN_USERNAME);
        passWord=StorageFactory.getInstance().getSharedPreference(LoginActivity.this).getString(UserApi.LOGIN_USER_PWD);
        isforgetPwd=StorageFactory.getInstance().getSharedPreference(LoginActivity.this).getBoolean(UserApi.ISFORGETPWD);

        et_UserName.setText(userName);
        et_Password.setText(passWord);
        cb_Forgetpwd.setChecked(isforgetPwd);
    }


    private void setListener() {
        bt_Login.setOnClickListener(this);
        cb_Forgetpwd.setOnCheckedChangeListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_user_login: //登录
                userName = et_UserName.getText().toString().replace(" ", "");
                passWord = et_Password.getText().toString().replace(" ", "");
                if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(passWord)) {
                    Toast.makeText(this, "账号和密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if(cb_Forgetpwd.isChecked()){ //如果点击了记住密码则保存密码
                        StorageFactory.getInstance().getSharedPreference(LoginActivity.this).saveString(UserApi.LOGIN_USERNAME,userName);
                        StorageFactory.getInstance().getSharedPreference(LoginActivity.this).saveString(UserApi.LOGIN_USER_PWD,passWord);
                }else{ //保存账号
                    StorageFactory.getInstance().getSharedPreference(LoginActivity.this).saveString(UserApi.LOGIN_USERNAME,userName);
                    StorageFactory.getInstance().getSharedPreference(LoginActivity.this).saveString(UserApi.LOGIN_USER_PWD,"");
                }
                login();
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        dialog.show();
        HttpUtils.get(UserApi.LOGIN + "?model=get_access_token" + "&user_name=" + userName + "&user_pwd=" + passWord, new IHttpUtilsCallBack() {
            @Override
            public void onFailure(HttpRequest request, IOException e) {
                Logger.i(TAG,e.toString());
            }

            @Override
            public void onSuccess(String result) throws Exception {
                dialog.dismiss();
                Logger.i(TAG,result);
                Log.e("登录返回数据：","result="+result);
                if (!"".equals(result) && !"null".equals(result)) {
                    Gson gson = HDateGsonAdapter.createGson();
                    UserInfo data = gson.fromJson(result, UserInfo.class);
                    if (null != data && data.getCode() == 1&&null!=data.getResult()) { //登录成功
                        BaseApplication.userInfo=data;
                        StorageFactory.getInstance().getSharedPreference(LoginActivity.this).saveDao(UserApi.USERINFOR,data);
                        Intent intent =new Intent(LoginActivity.this,MainWebViewActivity.class);
//                        intent.putExtra("url","http://dljk.st.somantou365.online/apps/mt/index.aspx");
                        intent.putExtra("url",UserApi.MAINWEBURL+"?access_token="+data.getResult().getAccess_token()+"&user_id="+data.getResult().getUser_id());
                        startActivity(intent);
//                        Intent intent =new Intent(LoginActivity.this,DeviceListActivity.class);
//                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onProgress(float progress, long total) {

            }
        });
    }

    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            StorageFactory.getInstance().getSharedPreference(LoginActivity.this).saveBoolean(UserApi.ISFORGETPWD,isChecked);
        }
    };
}
