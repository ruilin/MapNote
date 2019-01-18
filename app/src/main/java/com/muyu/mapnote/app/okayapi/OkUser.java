package com.muyu.mapnote.app.okayapi;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyu.mapnote.app.okayapi.callback.SignInCallback;
import com.muyu.mapnote.app.okayapi.callback.SignUpCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkUser {
    private static final String URL_SIGNUP = OkayApi.getHost() + "?s=App.User.Login";
    private static final String URL_SIGNIN = OkayApi.getHost() + "?s=App.User.Register";
    private String mUserName;
    private String mPassword;
    private String mUuid;
    private String mToken;

    public OkUser() {}

    public void setUsername(String username) {
        mUserName = username;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    private void setUuid(String uuid) {
        mUuid = uuid;
    }

    private void setToken(String token) {
        mToken = token;
    }

    protected String getCommonGetParam() {
        return "&app_key=" + OkayApi.getAppKey() + "&sign=" + "" + "&uuid=" + mUuid + "&token=" + mToken;
    }

    public void signUpInBackground(@NonNull SignUpCallback callback) {
        OkHttpClient client = new OkHttpClient();

        final Request req = new Request.Builder()
                            .url(URL_SIGNUP)
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
                    if (response.code() == 200) {
                        String jsonStr = response.body().string();
                        JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();

                        String resCode = jsonObj.get("ret").getAsString();
                        if (resCode.equals("200")) {
                            callback.done(null);

                        } else if (resCode.startsWith("4")) {
                            String msg = jsonObj.get("msg").getAsString();
                            OkException oe = new OkException(msg);
                            callback.done(oe);

                        } else if (resCode.startsWith("5")) {
                            String msg = jsonObj.get("msg").getAsString();
                            OkException oe = new OkException(msg);
                            callback.done(oe);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void signInInBackground(String username, String password, SignInCallback callback) {
        OkHttpClient client = new OkHttpClient();
        final Request req = new Request.Builder()
                .url(URL_SIGNIN)
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
                    if (response.code() == 200) {
                        String jsonStr = response.body().string();
                        JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();

                        String resCode = jsonObj.get("ret").getAsString();
                        if (resCode.equals("200")) {
                            String uuid = jsonObj.get("uuid").getAsString();
                            String token = jsonObj.get("token").getAsString();
                            OkUser user = new OkUser();
                            user.setUsername(username);
                            user.setPassword(password);
                            user.setUuid(uuid);
                            user.setToken(token);
                            callback.done(user, null);

                        } else if (resCode.startsWith("4")) {
                            String msg = jsonObj.get("msg").getAsString();
                            OkException oe = new OkException(msg);
                            callback.done(null, oe);

                        } else if (resCode.startsWith("5")) {
                            String msg = jsonObj.get("msg").getAsString();
                            OkException oe = new OkException(msg);
                            callback.done(null, oe);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
