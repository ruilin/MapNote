package com.muyu.mapnote.footmark;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.FileUtils;

import java.io.File;

import com.muyu.minimalism.utils.Share2;
import com.muyu.minimalism.utils.ShareContentType;

public class ShareDialog {

    public static void showDialog(final Activity context, Bitmap bmp) {
        //1.创建一个Dialog对象，如果是AlertDialog对象的话，弹出的自定义布局四周会有一些阴影，效果不好
        Dialog mDialog = new Dialog(context);

        //去除标题栏
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //2.填充布局
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.view_dialog_share, null);

        //将自定义布局设置进去
        mDialog.setContentView(dialogView);

        //3.设置指定的宽高,如果不设置的话，弹出的对话框可能不会显示全整个布局，当然在布局中写死宽高也可以
        WindowManager.LayoutParams lp     = new WindowManager.LayoutParams();
        Window window = mDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //注意要在Dialog show之后，再将宽高属性设置进去，才有效果
        mDialog.show();
        window.setAttributes(lp);

        //设置点击其它地方不让消失弹窗
        mDialog.setCancelable(false);

        EditText editText = dialogView.findViewById(R.id.dialog_share_ed);

        ((ImageView)dialogView.findViewById(R.id.dialog_share_image)).setImageBitmap(bmp);
        dialogView.findViewById(R.id.dialog_share_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.dialog_share_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Uri uri = FileUtils.getImageUri(context, bmp);
                new Share2.Builder(context)
                        // 指定分享的文件类型
                        .setContentType(ShareContentType.IMAGE)
                        // 设置要分享的文件 Uri
                        .setShareFileUri(uri)
                        // 设置分享选择器的标题
                        .setTitle("分享我的足迹")
                        .setTextContent(editText.getText().toString())
                        .build()
                        // 发起分享
                        .shareBySystem();
                mDialog.dismiss();
            }
        });
    }
}
