package com.muyu.mapnote.note;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.muyu.mapnote.R;
import com.muyu.mapnote.app.Config;
import com.muyu.mapnote.app.MapApplication;
import com.muyu.mapnote.app.MapBaseActivity;
import com.muyu.mapnote.app.Umeng;
import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkImage;
import com.muyu.mapnote.app.okayapi.OkMoment;
import com.muyu.mapnote.app.okayapi.callback.MomentPostCallback;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.mapnote.map.map.poi.SearchHelper;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.muyu.minimalism.utils.Logs;
import com.muyu.minimalism.utils.MathUtils;
import com.muyu.minimalism.utils.SysUtils;
import com.muyu.minimalism.view.BottomMenu;
import com.muyu.minimalism.view.DialogUtils;
import com.muyu.minimalism.view.Loading;
import com.muyu.minimalism.view.MediaLoader;
import com.muyu.minimalism.view.Msg;
import com.muyu.minimalism.view.imagebox.ZzImageBox;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PublishActivity extends MapBaseActivity {
    private final int MAX_COUNT = 4;
    private ZzImageBox imageBox;
    private EditText editText;
    private TextView placeTextView;
    private Loading loading;
    private boolean isPublished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        editText = findViewById(R.id.publish_et);
        isPublished = false;
        initTitleBar();
        initImageBox();
        initPlace();

        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .build());

        loading = new Loading(this);

        popBottomMenu();
    }

    private void initPlace() {
        placeTextView = findViewById(R.id.publish_place_text);
        Location loc = LocationHelper.INSTANCE.getLastLocation();
        placeTextView.setText("经纬度(" + MathUtils.round(loc.getLatitude(), 8) + "," + MathUtils.round(loc.getLongitude(), 8) + ")");
        SearchHelper.searchLocationInfo(this, LocationHelper.INSTANCE.getLastLocation(), new SearchHelper.OnSearchLocationCallback() {
            @Override
            public void onSearchLocationCallback(String info) {
                placeTextView.setText(info);
            }
        });

        TextView copyButton = findViewById(R.id.publish_copy);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("copy from " + MapApplication.getInstance().getAppName(), placeTextView.getText());
                clipboardManager.setPrimaryClip(clipData);
                Msg.show("复制成功");
            }
        });
    }

    private void initTitleBar() {
        CommonTitleBar titleBar = findViewById(R.id.publish_title);
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    finish();
                } else if (action == CommonTitleBar.ACTION_RIGHT_TEXT) {
                    SysUtils.hideSoftInput(v);
                    if (imageBox.getCount() == 0) {
                        Msg.show("选一张照片分享下吧");
                        return;
                    }
                    DialogUtils.show(PublishActivity.this, "发表", "您确定发表该游记吗？", new DialogUtils.DialogCallback() {

                        @Override
                        public void onPositiveClick(DialogInterface dialog) {
                            /** 上传数据 */
                            dialog.dismiss();
                            loading.show("发表中，请耐心等待哦～");
                            OkMoment moment = OkMoment.newInstance()
                                    .setContent(editText.getText().toString())
                                    .setLocation(LocationHelper.INSTANCE.getLastLocation(), placeTextView.getText().toString());
                            for (int i = 0; i < imageBox.getCount(); i++) {
                                moment.addImage(new OkImage(imageBox.getImagePathAt(i), 90));
                            }
                            moment.postInBackground(new MomentPostCallback() {
                                @Override
                                public void onPostSuccess() {
                                    isPublished = true;
                                    loading.dismiss();
                                    MapOptEvent.updateMap();
                                    finish();
                                    Msg.show("发表成功");
                                }

                                @Override
                                public void onPostFail(OkException e) {
                                    Logs.e(e.getMessage());
                                    Msg.show("网络异常，请检查设置");
                                    loading.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void finish() {
        if (!isPublished && (imageBox.getCount() != 0 || editText.length() != 0)) {
            DialogUtils.show(PublishActivity.this, "提示", "要放弃正在编辑的内容吗？", new DialogUtils.DialogCallback() {

                @Override
                public void onPositiveClick(DialogInterface dialog) {
                    dialog.dismiss();
                    PublishActivity.super.finish();
                }
            });
        } else {
            super.finish();
        }
    }

    private void initImageBox() {
        imageBox = findViewById(R.id.zz_image_box);
        imageBox.setOnImageClickListener(new ZzImageBox.OnImageClickListener() {

            @Override
            public void onImageClick(int position, String url, String realPath, int realType, ImageView iv) {
                // 预览图片
                Album.gallery(PublishActivity.this)
                        .checkedList((ArrayList<String>) imageBox.getAllImages()) // List of image to view: ArrayList<String>.
                        .currentPosition(position)
                        .checkable(true) // Whether there is a selection function.
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
                imageBox.removeImage(position);
            }

            @Override
            public void onAddClick() {
                Log.d("ZzImageBox", "all images\n"+imageBox.getAllImages().toString());
                popBottomMenu();
            }
        });
    }

    private void popBottomMenu() {
        BottomMenu.show(PublishActivity.this, new String[]{"拍照", "从相册选取"}, new BottomMenu.OnItemClickedListener() {
            @Override
            public void OnItemClicked(int position) {
                switch (position) {
                    case 0:
                        takePhotoToDir();
                        break;
                    case 1:
                        showSelector();
                        break;
                }
            }
        });
    }

    private void showSelector() {
        Album.image(PublishActivity.this) // Image selection.
                .multipleChoice()
                .widget(Widget.newLightBuilder(PublishActivity.this)
                        .title(R.string.image_selector_title)
                        .statusBarColor(Color.WHITE) // StatusBar color.
                        .toolBarColor(Color.WHITE) // Toolbar color.
//                        .navigationBarColor(Color.WHITE) // Virtual NavigationBar color of Android5.0+.
//                        .mediaItemCheckSelector(Config.colorPrimaryDark, Config.colorPrimary) // Image or video selection box.
//                        .bucketItemCheckSelector(Config.colorPrimaryDark, Config.colorPrimary) // Select the folder selection box.
                        .build()
                )
                .camera(false)
                .columnCount(4)
                .selectCount(MAX_COUNT - imageBox.getCount())
                .checkedList(null)
//                                        .filterSize() // Filter the file size.
//                                        .filterMimeType() // Filter file format.
//                                        .afterFilterVisibility() // Show the filtered files, but they are not available.
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        for (AlbumFile file : result) {
                            imageBox.addImage(file.getPath());
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

    final int TAKE_PHOTO_REQUEST = 101;
    String cameraPath = Config.genPhotoPath();

    private void takePhotoToDir() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 222);
                return;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
                return;
            }
        }

        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //指定保存路径
        cameraPath = Config.genPhotoPath();
        File imageFile = new File(cameraPath);

        //创建一个图片保存的Uri
        Uri imageFileUri = Uri.fromFile(imageFile);
        intentFromCapture.putExtra(MediaStore.Images.Media.ORIENTATION, 0);

        //设置MediaStore.EXTRA_OUTPUT的输出路径为imageFileUri
        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intentFromCapture, TAKE_PHOTO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO_REQUEST:
                if (resultCode == RESULT_CANCELED) {
                    // TODO 取消拍照
                    return;
                }
                imageBox.addImage(cameraPath);
                List<String> path = imageBox.getAllRealPath();
                Logs.e(path.size()+"");
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        takePhotoToDir();
    }

    @Override
    public void onResume() {
        super.onResume();
        Umeng.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Umeng.onPause(this);
    }
}
