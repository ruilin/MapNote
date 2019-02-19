package com.muyu.mapnote.app.okayapi.callback;

import com.muyu.mapnote.app.okayapi.OkException;

public interface MomentPostCallback {
    void onPostSuccess();
    void onPostFail(OkException e);
}
