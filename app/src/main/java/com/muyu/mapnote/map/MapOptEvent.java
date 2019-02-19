package com.muyu.mapnote.map;

import android.location.Location;

import com.muyu.mapnote.map.map.poi.Poi;
import com.tencent.lbssearch.object.result.SearchResultObject;

import org.greenrobot.eventbus.EventBus;

public class MapOptEvent<T> {
    public static final byte MAP_EVENT_GOTO_LOCATION = 1;
    public static final byte MAP_EVENT_LOGIN_SUCCESS = 2;

    public byte eventId = 0;
    public T object;
    public String message;

    public MapOptEvent(byte mapEvent, T object, String message) {
        this.eventId = mapEvent;
        this.object = object;
        this.message = message;
    }

    public static void showSearchResult(String keyword, Poi poi) {
        EventBus.getDefault().post(new MapOptEvent(MAP_EVENT_GOTO_LOCATION, poi, keyword));
    }

    public static void loginSuccess() {
        EventBus.getDefault().post(new MapOptEvent(MAP_EVENT_LOGIN_SUCCESS, null, "Login Success"));
    }
}
