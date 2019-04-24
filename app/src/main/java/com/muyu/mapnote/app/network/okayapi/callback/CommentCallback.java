package com.muyu.mapnote.app.network.okayapi.callback;

import com.muyu.mapnote.app.network.okayapi.OkException;
import com.muyu.mapnote.app.network.okayapi.been.OkCommentItem;

import java.util.ArrayList;

public interface CommentCallback {
    void onSuccess(ArrayList<OkCommentItem> list);
    void onFail(OkException e);
}
