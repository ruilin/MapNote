package com.muyu.mapnote.app.okayapi.callback;

import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkUser;

public interface LoginCallback {

    public void done(OkUser user, OkException e);
}