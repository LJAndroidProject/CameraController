package com.lj.cameracontroller.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lj.cameracontroller.R;


/**
 * 通用网络请求等待dialog
 */
public class MyDialog extends Dialog {

    private ImageView loading_img;
    private TextView load_title;
    private boolean titleVisible = true;//是否显示标题

    public MyDialog(Context context, int addDialog) {
        super(context, addDialog);
    }

    public MyDialog(Context context, int addDialog, boolean titleVisible) {
        super(context, addDialog);
        this.titleVisible = titleVisible;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mydialog_item);
        loading_img = (ImageView) findViewById(R.id.loading_img);
        setCanceledOnTouchOutside(false);
        load_title = (TextView) findViewById(R.id.load_title);
        if (titleVisible) {
            load_title.setVisibility(View.VISIBLE);
        } else {
            load_title.setVisibility(View.GONE);
        }
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                loading_img.setImageResource(R.drawable.lading_live);
                AnimationDrawable animationDrawable = (AnimationDrawable) loading_img
                        .getDrawable();
                animationDrawable.start();
            }
        });
        setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                loading_img.clearAnimation();
            }
        });
    }

    public void setTitle(String title) {
        load_title.setText(title + "");
    }
}
