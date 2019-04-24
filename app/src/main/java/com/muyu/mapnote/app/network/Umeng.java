package com.muyu.mapnote.app.network;

import android.app.Activity;
import android.content.Context;

import com.muyu.mapnote.BuildConfig;
import com.muyu.mapnote.app.MapApplication;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.HashMap;

public class Umeng {

    public static void init(Context context) {
        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:【友盟+】 AppKey
         * 参数3:【友盟+】 Channel
         * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数5:Push推送业务的secret
         */
//        UMConfigure.init(context, "5c768e52b465f59b8900046c", "Common_Channel", UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, null);
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        /**
         * 子进程是否支持自定义事件统计。
         * 参数：boolean 默认不使用
         */
        UMConfigure. setProcessEvent(false);

        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    public static void onResume(Activity activity) {
//        MobclickAgent.onResume(activity);
    }

    public static void onPause(Activity activity) {
//        MobclickAgent.onPause(activity);
    }

    public static void record(String key, String value) {
        MobclickAgent.onEvent(MapApplication.getInstance(), key, value);
    }

    public static void login(String userId) {
        MobclickAgent.onProfileSignIn(userId);
    }
}
