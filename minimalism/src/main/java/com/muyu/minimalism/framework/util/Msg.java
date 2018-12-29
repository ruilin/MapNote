package com.muyu.minimalism.framework.util;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import com.muyu.minimalism.BuildConfig;

public class Msg {
    private static Application sAppContext;

    public static void setAppContext(Application appContext) {
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
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
