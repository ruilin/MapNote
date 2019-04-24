package com.muyu.mapnote.app.network.okayapi.callback;

import com.muyu.mapnote.app.network.okayapi.OkException;

public interface MomentPostCallback {
    void onPostSuccess();
    void onPostFail(OkException e);
}
