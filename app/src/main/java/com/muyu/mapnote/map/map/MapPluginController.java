package com.muyu.mapnote.map.map;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.framework.controller.SubController;

public abstract class MapPluginController extends SubController {

    protected abstract void onMapCreated(MapboxMap map, MapView mapView);
}
