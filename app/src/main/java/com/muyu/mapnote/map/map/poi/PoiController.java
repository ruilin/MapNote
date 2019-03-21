package com.muyu.mapnote.map.map.poi;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.map.MapController;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.mapnote.map.map.moment.MomentMarker;
import com.muyu.mapnote.map.map.moment.MomentPoi;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.muyu.mapnote.note.MomentPopupView;

import java.util.ArrayList;

public class PoiController extends MapPluginController {
    private static final String TAG = "PoiController";

    @Override
    protected void onMapCreated(MapController map) {
        super.onMapCreated(map);
        init();
    }

    private void init() {
        getMapboxMap().setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                if (marker instanceof MomentMarker) {

                }
                View view = PoiController.this.getActivity().getLayoutInflater().inflate(R.layout.marker_info_search, null);
                ((TextView) view.findViewById(R.id.marker_info_search_title)).setText(marker.getTitle());
                ((TextView) view.findViewById(R.id.marker_info_search_content)).setText(marker.getSnippet());
                TextView routeView = view.findViewById(R.id.marker_info_route);
                routeView.setText("[查看路线]");
                routeView.setVisibility(View.VISIBLE);

                ImageView imgView = view.findViewById(R.id.marker_info_search_img);
                Icon icon = marker.getIcon();
                Bitmap bitmap = null;
                if (icon != null) {
//                    bitmap = icon.getBitmap();
                }
                if (bitmap != null) {
                    imgView.setImageBitmap(icon.getBitmap());
                    imgView.setVisibility(View.VISIBLE);
                } else {
                    imgView.setVisibility(View.GONE);
                }
                return view;
            }
        });
        getMapboxMap().setOnInfoWindowClickListener(new MapboxMap.OnInfoWindowClickListener() {
            @Override
            public boolean onInfoWindowClick(@NonNull Marker marker) {

                Location myLocation = LocationHelper.INSTANCE.getLastLocationCheckChina();
                if (myLocation != null) {
                    LatLng myLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    Point start = Point.fromLngLat(myLatlng.getLongitude(), myLatlng.getLatitude());
                    Point destination = Point.fromLngLat(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
                    getMap().getRoute().route(start, destination);
                }
                return false;
            }
        });
//        markerViewManager = new MarkerViewManager(mMapView, mMap);
    }

    MomentPopupView dialog;

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker instanceof MomentMarker) {
            MomentPoi poi = ((MomentMarker) marker).getMomentPoi();
//            DetailActivity.startDetailPage(getActivity(), ((MomentMarker)marker).getMomentPoi().id);
            if (dialog != null) {
                dialog.dismiss();
            }
            dialog = new MomentPopupView(this, poi);
            dialog.show(getActivity().getCurrentFocus());
            return false;
        }
        return super.onMarkerClick(marker);
    }

    @Override
    public void onMapClick(@NonNull LatLng point, @NonNull PointF screenPoint) {
        MomentPoi poi = PoiManager.searchMomentByScreenPoint(getMapboxMap(), screenPoint);
        if (poi != null) {
            if (dialog != null) {
                dialog.dismiss();
            }
            dialog = new MomentPopupView(this, poi);
            dialog.show(getActivity().getCurrentFocus());
        } else {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            super.onMapClick(point, screenPoint);
        }
    }

    @Override
    public void onMapMoveStart(int reason) {
        super.onMapMoveStart(reason);
    }

    @Override
    public void onDetached() {
        super.onDetached();
    }

}
