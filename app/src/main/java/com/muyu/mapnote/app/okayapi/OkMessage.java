package com.muyu.mapnote.app.okayapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.muyu.mapnote.app.okayapi.been.OkMessageItem;
import com.muyu.mapnote.app.okayapi.been.OkMomentItem;
import com.muyu.mapnote.app.okayapi.callback.CommonCallback;
import com.muyu.mapnote.app.okayapi.callback.MessageListCallback;
import com.muyu.mapnote.app.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.Logs;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class OkMessage {
    private static final String MODEL_NAME = "okayapi_user_message";
    public static final int TYPE_SYSTEM = 0;
    public static final int TYPE_MOMENT_LIKE = 1;
    public static final int TYPE_MOMENT_COMMENT = 2;

    public static void postMessage(String targetUuid, String message, String icon, int type, String source) {
        new OkObject().postCommonRequest(new CommonCallback() {
            @Override
            public void onSuccess(String result) {
                Logs.e(result);
            }

            @Override
            public void onFail(OkException e) {
                Logs.e(e.getMessage());
            }
        }, new OkObject.UrlCallback() {
            @Override
            public String getUrl() {
                String apiKey = "App.Table.Create";

                JsonObject obj = new JsonObject();
                obj.addProperty("user_id", targetUuid);
                obj.addProperty("message", message);
                obj.addProperty("icon", icon);
                obj.addProperty("message_type", type);
                obj.addProperty("message_source", source);
                String json = new Gson().toJson(obj);

                StringBuffer sb = new StringBuffer();
                sb.append(OkayApi.get().getHost());
                sb.append("/?s=" + apiKey);
                sb.append("&app_key=" + OkayApi.get().getAppKey());
                sb.append("&model_name=" + MODEL_NAME);
                sb.append("&perpage=100");
                sb.append("&data=" + json);

                SortedMap<String, String> map = new TreeMap<>();
                map.put("s", apiKey);
                map.put("app_key", OkayApi.get().getAppKey());
                map.put("model_name", MODEL_NAME);
                map.put("perpage", ""+100);
                map.put("data", json);

                String sign = SignUtils.getSign(map);
                sb.append("&sign=" + sign);
                return sb.toString();
            }
        });
    }

    public static void requestMessages(MessageListCallback callback) {
        if (!OkayApi.get().isLogined()) {
            return;
        }
        new OkObject().postCommonRequest(new CommonCallback() {
            @Override
            public void onSuccess(String result) {
                Logs.e(result);
                Gson gson = new Gson();
                JsonObject object = gson.fromJson(result, JsonObject.class);
                JsonArray array = object.get("list").getAsJsonArray();
                ArrayList<OkMessageItem> list = new ArrayList<>();
                for(JsonElement obj : array) {
                    OkMessageItem item = gson.fromJson(obj, OkMessageItem.class);
                    list.add(item);
                }
                callback.onSuccess(list);
            }

            @Override
            public void onFail(OkException e) {
                Logs.e(e.getMessage());
                callback.onFail(e);
            }
        }, new OkObject.UrlCallback() {
            @Override
            public String getUrl() {
                String apiKey = "App.Table.FreeQuery";

                StringBuffer sb = new StringBuffer();
                sb.append(OkayApi.get().getHost());
                sb.append("/?s=" + apiKey);
                sb.append("&app_key=" + OkayApi.get().getAppKey());
                sb.append("&model_name=" + MODEL_NAME);
                sb.append("&select=user_id,message_type,message_source,message,icon,add_time");
                sb.append("&where=[[\"user_id\",\"=\",\"" + OkayApi.get().getCurrentUser().getUuid() + "\"]]");
                sb.append("&order=[\"add_time DESC\"]");

                SortedMap<String, String> map = new TreeMap<>();
                map.put("s", apiKey);
                map.put("app_key", OkayApi.get().getAppKey());
                map.put("model_name", MODEL_NAME);
                map.put("select", "user_id,message_type,message_source,message,icon,add_time");
                map.put("where", "[[\"user_id\",\"=\",\"" + OkayApi.get().getCurrentUser().getUuid() + "\"]]");
                map.put("order", "[\"add_time DESC\"]");

                String sign = SignUtils.getSign(map);
                sb.append("&sign=" + sign);
                return sb.toString();
            }
        });
    }

    public static void requestMessageCount(CommonCallback callback) {
        if (!OkayApi.get().isLogined()) {
            return;
        }
        new OkObject().postCommonRequest(new CommonCallback() {
            @Override
            public void onSuccess(String result) {
                Logs.e(result);
                Gson gson = new Gson();
                JsonObject object = gson.fromJson(result, JsonObject.class);
                String count = object.get("total").getAsString();
                callback.onSuccess(count);
            }

            @Override
            public void onFail(OkException e) {
                Logs.e(e.getMessage());
                callback.onFail(e);
            }
        }, new OkObject.UrlCallback() {
            @Override
            public String getUrl() {
                String apiKey = "App.Table.Count";

                StringBuffer sb = new StringBuffer();
                sb.append(OkayApi.get().getHost());
                sb.append("/?s=" + apiKey);
                sb.append("&app_key=" + OkayApi.get().getAppKey());
                sb.append("&model_name=" + MODEL_NAME);

                SortedMap<String, String> map = new TreeMap<>();
                map.put("s", apiKey);
                map.put("app_key", OkayApi.get().getAppKey());
                map.put("model_name", MODEL_NAME);

                String sign = SignUtils.getSign(map);
                sb.append("&sign=" + sign);
                return sb.toString();
            }
        });
    }

}
