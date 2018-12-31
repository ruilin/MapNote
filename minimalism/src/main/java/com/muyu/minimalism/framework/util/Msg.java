package com.muyu.minimalism.framework.util;

import android.util.Log;
import android.widget.Toast;
import com.muyu.minimalism.BuildConfig;
import com.muyu.minimalism.framework.app.BaseApplication;

public class Msg {
    private final static String TAG = "MapNote";

    public static void show(final String text) {
        if (text != null)
            Toast.makeText(BaseApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
        else
            Log.e(TAG, "no text");
    }

    public static void showDebug(final String text) {
        if (BuildConfig.DEBUG) {
            show(text);
        }
    }
}
