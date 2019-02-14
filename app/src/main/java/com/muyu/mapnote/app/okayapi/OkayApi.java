package com.muyu.mapnote.app.okayapi;

import android.support.annotation.NonNull;

import com.muyu.minimalism.framework.app.BaseApplication;
import com.muyu.minimalism.utils.SPUtils;

public class OkayApi {
    private static final String SP_KEY_USER_TOKEN = "SP_KEY_USER_TOKEN";
    private static final String SP_KEY_USER_UUID = "SP_KEY_USER_UUID";
    private static final String SP_KEY_USER_NAME = "SP_KEY_USER_NAME";
    private static final String SP_KEY_USER_PASSWORD = "SP_KEY_USER_PASSWORD";
    private static OkayApi instance = null;
    private String host = "";
    private String appKey = "";
    private String appSecret = "";
    private OkUser user = null;

    public static void create(String host, String appKey, String appSecret) {
        instance = new OkayApi();
        instance.host = host;
        instance.appKey = appKey;
        instance.appSecret = appSecret;

        if (SPUtils.contains(SP_KEY_USER_TOKEN)) {
            instance.user = new OkUser();
            instance.user.setToken(SPUtils.get(SP_KEY_USER_TOKEN, ""));
            instance.user.setUuid(SPUtils.get(SP_KEY_USER_UUID, ""));
            instance.user.setUsername(SPUtils.get(SP_KEY_USER_NAME, ""));
            instance.user.setPassword(SPUtils.get(SP_KEY_USER_PASSWORD, ""));
        }
    }

    public static OkayApi get() { return instance; }
    public String getHost() { return host; }
    public String getAppKey() { return appKey; }
    public String getAppSecret() { return appSecret; }

    public void setUser(@NonNull OkUser user) {
        this.user = user;
        SPUtils.put(SP_KEY_USER_TOKEN, user.getToken());
        SPUtils.put(SP_KEY_USER_UUID, user.getUuid());
        SPUtils.put(SP_KEY_USER_NAME, user.getUserName());
        SPUtils.put(SP_KEY_USER_PASSWORD, user.getPassword());
    }

    public OkUser getCurrentUser() {
        return user;
    }

    public boolean isLogined() {
        return instance.user != null;
    }

    public void logOut() {
        if (isLogined()) {
            SPUtils.remove(BaseApplication.getInstance(), SP_KEY_USER_TOKEN);
            SPUtils.remove(BaseApplication.getInstance(), SP_KEY_USER_UUID);
            SPUtils.remove(BaseApplication.getInstance(), SP_KEY_USER_NAME);
            SPUtils.remove(BaseApplication.getInstance(), SP_KEY_USER_PASSWORD);
            setUser(null);
        }
    }
}
