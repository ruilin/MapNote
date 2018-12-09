package com.muyu.mapnote.framework.controller;

import java.util.ArrayList;

public abstract class SubController {

    private ArrayList<SubController> mControllerList = new ArrayList<>();

    protected void addController(BaseActivity activity, SubController controller) {
        if (!mControllerList.contains(controller)) {
            mControllerList.add(controller);
            controller.onCreate(activity);
        }
    }

    protected void removeController(ActivityController controller) {
        controller.onRemoved();
        mControllerList.remove(controller);
    }

    public abstract void onCreate(BaseActivity activity);

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

    public void onRemoved() {
        for (SubController controller : mControllerList) {
            controller.onRemoved();
        }
        mControllerList.clear();
    }
}
