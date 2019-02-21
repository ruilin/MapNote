package com.muyu.mapnote.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.map.moment.MomentPoi;
import com.muyu.mapnote.map.map.poi.PoiManager;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.StringUtils;
import com.muyu.minimalism.view.BottomMenu;
import com.muyu.minimalism.view.MediaLoader;
import com.muyu.minimalism.view.imagebox.ZzImageBox;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import java.util.ArrayList;

public class DetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .build());

        String id = getIntent().getStringExtra("MomentId");
        MomentPoi poi = null;
        if (!StringUtils.isEmpty(id)) {
            poi = PoiManager.getMomentPoi(id);
        }
        if (poi != null) {
            TextView userTv = findViewById(R.id.detail_user);
            userTv.setText(poi.nickname);
            TextView contentTv = findViewById(R.id.detail_content);
            contentTv.setText(poi.content);
            TextView timeTv = findViewById(R.id.detail_time);
            timeTv.setText(poi.createtime);
            TextView placeTv = findViewById(R.id.detail_place_text);
            placeTv.setText(poi.place);
            TextView likeTv = findViewById(R.id.detail_like_count);
            likeTv.setText(String.valueOf(poi.like));

            ZzImageBox imageBox = findViewById(R.id.detail_image_box);
            if (poi.pictureUrlLiat.isEmpty()) {
                imageBox.setVisibility(View.GONE);
            } else {
                imageBox.setImageSizeOneLine(poi.pictureUrlLiat.size());
                for (String url : poi.pictureUrlLiat) {
                    imageBox.addImageOnline(url);
                }

            }
            imageBox.setOnImageClickListener(new ZzImageBox.OnImageClickListener() {

                @Override
                public void onImageClick(int position, String url, String realPath, int realType, ImageView iv) {
                    // 预览图片
                    Album.gallery(DetailActivity.this)
                            .checkedList((ArrayList<String>) imageBox.getAllImages()) // List of image to view: ArrayList<String>.
                            .currentPosition(position)
                            .checkable(false) // Whether there is a selection function.
                            .onResult(new Action<ArrayList<String>>() { // If checkable(false), action not required.
                                @Override
                                public void onAction(@NonNull ArrayList<String> result) {
                                    imageBox.removeAllImages();
                                    for (String path : result) {
                                        imageBox.addImage(path);
                                    }
                                }
                            })
                            .onCancel(new Action<String>() {
                                @Override
                                public void onAction(@NonNull String result) {
                                }
                            })
                            .start();
                }

                @Override
                public void onDeleteClick(int position, String url, String realPath, int realType) {
                }

                @Override
                public void onAddClick() {
                }
            });
        }


        CommonTitleBar titleBar = findViewById(R.id.detail_title);
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                }
            }
        });

        CheckBox checkBox = titleBar.getRightCustomView().findViewById(R.id.detail_like);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    public static void startDetailPage(BaseActivity activity, String id) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra("MomentId", id);
        activity.startActivity(intent);
    }
}
