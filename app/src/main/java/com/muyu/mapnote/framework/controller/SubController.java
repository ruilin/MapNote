package com.muyu.mapnote.framework.controller;

import com.muyu.mapnote.framework.app.BaseActivity;

import java.util.ArrayList;

public abstract class SubController {

    private ArrayList<SubController> mControllerList = new ArrayList<>();

    protected void addController(BaseActivity activity, SubController controller) {
        if (!mControllerList.contains(controller)) {
            mControllerList.add(controller);
            controller.onAttached(activity);
        }
    }

    protected void removeController(ActivityController controller) {
        controller.onRemoved();
        mControllerList.remove(controller);
    }

    public abstract void onAttached(BaseActivity activity);

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

    public void onDetached() {
        for (SubController controller : mControllerList) {
            controller.onDetached();
        }
        mControllerList.clear();
    }
}
