package com.muyu.mapnote.app.network.okayapi.callback;

import com.muyu.mapnote.app.network.okayapi.OkException;

public interface CommonCallback {
    void onSuccess(String result);
    void onFail(OkException e);
}
