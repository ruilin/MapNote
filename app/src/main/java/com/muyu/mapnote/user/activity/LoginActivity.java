package com.muyu.mapnote.user.activity;

import android.annotation.TargetApi;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.muyu.mapnote.R;
import com.muyu.mapnote.app.MapBaseActivity;
import com.muyu.mapnote.app.Umeng;
import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkUser;
import com.muyu.mapnote.app.okayapi.callback.LoginCallback;
import com.muyu.mapnote.app.okayapi.callback.RegisterCallback;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.minimalism.utils.LoginUtils;
import com.muyu.minimalism.view.Loading;
import com.muyu.minimalism.view.Msg;
import com.muyu.minimalism.utils.SPUtils;
import com.muyu.minimalism.utils.SysUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends MapBaseActivity {
    public final static String SP_KEY_USERNAME = "SP_KEY_LOGIN_USERNAME";

    private EditText mMobileView;
    private EditText mPasswordView;
    private Loading mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SysUtils.setStatusBarColor(this, getResources().getColor(R.color.white));

        findViewById(R.id.login_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Set up the login form.
        mMobileView = findViewById(R.id.login_et_mobile);
        if (SPUtils.contains(SP_KEY_USERNAME)) {
            mMobileView.setText(SPUtils.get(SP_KEY_USERNAME, ""));
        }
        mPasswordView = findViewById(R.id.login_et_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    SysUtils.hideSoftInput(textView);
                    attemptLogin(true);
                    return true;
                }
                return false;
            }
        });
        initPasswordShow();

        View button = findViewById(R.id.login_bt_login);
        button.setOnClickListener(view -> {
            SysUtils.hideSoftInput(view);
            attemptLogin(true);
        });

        button = findViewById(R.id.login_bt_register);
        button.setOnClickListener(view -> {
            SysUtils.hideSoftInput(view);
            attemptLogin(false);
        });

        button = findViewById(R.id.login_to_register);
        button.setOnClickListener(view -> {
            startActivity(RegisterActivity.class);
            finish();
        });

        mLoading = new Loading(this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(boolean isLogin) {
        // Reset errors.
        mMobileView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String mobile = mMobileView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid mobile address.
        if (TextUtils.isEmpty(mobile)) {
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
            SPUtils.saveObject(SP_KEY_USERNAME, mobile);
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoading.show("登录中……");

            OkUser user = new OkUser();
            user.setUsername(mobile);
            user.setPassword(password);

            if (isLogin) {
                user.loginInBackground(new LoginCallback() {
                    @Override
                    public void done(OkUser user, OkException e) {
                        SysUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLoading.dismiss();
                                if (e == null) {
                                    Msg.show("登录成功!");
                                    MapOptEvent.loginSuccess();
                                    finish();
                                } else {
                                    Msg.show(e.getMessage());
                                }
                            }
                        });
                    }
                });
            }
        }
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

