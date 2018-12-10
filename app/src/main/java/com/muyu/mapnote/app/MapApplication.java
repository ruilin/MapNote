package com.muyu.mapnote.app;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.mapbox.mapboxsdk.Mapbox;
import com.muyu.minimalism.framework.app.BaseApplication;


/**
 * Created by ruilin on 2018/1/23.
 */

public class MapApplication extends BaseApplication {
    private static MapApplication Instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        Mapbox.getInstance(getApplicationContext(), "sk.eyJ1IjoiemhlbmdydWlsaW4iLCJhIjoiY2pjbGp0emhvMGJqaDJ4cm5vcWY2aGpmbSJ9.9SbamWpiv5iegnxrwU1WWA");


        // 初始化参数依次为 this, AppId, AppKey
        // AVOSCloud.initialize(this,"{{appid}}","{{appkey}}");

        //Log.e("Map", "xxxxxxxxxx   " + sHA1(this));
    }

    public static MapApplication getInstance() {
        return Instance;
    }

}
