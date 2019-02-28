package com.muyu.mapnote.app;

import com.muyu.minimalism.framework.app.BaseActivity;

public class MapBaseActivity extends BaseActivity {

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
