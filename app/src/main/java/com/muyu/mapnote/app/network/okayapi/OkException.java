package com.muyu.mapnote.app.network.okayapi;


public class OkException extends Exception {

    public static final int CODE_UNKNOWN = 0;
    public static final int CODE_NOT_LOGIN = 1;

    private int code = CODE_UNKNOWN;

    public OkException(String message) {
        super(message);
    }

    public OkException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
