package com.muyu.mapnote.user.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.muyu.mapnote.map.activity.MainActivity;
import com.muyu.mapnote.R;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

/**
 * Created by zzc on 2018/3/8.
 */

public class RegisterActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_register);

        final EditText etName = findViewById(R.id.reg_et_name);
        final EditText etPhone = findViewById(R.id.reg_et_phone);
        final EditText etPass = findViewById(R.id.reg_et_pass);

        findViewById(R.id.reg_bt_reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = getTextFromEditText(etName);
                String phone = getTextFromEditText(etPhone);
                String password = getTextFromEditText(etPass);

                AVUser user = new AVUser();// 新建 AVUser 对象实例
                user.setUsername(username);// 设置用户名
                user.setMobilePhoneNumber(phone);
                user.setPassword(password);// 设置密码
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            RegisterActivity.this.finish();
                        } else {
                            // 失败的原因可能有多种，常见的是用户名已经存在。
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public String getTextFromEditText(EditText editText) {
        return editText.getText() != null ? editText.getText().toString() : "";
    }
}
