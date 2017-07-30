package com.lj.cameracontroller.view;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.hikvision.netsdk.NET_DVR_PRESET_NAME;
import com.hikvision.netsdk.NET_DVR_PRESET_NAME_ARRAY;
import com.lj.cameracontroller.R;
import com.lj.cameracontroller.adapter.PresetPupAdapter;

/**
 * Created by Administrator on 2017/7/24 0024.
 */

public class PresetListPupwindow extends PopupWindow{
    private View windowView;
    private RecyclerView dataList;
    private PresetPupAdapter presetPupAdapter;
    /**
     * 预置点pupwindow初始化构造方法
     * @param context 上下文对象
     * @param datas 需要加载的数据
     * @param width 预置点控件宽度
     */
    public PresetListPupwindow(Context context, NET_DVR_PRESET_NAME[] datas, int width,int height){
        windowView = LayoutInflater.from(context).inflate(R.layout.pupwindow_preset,null);
        dataList = (RecyclerView) windowView.findViewById(R.id.pup_presetRlv);
        presetPupAdapter = new PresetPupAdapter(context,datas);
        dataList.setLayoutManager(new LinearLayoutManager(context));
        dataList.setAdapter(presetPupAdapter);
        // 设置SelectPicPopupWindow的View
        this.setContentView(windowView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(height);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        windowView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int top = windowView.findViewById(R.id.pup_presetRlv)
                        .getTop();
                int bottom = windowView.findViewById(R.id.pup_presetRlv).getBottom();
                int left = windowView.findViewById(R.id.pup_presetRlv).getLeft();
                int right = windowView.findViewById(R.id.pup_presetRlv).getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < top || y > bottom) {
                        dismiss();
                    }
                    if (x < left || x > right)
                        dismiss();
                }
                return true;
            }
        });
    }

    /**
     * 获取PresetPupAdapter对象
     * @return
     */
    public PresetPupAdapter getPresetPupAdapter(){
        return presetPupAdapter;
    }
}
