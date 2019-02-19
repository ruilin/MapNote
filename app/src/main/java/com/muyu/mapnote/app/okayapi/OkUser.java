package com.muyu.mapnote.app.okayapi;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.muyu.mapnote.app.okayapi.callback.LoginCallback;
import com.muyu.mapnote.app.okayapi.callback.RegisterCallback;
import com.muyu.mapnote.app.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.MD5Utils;
import com.muyu.minimalism.utils.Logs;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkUser extends OkObject {
    private String userName;
    private String password;
    private String uuid;
    private String token;

    public OkUser() {}

    public void setUsername(String username) {
        this.userName = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getUuid() {
        return uuid;
    }

    public String getToken() {
        return token;
    }


    private String getRegisterUrl() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", "App.User.Register");
        map.put("app_key", OkayApi.get().getAppKey());
        map.put("username", userName);
        map.put("password", MD5Utils.md5(password));
        String sign = SignUtils.getSign(map);
        return OkayApi.get().getHost() + "?s=App.User.Register"
                + "&username=" + userName
                + "&password=" + MD5Utils.md5(password)
                + "&app_key=" + OkayApi.get().getAppKey()
                + "&sign=" + sign;
    }

    private String getLoginUrl() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", "App.User.Login");
        map.put("app_key", OkayApi.get().getAppKey());
//        map.put("uuid", mUuid);
//        map.put("token", mToken);
        map.put("username", userName);
        map.put("password", MD5Utils.md5(password));
        String sign = SignUtils.getSign(map);
        return OkayApi.get().getHost() + "?s=App.User.Login"
                + "&username=" + userName
                + "&password=" + MD5Utils.md5(password)
                + "&app_key=" + OkayApi.get().getAppKey()
                + "&sign=" + sign;
    }

    public void registerInBackground(@NonNull RegisterCallback callback) {
        OkHttpClient client = new OkHttpClient();
        final Request req = new Request.Builder()
                            .url(getRegisterUrl())
                            .get()
                            .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkException oe = new OkException(e.getMessage());
                callback.done(oe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JsonObject jsonData = getResponseJson(response);
                    if (jsonData != null) {
                        String errCode = jsonData.get("err_code").getAsString();
                        if (errCode.equals("0")) {
                            callback.done(null);
                        } else {
                            String msg = jsonData.get("err_msg").getAsString();
                            OkException oe = new OkException(msg);
                            callback.done(oe);
                        }
                    } else {
                        OkException oe = new OkException("连接失败");
                        callback.done(oe);
                    }
                } catch (OkException e) {
                    callback.done(e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loginInBackground(LoginCallback callback) {
        OkHttpClient client = new OkHttpClient();
        final Request req = new Request.Builder()
                .url(getLoginUrl())
                .get()
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkException oe = new OkException(e.getMessage());
                callback.done(null, oe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JsonObject jsonData = getResponseJson(response);
                    if (jsonData != null) {
                        String errCode = jsonData.get("err_code").getAsString();
                        if (errCode.equals("0")) {
                            String uuid = jsonData.get("uuid").getAsString();
                            String token = jsonData.get("token").getAsString();
                            setUuid(uuid);
                            setToken(token);
                            OkayApi.get().setUser(OkUser.this);
                            callback.done(OkUser.this, null);
                        } else {
                            String msg = jsonData.get("err_msg").getAsString();
                            Logs.e(msg);
                            OkException oe = new OkException("用户名或密码错误");
                            callback.done(null, oe);
                        }
                    } else {
                        OkException oe = new OkException("连接失败");
                        callback.done(null, oe);
                    }
                } catch (OkException e) {
                    callback.done(null, e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
