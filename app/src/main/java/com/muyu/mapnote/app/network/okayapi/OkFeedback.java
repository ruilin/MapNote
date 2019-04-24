package com.muyu.mapnote.app.network.okayapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.muyu.mapnote.app.network.okayapi.callback.CommonCallback;
import com.muyu.mapnote.app.network.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.StringUtils;
import com.muyu.minimalism.utils.SysUtils;

import java.util.SortedMap;
import java.util.TreeMap;

public class OkFeedback extends OkObject {
    String content;
    String contact;

    public OkFeedback(String content, String contact) {
        this.content = content;
        this.contact = contact;
    }

    public void postInBackground(CommonCallback callback) {
        if (!OkayApi.get().isLogined()) {
            SysUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onFail(new OkException("请先登录"));
                }
            });
            return;
        }
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
                sb.append("&message_key=" + "feedback");
                String json = "";
                if (!StringUtils.isEmpty(contact)) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("message_contact", contact);
                    json = new Gson().toJson(obj);
                    sb.append("&more_data=" + json);
                }

                SortedMap<String, String> map = new TreeMap<>();
                map.put("s", apiKey);
                map.put("app_key", OkayApi.get().getAppKey());
                map.put("uuid", OkayApi.get().getCurrentUser().getUuid());
                map.put("token", OkayApi.get().getCurrentUser().getToken());
                map.put("message_content", content);
                map.put("message_nickname", OkayApi.get().getCurrentUser().getNickname());
                map.put("message_key", "feedback");
                if (!StringUtils.isEmpty(contact)) {
                    map.put("more_data", json);
                }

                String sign = SignUtils.getSign(map);
                sb.append("&sign=" + sign);
                return sb.toString();
            }
        });
    }
}
