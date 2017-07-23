package com.lj.cameracontroller.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
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
import com.lj.cameracontroller.base.BaseApplication;
import com.lj.cameracontroller.utils.StringUtils;


/**
 * Created by 刘劲松 on 2017/7/5.
 * 通用标题
 */

public class TitleView extends LinearLayout {
    private View view;
    private TextView tv_title, tv_back, tv_right;
    private ImageView iv_back, iv_right,iv_right2;
    //    public Button bt_right;
    private LinearLayout ll_back, ll_right;
    private int leftBtnImg, rightBtnImg,rightBtnImg2;
    private String leftText, rightText, titleContent;
    private boolean leftBtnVisible, rightBtnVisible;//左右图片按钮是否显示
    private boolean InterceptOrReturn = false;//是否拦截返回按钮点击事件

    public interface backOnclickListener {
        public void onClick();
    }

    backOnclickListener listener;

    public void setOnBackClickListener(backOnclickListener onclickListener) {
        listener = onclickListener;
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(final Context context, AttributeSet attrs) {

        view = LayoutInflater.from(context).inflate(R.layout.layout_titleview, null);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.titleView);
        try {
            leftBtnImg = a.getResourceId(R.styleable.titleView_leftImage, 0);
            leftText = a.getString(R.styleable.titleView_leftText);
            leftBtnVisible = a.getBoolean(R.styleable.titleView_leftImageVisible, true);
            titleContent = a.getString(R.styleable.titleView_titleContent);
            rightBtnImg = a.getResourceId(R.styleable.titleView_rightImage, 0);
            rightBtnImg2 = a.getResourceId(R.styleable.titleView_rightImage2, 0);
            rightText = a.getString(R.styleable.titleView_rightText);
            rightBtnVisible = a.getBoolean(R.styleable.titleView_rightImageVisible, false);
            InterceptOrReturn = a.getBoolean(R.styleable.titleView_interceptOrReturn, false);

            ll_back = (LinearLayout) view.findViewById(R.id.ll_back);
            iv_back = (ImageView) view.findViewById(R.id.iv_back);
            iv_back.setImageResource(leftBtnImg);
            if (leftBtnVisible == true) {
                iv_back.setVisibility(VISIBLE);
            } else {
                iv_back.setVisibility(GONE);
            }
            tv_back = (TextView) view.findViewById(R.id.tv_back);
            tv_back.setText(leftText);
            if (0 == leftBtnImg && StringUtils.isEmpty(leftText)) {
                ll_back.setVisibility(INVISIBLE);
            } else {
                ll_back.setVisibility(VISIBLE);
            }

            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_title.setText(titleContent);

            ll_right = (LinearLayout) view.findViewById(R.id.ll_right);
            iv_right = (ImageView) view.findViewById(R.id.iv_right);
            iv_right2 = (ImageView) view.findViewById(R.id.iv_right2);
            iv_right.setImageResource(rightBtnImg);
            iv_right2.setImageResource(rightBtnImg2);
            if (rightBtnVisible == true) {
                iv_right.setVisibility(VISIBLE);
                iv_right2.setVisibility(VISIBLE);
            } else {
                iv_right.setVisibility(INVISIBLE);
                iv_right2.setVisibility(INVISIBLE);
            }
            if(0==rightBtnImg){
                iv_right.setVisibility(INVISIBLE);
            }else{
                iv_right.setVisibility(VISIBLE);
            }
            if(0==rightBtnImg2){
                iv_right2.setVisibility(GONE);
            }else{
                iv_right2.setVisibility(VISIBLE);
            }
            tv_right = (TextView) view.findViewById(R.id.tv_right);
            tv_right.setText(rightText);
            if (0 == rightBtnImg && StringUtils.isEmpty(rightText)) {
                ll_right.setVisibility(INVISIBLE);
            } else {
                ll_right.setVisibility(VISIBLE);
            }

            if (InterceptOrReturn == false) {
                ll_back.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Activity) context).finish();
                    }
                });
            } else {
                ll_back.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != listener) {
                            listener.onClick();
                        }
                    }
                });
            }
            addView(view);
        }finally {
            a.recycle();
        }
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTv_title(String title) {
        titleContent=title;
        tv_title.setText(titleContent);
    }

    /**
     * 设置右上角第一个按钮监听
     *
     * @param listener 点击监听
     */
    public void setrightBtnOnclick(OnClickListener listener) {
        iv_right.setOnClickListener(listener);
    }
    /**
     * 设置右上角第二个按钮监听
     *
     * @param listener 点击监听
     */
    public void setrightBtnOnclick2(OnClickListener listener) {
        iv_right2.setOnClickListener(listener);
    }

    public void setrightTextOnclick(OnClickListener listener){
        tv_right.setOnClickListener(listener);
    }

}
