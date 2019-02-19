package com.muyu.mapnote.map.map.poi;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.app.BaseApplication;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class PoiManager {

    public final static byte POI_TYPE_TARGET = 0;
    public final static byte POI_TYPE_SEARCH_FIRST = 1;
    public final static byte POI_TYPE_SEARCH_OTHER = 2;
    public final static byte POI_TYPE_MOMENT = 3;

    private static Hashtable<String, Marker> mKeywordPoiMap = new Hashtable<String, Marker>();
    private static Hashtable<String, Marker> mMomentPoiMap = new Hashtable<String, Marker>();

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
            case POI_TYPE_MOMENT:
                icon = iconFactory.fromResource(R.drawable.green_marker);
                break;
        }
        return map.addMarker(new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(snippet)
                .icon(icon)
        );
    }

    public static void showPoi(MapboxMap map, Poi poi) {
        byte poiType = PoiManager.POI_TYPE_SEARCH_OTHER;
//        if (isFirst) {
//            poiType = PoiHelper.POI_TYPE_SEARCH_FIRST;
//            isFirst = false;
//        }
        Marker marker = showPoi(map, poi.title, poi.address, new LatLng(poi.lat, poi.lng), poiType);
        mKeywordPoiMap.put(poi.title, marker);
    }

    public static void showMoment(MapboxMap map, MomentPoi poi) {
        Marker marker = showPoi(map, poi.title, poi.address, new LatLng(poi.lat, poi.lng), POI_TYPE_MOMENT);
        mMomentPoiMap.put(poi.title, marker);
    }

    public static void removePoiByType(MapboxMap map, byte type) {
        switch (type) {
            case POI_TYPE_TARGET:
                break;
            case POI_TYPE_SEARCH_FIRST:
                break;
            case POI_TYPE_SEARCH_OTHER:
                for(Iterator<Map.Entry<String, Marker>> iterator = mKeywordPoiMap.entrySet().iterator(); iterator.hasNext();){
                    Map.Entry<String, Marker> entry = iterator.next();
                    map.removeMarker(entry.getValue());
                }
                break;
        }
    }

    public static void removePoiByKey(MapboxMap map, String key) {
        map.removeMarker(mKeywordPoiMap.get(key));
    }
}
