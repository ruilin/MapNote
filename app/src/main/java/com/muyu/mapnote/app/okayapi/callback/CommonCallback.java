package com.muyu.mapnote.app.okayapi.callback;

import com.google.gson.JsonObject;
import com.muyu.mapnote.app.okayapi.OkException;

public interface CommonCallback {
    void onSuccess(String result);
    void onFail(OkException e);
}
