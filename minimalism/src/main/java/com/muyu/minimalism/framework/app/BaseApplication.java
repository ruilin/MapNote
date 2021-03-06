package com.muyu.minimalism.framework.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.muyu.minimalism.BuildConfig;
import com.muyu.minimalism.utils.Logs;
import com.muyu.minimalism.view.Msg;
import com.muyu.minimalism.utils.SPUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public abstract class BaseApplication extends Application {
    private final String SP_KEY_LAUNCH = "launch_count";
    protected static BaseApplication Instance;

    public static BaseApplication getInstance() {
        return Instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;

        if (BuildConfig.DEBUG) {
            Logs.e("sHA1: " + sHA1(this));
        }
        initPhotoError();
        Msg.create(this);
        SPUtils.create(this);

    }

    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    public boolean isFirshLaunch() {
        int launchCount = SPUtils.get(SP_KEY_LAUNCH, 0);
        SPUtils.put(SP_KEY_LAUNCH, ++launchCount);
        return launchCount <= 1;
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
