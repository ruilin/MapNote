package com.muyu.mapnote.framework.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public abstract class ActivityController {

    private ArrayList<SubController> mControllerList = new ArrayList<>();

    protected void addController(BaseActivity activity, SubController controller) {
        if (!mControllerList.contains(controller)) {
            mControllerList.add(controller);
            controller.onCreate(activity);
        }
    }

    protected void removeController(ActivityController controller) {
        mControllerList.remove(controller);
        controller.onRemoved();
    }

    protected final ArrayList<SubController> getSubControllers() {
        return mControllerList;
    }

    public abstract void onCreate(BaseActivity activity);

    protected void onStart() {
    }

    protected void onResume() {
        for (SubController controller : mControllerList) {
            controller.onResume();
        }
    }

    protected void onPause() {
        for (SubController controller : mControllerList) {
            controller.onPause();
        }
    }

    protected void onStop() {
    }

    protected void onSaveInstanceState(Bundle outState) {
    }

    protected void onDestroy() {
        onRemoved();
    }

    public void onLowMemory() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onRemoved() {
        for (SubController controller : mControllerList) {
            controller.onRemoved();
        }
        mControllerList.clear();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }
}
