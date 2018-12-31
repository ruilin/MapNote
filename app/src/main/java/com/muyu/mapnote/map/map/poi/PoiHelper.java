package com.muyu.mapnote.map.map.poi;

import android.content.Context;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.app.BaseApplication;

public class PoiHelper {

    public final static byte POI_TYPE_TARGET = 0;
    public final static byte POI_TYPE_SEARCH_FIRST = 1;
    public final static byte POI_TYPE_SEARCH_OTHER = 2;

    public static Marker showPoi(MapboxMap map, String title, String snippet, LatLng point, byte poiType) {
        IconFactory iconFactory = IconFactory.getInstance(BaseApplication.getInstance());
        Icon icon = iconFactory.fromResource(R.drawable.yellow_marker);
        switch (poiType) {
            case POI_TYPE_TARGET:
                icon = iconFactory.fromResource(R.drawable.map_default_map_marker);
                break;
            case POI_TYPE_SEARCH_FIRST:
                icon = iconFactory.fromResource(R.drawable.yellow_marker);
                break;
            case POI_TYPE_SEARCH_OTHER:
                icon = iconFactory.fromResource(R.drawable.blue_marker);
                break;
        }
        return map.addMarker(new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(snippet)
                .icon(icon)
        );
    }
}
