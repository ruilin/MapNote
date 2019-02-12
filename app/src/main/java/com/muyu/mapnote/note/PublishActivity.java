package com.muyu.mapnote.note;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.muyu.mapnote.R;
import com.muyu.mapnote.app.Config;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.Logs;
import com.muyu.minimalism.utils.SysUtils;
import com.muyu.minimalism.view.BottomMenu;
import com.muyu.minimalism.view.DialogUtils;
import com.muyu.minimalism.view.MediaLoader;
import com.muyu.minimalism.view.Msg;
import com.muyu.minimalism.view.imagebox.ZzImageBox;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PublishActivity extends BaseActivity {
    private final int MAX_COUNT = 4;
    private ZzImageBox imageBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initTitleBar();
        initImageBox();

        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .build());
        Album.image(this)
                .multipleChoice()
                .widget(Widget.newLightBuilder(this)
                        .statusBarColor(Color.WHITE) // StatusBar color.
                        .toolBarColor(Color.WHITE) // Toolbar color.
                        .navigationBarColor(Color.WHITE) // Virtual NavigationBar color of Android5.0+.
                        .mediaItemCheckSelector(Color.BLUE, Color.GREEN) // Image or video selection box.
                        .bucketItemCheckSelector(Color.RED, Color.YELLOW) // Select the folder selection box.
                        .build()
                );
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
                    DialogUtils.show(PublishActivity.this, "发表", "确定发表游记是吗？", new DialogUtils.DialogCallback() {

                        @Override
                        public void onPositiveClick(DialogInterface dialog) {
                            dialog.dismiss();
                            finish();
                            Msg.show("发表成功");
                        }
                    });
                }
            }
        });
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
                BottomMenu.show(PublishActivity.this, new String[]{"拍照", "从相册选取"}, new BottomMenu.OnItemClickedListener() {
                    @Override
                    public void OnItemClicked(int position) {
                        switch (position) {
                            case 0:
                                takePhotoToDir();
                                break;
                            case 1:
                                Album.image(PublishActivity.this) // Image selection.
                                        .multipleChoice()
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
                                break;
                        }
                    }
                });
            }
        });
    }

    final int TAKE_PHOTO_REQUEST = 101;
    SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    String cameraPath = Config.PHOTO_PATH + format.format(new Date()) + ".jpg";

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

}
