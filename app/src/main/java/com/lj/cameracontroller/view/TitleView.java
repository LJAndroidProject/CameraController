package com.lj.cameracontroller.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lj.cameracontroller.R;
import com.lj.cameracontroller.utils.StringUtils;


/**
 * Created by 刘劲松 on 2017/7/5.
 * 通用标题
 */

public class TitleView extends LinearLayout {
    public View view;
    public TextView tv_title;
    public ImageView iv_back;
    public Button bt_right;


    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_titleview, null);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        bt_right = (Button) view.findViewById(R.id.bt_right);
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        addView(view);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTv_title(String title) {
        tv_title.setText(title);
    }

    /**
     * 设置返回按钮是否可见
     * @param visible
     */
    public void setIv_backVisible(boolean visible) {
        if (visible) {
            iv_back.setVisibility(VISIBLE);
        } else {
            iv_back.setVisibility(INVISIBLE);
        }
    }

    /**
     * 设置右上角按钮内容及是否可见
     *
     * @param rightText 文字内容
     * @param drawable  图片资源
     * @param listener  点击监听
     */
    public void setBt_right(String rightText, Drawable drawable, OnClickListener listener) {
        if (StringUtils.isEmpty(rightText) && null == drawable) {
            bt_right.setVisibility(INVISIBLE);
        } else {
            bt_right.setVisibility(VISIBLE);
            bt_right.setText(rightText);
            if (null != drawable) {
                bt_right.setBackground(drawable);
            }
        }
        bt_right.setOnClickListener(listener);
    }


}
