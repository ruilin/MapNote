package com.muyu.mapnote.map.map.poi;

import android.content.Context;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.R;

public class PoiHelper {

    private void showPoi(Context context, MapboxMap map, LatLng point) {
        IconFactory iconFactory = IconFactory.getInstance(context);
        Icon icon = iconFactory.fromResource(R.drawable.yellow_marker);

        map.addMarker(new MarkerOptions()
                .position(new LatLng(52.5173,13.3889))
                .title("柏林")
                .snippet("德国")
                .icon(icon)
        );
    }
}
