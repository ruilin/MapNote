package com.muyu.mapnote.app;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.muyu.mapnote.R;

public class ImageLoader {

    public static void loadHead(Activity activity, String url, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.head_def);
        requestOptions.error(R.drawable.head_def);
        Glide.with(activity).load(url).apply(requestOptions).into(imageView);
    }
}
