package com.muyu.mapnote.app.okayapi.callback;

import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkCommentItem;

import java.util.ArrayList;

public interface CommentCallback {
    void onSuccess(ArrayList<OkCommentItem> list);
    void onFail(OkException e);
}
