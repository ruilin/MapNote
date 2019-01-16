package com.muyu.mapnote.map.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.util.Msg;
import com.muyu.minimalism.view.imagebox.ZzImageBox;

public class PublishActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        final ZzImageBox imageBox = (ZzImageBox) findViewById(R.id.zz_image_box);
        imageBox.setOnImageClickListener(new ZzImageBox.OnImageClickListener() {

            @Override
            public void onImageClick(int position, String url, String realPath, int realType, ImageView iv) {
                Log.d("ZzImageBox", "image clicked:" + position + "," + realPath);
            }

            @Override
            public void onDeleteClick(int position, String url, String realPath, int realType) {
                imageBox.removeImage(position);
                Log.d("ZzImageBox", "delete clicked:" + position + "," + realPath);
                Log.d("ZzImageBox", "all images\n"+imageBox.getAllImages().toString());
            }

            @Override
            public void onAddClick() {
                imageBox.addImage(null);
                Log.d("ZzImageBox", "add clicked");
                Log.d("ZzImageBox", "all images\n"+imageBox.getAllImages().toString());
            }
        });
    }
}
