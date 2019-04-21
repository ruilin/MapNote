package com.muyu.minimalism.utils;

import android.util.Log;

import com.muyu.minimalism.BuildConfig;

public class Logs {

    private final static String TAG = Logs.class.getSimpleName() + ":";

    public static void i(String text) {
        Log.i(getTag(), text);
    }

    public static void d(String text) {
        if (BuildConfig.DEBUG)
            Log.d(getTag(), text);
    }

    public static void e(String text) {
        if (!StringUtils.isEmpty(text)) {
            Log.e(getTag(), text);
        }
    }

    private static String getTag() {
        String invokerName = "";
        StackTraceElement[] stack = new Exception().getStackTrace();
        if (stack.length > 2) {
            invokerName = stack[2].getFileName() + ":" + stack[2].getLineNumber();
        }
        return TAG + invokerName;
    }
}
