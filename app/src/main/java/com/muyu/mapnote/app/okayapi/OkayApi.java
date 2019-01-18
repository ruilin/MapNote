package com.muyu.mapnote.app.okayapi;

public class OkayApi {

    private static String sHost = "";
    private static String sAppKey = "";
    private static String sAppSecret = "";

    public static void create(String host, String appKey, String appSecret) {
        sHost = host;
        sAppKey = appKey;
        sAppSecret = appSecret;
    }

    protected static String getHost() { return sHost; }
    protected static String getAppKey() { return sAppKey; }
    protected static String getAppSecret() { return sAppSecret; }

}
