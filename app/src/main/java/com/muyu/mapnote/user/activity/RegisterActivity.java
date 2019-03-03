package com.muyu.mapnote.user.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.Config;
import com.muyu.mapnote.app.MapBaseActivity;
import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkUser;
import com.muyu.mapnote.app.okayapi.callback.LoginCallback;
import com.muyu.mapnote.app.okayapi.callback.RegisterCallback;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.mapnote.note.PublishActivity;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.LoginUtils;
import com.muyu.minimalism.utils.Logs;
import com.muyu.minimalism.utils.SPUtils;
import com.muyu.minimalism.utils.SysUtils;
import com.muyu.minimalism.view.BottomMenu;
import com.muyu.minimalism.view.Loading;
import com.muyu.minimalism.view.MediaLoader;
import com.muyu.minimalism.view.Msg;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends MapBaseActivity {
    private EditText mMobileView;
    private EditText mNicknameView;
    private EditText mPasswordView;
    private Loading mLoading;
    private ImageView headView;
    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SysUtils.setStatusBarColor(this, getResources().getColor(R.color.white));

        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .build());

        findViewById(R.id.register_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Set up the login form.
        mNicknameView = findViewById(R.id.register_et_nickname);
        mMobileView = findViewById(R.id.register_et_mobile);
        mPasswordView = findViewById(R.id.register_et_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    SysUtils.hideSoftInput(textView);
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        initPasswordShow();

        View button = findViewById(R.id.register_bt_register);
        button.setOnClickListener(view -> {
            SysUtils.hideSoftInput(view);
            attemptLogin();
        });

        button = findViewById(R.id.register_to_login);
        button.setOnClickListener(view -> {
            startActivity(LoginActivity.class);
            finish();
        });

        initHead();

        mLoading = new Loading(this);
    }

    private void initHead() {
        (headView = findViewById(R.id.register_head)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomMenu.show(RegisterActivity.this, new String[]{"拍照", "从相册选取"}, new BottomMenu.OnItemClickedListener() {
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
        });
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
//                imageBox.addImage(cameraPath);
//                List<String> path = imageBox.getAllRealPath();
                Glide.with(RegisterActivity.this).load(cameraPath).into(headView);
                imagePath = cameraPath;
                break;
        }
    }

    private void showSelector() {
        Album.image(this) // Image selection.
                .singleChoice()
                .widget(Widget.newLightBuilder(this)
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
//                                        .filterSize() // Filter the file size.
//                                        .filterMimeType() // Filter file format.
//                                        .afterFilterVisibility() // Show the filtered files, but they are not available.
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                        for (AlbumFile file : result) {
                            imagePath = file.getPath();
                            Glide.with(RegisterActivity.this).load(file.getPath()).into(headView);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        takePhotoToDir();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mMobileView.setError(null);
        mPasswordView.setError(null);
        mNicknameView.setError(null);

        // Store values at the time of the login attempt.
        String nickname = mNicknameView.getText().toString();
        String mobile = mMobileView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid mobile address.
        if (TextUtils.isEmpty(nickname)) {
            Msg.show(getString(R.string.error_field_required_nickname));
            focusView = mNicknameView;
            cancel = true;
        } else if (TextUtils.isEmpty(mobile)) {
//            mMobileView.setError(getString(R.string.error_field_required));
            Msg.show(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        } else if (!isMobileNumberValid(mobile)) {
//            mMobileView.setError(getString(R.string.error_invalid_mobile));
            Msg.show(getString(R.string.error_invalid_mobile));
            focusView = mMobileView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
            Msg.show(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoading.show("注册中……");

            OkUser user = new OkUser();
            user.setNickname(nickname);
            user.setSex(0);
            user.setUsername(mobile);
            user.setPassword(password);
            user.registerInBackground(imagePath, new RegisterCallback() {
                @Override
                public void done(OkException e) {
                    SysUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoading.dismiss();
                            if (e == null) {
                                SPUtils.saveObject(LoginActivity.SP_KEY_USERNAME, mobile);
                                startActivity(LoginActivity.class);
                                finish();
                                Msg.show("注册成功，请登录！");
                            } else {
                                Msg.show(e.getMessage());
                            }
                        }
                    });
                }
            });
        }
    }

    private boolean isMobileNumberValid(String mobile) {
        return LoginUtils.isMobileNO(mobile);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6 && password.length() <= 20;
    }

    private void initPasswordShow() {
        ((CheckBox) findViewById(R.id.cbDisplayPassword)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    //mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    /**
                     * 第二种
                     */
                    mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    //mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    /**
                     * 第二种
                     */
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }
}
