package com.muyu.mapnote.map.map;

import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.minimalism.framework.controller.SubController;

public abstract class MapPluginController extends SubController {

    protected abstract void onMapCreated(MapboxMap map, MapView mapView);

    public void onMapClick(@NonNull LatLng point, @NonNull PointF screenPoint) {

    }
}
