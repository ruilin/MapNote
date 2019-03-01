package com.muyu.minimalism.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;

public class SysUtils {

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static void runOnUiThread(Runnable run) {
        new Handler(Looper.getMainLooper()).post(run);
    }

    public static void runOnUiThreadDelayed(Runnable run, long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(run, delay);
    }

    public static void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    public static void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        }
    }

    public static void setStatusBarColor(Activity activity, @ColorInt int color) {
        StatusBarCompat.setStatusBarColor(activity, color, true);
    }
}
