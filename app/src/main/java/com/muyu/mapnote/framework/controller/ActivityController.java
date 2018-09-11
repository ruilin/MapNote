package com.muyu.mapnote.base;

import android.content.Intent;
import android.os.Bundle;

import com.muyu.mapnote.map.navigation.location.LocationHelper;

public abstract class ActivityController {

    public abstract void onCreate(BaseActivity activity);

    protected void onStart() {
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    protected void onStop() {
    }

    protected void onSaveInstanceState(Bundle outState) {
    }

    protected void onDestroy() {
    }

    public void onLowMemory() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onRemoved() {
    }
}
