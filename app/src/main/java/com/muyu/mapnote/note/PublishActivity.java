package com.muyu.mapnote.note;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.SysUtils;
import com.muyu.minimalism.view.BottomMenu;
import com.muyu.minimalism.view.DialogUtils;
import com.muyu.minimalism.view.Msg;
import com.muyu.minimalism.view.imagebox.ZzImageBox;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class PublishActivity extends BaseActivity {
    ZzImageBox imageBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initTitleBar();
        initImageBox();
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
                Log.d("ZzImageBox", "all images\n"+imageBox.getAllImages().toString());
                BottomMenu.show(PublishActivity.this, new String[]{"拍照", "从相册选取"}, new BottomMenu.OnItemClickedListener() {
                    @Override
                    public void OnItemClicked(int position) {
                        switch (position) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                break;
                        }
                    }
                });
            }
        });
    }

    final int TAKE_PHOTO_REQUEST = 101;
    Uri imageUri;
    private void takePhoto() {
        imageUri = createImageUri(this);
        if (imageUri == null)
            return;
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//如果不设置EXTRA_OUTPUT getData()  获取的是bitmap数据  是压缩后的

        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 222);
                return;
            } else {
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO_REQUEST:
                if (resultCode == RESULT_CANCELED) {
                    delteImageUri(PublishActivity.this, imageUri);
                    return;
                }
                imageBox.addImage(imageUri.getPath());
                Msg.show(imageUri.getPath());
                break;
        }
    }

    private static Uri createImageUri(Activity activity) {
        Uri uri = null;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
        } else {
            String name = "takePhoto" + System.currentTimeMillis();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE, name);
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpeg");
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }
        return uri;
    }

    public static void delteImageUri(Context context, Uri uri) {
        context.getContentResolver().delete(uri, null, null);
    }
}
