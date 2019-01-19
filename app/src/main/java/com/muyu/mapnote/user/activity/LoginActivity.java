package com.muyu.mapnote.user.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.graphics.Color;
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
import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkUser;
import com.muyu.mapnote.app.okayapi.callback.LoginCallback;
import com.muyu.mapnote.app.okayapi.callback.RegisterCallback;
import com.muyu.minimalism.Loading;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.LoginUtils;
import com.muyu.minimalism.utils.Msg;
import com.muyu.minimalism.utils.SPUtils;
import com.muyu.minimalism.utils.SysUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {
    private final static String SP_KEY_USERNAME = "SP_KEY_LOGIN_USERNAME";

    private AutoCompleteTextView mMobileView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private Loading mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SysUtils.setStatusBarColor(this, getResources().getColor(R.color.bgGray));
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

        Button button = findViewById(R.id.login_bt_login);
        button.setOnClickListener(view -> {
            SysUtils.hideSoftInput(view);
            attemptLogin(true);
        });

        button = findViewById(R.id.login_bt_register);
        button.setOnClickListener(view -> {
            SysUtils.hideSoftInput(view);
            attemptLogin(false);
        });

        mLoginFormView = findViewById(R.id.login_form);

        mLoading = new Loading(this) {
            @Override
            public void cancle() {

            }
        };
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
            showProgress(true);

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
                                showProgress(false);
                                if (e == null) {
                                    Msg.show("登录成功!");
                                    finish();
                                } else {
                                    Msg.show(e.getMessage());
                                }
                            }
                        });
                    }
                });
            } else {
                user.registerInBackground(new RegisterCallback() {
                    @Override
                    public void done(OkException e) {
                        SysUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                                if (e == null) {
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
    }

    private boolean isMobileNumberValid(String mobile) {
        return LoginUtils.isMobileNO(mobile);
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6 && password.length() <= 20;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (show)
            mLoading.show();
        else
            mLoading.dismiss();
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

