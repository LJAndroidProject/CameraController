package com.lj.cameracontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lj.cameracontroller.R;
import com.lj.cameracontroller.adapter.DeviceAdapter;
import com.lj.cameracontroller.base.BaseActivity;
import com.lj.cameracontroller.base.HDateGsonAdapter;
import com.lj.cameracontroller.constant.UserApi;
import com.lj.cameracontroller.entity.DeviceListEntity;
import com.lj.cameracontroller.entity.HttpRequest;
import com.lj.cameracontroller.entity.SampleItem;
import com.lj.cameracontroller.entity.UserInfo;
import com.lj.cameracontroller.interfacecallback.IHttpUtilsCallBack;
import com.lj.cameracontroller.utils.HttpUtils;
import com.lj.cameracontroller.utils.Logger;
import com.lj.cameracontroller.utils.StorageFactory;
import com.lj.cameracontroller.utils.StringUtils;
import com.lj.cameracontroller.view.MyDialog;
import com.lj.cameracontroller.view.SampleListFragment;
import com.lj.cameracontroller.view.TitleView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘劲松 on 2017/7/19.
 * 设备列表页面
 */

public class DeviceListActivity extends BaseActivity {
    private static final String TAG = "DeviceListActivity";
    private SlidingMenu menu;
    private SampleListFragment fragment;
    private TitleView titleView;
    private LinearLayout ll_search;//搜索布局
    private EditText et_search_text;//搜索输入框
    private Button btn_clear;//删除
    private TextView tv_cancel;//取消
    private UserInfo userInfo = null;
    private String userId = "";
    private String accessToken = "";
    private  List<DeviceListEntity.DeviceEntity> listData=new ArrayList<DeviceListEntity.DeviceEntity>();
    private  List<DeviceListEntity.DeviceEntity> listData2=new ArrayList<DeviceListEntity.DeviceEntity>();
    private RecyclerView rv_listView;
    private DeviceAdapter adapter;
    private MyDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist);
        initView();
        initData();
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.tv_top);
        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        et_search_text = (EditText) findViewById(R.id.et_search_text);
        btn_clear = (Button) findViewById(R.id.btn_clear_search2);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        rv_listView=(RecyclerView) findViewById(R.id.rv_recyclerview);
        rv_listView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new DeviceAdapter(DeviceListActivity.this,listData);
        rv_listView.setAdapter(adapter);
        fragment = new SampleListFragment();
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
        fragment.setOnItemClickListener(new SampleListFragment.OnItemClickListener() {
            @Override
            public void Onclick(int position) {
                menu.showContent();
                switch (position){
                    case 0:  //主页
                        finish();
                        break;
                    case 1:  //设置
                        Intent intentSet =new Intent(DeviceListActivity.this,SettingWebActivity.class);
                        intentSet.putExtra("url",UserApi.SETTINGWEBURL+"?access_token="+accessToken+"&user_id="+userId
                                +"&mobile="+ StorageFactory.getInstance().getSharedPreference(DeviceListActivity.this).getString(UserApi.LOGIN_USERNAME));
                        startActivity(intentSet);
                        finish();
                        break;
                    case 2:  //注销
                        Intent intent =new Intent(DeviceListActivity.this,LoginActivity.class);
                        startActivity(intent);
                        StorageFactory.getInstance().getSharedPreference(DeviceListActivity.this).saveBoolean(UserApi.ISFORGETPWD,false);
                        if (null != MainWebViewActivity.mActiviry)
                        MainWebViewActivity.mActiviry.finish();
                        finish();
                        break;
                    case 3: //退出
                        StorageFactory.getInstance().getSharedPreference(DeviceListActivity.this).saveBoolean(UserApi.ISFORGETPWD,false);
                        if (null != MainWebViewActivity.mActiviry)
                            MainWebViewActivity.mActiviry.finish();
                       System.exit(0);
                        break;
                    case 4: //版本信息
                        Intent intent4=new Intent(DeviceListActivity.this,VersionInforActivity.class);
                        startActivity(intent4);
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
        //TODO 点击搜索按钮
        titleView.setrightBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleView.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
            }
        });

        //TODO 点击刷新按钮
        titleView.setrightBtnOnclick2(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceList();
            }
        });

        //TODO 点击搜索时的取消按钮
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listData.clear();
                listData.addAll(listData2);
                adapter.notifyDataSetChanged();
                titleView.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.GONE);
            }
        });

        //TODO 点击搜索框中清空按钮
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search_text.setText("");
            }
        });

        //TODO 对搜索输入框设置监听
        et_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!StringUtils.isEmpty(text)) {
                    btn_clear.setVisibility(View.VISIBLE);
                    search(text);
                }else{
                    btn_clear.setVisibility(View.GONE);
                    listData.clear();
                    listData.addAll(listData2);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //TODO 点击设备列表中的某一项
        adapter.setOnItemClickListener(new DeviceAdapter.OnItemclickListener() {
            @Override
            public void ItemClick(int position) {
                Intent intent=new Intent(DeviceListActivity.this,IPCPlayControlActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",listData.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //TODO 点击设备列表某一项中的设置按钮
        adapter.setSettingOclick(new DeviceAdapter.SettingOclick() {
            @Override
            public void ClickSet(int position) {
//                Toast.makeText(DeviceListActivity.this, "点击了设置" + position, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void initData() {
        dialog=new MyDialog(this,0);
        userInfo = StorageFactory.getInstance().getSharedPreference(DeviceListActivity.this).getDao(UserApi.USERINFOR, UserInfo.class);
        if (null != userInfo && null != userInfo.getResult()) {
            userId = userInfo.getResult().getUser_id();
            accessToken = userInfo.getResult().getAccess_token();
            getDeviceList();
        } else {

        }
    }


    public void search(String str){
        listData.clear();
        if(null!=listData2){
            for(int i=0;i<listData2.size();i++){
                String name=listData2.get(i).getGds_name()
                        +"-"+listData2.get(i).getPdf_name()
                        +"-"+listData2.get(i).getIpc_name();
                if(name.contains(str)){
                    listData.add(listData2.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    /**
     * 获取设备列表数据
     */
    public void getDeviceList() {
        dialog.show();
        HttpUtils.get(UserApi.IPC_INFO + "?access_token=" + accessToken
                + "&model=get_ipc_info_list"
                + "&user_id=" + userId, new IHttpUtilsCallBack() {
            @Override
            public void onFailure(HttpRequest request, IOException e) {
                Logger.e(TAG, e.toString());
            }

            @Override
            public void onSuccess(String result) throws Exception {
                dialog.dismiss();
                Logger.e(TAG, result);
                Log.e("设备列表返回数据：","result="+result);
                Message message = Message.obtain();
                message.obj = result;
                message.what = 0;
                myHandler.sendMessage(message);
            }

            @Override
            public void onProgress(float progress, long total) {

            }
        });
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

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    String result = (String) msg.obj;
                    if (!"".equals(result) && !"null".equals(result)) {
                        Gson gson = HDateGsonAdapter.createGson();
                        DeviceListEntity data = gson.fromJson(result, DeviceListEntity.class);
                        if (null != data && data.getCode() == 1 && null != data.getResult() && data.getResult().size() > 0) { //获取数据成功
                            listData.clear();
                            listData2.clear();
                            listData.addAll(data.getResult());
                            listData2.addAll(data.getResult());
                            adapter.notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }
    };

}
