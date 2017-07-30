package com.lj.cameracontroller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hikvision.netsdk.NET_DVR_PRESET_NAME;
import com.hikvision.netsdk.NET_DVR_PRESET_NAME_ARRAY;
import com.lj.cameracontroller.R;
import com.lj.cameracontroller.utils.Logger;
import com.lj.cameracontroller.view.SoftReferenceImageView;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/7/24 0024.
 */

public class PresetPupAdapter extends RecyclerView.Adapter<PresetPupAdapter.MyViewHolder> {
    private final String TAG = "PresetPupAdapter";
    private NET_DVR_PRESET_NAME[] datas;
    private LayoutInflater inflater;
    private Context context;
    private PresetPupAdapterListener presetPupAdapterListener;
    public PresetPupAdapter(Context context, NET_DVR_PRESET_NAME[] datas){
        this.datas = datas;
        inflater = LayoutInflater.from(context);
        context = this.context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(inflater.inflate(R.layout.item_pup_preset,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(null != datas && datas.length > 0){
                holder.tv_presetName.setText(""+(position+1));
            holder.tv_presetName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Logger.e(TAG,"diandiandian");
                    if(null != presetPupAdapterListener){
                        presetPupAdapterListener.clickItem(position+1);
                    }
                }
            });
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return datas.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_presetName;
        public MyViewHolder(View view){
            super(view);
            tv_presetName=(TextView) view.findViewById(R.id.preset_name);
        }
    }

    public void setPresetPupAdapterListener(PresetPupAdapterListener presetPupAdapterListener){
        this.presetPupAdapterListener = presetPupAdapterListener;
    }

    public interface PresetPupAdapterListener{
        public void clickItem(int dwPresetIndex);
    }
}
