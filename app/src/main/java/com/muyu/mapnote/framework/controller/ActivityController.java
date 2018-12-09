package com.muyu.mapnote.framework.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }
}
