package com.muyu.mapnote.map;

import android.location.Location;

import com.muyu.mapnote.map.map.poi.Poi;
import com.tencent.lbssearch.object.result.SearchResultObject;

import org.greenrobot.eventbus.EventBus;

public class MapOptEvent<T> {
    public static final byte MAP_EVENT_GOTO_LOCATION = 1;

    public byte eventId = 0;
    public T object;

    public MapOptEvent(byte mapEvent, T object) {
        this.eventId = mapEvent;
        this.object = object;
    }

    public static void toLocation(Poi poi) {
        EventBus.getDefault().post(new MapOptEvent(MAP_EVENT_GOTO_LOCATION, poi));
    }
}
