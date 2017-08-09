package com.lj.cameracontroller.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lj.cameracontroller.R;
import com.lj.cameracontroller.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 2017/7/21
 *
 * 功能描述：底部弹窗
 **/
public class ActionSheetDialog {

    private Context context;
    private Dialog dialog;
    private TextView txt_cancel;
    private LinearLayout lLayout_content;
    private ScrollView sLayout_content;
    private List<SheetItem> sheetItemList;
    private Display display;

    public ActionSheetDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public ActionSheetDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.actionsheet_dialog, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件
        sLayout_content = (ScrollView) view.findViewById(R.id.sLayout_content);
        lLayout_content = (LinearLayout) view
                .findViewById(R.id.lLayout_content);
        txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();


        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        return this;
    }

    /**
     * 设置为false，dialog弹出后会点击屏幕或物理返回键，dialog不消失
     *
     * @param cancel
     * @return
     */
    public ActionSheetDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    /**
     * 设置为false,dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
     *
     * @param cancel
     * @return
     */
    public ActionSheetDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    /**
     * @param strItem  条目名称
     * @param color    条目字体颜色，设置null则默认蓝色
     * @param listener
     * @return
     */
    public ActionSheetDialog addSheetItem(String strItem, SheetItemColor color,
                                          OnSheetItemClickListener listener) {
        if (sheetItemList == null) {
            sheetItemList = new ArrayList<SheetItem>();
        }
        sheetItemList.add(new SheetItem(strItem, color, listener));
        return this;
    }

    /**
     * 设置条目布局
     */
    private void setSheetItems() {
        if (sheetItemList == null || sheetItemList.size() <= 0) {
            return;
        }

        int size = sheetItemList.size();

        // TODO 高度控制，非最佳解决办法
        // 添加条目过多的时候控制高度
        if (size >= 7) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sLayout_content
                    .getLayoutParams();
            params.height = display.getHeight() / 2;
            sLayout_content.setLayoutParams(params);
        }

        // 循环添加条目
        for (int i = 1; i <= size; i++) {
            final int index = i;
            SheetItem sheetItem = sheetItemList.get(i - 1);
            String strItem = sheetItem.name;
            SheetItemColor color = sheetItem.color;
            final OnSheetItemClickListener listener = (OnSheetItemClickListener) sheetItem.itemClickListener;

            TextView textView = new TextView(context);
            textView.setText(strItem);
            textView.setTextSize(ThemeUtils.getThemeTextSize(context, R.attr.textSizeSmall));
            textView.setGravity(Gravity.CENTER);

            // 背景图片
            if (size == 1) {

                textView.setBackgroundResource(R.drawable.selector_actionsheet_single);

            } else {

                if (i == 1) {
                    textView.setBackgroundResource(R.drawable.selector_actionsheet_top);
                } else if (i < size) {
                    textView.setBackgroundResource(R.drawable.selector_actionsheet_middle);
                } else {
                    textView.setBackgroundResource(R.drawable.selector_actionsheet_bottom);
                }

            }
            // 字体颜色
            if (color == null) {
                textView.setTextColor(Color.parseColor(SheetItemColor.Gray
                        .getName()));
            } else {
                textView.setTextColor(Color.parseColor(color.getName()));
            }

            // 高度
            float scale = context.getResources().getDisplayMetrics().density;
            int height = (int) (45 * scale + 0.5f);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, height));

            // 点击事件
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(index);
                    dialog.dismiss();
                }
            });

            lLayout_content.addView(textView);
        }
    }

    /**
     * dialog是否显示
     *
     * @return
     */
    public boolean isShow() {
        return dialog.isShowing();
    }

    /**
     * dialog显示
     */
    public void show() {
        setSheetItems();
        dialog.show();
    }

    /**
     * dialog关闭
     */
    public void dissmiss() {
        dialog.dismiss();
    }

    /**
     * 对外点击接口
     */
    public interface OnSheetItemClickListener {
        void onClick(int which);
    }

    /**
     * 内部类
     */
    public class SheetItem {
        String name;
        OnSheetItemClickListener itemClickListener;
        SheetItemColor color;

        public SheetItem(String name, SheetItemColor color,
                         OnSheetItemClickListener itemClickListener) {
            this.name = name;
            this.color = color;
            this.itemClickListener = itemClickListener;
        }
    }

    /**
     * 初始化设定值
     */
    public enum SheetItemColor {
        Blue("#007aff"), Red("#FD4A2E"), Main_Color("#F76114"), Gray("#999999");

        private String name;

        private SheetItemColor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
