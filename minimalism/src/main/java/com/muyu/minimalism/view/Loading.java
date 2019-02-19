package com.muyu.minimalism;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.muyu.minimalism.utils.StringUtils;

public abstract class Loading extends Dialog {

    public abstract void cancel();
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
