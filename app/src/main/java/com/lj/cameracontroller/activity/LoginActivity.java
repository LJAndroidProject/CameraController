package com.lj.cameracontroller.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.lj.cameracontroller.R;
import com.lj.cameracontroller.base.BaseActivity;
import com.lj.cameracontroller.constant.UserApi;
import com.lj.cameracontroller.entity.HttpRequest;
import com.lj.cameracontroller.interfacecallback.IHttpUtilsCallBack;
import com.lj.cameracontroller.utils.HttpUtils;
import com.lj.cameracontroller.utils.StringUtils;
import com.lj.cameracontroller.view.TitleView;

import java.io.IOException;

/**
 * Created by 刘劲松 on 2017/7/16.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private EditText et_UserName,et_Password;
    private Button bt_Login;
    private CheckBox  cb_Forgetpwd;
    private TitleView titleView;
    private String userName="";
    private String passWord="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private  void initView(){
        titleView=(TitleView) findViewById(R.id.tv_top);
        titleView.setTv_title(getResources().getString(R.string.str_login));
        et_UserName=(EditText) findViewById(R.id.et_user_login_user_no);
        et_Password=(EditText) findViewById(R.id.et_user_login_pswd);
        bt_Login=(Button) findViewById(R.id.but_user_login);
        cb_Forgetpwd=(CheckBox) findViewById(R.id.remindPwd);

    }

    private void setListener(){
        bt_Login.setOnClickListener(this);
        cb_Forgetpwd.setOnCheckedChangeListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.but_user_login: //登录
                userName=et_UserName.getText().toString().replace(" ","");
                passWord=et_Password.getText().toString().replace(" ","");
                if(StringUtils.isEmpty(userName)||StringUtils.isEmpty(passWord)){
                    Toast.makeText(this,"账号和密码不能为空",Toast.LENGTH_LONG).show();
                    return;
                }

                break;
            default:
                break;
        }
    }

    private void login(){
        UserApi.map.clear();
        UserApi.map.put("","");
        UserApi.map.put("","");
        UserApi.map.put("","");
        HttpUtils.post(UserApi.LOGIN, UserApi.map, new IHttpUtilsCallBack() {

            @Override
            public void onFailure(HttpRequest request, IOException e) {

            }

            @Override
            public void onSuccess(String result) throws Exception {

            }

            @Override
            public void onProgress(float progress, long total) {

            }
        });
    }
    private CompoundButton.OnCheckedChangeListener listener= new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    };
}
