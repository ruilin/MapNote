package com.muyu.minimalism.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;

import com.muyu.minimalism.R;

public class DialogUtils {

    public static AlertDialog.Builder getBaseDialogBuilder(Context context) {
        return new AlertDialog.Builder(context,  android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    public static void show(Context context, String title, String content, DialogCallback callback) {
        AlertDialog dialog = getBaseDialogBuilder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPositiveClick(dialog);
                    }
                })
                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onNegativeClick(dialog);
                    }
                })
                .create();
        dialog.show();
    }

    public static void show(Context context, @DrawableRes int iconRes, String title, String content, DialogCallback callback) {
        AlertDialog dialog = getBaseDialogBuilder(context)
                .setTitle(title)
                .setMessage(content)
                .setIcon(iconRes)
                .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPositiveClick(dialog);
                    }
                })
                .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onNegativeClick(dialog);
                    }
                })
                .create();
        dialog.show();
    }

    public static abstract class DialogCallback {
        public abstract void onPositiveClick(DialogInterface dialog);
        public void onNegativeClick(DialogInterface dialog) { dialog.dismiss(); }
    }
}
