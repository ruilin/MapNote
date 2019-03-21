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
    private MapController mMap;
    private MapboxMap mMapboxMap;
    private MapView mMapView;
    private MapController map;
    private PermissionsManager mPermissionsManager;

    protected void onMapCreated(MapController map) {
        mMap = map;
        mMapboxMap = map.mapboxMap;
        mMapView = map.mapView;
    }

    public void onMapClick(@NonNull LatLng point, @NonNull PointF screenPoint) {}

    public void onMapMoveStart(int reason) {}

    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }

    public boolean onLocationClick() {
        return false;
    }

    public void onRequestPermissionsResult(boolean granted) {
    }

    public MapController getMap() {
        return mMap;
    }

    public MapboxMap getMapboxMap() {
        return mMapboxMap;
    }

    public MapView getMapView() {
        return mMapView;
    }
}
