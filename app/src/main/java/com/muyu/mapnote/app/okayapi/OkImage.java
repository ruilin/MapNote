package com.muyu.mapnote.app.okayapi;

import com.muyu.mapnote.app.okayapi.utils.SignUtils;
import com.muyu.minimalism.utils.Logs;
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

public class OkImage {
    private String path;

    public OkImage(@NotNull String filePath) {
        path = filePath;
    }

    private String getUploadUrl() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("s", "App.CDN.UploadImg");
        map.put("app_key", OkayApi.get().getAppKey());
        map.put("username", OkayApi.get().getCurrentUser().getUserName());
        map.put("password", MD5Utils.md5(OkayApi.get().getCurrentUser().getPassword()));
        String sign = SignUtils.getSign(map);
        return OkayApi.get().getHost()
                + "/?s=App.CDN.UploadImg&app_key=" + OkayApi.get().getAppKey()
                + "&sign=" + sign;
    }


    public boolean upload() {
        if (!OkayApi.get().isLogined()) {
            return false;
        }
        OkHttpClient mOkHttpClent = new OkHttpClient();
        File file = new File(path);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
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
                Logs.e(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Logs.e(response.body().string());
            }
        });
        return true;
    }
}
