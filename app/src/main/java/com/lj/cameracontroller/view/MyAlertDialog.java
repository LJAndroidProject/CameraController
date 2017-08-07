package com.lj.cameracontroller.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.lj.cameracontroller.R;
import com.lj.cameracontroller.utils.Logger;


/**
 * Created by 刘劲松 on 2017/7/4.
 * 通用对话框
 */

public class MyAlertDialog extends AlertDialog implements View.OnClickListener {
    private Context mContext;
    private View view;
    private Button yes_btn;
    private Button no_btn;
    private View.OnClickListener okListener;  //确认按钮监听
    private View.OnClickListener cancelListener; //取消按钮监听
    private TextView content_tv;   //对话框内容
    private TextView dialog_tip_tv; //对话框标题


    public MyAlertDialog(Context context) {
        super(context, R.style.Dialog);
        this.mContext = context;
    }

    public MyAlertDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    /**
     * 初始化通用提示对话框
     * @return
     */
    public MyAlertDialog GeneralTipsDialog(){
        view= LayoutInflater.from(mContext).inflate(
                R.layout.layout_alertdialog_prompt, null);
        dialog_tip_tv= (TextView) view.findViewById(R.id.tv_title);
        content_tv= (TextView) view.findViewById(R.id.tv_content);
        yes_btn= (Button) view.findViewById(R.id.bt_yes);
        no_btn= (Button) view.findViewById(R.id.bt_no);
        yes_btn.setOnClickListener(this);
        no_btn.setOnClickListener(this);
        super.show();
        return this;
    }


    /**
     * 初始化通用确认操作提示对话框
     * @return
     */
    public MyAlertDialog ValidationOperationDialog(){
        view= LayoutInflater.from(mContext).inflate(
                R.layout.layout_alertdialog_validationoperation, null);
        content_tv= (TextView) view.findViewById(R.id.tv_content);
        yes_btn= (Button) view.findViewById(R.id.bt_yes);
        no_btn= (Button) view.findViewById(R.id.bt_no);
        yes_btn.setOnClickListener(this);
        no_btn.setOnClickListener(this);
        super.show();
        return this;
    }

    /**
     * 标题内容
     *
     * @param content
     */
    public MyAlertDialog setTitleTip(String content) {

        if (content != null) {
            dialog_tip_tv.setText(content);
        }
        return this;
    }

    /**
     * 提示框内容
     *
     * @param content
     */
    public MyAlertDialog setMsg(String content) {

        if (content != null) {
            content_tv.setText(content);
            Logger.e("MyAlertDialog",""+content);
        }
        Logger.e("MyAlertDialog",""+content);
        return this;
    }

    /**
     * 设置两个按钮的内容
     * @param yesBtn 右边按钮内容
     * @param noBtn  左边按钮内容
     * @return
     */
    public MyAlertDialog setButtonContent(String yesBtn,String noBtn){
        yes_btn.setText(yesBtn);
        no_btn.setText(noBtn);
        return this;
    }

    /**
     * 设置确定按钮是否可见
     * @param gone
     * @return
     */
    public MyAlertDialog setYesButtonGone(boolean gone){
        if(gone){
            yes_btn.setVisibility(View.GONE);
        }
        return this;
    }
    /**
     * 设置取消按钮是否可见
     * @param gone
     * @return
     */
    public MyAlertDialog setNoButtonGone(boolean gone){
        if(gone){
            no_btn.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 显示对话框
     */
    public void show() {
        getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setContentView(view);
        Logger.e("MyAlertDialog","路过一波");
    }

    /**
     * 确定点击事件
     */
    public MyAlertDialog setPositiveButton(
            View.OnClickListener listener) {
        this.okListener = listener;
        return this;
    }

    /**
     * 取消点击事件
     *
     * @param listener
     * @return
     */
    public MyAlertDialog setNegativeButton(
            View.OnClickListener listener) {
        this.cancelListener = listener;
        return this;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bt_yes){
            this.okListener.onClick(v);
        }else if(v.getId()==R.id.bt_no){
            this.cancelListener.onClick(v);
        }
    }
}
