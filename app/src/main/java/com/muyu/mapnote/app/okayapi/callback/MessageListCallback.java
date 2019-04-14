package com.muyu.mapnote.app.okayapi.callback;

import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.been.OkMessageItem;
import com.muyu.mapnote.app.okayapi.been.OkMomentItem;

import java.util.ArrayList;

public interface MessageListCallback {
    void onSuccess(ArrayList<OkMessageItem> list);
    void onFail(OkException e);
}
