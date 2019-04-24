package com.muyu.mapnote.app.network.okayapi;

import android.support.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyu.mapnote.app.network.Network;
import com.muyu.mapnote.app.network.okayapi.callback.CommonCallback;
import com.muyu.mapnote.app.network.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.Logs;
import com.muyu.minimalism.utils.MD5Utils;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    private static String getCustomData(String modelName, int id) {
        String apiKey = "App.Table.Get";

        StringBuffer sb = new StringBuffer();
        sb.append(OkayApi.get().getHost());
        sb.append("/?s=" + apiKey);
        sb.append("&app_key=" + OkayApi.get().getAppKey());
        //sb.append("&uuid=" + OkayApi.get().getCurrentUser().getUuid());
        //sb.append("&token=" + OkayApi.get().getCurrentUser().getToken());
        sb.append("&model_name=" + modelName);
        sb.append("&id=" + id);

        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", apiKey);
        map.put("app_key", OkayApi.get().getAppKey());
        //map.put("uuid", OkayApi.get().getCurrentUser().getUuid());
        //map.put("token", OkayApi.get().getCurrentUser().getToken());
        map.put("model_name", modelName);
        map.put("id", String.valueOf(id));

        String sign = SignUtils.getSign(map);
        sb.append("&sign=" + sign);
        return sb.toString();
    }

    private static String getCustomUpdateUrl(String modelName, int id, String jsonStr) {
        String apiKey = "App.Table.Update";

        StringBuffer sb = new StringBuffer();
        sb.append(OkayApi.get().getHost());
        sb.append("/?s=" + apiKey);
        sb.append("&app_key=" + OkayApi.get().getAppKey());
        //sb.append("&uuid=" + OkayApi.get().getCurrentUser().getUuid());
        //sb.append("&token=" + OkayApi.get().getCurrentUser().getToken());
        sb.append("&model_name=" + modelName);
        sb.append("&id=" + id);
        sb.append("&data=" + jsonStr);

        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", apiKey);
        map.put("app_key", OkayApi.get().getAppKey());
        //map.put("uuid", OkayApi.get().getCurrentUser().getUuid());
        //map.put("token", OkayApi.get().getCurrentUser().getToken());
        map.put("model_name", modelName);
        map.put("id", String.valueOf(id));
        map.put("data", jsonStr);

        String sign = SignUtils.getSign(map);
        sb.append("&sign=" + sign);
        return sb.toString();
    }

    @Nullable
    protected static JsonObject getResponseJson(Response response) throws Exception {
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

    public static void getCustomData(String modelName, int id, CommonCallback callback) {
        OkHttpClient client = Network.getClient();
        final Request req = new Request.Builder()
                .url(getCustomData(modelName, id))
                .get()
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkException oe = new OkException(e.getMessage());
                callback.onFail(oe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JsonObject jsonData = getResponseJson(response);
                    if (jsonData != null) {
                        String errCode = jsonData.get("err_code").getAsString();
                        if (errCode.equals("0")) {
                            String data = jsonData.get("data").toString();
                            callback.onSuccess(data);
                        } else {
                            String msg = jsonData.get("err_msg").getAsString();
                            Logs.e(msg);
                            OkException oe = new OkException(msg);
                            callback.onFail(oe);
                        }
                    } else {
                        OkException oe = new OkException("连接失败");
                        callback.onFail(oe);
                    }
                } catch (OkException e) {
                    callback.onFail(e);
                } catch (Exception e) {
                    callback.onFail(new OkException(e.getMessage()));
                    e.printStackTrace();
                }
            }
        });
    }

    public static void setCustomData(String modelName, int id, String jsonStr, CommonCallback callback) {
        OkHttpClient client = Network.getClient();
        final Request req = new Request.Builder()
                .url(getCustomUpdateUrl(modelName, id, jsonStr))
                .get()
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkException oe = new OkException(e.getMessage());
                callback.onFail(oe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JsonObject jsonData = getResponseJson(response);
                    if (jsonData != null) {
                        String errCode = jsonData.get("err_code").getAsString();
                        if (errCode.equals("0")) {
                            JsonElement ele = jsonData.get("data");
                            if (ele != null) {
                                callback.onSuccess(ele.getAsString());
                            } else {
                                callback.onSuccess(jsonData.toString());
                            }
                        } else {
                            String msg = jsonData.get("err_msg").getAsString();
                            Logs.e(msg);
                            OkException oe = new OkException(msg);
                            callback.onFail(oe);
                        }
                    } else {
                        OkException oe = new OkException("连接失败");
                        callback.onFail(oe);
                    }
                } catch (OkException e) {
                    callback.onFail(e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void postCommonRequest(CommonCallback callback, UrlCallback getUrl) {
        OkHttpClient client = Network.getClient();
        final Request req = new Request.Builder()
                .url(getUrl.getUrl())
                .get()
                .build();
        Call call = client.newCall(req);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkException oe = new OkException(e.getMessage());
                callback.onFail(oe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JsonObject jsonData = getResponseJson(response);
                    if (jsonData != null) {
                        String errCode = jsonData.get("err_code").getAsString();
                        if (errCode.equals("0")) {
                            callback.onSuccess(jsonData.toString());
                        } else {
                            String msg = jsonData.get("err_msg").getAsString();
                            Logs.e(msg);
                            OkException oe = new OkException(msg);
                            callback.onFail(oe);
                        }
                    } else {
                        OkException oe = new OkException("连接失败");
                        callback.onFail(oe);
                    }
                } catch (OkException e) {
                    callback.onFail(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFail(new OkException(e.getMessage()));
                }
            }
        });
    }

    interface UrlCallback {
        String getUrl();
    }
}
