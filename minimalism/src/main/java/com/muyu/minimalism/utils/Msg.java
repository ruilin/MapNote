package com.muyu.minimalism.utils;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import com.muyu.minimalism.BuildConfig;

public class Msg {
    private static Application sAppContext;

    public static void create(Application appContext) {
        sAppContext = appContext;
    }

    public static void show(final String text) {
        if (sAppContext != null) {
            show(sAppContext, text);
        } else {
            throw new RuntimeException("Please invoke setAppContext() first when Application onCreated().");
        }
    }

    public static void showDebug(final String text) {
        if (BuildConfig.DEBUG) {
            show(text);
        }
    }

    public static void show(final Context context, final String text) {
        if (text != null)
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        else
            MLog.e("text == null");
    }
}
