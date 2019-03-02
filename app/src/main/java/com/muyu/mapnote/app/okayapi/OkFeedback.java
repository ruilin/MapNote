package com.muyu.mapnote.app.okayapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyu.mapnote.app.Network;
import com.muyu.mapnote.app.okayapi.callback.CommonCallback;
import com.muyu.mapnote.app.okayapi.callback.MomentPostCallback;
import com.muyu.mapnote.app.okayapi.callback.UploadCallback;
import com.muyu.mapnote.app.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.Logs;
import com.muyu.minimalism.utils.StringUtils;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkFeedback extends OkObject {
    String content;
    String contact;

    public OkFeedback(String content, String contact) {
        this.content = content;
        this.contact = contact;
    }

    public void postInBackground(CommonCallback callback) {
        postCommonRequest(callback, new UrlCallback() {
            @Override
            public String getUrl() {
                String apiKey = "App.Market_Message.Post";

                StringBuffer sb = new StringBuffer();
                sb.append(OkayApi.get().getHost());
                sb.append("/?s=" + apiKey);
                sb.append("&app_key=" + OkayApi.get().getAppKey());
                sb.append("&uuid=" + OkayApi.get().getCurrentUser().getUuid());
                sb.append("&token=" + OkayApi.get().getCurrentUser().getToken());
                sb.append("&message_content=" + content);
                sb.append("&message_nickname=" + OkayApi.get().getCurrentUser().getNickname());
                String json = "";
                if (!StringUtils.isEmpty(contact)) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("contact", contact);
                    json = new Gson().toJson(obj);
                    sb.append("&message_key=" + json);
                }

                SortedMap<String, String> map = new TreeMap<>();
                map.put("s", apiKey);
                map.put("app_key", OkayApi.get().getAppKey());
                map.put("uuid", OkayApi.get().getCurrentUser().getUuid());
                map.put("token", OkayApi.get().getCurrentUser().getToken());
                map.put("message_content", content);
                map.put("message_nickname", OkayApi.get().getCurrentUser().getNickname());
                if (!StringUtils.isEmpty(contact)) {
                    map.put("message_key", json);
                }

                String sign = SignUtils.getSign(map);
                sb.append("&sign=" + sign);
                return sb.toString();
            }
        });
    }
}
