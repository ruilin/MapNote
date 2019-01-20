package com.muyu.mapnote.note;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.SysUtils;
import com.muyu.minimalism.view.Dialog;
import com.muyu.minimalism.view.Dialog.DialogCallback;
import com.muyu.minimalism.view.Msg;
import com.muyu.minimalism.view.imagebox.ZzImageBox;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class PublishActivity extends BaseActivity {

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
                    Dialog.show(PublishActivity.this, "发表", "确定发表游记是吗？", new Dialog.DialogCallback() {

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
