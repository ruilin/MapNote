package com.muyu.mapnote.framework.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.muyu.mapnote.framework.controller.ActivityController;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    private ArrayList<ActivityController> mControllerList = new ArrayList<>();
    private boolean hasCreated = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasCreated = true;
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
        for (ActivityController controller : mControllerList)
            controller.onDestroy();
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
}
