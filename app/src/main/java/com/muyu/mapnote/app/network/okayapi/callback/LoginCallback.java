package com.muyu.mapnote.app.network.okayapi.callback;

import com.muyu.mapnote.app.network.okayapi.OkException;
import com.muyu.mapnote.app.network.okayapi.OkUser;

public interface LoginCallback {

    public void done(OkUser user, OkException e);
}
