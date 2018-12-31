package com.muyu.mapnote.map.poi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.MapApplication;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.minimalism.framework.app.BaseActivity;

public class PoiController extends MapPluginController {
    private MarkerViewManager markerViewManager;


    @Override
    protected void onMapCreated(MapboxMap map, MapView mapView) {

        map.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                View view = PoiController.this.getActivity().getLayoutInflater().inflate(R.layout.marker_info_search, null);
                ((TextView) view.findViewById(R.id.marker_info_search_title)).setText(marker.getTitle());
                ((TextView) view.findViewById(R.id.marker_info_search_content)).setText(marker.getSnippet());
                ImageView imgView = view.findViewById(R.id.marker_info_search_img);
                Icon icon = marker.getIcon();
                if (icon != null) {
                    imgView.setImageBitmap(icon.getBitmap());
                    imgView.setVisibility(View.VISIBLE);
                } else {
                    imgView.setVisibility(View.GONE);
                }
                return view;
            }
        });

        markerViewManager = new MarkerViewManager(mapView, map);
//        ImageView iv = new ImageView(MapApplication.getInstance());
//        iv.setImageResource(R.drawable.mapbox_ic_arrow_back);
//        MarkerView markerView = new MarkerView(new LatLng(52.5173, 13.3889), iv);
//        markerViewManager.addMarker(markerView);


        IconFactory iconFactory = IconFactory.getInstance(getActivity());
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
