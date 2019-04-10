package com.muyu.minimalism.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.view.Msg;

public class ShareUtils {

    public static void shareToWeChat(BaseActivity activity, String content){
        try {
            Intent wechatIntent = new Intent(Intent.ACTION_SEND);
            wechatIntent.setPackage("com.tencent.mm");
            wechatIntent.setType("text/plain");
            wechatIntent.putExtra(Intent.EXTRA_TEXT, content);
            activity.startActivity(wechatIntent);
        } catch (ActivityNotFoundException e) {
            Msg.show("未安装微信");
        } catch (Exception e) {
            Msg.show("分享异常");
        }
    }
}
