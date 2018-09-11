package com.muyu.mapnote.map.map;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public interface OnMapCreated {
    void onMapCreated(MapboxMap map, MapView mapView);
}
