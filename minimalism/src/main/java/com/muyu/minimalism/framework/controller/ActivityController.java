package com.muyu.minimalism.framework.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.muyu.minimalism.framework.app.BaseActivity;

import java.util.ArrayList;

public abstract class ActivityController {

    private ArrayList<SubController> mControllerList = new ArrayList<>();

    public void addController(@NonNull BaseActivity activity, @NonNull SubController controller) {
        if (!mControllerList.contains(controller)) {
            mControllerList.add(controller);
            controller.onAttached(activity);
        }
    }

    public void removeController(@NonNull ActivityController controller) {
        mControllerList.remove(controller);
        controller.onRemoved();
    }

    public final ArrayList<SubController> getSubControllers() {
        return mControllerList;
    }

    public abstract void onCreate(BaseActivity activity);

    public void onStart() {
    }

    public void onResume() {
        for (SubController controller : mControllerList) {
            controller.onResume();
        }
    }

    public void onPause() {
        for (SubController controller : mControllerList) {
            controller.onPause();
        }
    }

    public void onStop() {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onDestroy() {
        onRemoved();
    }

    public void onLowMemory() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onRemoved() {
        for (SubController controller : mControllerList) {
            controller.onDetached();
        }
        mControllerList.clear();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }
}
