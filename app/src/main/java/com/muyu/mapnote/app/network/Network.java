package com.muyu.mapnote.app.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Network {
    private static OkHttpClient client;

    public static void init() {
        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)       //设置连接超时
                .readTimeout(60, TimeUnit.SECONDS)          //设置读超时
                .writeTimeout(60, TimeUnit.SECONDS)          //设置写超时
                .retryOnConnectionFailure(true)             //是否自动重连
                .build();                                   //构建OkHttpClient对象
    }

    public static OkHttpClient getClient() {
        return client;
    }
}
