package com.muyu.minimalism;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public abstract class Loading extends Dialog {

    public abstract void cancle();

    public Loading(Context context) {
        super(context, R.style.Loading);
        // 加载布局
        setContentView(R.layout.view_loading);
        // 设置Dialog参数
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    // TODO 封装Dialog消失的回调
    @Override
    public void onBackPressed() {
        // 回调
        cancle();
        // 关闭Loading
        dismiss();
    }
}
