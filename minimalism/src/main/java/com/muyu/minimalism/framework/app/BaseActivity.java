package com.muyu.minimalism.framework.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.githang.statusbar.StatusBarCompat;
import com.muyu.minimalism.R;
import com.muyu.minimalism.framework.controller.ActivityController;
import com.muyu.minimalism.utils.SysUtils;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class BaseActivity extends AppCompatActivity {

    private ArrayList<ActivityController> mControllerList = new ArrayList<>();
    private View mLayout;
    private boolean hasCreated = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasCreated = true;
    }

    @Override
    public void setContentView(View view) {
        mLayout = view;
        super.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        mLayout = getLayoutInflater().inflate(layoutResID, null);
        super.setContentView(mLayout);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mLayout = view;
        super.setContentView(view, params);
    }

    public View getContentView() {
        return mLayout;
    }

    public void startActivity(@NonNull Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
    }

    protected void addController(ActivityController controller) {
        if (!mControllerList.contains(controller)) {
            mControllerList.add(controller);
            if (hasCreated) {
                controller.onCreate(this);
            }
        }
    }

    protected void removeController(ActivityController controller) {
        mControllerList.remove(controller);
        if (hasCreated) {
            controller.onRemoved();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        for (ActivityController controller : mControllerList)
            controller.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (ActivityController controller : mControllerList)
            controller.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (ActivityController controller : mControllerList)
            controller.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (ActivityController controller : mControllerList)
            controller.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for (ActivityController controller : mControllerList)
            controller.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = mControllerList.size() - 1; i > 0; --i) {
            mControllerList.get(i).onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        for (ActivityController controller : mControllerList)
            controller.onLowMemory();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (ActivityController controller : mControllerList)
            controller.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (ActivityController controller : mControllerList)
            controller.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setStatusBarColor(@ColorInt int color) {
        SysUtils.setStatusBarColor(this, color);
    }

    public void setStatusBarTrans(boolean yes) {
        if (yes) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            StatusBarCompat.setStatusBarColor(this, Color.TRANSPARENT, false);
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
