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
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.mapnote.map.map.MapController;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.mapnote.map.map.moment.MomentMarker;
import com.muyu.mapnote.map.map.moment.MomentPoi;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.muyu.mapnote.note.MomentPopupView;
import com.muyu.mapnote.note.PublishActivity;
import com.muyu.minimalism.utils.MathUtils;
import com.muyu.minimalism.view.BottomMenu;
import com.muyu.minimalism.view.Msg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class PoiController extends MapPluginController {
    private static final String TAG = "PoiController";

    @Override
    protected void onMapCreated(MapController map) {
        super.onMapCreated(map);
        EventBus.getDefault().register(this);
        init();
        FootManager.init(getMapboxMap());
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
                if (marker.getTitle().equals("标记")) {
                    routeView.setText("[点击编辑]");
                } else {
                    routeView.setText("[查看路线]");
                }
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
                if (marker.getTitle().equals("标记")) {
                    BottomMenu.show(PoiController.this.getActivity(), new String[]{"发布游记", "删除"}, new BottomMenu.OnItemClickedListener() {
                        @Override
                        public void OnItemClicked(int position) {
                            Location location = new Location("FootRecord");
                            location.setLatitude(marker.getPosition().getLatitude());
                            location.setLongitude(marker.getPosition().getLongitude());
                            switch (position) {
                                case 0:
                                    PublishActivity.startPublishPage(getActivity(), location);
                                    break;
                                case 1:
                                    FootManager.removeFootMarker(PoiController.this.getMapboxMap(), location);
                                    break;
                            }
                        }
                    });
                } else {
                    Location myLocation = LocationHelper.INSTANCE.getLastLocationCheckChina();
                    if (myLocation != null) {
                        LatLng myLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        Point start = Point.fromLngLat(myLatlng.getLongitude(), myLatlng.getLatitude());
                        Point destination = Point.fromLngLat(marker.getPosition().getLongitude(), marker.getPosition().getLatitude());
                        getMap().getRoute().route(start, destination);
                    }
                }
                return false;
            }
        });
//        markerViewManager = new MarkerViewManager(mMapView, mMap);
    }

    MomentPopupView dialog;

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        removeDialog();
        if (marker instanceof MomentMarker) {
            MomentPoi poi = ((MomentMarker) marker).getMomentPoi();
//            DetailActivity.startDetailPage(getActivity(), ((MomentMarker)marker).getMomentPoi().id);
            removeDialog();
            dialog = new MomentPopupView(this, poi);
            dialog.show(getActivity().getCurrentFocus());
            return false;
        }
        updateCamera(marker.getPosition());
        return super.onMarkerClick(marker);
    }

    private void updateCamera(LatLng latLng) {
        getMapboxMap().animateCamera(CameraUpdateFactory.newLatLng(
                latLng));
    }

    @Override
    public void onMapClick(@NonNull LatLng point, @NonNull PointF screenPoint) {
        MomentPoi poi = PoiManager.searchMomentByScreenPoint(getMapboxMap(), screenPoint);
        removeDialog();
        if (poi != null) {
            dialog = new MomentPopupView(this, poi);
            dialog.show(getActivity().getCurrentFocus());
            updateCamera(new LatLng(poi.lat, poi.lng));
        } else {
            super.onMapClick(point, screenPoint);
        }
    }

    public void removeDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onMapMoveStart(int reason) {
        super.onMapMoveStart(reason);
    }

    @Override
    public void onDetached() {
        super.onDetached();
        EventBus.getDefault().unregister(this);
    }

    public void addFootRecord(Location location) {
        if (FootManager.addFootMarker(getMapboxMap(), location)) {
            getMap().processLocation();
            Msg.show("添加成功");
        } else {
            Msg.show("这里已经添加过了");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapOptEvent(MapOptEvent event) {
        if (event.eventId == MapOptEvent.MAP_EVENT_REMOVE_FOOT) {
            FootManager.removeFootMarker(getMapboxMap(), (Location) event.object);
        }
    }
}
