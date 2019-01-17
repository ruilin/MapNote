package com.muyu.mapnote.app.okayapi;

import android.support.annotation.NonNull;

import com.muyu.mapnote.app.okayapi.callback.SignUpCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkUser {

    private String mUserName;
    private String mPassword;

    public OkUser() {}

    public void setUsername(String username) {
        mUserName = username;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public void signUpInBackGround(@NonNull SignUpCallback callback) {
        OkHttpClient client = new OkHttpClient();
        final Request req = new Request.Builder()
                            .url("")
                            .get()
                            .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.done(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.done(null);
            }
        });
    }
}
