package com.lj.cameracontroller.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lj.cameracontroller.R;
import com.lj.cameracontroller.entity.DeviceListEntity;
import com.lj.cameracontroller.utils.StringUtils;
import com.lj.cameracontroller.view.SoftReferenceImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘劲松 on 2017/7/23.
 * 设备列表适配器
 */

public class DeviceAdapter  extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder>{

    private Activity mActivity;
    private List<DeviceListEntity.DeviceEntity> listData= new ArrayList<DeviceListEntity.DeviceEntity>();

    public DeviceAdapter(Activity act,List<DeviceListEntity.DeviceEntity> list){
        mActivity=act;
        listData=list;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.device_item, parent,false));

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            if(null!=listData&&listData.size()>0){
                if("1".equals(listData.get(position).getIpc_status())){ //在线
                    holder.tv_start.setText(mActivity.getResources().getString(R.string.str_online));
                    holder.ll_content.setBackgroundColor(mActivity.getResources().getColor(R.color.secondary_color_999999));
                }else{
                    holder.tv_start.setText(mActivity.getResources().getString(R.string.str_offline));
                    holder.ll_content.setBackgroundColor(mActivity.getResources().getColor(R.color.secondary_color_cccccc));
                }
                if(!StringUtils.isEmpty(listData.get(position).getIpc_pic())){
                    holder.iv_image.setDefaultImage(R.mipmap.tukuxuanze_img);
                    holder.iv_image.setImageUrlAndSaveLocal(listData.get(position).getIpc_pic(),true,
                            ImageView.ScaleType.CENTER_CROP);
                }else{
                    holder.iv_image.setDefaultImage(R.mipmap.tukuxuanze_img);
                }
                holder.tv_name.setText(listData.get(position).getGds_name()
                        +"-"+listData.get(position).getPdf_name()
                        +"-"+listData.get(position).getIpc_name());
            }
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout ll_content;
        public SoftReferenceImageView iv_image;
//        public ImageView iv_image;
        public TextView tv_start,tv_name;
        public ImageView iv_setting;
        public MyViewHolder(View view){
            super(view);
            ll_content=(LinearLayout) view.findViewById(R.id.ll_content);
            iv_image=(SoftReferenceImageView) view.findViewById(R.id.iv_image);
//            iv_image=(ImageView) view.findViewById(R.id.iv_image);
            tv_start=(TextView) view.findViewById(R.id.tv_start);
            tv_name=(TextView) view.findViewById(R.id.tv_name);
            iv_setting=(ImageView) view.findViewById(R.id.iv_setting);
        }
    }

}
