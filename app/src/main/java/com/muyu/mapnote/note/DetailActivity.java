package com.muyu.mapnote.note;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.network.ImageLoader;
import com.muyu.mapnote.app.MapBaseActivity;
import com.muyu.mapnote.app.network.okayapi.OkException;
import com.muyu.mapnote.app.network.okayapi.OkMessage;
import com.muyu.mapnote.app.network.okayapi.OkMoment;
import com.muyu.mapnote.app.network.okayapi.been.OkMomentItem;
import com.muyu.mapnote.app.network.okayapi.OkayApi;
import com.muyu.mapnote.app.network.okayapi.callback.CommonCallback;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.mapnote.map.map.poi.PoiManager;
import com.muyu.mapnote.note.comment.CommentController;
import com.muyu.mapnote.user.activity.RegisterActivity;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.StringUtils;
import com.muyu.minimalism.view.MediaLoader;
import com.muyu.minimalism.view.Msg;
import com.muyu.minimalism.view.imagebox.ZzImageBox;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.util.ArrayList;

public class DetailActivity extends MapBaseActivity {
    private boolean checkable;
    private CommentController commentController;
    TextView likeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .build());

//        Rect outRect = new Rect();
//        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.detail_input).getLayoutParams();
//        params.height = outRect.bottom - outRect.top;


        OkMomentItem data = getIntent().getParcelableExtra("MomentData");
        final OkMomentItem poi = data;
        if (poi != null) {
            TextView userTv = findViewById(R.id.detail_user);
            userTv.setText(poi.moment_nickname);

            ImageView headView = findViewById(R.id.detail_head);
            ImageLoader.loadHead(this, poi.moment_headimg, headView);
            headView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!StringUtils.isEmpty(poi.moment_headimg)) {
                        // 放大预览头像
                        ArrayList<String> list = new ArrayList<>();
                        list.add(poi.moment_headimg);
                        Album.gallery(DetailActivity.this)
                                .checkedList(list) // List of image to view: ArrayList<String>.
                                .currentPosition(0)
                                .checkable(false) // Whether there is a selection function.
                                .start();
                    }
                }
            });

            TextView contentTv = findViewById(R.id.detail_content);
            if (!StringUtils.isEmpty(poi.moment_content)) {
                contentTv.setText(poi.moment_content);
            } else {
                contentTv.setVisibility(View.GONE);
            }
            TextView timeTv = findViewById(R.id.detail_time);
            timeTv.setText(poi.moment_createtime);
            TextView placeTv = findViewById(R.id.detail_place_text);
            placeTv.setText(poi.moment_place);
            likeTv = findViewById(R.id.detail_like_count);
            likeTv.setText(String.valueOf(poi.moment_like));

            ZzImageBox imageBox = findViewById(R.id.detail_image_box);
            if (StringUtils.isEmpty(poi.moment_picture1)) {
                imageBox.setVisibility(View.GONE);
            } else {
                ArrayList<String> list = new ArrayList<>();
                if (!StringUtils.isEmpty(poi.moment_picture1)) {
                    list.add(poi.moment_picture1);
                }
                if (!StringUtils.isEmpty(poi.moment_picture2)) {
                    list.add(poi.moment_picture2);
                }
                if (!StringUtils.isEmpty(poi.moment_picture3)) {
                    list.add(poi.moment_picture3);
                }
                if (!StringUtils.isEmpty(poi.moment_picture4)) {
                    list.add(poi.moment_picture4);
                }
                if (!StringUtils.isEmpty(poi.moment_picture5)) {
                    list.add(poi.moment_picture5);
                }
                if (!StringUtils.isEmpty(poi.moment_picture6)) {
                    list.add(poi.moment_picture6);
                }
                if (!StringUtils.isEmpty(poi.moment_picture7)) {
                    imageBox.addImageOnline(poi.moment_picture7);
                }
                if (!StringUtils.isEmpty(poi.moment_picture8)) {
                    list.add(poi.moment_picture8);
                }
                if (!StringUtils.isEmpty(poi.moment_picture9)) {
                    list.add(poi.moment_picture9);
                }
                imageBox.setImageSizeOneLine(list.size());
                for (String url : list) {
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
        } else {
            Msg.show("地图正在刷新");
            MapOptEvent.updateMap();
            finish();
            return;
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
        checkBox.setChecked(LikeManager.get().hadPut(poi.id));
        checkable = !LikeManager.get().hadPut(poi.id);
        if (!OkayApi.get().isLogined()) {
            checkable = false;
        }
        checkBox.setClickable(checkable);
        checkBox.setFocusable(checkable);
        checkBox.setEnabled(checkable);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkable) {
                    return;
                }
                if (poi != null) {
                    OkMoment.postLike(poi.id, new CommonCallback() {
                        @Override
                        public void onSuccess(String result) {
                            LikeManager.get().put(poi.id);
                            checkBox.setChecked(true);
                            checkBox.setClickable(false);
                            poi.moment_like++;
                            OkMessage.postMessage(poi.uuid, OkayApi.get().getCurrentUser().getNickname()
                                    + " 赞了你在<"
                                    + poi.moment_place
                                    + ">发表的游记!",
                                    poi.moment_picture1,
                                    OkMessage.TYPE_MOMENT_LIKE,
                                    poi.id);
                            likeTv.post(new Runnable() {
                                @Override
                                public void run() {
                                    likeTv.setText(String.valueOf(poi.moment_like));
                                    PoiManager.updateLike(poi.id, poi.moment_like);
                                    Msg.show("点赞成功");
                                }
                            });
                        }

                        @Override
                        public void onFail(OkException e) {
                            checkBox.setChecked(false);
                            Msg.show(e.getMessage());
                        }
                    });
                }
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                }
            }
        });

        initController(poi);
    }

    private void initController(OkMomentItem poi) {
        commentController = new CommentController(poi);
        addController(commentController);
    }

    public static void startDetailPage(BaseActivity activity, OkMomentItem poi) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra("MomentData", poi);
        activity.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LikeManager.get().finish();
    }
}
