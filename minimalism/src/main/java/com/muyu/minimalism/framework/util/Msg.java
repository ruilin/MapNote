package com.muyu.minimalism.framework.util;

import android.widget.Toast;
import com.muyu.minimalism.BuildConfig;
import com.muyu.minimalism.framework.app.BaseApplication;

public class Msg {
    public static void show(final String text) {
        Toast.makeText(BaseApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showDebug(final String text) {
        if (BuildConfig.DEBUG) {
            show(text);
        }
    }
}
