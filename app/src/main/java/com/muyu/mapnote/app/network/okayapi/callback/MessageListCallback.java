package com.muyu.mapnote.app.network.okayapi.callback;

import com.muyu.mapnote.app.network.okayapi.OkException;
import com.muyu.mapnote.app.network.okayapi.been.OkMessageItem;

import java.util.ArrayList;

public interface MessageListCallback {
    void onSuccess(ArrayList<OkMessageItem> list);
    void onFail(OkException e);
}
