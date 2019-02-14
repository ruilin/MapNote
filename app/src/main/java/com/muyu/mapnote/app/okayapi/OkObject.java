package com.muyu.mapnote.app.okayapi;

import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyu.mapnote.app.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.MD5Utils;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Response;

public class OkObject {

    private String getMetaUrl() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", "App.Main_Meta.Create");
        map.put("app_key", OkayApi.get().getAppKey());
        map.put("username", OkayApi.get().getCurrentUser().getUserName());
        map.put("password", MD5Utils.md5(OkayApi.get().getCurrentUser().getPassword()));
        String sign = SignUtils.getSign(map);
        return OkayApi.get().getHost()
                + "/?s=App.CDN.UploadImg"
                + "&app_key=" + OkayApi.get().getAppKey()
                + "&sign=" + sign;
    }

    @Nullable
    protected JsonObject getResponseData(Response response) throws Exception {
        if (response.code() == 200) {
            String jsonStr = response.body().string();
            JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();

            String resCode = jsonObj.get("ret").getAsString();
            if (resCode.equals("200")) {
                return jsonObj.get("data").getAsJsonObject();

            } else if (resCode.startsWith("4")) {
                String msg = jsonObj.get("msg").getAsString();
                OkException oe = new OkException(msg);
                throw oe;

            } else if (resCode.startsWith("5")) {
                String msg = jsonObj.get("msg").getAsString();
                OkException oe = new OkException(msg);
                throw oe;
            }
        }
        return null;
    }
}
