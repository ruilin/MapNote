package com.muyu.mapnote.map.poi;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.minimalism.framework.app.BaseActivity;

public class PoiController extends MapPluginController {
    private MarkerViewManager markerViewManager;
    BaseActivity activity;
    @Override
    public void onAttached(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onMapCreated(MapboxMap map, MapView mapView) {
        markerViewManager = new MarkerViewManager(mapView, map);
//        ImageView iv = new ImageView(MapApplication.getInstance());
//        iv.setImageResource(R.drawable.mapbox_ic_arrow_back);
//        MarkerView markerView = new MarkerView(new LatLng(52.5173, 13.3889), iv);
//        markerViewManager.addMarker(markerView);


        IconFactory iconFactory = IconFactory.getInstance(activity);
        Icon icon = iconFactory.fromResource(R.drawable.yellow_marker);

        map.addMarker(new MarkerOptions()
                .position(new LatLng(52.5173,13.3889))
                .title("柏林")
                .snippet("德国")
                .icon(icon)
        );
    }

    @Override
    public void onDetached() {
        if (markerViewManager != null) {
            markerViewManager.onDestroy();
        }
    }
}
