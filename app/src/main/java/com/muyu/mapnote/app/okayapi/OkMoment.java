package com.muyu.mapnote.app.okayapi;

import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.muyu.mapnote.app.Network;
import com.muyu.mapnote.app.okayapi.callback.CommonCallback;
import com.muyu.mapnote.app.okayapi.callback.MomentListCallback;
import com.muyu.mapnote.app.okayapi.callback.MomentPostCallback;
import com.muyu.mapnote.app.okayapi.callback.UploadCallback;
import com.muyu.mapnote.app.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.Logs;
import com.muyu.minimalism.utils.MathUtils;
import com.muyu.minimalism.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkMoment extends OkObject {
    ArrayList<OkImage> okImages = new ArrayList<>(9);
    ArrayList<String> okImagesUrl = new ArrayList<>(9);
    String content = "";
    double lat = 0;
    double lng = 0;
    String place = "";
    Call network;

    public static OkMoment newInstance() {
        OkMoment moment = new OkMoment();
        return moment;
    }

    public OkMoment setContent(String content) {
        this.content = content;
        return this;
    }

    public OkMoment setLocation(Location location, String place) {
        this.lat = MathUtils.round(location.getLatitude(), 8);
        this.lng = MathUtils.round(location.getLongitude(), 8);
        this.place = place;
        return this;
    }

    public OkMoment addImage(@NotNull OkImage image) {
        this.okImages.add(image);
        return this;
    }

    private String getPostUrl() {
//        JsonObject object = new JsonObject();
//        object.addProperty("headimg", OkayApi.get().getCurrentUser().getHeadimg());
//        String json = new Gson().toJson(object);
        String json = "";

        String apiKey = "App.Market_Minimoments.PostMoment";
        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", apiKey);
        map.put("app_key", OkayApi.get().getAppKey());
        map.put("uuid", OkayApi.get().getCurrentUser().getUuid());
        map.put("token", OkayApi.get().getCurrentUser().getToken());
        map.put("moment_nickname", OkayApi.get().getCurrentUser().getNickname());
        map.put("moment_access", "0");
        map.put("moment_lat", String.valueOf(lat));
        map.put("moment_lng", String.valueOf(lng));
        map.put("moment_place", place);
        map.put("moment_url", OkayApi.get().getCurrentUser().getHeadimg());
        map.put("ext_data", json);

        StringBuffer sb = new StringBuffer();
        sb.append(OkayApi.get().getHost());
        sb.append("/?s=" + apiKey);
        sb.append("&app_key=" + OkayApi.get().getAppKey());
        sb.append("&uuid=" + OkayApi.get().getCurrentUser().getUuid());
        sb.append("&token=" + OkayApi.get().getCurrentUser().getToken());
        sb.append("&moment_nickname=" + OkayApi.get().getCurrentUser().getNickname());
        sb.append("&moment_access=" + "0");
        sb.append("&moment_lat=" + String.valueOf(lat));
        sb.append("&moment_lng=" + String.valueOf(lng));
        sb.append("&moment_place=" + place);
        sb.append("&moment_url=" + OkayApi.get().getCurrentUser().getHeadimg());
        sb.append("&ext_data=" + json);
        if (!StringUtils.isEmpty(content)) {
            sb.append("&moment_content=" + content);
            map.put("moment_content", content);
        }
        for (int i = 0; i < okImagesUrl.size(); i++) {
            sb.append("&moment_picture" + (i+1) + "=" + okImagesUrl.get(i));
            map.put("moment_picture" + (i+1), okImagesUrl.get(i));
        }
        String sign = SignUtils.getSign(map);
        sb.append("&sign=" + sign);
        return sb.toString();
    }

    private static String getAllMomentUrl() {
        String apiKey = "App.Market_Minimoments.ShowMoment";

        StringBuffer sb = new StringBuffer();
        sb.append(OkayApi.get().getHost());
        sb.append("/?s=" + apiKey);
        sb.append("&app_key=" + OkayApi.get().getAppKey());
        sb.append("&uuid=" + OkayApi.get().getCurrentUser().getUuid());
        sb.append("&token=" + OkayApi.get().getCurrentUser().getToken());
        sb.append("&perpage=100");

        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", apiKey);
        map.put("app_key", OkayApi.get().getAppKey());
        map.put("uuid", OkayApi.get().getCurrentUser().getUuid());
        map.put("token", OkayApi.get().getCurrentUser().getToken());
        //map.put("lastid", "2");
        map.put("perpage", "100");
        String sign = SignUtils.getSign(map);

        sb.append("&sign=" + sign);
        return sb.toString();
    }

    private static String getMyMomentUrl() {
        String apiKey = "App.Market_Minimoments.GetMyMoment";

        StringBuffer sb = new StringBuffer();
        sb.append(OkayApi.get().getHost());
        sb.append("/?s=" + apiKey);
        sb.append("&app_key=" + OkayApi.get().getAppKey());
        sb.append("&uuid=" + OkayApi.get().getCurrentUser().getUuid());
        sb.append("&token=" + OkayApi.get().getCurrentUser().getToken());
//        sb.append("&moment_lat=" + lat);
//        sb.append("&moment_lng=" + lng);
//        sb.append("&moment_place=" + place);

        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", apiKey);
        map.put("app_key", OkayApi.get().getAppKey());
        map.put("uuid", OkayApi.get().getCurrentUser().getUuid());
        map.put("token", OkayApi.get().getCurrentUser().getToken());
//        map.put("moment_lat", String.valueOf(lat));
//        map.put("moment_lng", String.valueOf(lng));
//        map.put("moment_place", String.valueOf(place));
        String sign = SignUtils.getSign(map);

        sb.append("&sign=" + sign);
        return sb.toString();
    }

    private static String getGiveLikeUrl(String id) {
        String apiKey = "App.Market_Minimoments.GiveLike";

        StringBuffer sb = new StringBuffer();
        sb.append(OkayApi.get().getHost());
        sb.append("/?s=" + apiKey);
        sb.append("&app_key=" + OkayApi.get().getAppKey());
        sb.append("&uuid=" + OkayApi.get().getCurrentUser().getUuid());
        sb.append("&token=" + OkayApi.get().getCurrentUser().getToken());
        sb.append("&id=" + id);

        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", apiKey);
        map.put("app_key", OkayApi.get().getAppKey());
        map.put("uuid", OkayApi.get().getCurrentUser().getUuid());
        map.put("token", OkayApi.get().getCurrentUser().getToken());
        map.put("id", id);

        String sign = SignUtils.getSign(map);

        sb.append("&sign=" + sign);
        return sb.toString();
    }

    static int uploadCount = 0;
    public void postInBackground(MomentPostCallback callback) {
        uploadCount = 0;
        okImagesUrl.clear();
        for (int i = 0; i < okImages.size(); i++) {
            okImagesUrl.add("");
        }
        if (okImages.size() > 0) {
            for (int i = 0; i < okImages.size(); i++) {
                final int ind = i;
                okImages.get(i).upload(new UploadCallback() {
                    @Override
                    public void onSuccess(String url) {
                        okImagesUrl.set(ind, url);
                        uploadCount++;
                        if (uploadCount == okImages.size()) {
                            postMoment(callback);
                        }
                    }

                    @Override
                    public void onFail(OkException e) {
                        callback.onPostFail(e);
                    }
                });
            }
        } else {
            postMoment(callback);
        }
    }

    private void postMoment(MomentPostCallback callback) {
        OkHttpClient client = Network.getClient();
        final Request req = new Request.Builder()
                .url(getPostUrl())
                .get()
                .build();
        network = client.newCall(req);
        network.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkException oe = new OkException(e.getMessage());
                callback.onPostFail(oe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JsonObject jsonData = getResponseJson(response);
                    if (jsonData != null) {
                        String errCode = jsonData.get("err_code").getAsString();
                        if (errCode.equals("0")) {
                            callback.onPostSuccess();
                            /*
                            int id = jsonData.get("id").getAsInt();
                            JsonObject obj = new JsonObject();
                            obj.addProperty("moment_lat", lat);
                            obj.addProperty("moment_lng", lng);
                            obj.addProperty("moment_place", place);
                            setCustomData("okayapi_moment", id, obj.toString(), new CommonCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    callback.onPostSuccess();
                                }

                                @Override
                                public void onFail(OkException e) {
                                    callback.onPostFail(e);
                                }
                            });
                            */
                        } else {
                            String msg = jsonData.get("err_msg").getAsString();
                            Logs.e(msg);
                            OkException oe = new OkException(msg);
                            callback.onPostFail(oe);
                        }
                    } else {
                        OkException oe = new OkException("连接失败");
                        callback.onPostFail(oe);
                    }
                } catch (OkException e) {
                    callback.onPostFail(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onPostFail(new OkException(e.getMessage()));
                }
            }
        });
    }

    static int itemCount = 0;
    private static void getMoments(MomentListCallback callback, String url) {
        OkHttpClient client = Network.getClient();
        final Request req = new Request.Builder()
                .url(url)
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
                            ArrayList<OkMomentItem> list = new ArrayList<>();
                            JsonArray array = jsonData.get("items").getAsJsonArray();
                            Gson gson = new Gson();
                            for(JsonElement obj : array){
                                OkMomentItem item = gson.fromJson( obj , OkMomentItem.class);
                                list.add(item);
                            }
                            callback.onSuccess(list);
                            /*
                            itemCount = 0;
                            for (OkMomentItem item : list) {
                                getCustomData("okayapi_moment", item.id, new CommonCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        JsonObject object = gson.fromJson(result, JsonObject.class);
                                        item.moment_lat = object.get("moment_lat").getAsDouble();
                                        item.moment_lng = object.get("moment_lng").getAsDouble();
                                        item.moment_place = object.get("moment_place").getAsString();
                                        item.moment_nickname = object.get("moment_nickname").getAsString();
                                        if (Double.compare(item.moment_lat, 0) == 0 || Double.compare(item.moment_lng, 0) == 0) {
                                            item.isValid = false;
                                        }
                                        itemCount++;
                                        if (itemCount == list.size()) {
                                            Iterator<OkMomentItem> it = list.iterator();
                                            while (it.hasNext()) {
                                                OkMomentItem item = it.next();
                                                if (!item.isValid) {
                                                    it.remove();
                                                }
                                            }
                                            callback.onSuccess(list);

                                        }
                                    }

                                    @Override
                                    public void onFail(OkException e) {
                                        item.isValid = false;
                                        itemCount++;
                                    }
                                });
                            }
                            */
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

    public static void getAllMoment(MomentListCallback callback) {
        getMoments(callback, getAllMomentUrl());
    }

    public static void getMyMoment(MomentListCallback callback) {
        getMoments(callback, getMyMomentUrl());
    }

    public static void postLike(String id, CommonCallback callback) {
        OkHttpClient client = Network.getClient();
        final Request req = new Request.Builder()
                .url(getGiveLikeUrl(id))
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
                            callback.onSuccess(id);
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

    public void cancel() {
        if (network != null) {
            network.cancel();
        }
    }
}
