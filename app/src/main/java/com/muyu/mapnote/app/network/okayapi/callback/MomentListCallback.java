package com.muyu.mapnote.app.network.okayapi.callback;

import com.muyu.mapnote.app.network.okayapi.OkException;
import com.muyu.mapnote.app.network.okayapi.been.OkMomentItem;

import java.util.ArrayList;

public interface MomentListCallback {
    void onSuccess(ArrayList<OkMomentItem> list);
    void onFail(OkException e);
}
