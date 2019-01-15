package com.muyu.mapnote.map.map.location;

import android.location.Location;
import android.support.annotation.NonNull;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.mapnote.map.navigation.location.LocationHelper;

import java.util.List;

public class LocationController extends MapPluginController {
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private Location originLocation;
    boolean isFirst = true;

    @Override
    protected void onMapCreated(MapboxMap map, MapView mapView) {
        super.onMapCreated(map, mapView);
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Create an instance of LOST location engine
        initializeLocationEngine();

        locationPlugin = new LocationLayerPlugin(getMapView(), getMap());
        locationPlugin.setLocationLayerEnabled(true);
        locationPlugin.setRenderMode(RenderMode.COMPASS);
    }

    @Override
    public void onRequestPermissionsResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        try {
            LocationHelper.INSTANCE.init();
            LocationHelper.INSTANCE.start();
            LocationHelper.INSTANCE.addListener(new LocationHelper.OnLocationListener() {
                @Override
                public void onLocationUpdate(Location location) {
                    originLocation = location;
                    if (isFirst) {
                        setCameraPosition(location);
                        isFirst = false;
                    }
                    locationPlugin.forceLocationUpdate(location);
                }
            });
            Location lastLocation = LocationHelper.INSTANCE.getLastLocation();
            if (lastLocation != null) {
                originLocation = lastLocation;
                setCameraPosition(lastLocation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCameraPosition(Location location) {
//        if (!LocationHelper.isInChina(location.getLatitude(), location.getLongitude())) {
            getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));
//        }
    }

    @Override
    public boolean onLocationClick() {
        setCameraPosition(originLocation);
        return true;
    }
}
