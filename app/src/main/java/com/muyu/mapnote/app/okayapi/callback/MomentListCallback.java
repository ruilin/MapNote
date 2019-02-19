package com.muyu.mapnote.app.okayapi.callback;

import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkMomentItem;

import java.util.ArrayList;

public interface MomentListCallback {
    void onSuccess(ArrayList<OkMomentItem> list);
    void onFail(OkException e);
}
