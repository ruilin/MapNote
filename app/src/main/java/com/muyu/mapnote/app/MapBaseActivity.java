package com.muyu.mapnote.app;

import com.muyu.minimalism.framework.app.BaseActivity;

public class MapBaseActivity extends BaseActivity {

    @Override
    protected void onResume() {
        super.onResume();
        Umeng.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Umeng.onPause(this);
    }
}
