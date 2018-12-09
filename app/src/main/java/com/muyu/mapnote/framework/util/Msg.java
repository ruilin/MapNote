package com.muyu.mapnote.framework.util;

import android.widget.Toast;

import com.muyu.mapnote.BuildConfig;
import com.muyu.mapnote.app.MapApplication;

public class Msg {
    public static void show(String text) {
        Toast.makeText(MapApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showDebug(String text) {
        if (BuildConfig.DEBUG) {
            show(text);
        }
    }
}
