package com.muyu.mapnote.app.okayapi;

import com.google.gson.JsonObject;
import com.muyu.mapnote.app.Config;
import com.muyu.mapnote.app.Network;
import com.muyu.mapnote.app.okayapi.callback.UploadCallback;
import com.muyu.mapnote.app.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.bitmap.BitmapUtils;
import com.muyu.minimalism.utils.MD5Utils;

import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkImage extends OkObject {
    private String path;

    public OkImage(@NotNull String filePath) {
        path = BitmapUtils.compressImage(filePath, Config.genPhotoPathRandom(), 30);
    }

    public OkImage(@NotNull String filePath, int quality) {
        path = BitmapUtils.compressImage(filePath, Config.genPhotoPathRandom(), quality);
    }

    private String getUploadUrl() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", "App.CDN.UploadImg");
        map.put("app_key", OkayApi.get().getAppKey());
        String sign = SignUtils.getSign(map);
        return OkayApi.get().getHost()
                + "/?s=App.CDN.UploadImg&app_key=" + OkayApi.get().getAppKey()
                + "&sign=" + sign;
    }


    public boolean upload(@NotNull UploadCallback callback) {
        OkHttpClient mOkHttpClent = Network.getClient();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        File file = new File(path);
        builder.addFormDataPart("file", file.getName(),
                RequestBody.create(MediaType.parse("image/png"), file));

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(getUploadUrl())
                .post(requestBody)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e != null)
                    callback.onFail(new OkException(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JsonObject jsonData = getResponseJson(response);
                    if (jsonData != null) {
                        String errCode = jsonData.get("err_code").getAsString();
                        if (errCode.equals("0")) {
                            String url = jsonData.get("url").getAsString();
                            callback.onSuccess(url);
                        } else {
                            String msg = jsonData.get("err_msg").getAsString();
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
        return true;
    }
}
