package com.muyu.mapnote.app;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.mapbox.mapboxsdk.Mapbox;
import com.muyu.mapnote.app.okayapi.OkayApi;
import com.muyu.minimalism.framework.app.BaseApplication;
import com.muyu.minimalism.utils.Msg;


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

        OkayApi.create("http://hn1.api.okayapi.com/", "6DE3414C5B914826ADC91EFF3555DBBF", "zypfNWZ40b8V2R4sStiSbczkqNKafncrNnN20pw35wzzqXCxWYkkde710");

        Mapbox.getInstance(getApplicationContext(), "sk.eyJ1IjoiemhlbmdydWlsaW4iLCJhIjoiY2pjbGp0emhvMGJqaDJ4cm5vcWY2aGpmbSJ9.9SbamWpiv5iegnxrwU1WWA");


        // 初始化参数依次为 this, AppId, AppKey
        // AVOSCloud.initialize(this,"{{appid}}","{{appkey}}");

        //Log.e("Map", "xxxxxxxxxx   " + sHA1(this));
    }

    public static MapApplication getInstance() {
        return Instance;
    }

}
