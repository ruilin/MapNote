package com.muyu.mapnote.app.network.okayapi;

import com.muyu.mapnote.app.network.okayapi.callback.CommonCallback;
import com.muyu.mapnote.app.network.okayapi.utils.SignUtils;

import java.util.SortedMap;
import java.util.TreeMap;

public class OkCaptcha extends OkObject {

    public void requestCaptcha() {
        postCommonRequest(new CommonCallback() {
            @Override
            public void onSuccess(String json) {

            }

            @Override
            public void onFail(OkException e) {

            }
        }, new UrlCallback() {
            @Override
            public String getUrl() {
                SortedMap<String, String> map = new TreeMap<>();
                String api = "App.Captcha.Create";
                map.put("s", api);
                map.put("app_key", OkayApi.get().getAppKey());
                String sign = SignUtils.getSign(map);
                return OkayApi.get().getHost() + "?s=" + api
                        + "&app_key=" + OkayApi.get().getAppKey()
                        + "&sign=" + sign;
            }
        });
    }
}
