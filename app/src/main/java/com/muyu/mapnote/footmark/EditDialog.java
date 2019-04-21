package com.muyu.mapnote.footmark;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.muyu.mapnote.R;
import com.muyu.minimalism.utils.FileUtils;
import com.muyu.minimalism.utils.Share2;
import com.muyu.minimalism.utils.ShareContentType;
import com.muyu.minimalism.utils.StringUtils;
import com.muyu.minimalism.view.Msg;

public class EditDialog {

    public static void showDialog(final Activity context, String oldContent, OnEditCallback callback) {
        //1.创建一个Dialog对象，如果是AlertDialog对象的话，弹出的自定义布局四周会有一些阴影，效果不好
        Dialog mDialog = new Dialog(context);

        //去除标题栏
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //2.填充布局
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.view_dialog_edit, null);

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

        EditText editText = dialogView.findViewById(R.id.edit_dialog_ed);
        editText.setText(oldContent);
        if (oldContent.length() > 0) {
            editText.setSelection(oldContent.length());
        }

        dialogView.findViewById(R.id.edit_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.edit_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newContent = editText.getText().toString();
                if (newContent.equals(oldContent)) {
                    Msg.show("内容未修改");
                } else if (StringUtils.isEmpty(newContent)) {
                    Msg.show("请输入新内容");
                } else {
                    callback.onEdit(newContent);
                    mDialog.dismiss();
                }
            }
        });
    }

    interface OnEditCallback {
        void onEdit(String text);
    }
}
