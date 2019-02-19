package com.muyu.mapnote.app.okayapi.callback;

import com.muyu.mapnote.app.okayapi.OkException;

public interface UploadCallback {
    void onSuccess(String url);
    void onFail(OkException e);
}
