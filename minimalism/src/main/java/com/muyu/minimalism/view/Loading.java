package com.muyu.minimalism.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Build;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.muyu.minimalism.R;
import com.muyu.minimalism.utils.StringUtils;

public class Loading extends Dialog {

    private TextView tv;

    public Loading(Context context) {
        super(context, R.style.Loading);
        // 加载布局
        setContentView(R.layout.view_loading);
        tv = findViewById(R.id.loading_tv);

        // 设置Dialog参数
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public Loading(Context context, boolean light) {
        super(context, R.style.LoadingLight);
        // 加载布局
        setContentView(R.layout.view_loading);
        ProgressBar bar = findViewById(R.id.login_progress);
        int color = context.getResources().getColor(R.color.colorPrimaryDark);
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bar.setIndeterminateTintList(colorStateList);
            bar.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
        }
        tv = findViewById(R.id.loading_tv);
        tv.setTextColor(color);
        // 设置Dialog参数
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public void show(String message) {
        if (!StringUtils.isEmpty(message)) {
            tv.setText(message);
        } else {
            tv.setText("");
        }
        tv.setText(message);
        show();
    }

    // TODO 封装Dialog消失的回调
    @Override
    public void onBackPressed() {
        // 回调
        cancel();
        // 关闭Loading
        dismiss();
    }
}
