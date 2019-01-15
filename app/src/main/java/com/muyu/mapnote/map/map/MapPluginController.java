package com.muyu.mapnote.map.map;

import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.minimalism.framework.controller.SubController;

public abstract class MapPluginController extends SubController {
    private MapboxMap mMap;
    private MapView mMapView;
    private PermissionsManager mPermissionsManager;

    protected void onMapCreated(MapboxMap map, MapView mapView) {
        mMap = map;
        mMapView = mapView;
    }

    public void onMapClick(@NonNull LatLng point, @NonNull PointF screenPoint) {}

    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    public boolean onLocationClick() {
        return false;
    }

    public void onRequestPermissionsResult(boolean granted) {
    }

    protected MapboxMap getMap() {
        return mMap;
    }

    protected MapView getMapView() {
        return mMapView;
    }
}
