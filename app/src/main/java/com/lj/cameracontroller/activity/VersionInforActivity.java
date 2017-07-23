package com.lj.cameracontroller.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lj.cameracontroller.R;
import com.lj.cameracontroller.base.BaseActivity;
import com.lj.cameracontroller.utils.VersionUtils;
import com.lj.cameracontroller.view.MyDialog;
import com.lj.cameracontroller.view.SampleListFragment;

/**
 * Created by 刘劲松 on 2017/7/23.
 * 版本信息
 */

public class VersionInforActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_version,tv_neibu_version;
    private Button bt_update;
    private String versionName="";
    private int versionCode=0;
    private MyDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_versioninfor);
        initView();
        initData();
    }

    public void initView(){
        tv_version=(TextView) findViewById(R.id.tv_version);
        tv_neibu_version=(TextView) findViewById(R.id.tv_neibu_version);
        bt_update=(Button) findViewById(R.id.bt_update);
        bt_update.setOnClickListener(this);
    }

    public void initData(){
        dialog=new MyDialog(this,0);
        versionCode= VersionUtils.getVersionCode(this);
        versionName=VersionUtils.getVersionName(this);
        tv_version.setText("v"+versionName);
        tv_neibu_version.setText(""+versionCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_update: //检查更新

                break;
            default:
                break;
        }

    }
}
