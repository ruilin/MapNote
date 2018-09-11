package com.muyu.mapnote.base;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

public abstract class ActivityController {

    public abstract void init(FragmentActivity activity);

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
