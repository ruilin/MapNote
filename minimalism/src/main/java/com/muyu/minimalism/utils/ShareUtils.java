package com.muyu.minimalism.utils;

import android.content.Intent;
import android.net.Uri;

import com.muyu.minimalism.framework.app.BaseActivity;

public class ShareUtils {

    public static void shareToWeChat(BaseActivity activity, String content){
        Intent wechatIntent = new Intent(Intent.ACTION_SEND);
        wechatIntent.setPackage("com.tencent.mm");
        wechatIntent.setType("text/plain");
        wechatIntent.putExtra(Intent.EXTRA_TEXT, content);
        activity.startActivity(wechatIntent);
    }
}
