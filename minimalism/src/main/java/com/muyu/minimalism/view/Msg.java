package com.muyu.minimalism.view;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import com.muyu.minimalism.BuildConfig;
import com.muyu.minimalism.utils.Logs;
import com.muyu.minimalism.utils.SysUtils;

public class Msg {
    private static Application sAppContext;

    public static void create(Application appContext) {
        sAppContext = appContext;
    }

    public static void show(final String text) {
        if (sAppContext != null) {
            show(sAppContext, text, false);
        } else {
            throw new RuntimeException("Please invoke setAppContext() first when Application onCreated().");
        }
    }

    public static void showLong(final String text) {
        if (sAppContext != null) {
            show(sAppContext, text, true);
        } else {
            throw new RuntimeException("Please invoke setAppContext() first when Application onCreated().");
        }
    }

    public static void showDebug(final String text) {
        if (BuildConfig.DEBUG) {
            show(text);
        }
    }

    public static void show(final Context context, final String text, final boolean showLong) {
        if (text != null) {
            if (SysUtils.isMainThread()) {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            } else {
                SysUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, text, (showLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
                    }
                });
            }
        } else {
            Logs.e("text == null");
        }
    }
}
