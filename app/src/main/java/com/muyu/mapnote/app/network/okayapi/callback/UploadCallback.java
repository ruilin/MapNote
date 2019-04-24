package com.muyu.mapnote.app.network.okayapi.callback;

import com.muyu.mapnote.app.network.okayapi.OkException;

public interface UploadCallback {
    void onSuccess(String url);
    void onFail(OkException e);
}
