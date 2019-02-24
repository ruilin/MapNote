package com.muyu.mapnote.map.map.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.muyu.minimalism.view.Msg;

import java.util.List;

public class LocationController extends MapPluginController {
    private PermissionsManager permissionsManager;
//    private LocationLayerPlugin locationPlugin;
    private LocationComponent locationComponent;
    private Location originLocation;
    boolean isFirst = true;

    @Override
    protected void onMapCreated(MapboxMap map, MapView mapView) {
        super.onMapCreated(map, mapView);
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin() {
        // Create an instance of LOST location engine
        initializeLocationEngine();

//        locationPlugin = new LocationLayerPlugin(getMapView(), getMap());
//        locationPlugin.setLocationLayerEnabled(true);
//        locationPlugin.setRenderMode(RenderMode.COMPASS);
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {
        initializeLocationEngine();

        // Get an instance of the component
        locationComponent = getMap().getLocationComponent();

        // Activate with options
        locationComponent.activateLocationComponent(getActivity(), getMap().getStyle());

        // Enable to make component visible
        locationComponent.setLocationComponentEnabled(true);

        // Set the component's camera mode
        locationComponent.setCameraMode(CameraMode.TRACKING);

        // Set the component's render mode
        locationComponent.setRenderMode(RenderMode.COMPASS);

    }

    @Override
    public void onRequestPermissionsResult(boolean granted) {
        if (granted) {
//            enableLocationPlugin();
            enableLocationComponent();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        try {
            LocationHelper.INSTANCE.init();
            LocationHelper.INSTANCE.start();
            LocationHelper.INSTANCE.addListener(new LocationHelper.OnLocationListener() {
                @Override
                public void onLocationUpdate(Location location) {
                    originLocation = location;
                    if (isFirst) {
                        setCameraPosition(location.getLatitude(), location.getLongitude());
                        isFirst = false;
                    }
                    locationComponent.forceLocationUpdate(location);
                }
            });
            Location lastLocation = LocationHelper.INSTANCE.getLastLocation();
            if (lastLocation != null) {
                originLocation = lastLocation;
                setCameraPosition(lastLocation.getLatitude(), lastLocation.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCameraPosition(double lat, double lng) {
        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lat, lng), 13));
    }

    @Override
    public boolean onLocationClick() {
        if (originLocation == null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Msg.show("无法获取定位权限");
                return true;
            }
            originLocation = locationComponent.getLastKnownLocation();
        }
        if (originLocation == null) {
            Msg.show("无法获取定位");
        } else {
            setCameraPosition(originLocation.getLatitude(), originLocation.getLongitude());
        }
        return true;
    }
}
