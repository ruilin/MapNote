package com.muyu.mapnote.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.mapbox.mapboxsdk.Mapbox;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Created by zzc on 2018/1/23.
 */

public class MapApplication extends Application {
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


    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
