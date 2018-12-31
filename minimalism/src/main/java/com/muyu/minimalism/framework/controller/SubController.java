package com.muyu.minimalism.framework.controller;

import com.muyu.minimalism.framework.app.BaseActivity;

import java.util.ArrayList;

public abstract class SubController {

    private BaseActivity mActivity;

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

    public void onAttached(BaseActivity activity) {
        mActivity = activity;
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

    public void onDetached() {
        for (int i = mControllerList.size() - 1; i > 0; --i) {
            mControllerList.get(i).onDetached();
        }
        mControllerList.clear();
    }

    protected BaseActivity getActivity() {
        return mActivity;
    }
}
