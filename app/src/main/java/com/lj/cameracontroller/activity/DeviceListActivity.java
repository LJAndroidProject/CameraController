package com.lj.cameracontroller.activity;

import android.os.Bundle;
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
        fragment.setOnItemClickListener(new SampleListFragment.OnItemClickListener() {
            @Override
            public void Onclick(int position) {
                Toast.makeText(DeviceListActivity.this, "点击了" + position, Toast.LENGTH_LONG).show();
                menu.showContent();
            }
        });
        titleView.setOnBackClickListener(new TitleView.backOnclickListener() {
            @Override
            public void onClick() {
                menu.showMenu();
            }
        });
        titleView.setrightBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleView.setVisibility(View.GONE);
                ll_search.setVisibility(View.VISIBLE);
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleView.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.GONE);
            }
        });
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

                }
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
                if (!"".equals(result) && !"null".equals(result)) {
                    Gson gson = HDateGsonAdapter.createGson();
                    DeviceListEntity data = gson.fromJson(result, DeviceListEntity.class);
                    if (null != data && data.getCode() == 1 && null != data.getResult() && data.getResult().size() > 0) { //获取数据成功
                        listData.clear();
                        listData.addAll(data.getResult());
                        adapter.notifyDataSetChanged();
                    }
                }
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


}
