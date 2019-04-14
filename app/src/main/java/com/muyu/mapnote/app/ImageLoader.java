package com.muyu.mapnote.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.muyu.mapnote.R;
import com.muyu.minimalism.utils.StringUtils;
import com.muyu.minimalism.utils.bitmap.BitmapUtils;
import com.muyu.minimalism.utils.bitmap.CanvasUtils;

public class ImageLoader {

    public static void loadHead(Activity activity, String url, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.head_def);
        requestOptions.error(R.drawable.head_def);
        Glide.with(activity).load(url + "?imageView2/2/w/100/h/100").apply(requestOptions).into(imageView);
    }

    public static void loadPoi(Activity activity, String url, SimpleTarget<Bitmap> callback) {
        if (!StringUtils.isEmpty(url)) {
            Glide.with(activity).asBitmap().load(url + "?imageView2/2/w/110/h/110").into(callback);
        }
    }

    public static void loadMoment(Activity activity, String url, ImageView imageView) {
        if (!StringUtils.isEmpty(url)) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.def_photo);
            requestOptions.error(R.drawable.def_photo);
            Glide.with(activity).load(url).apply(requestOptions).into(imageView);
        }
    }
}
