package com.muyu.mapnote.footmark;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.Styles;
import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkMoment;
import com.muyu.mapnote.app.okayapi.been.OkMomentItem;
import com.muyu.mapnote.app.okayapi.OkayApi;
import com.muyu.mapnote.app.okayapi.callback.CommonCallback;
import com.muyu.mapnote.app.okayapi.callback.MomentListCallback;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.mapnote.map.activity.MapActivity;
import com.muyu.mapnote.map.map.MapSettings;
import com.muyu.mapnote.map.map.poi.PoiManager;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.muyu.mapnote.note.DetailActivity;
import com.muyu.minimalism.framework.app.BaseApplication;
import com.muyu.minimalism.framework.app.BaseFragment;
import com.muyu.minimalism.utils.StringUtils;
import com.muyu.minimalism.utils.SysUtils;
import com.muyu.minimalism.view.DialogUtils;
import com.muyu.minimalism.view.Msg;
import com.muyu.minimalism.view.recyclerview.CommonRecyclerAdapter;
import com.muyu.minimalism.view.recyclerview.CommonViewHolder;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineDasharray;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineTranslate;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class FootmarkFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener, Style.OnStyleLoaded {

    private FootmarkViewModel mViewModel;
    private View mLayout;
    private MapView mMapView;
    private MapboxMap mMap;
    private SupportMapFragment mMapFragment;
    private Marker mMarker;
    private int oldSelected = -1;
    private SwipeRefreshLayout mRefreshView;
    private View emptyView;
    private CommonRecyclerAdapter adapter;
    private CameraUpdate mapCamera;
    private boolean isChange;
    private Bitmap shareBitmap;
    private FeatureCollection lineFeatureCollection;
    private LinkedList<Marker> mMarkerList = new LinkedList<>();
    private SymbolManager symbolManager;

    public static FootmarkFragment newInstance() {
        return new FootmarkFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_footmark, container, false);
        emptyView = mLayout.findViewById(R.id.foot_empty);

        EventBus.getDefault().register(this);
        return mLayout;
    }

    ArrayList<OkMomentItem> mOkMomentItems;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(FootmarkViewModel.class);
        mViewModel.getMyMoment().observe(this, new Observer<ArrayList<OkMomentItem>>() {
            @Override
            public void onChanged(@Nullable ArrayList<OkMomentItem> okMomentItems) {
                emptyView.setVisibility(okMomentItems.size() > 0 ? View.GONE : View.VISIBLE);
                mOkMomentItems = okMomentItems;
                adapter.setDataList(okMomentItems);

                updateMap();
            }
        });


        RecyclerView listView = getView().findViewById(R.id.footmark_list);

        adapter = new CommonRecyclerAdapter<OkMomentItem>(getActivity(),
                mViewModel.getMyMoment().getValue(),
                R.layout.item_footmark) {

            @Override
            public void bindData(CommonViewHolder holder, OkMomentItem poi, int position) {
                View view = holder.itemView;
                view.setSelected(true);
                ImageView imageView = view.findViewById(R.id.moment_pupup_iv);
                if (!StringUtils.isEmpty(poi.moment_picture1)) {
                    Glide.with(getActivity()).load(poi.moment_picture1).into(imageView);
                }
                TextView tv = view.findViewById(R.id.footmark_content);
                tv.setText(poi.moment_content);
                tv = view.findViewById(R.id.footmark_time);
                tv.setText(poi.moment_createtime);
                tv = view.findViewById(R.id.footmark_pupup_like);
                tv.setText(String.valueOf(poi.moment_like));

                view.findViewById(R.id.footmark_item_line_top).setVisibility(View.VISIBLE);
                view.findViewById(R.id.footmark_item_line_bottom).setVisibility(View.VISIBLE);

                if (position == 0) {
                    view.findViewById(R.id.footmark_item_line_top).setVisibility(View.INVISIBLE);
                }
                if (position == adapter.getItemCount() - 1) {
                    view.findViewById(R.id.footmark_item_line_bottom).setVisibility(View.INVISIBLE);
                }

                View.OnClickListener clickDetail = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DetailActivity.startDetailPage(FootmarkFragment.this.baseActivity(), poi);
                        view.setSelected(true);
                        oldSelected = position;
                    }
                };
                imageView.setOnClickListener(clickDetail);
                view.findViewById(R.id.footmark_detail).setOnClickListener(clickDetail);

                holder.setCommonClickListener(new CommonViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {
                        LatLng latlng = LocationHelper.getChinaLatlng(poi.moment_lat, poi.moment_lng);
                        mark(latlng);
                        double zoom = mMap.getCameraPosition().zoom;
                        if (zoom < 12) {
                            zoom = 12;
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
                    }

                    @Override
                    public void onItemLongClickListener(int position) {
                        DialogUtils.show(getContext(), "删除", "确定删除该条记录吗？", new DialogUtils.DialogCallback() {
                            @Override
                            public void onPositiveClick(DialogInterface dialog) {
                                OkMoment.postDelete(String.valueOf(poi.id), new CommonCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Msg.show("删除成功");
                                        MapOptEvent.updateMap();
                                        mRefreshView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mRefreshView.setRefreshing(true);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(OkException e) {
                                        SysUtils.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Msg.show(e.getMessage());
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };
        listView.setAdapter(adapter);


        mLayout.findViewById(R.id.footmark_map_reset).setOnClickListener(this);
        mLayout.findViewById(R.id.footmark_publish_btn).setOnClickListener(this);
        mLayout.findViewById(R.id.footmark_map_share).setOnClickListener(this);
        mLayout.findViewById(R.id.footmark_map_change).setOnClickListener(this);

        /** refresh */
        mRefreshView = mLayout.findViewById(R.id.footmark_refresh);
        Styles.refreshView(getActivity(), mRefreshView);
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //        MapboxMapOptions options = new MapboxMapOptions().textureMode(true);
//        mMapView = new MapView(getActivity(), options);
//        ((ViewGroup)mLayout.findViewById(R.id.footmark_map_contain)).addView(mMapView);

//        mMapView = mLayout.findViewById(R.id.footmark_map);
//        mMapView.onCreate(savedInstanceState);
//        mMapView.getMapAsync(this);

        initMap();
    }

    private void initMap() {
        if (mMapFragment == null ||
                getChildFragmentManager().findFragmentByTag("com.muyu.track") == null) {
            MapboxMapOptions options = new MapboxMapOptions();
            options.textureMode(true);
            options.maxZoomPreference(19);
            LatLng paris = new LatLng(39.9071567, 116.39158504);
            options.camera(new CameraPosition.Builder()
                    .target(paris)
                    .zoom(1)
                    .build());
            // Create map fragment
            mMapFragment = SupportMapFragment.newInstance(options);
            mMapFragment.getMapAsync(this);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.footmark_map_contain, mMapFragment, "com.muyu.track");
            transaction.commit();

        }
    }

    private void removeMap() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.remove(mMapFragment);
        transaction.commit();
        mMapFragment = null;
        mMap = null;
        mMapView = null;
    }

    /**
     * Set up a GeoJsonSource and LineLayer in order to show the directions route from the device location
     * to the place picker location
     */
    private void initDottedLineSourceAndLayer(@NonNull Style style) {
        lineFeatureCollection = FeatureCollection.fromFeatures(new Feature[] {});
        String layerId = "DIRECTIONS_LAYER_ID";
        String sourceId = "SOURCE_ID";
        style.removeLayer(layerId);
        style.removeSource(sourceId);
        style.addSource(new GeoJsonSource(sourceId, lineFeatureCollection));
        try {
            style.addLayerAbove(
                    new LineLayer(layerId, sourceId)
                            .withProperties(
                            lineWidth(2.0f),
                            lineColor(getResources().getColor(R.color.rose)),
                            lineTranslate(new Float[] {0f, 4f}),
                            lineDasharray(new Float[] {3.0f, 1.0f})
                    ),
            "country-label");
        } catch (Exception e) {
            style.addLayer(
                    new LineLayer(layerId, sourceId)
                            .withProperties(
                                    lineWidth(2.0f),
                                    lineColor(getResources().getColor(R.color.rose)),
                                    lineTranslate(new Float[] {0f, 4f}),
                                    lineDasharray(new Float[] {3.0f, 1.0f})
                            ));
        }
    }

    private void drawPolyline(final List<Point> points) {
        if (mMap != null) {
            mMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    initDottedLineSourceAndLayer(style);
                    List<Feature> directionsRouteFeatureList = new ArrayList<>();
                    directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(points)));
                    lineFeatureCollection = FeatureCollection.fromFeatures(directionsRouteFeatureList);
                    GeoJsonSource source = style.getSourceAs("SOURCE_ID");
                    if (source != null) {
                        source.setGeoJson(lineFeatureCollection);
                    }
                }
            });
        }
    }

    private synchronized void updateMap() {
        ArrayList<OkMomentItem> okMomentItems = mOkMomentItems;
        if (mMap == null) {
            return;
        }
        if (okMomentItems != null && !okMomentItems.isEmpty()) {
            if (okMomentItems.size() > 1) {
                /* 标记当前选择 */
                LatLng latlng = LocationHelper.getChinaLatlng(okMomentItems.get(0).moment_lat, okMomentItems.get(0).moment_lng);
                mark(latlng);

                /* 初始化地图视角 */
//                        PolylineOptions opt = new PolylineOptions();
                List<OkMomentItem> list = mViewModel.getMyMoment().getValue();
                ArrayList<Point> points = new ArrayList<>();
                ArrayList<LatLng> latLngs = new ArrayList<>();
                for (OkMomentItem item : list) {
                    LatLng latLng = LocationHelper.getChinaLatlng(item.moment_lat, item.moment_lng);
                    latLngs.add(latLng);
                    Point point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
                    points.add(point);
                }

                /* 显示效果 */

//                LatLng latLng = new LatLng(23.1337054, 113.3249672);
//                latLngs.add(latLng);
//                Point point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng(23.4387, 117.025);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng(25.0509, 113.7553);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng(27.5958, 114.1473);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng(28.2025, 112.9996);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng( 37.492, 127.0267);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng(34.2549, 108.9377);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng(34.3197, 108.7178);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng(25.6879, 100.1623);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng(16.8142, 100.2717);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng(13.736, 100.5582);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);
//
//                latLng = new LatLng(1.3471, 103.8438);
//                latLngs.add(latLng);
//                point = Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
//                points.add(point);




//                setMarkerLayer(points);
                drawPolyline(points);
                setSEMarkers(latLngs);
//                        for (OkMomentItem item : list) {
//                            opt.add(LocationHelper.getChinaLatlng(item.moment_lat, item.moment_lng));
//                        }
//                        opt.color(FootmarkFragment.this.getActivity().getResources().getColor(R.color.orangered));
//                        opt.width(3);
//                        mMap.addPolyline(opt);
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .includes(latLngs)
                        .build();
                mapCamera = CameraUpdateFactory.newLatLngBounds(bounds, 150);
                mMap.animateCamera(mapCamera);

            } else {
                mapCamera = CameraUpdateFactory.newLatLngZoom(new LatLng(okMomentItems.get(0).moment_lat, okMomentItems.get(0).moment_lng), 2);
                mMap.animateCamera(mapCamera);
            }
        } else {
            Location loc = LocationHelper.INSTANCE.getLastLocation();
            if (loc != null) {
                LatLng latLng = LocationHelper.getChinaLatlng(loc.getLatitude(), loc.getLongitude());
                mapCamera = CameraUpdateFactory.newLatLngZoom(latLng, 1);
                mMap.animateCamera(mapCamera);
                mark(latLng);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.footmark_map_reset:
                resetMapCamera();
                break;
            case R.id.footmark_publish_btn:
                ((MapActivity)getActivity()).toPublishActivity();
                break;
            case R.id.footmark_map_change:
                setMapStyle(false);
                isChange = !isChange;
                break;
            case R.id.footmark_map_share:
                /* 地图截屏分享 */
                ImageView snap = FootmarkFragment.this.mLayout.findViewById(R.id.footmark_snapshot);
                mMap.snapshot(new MapboxMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(@NonNull Bitmap bitmap) {
                        SysUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                shareBitmap = bitmap;
                                if (verifyStoragePermissions(getActivity())) {
                                    ShareDialog.showDialog(FootmarkFragment.this.getActivity(), bitmap);
                                }
                            }
                        });
                    }
                });
                break;
        }
    }

    private void setMapStyle(boolean isFirst) {
        if (isFirst) {
            mMap.setStyle(Style.OUTDOORS);
        } else if (isChange) {
            mMap.setStyle(Style.OUTDOORS, this);
        } else {
            mMap.setStyle(Style.SATELLITE_STREETS, this);
        }
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        updateMap();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapOptEvent(MapOptEvent event) {
        switch (event.eventId) {
            case MapOptEvent.MAP_EVENT_DATA_UPDATE:
                update();
                break;
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mMapView = (MapView) mMapFragment.getView();
        mMap = mapboxMap;
        mMap.setMaxZoomPreference(15);
        setMapStyle(true);
        MapSettings.initMapStyle(mapboxMap, mMapView, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

            }
        });
        mRefreshView.setRefreshing(true);
        update();
    }

//    private void setMarkerLayer(final List<Point> points) {
//        if (mMap != null && points.size() > 2) {
//            mMap.getStyle(new Style.OnStyleLoaded() {
//                @Override
//                public void onStyleLoaded(@NonNull Style style) {
//                    List<Feature> markerCoordinates = new ArrayList<>();
//                    for (int i = points.size() - 2; i > 0; --i) {
//                        markerCoordinates.add(Feature.fromGeometry(points.get(i)));
//                    }
//                    style.addSource(new GeoJsonSource("marker-source",
//                            FeatureCollection.fromFeatures(markerCoordinates)));
//
//                    // 添加资源图片到地图
//                    style.addImage("my-marker-image", BitmapFactory.decodeResource(
//                            FootmarkFragment.this.getResources(), R.mipmap.ic_foot_dot));
//
//                    // Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
//                    // middle of the icon being fixed to the coordinate point.
//                    style.addLayer(new SymbolLayer("marker-layer", "marker-source")
//                            .withProperties(PropertyFactory.iconImage("my-marker-image"),
//                                    iconOffset(new Float[]{0f, 0f}))
//                    );
//
//                    // Add the selected marker source and layer
//                    style.addSource(new GeoJsonSource("selected-marker"));
//
//                    // Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
//                    // middle of the icon being fixed to the coordinate point.
//                    style.addLayer(new SymbolLayer("selected-marker-layer", "selected-marker")
//                            .withProperties(PropertyFactory.iconImage("my-marker-image"),
//                                    iconOffset(new Float[]{0f, 0f})));
//                }
//            });
//        }
//    }

    private void mark(LatLng latlng) {
        if (mMarker == null) {
            mMarker = PoiManager.createPoi(mMap, "", "", latlng, PoiManager.POI_TYPE_FOOTMARK);
        } else {
            mMarker.setPosition(latlng);
        }
//        setSymbol(latlng);
    }

    private void setSEMarkers(List<LatLng> points) {
        for (Marker marker : mMarkerList) {
            mMap.removeMarker(marker);
        }
        mMarkerList.clear();
        if (points.size() >= 2) {
            IconFactory iconFactory = IconFactory.getInstance(BaseApplication.getInstance());
            Icon icon = iconFactory.fromResource(R.mipmap.ic_foot_end);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(points.get(0))
                    .icon(icon)
            );
            mMarkerList.add(marker);
            iconFactory = IconFactory.getInstance(BaseApplication.getInstance());
            icon = iconFactory.fromResource(R.mipmap.ic_foot_start);
            marker =mMap.addMarker(new MarkerOptions()
                    .position(points.get(points.size() - 1))
                    .icon(icon)
            );
            mMarkerList.add(marker);
        }
    }

    private Marker createMarker(LatLng point) {
        IconFactory iconFactory = IconFactory.getInstance(BaseApplication.getInstance());
        Icon icon = iconFactory.fromResource(R.mipmap.ic_foot_dot);
        return mMap.addMarker(new MarkerOptions()
                .position(point)
                .icon(icon)
        );
    }

    public void update() {
        if (!OkayApi.get().isLogined()) {
            mRefreshView.setRefreshing(false);
            return;
        }
        OkMoment.getMyMoment(new MomentListCallback() {
            @Override
            public void onSuccess(ArrayList<OkMomentItem> list) {
                SysUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mViewModel.getMyMoment().postValue(list);
                        mRefreshView.setRefreshing(false);
                        //Msg.show("数据已刷新");
                    }
                });
            }

            @Override
            public void onFail(OkException e) {
                SysUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshView.setRefreshing(false);
                        Msg.show("网络异常");
                    }
                });
            }
        });
    }

    private void resetMapCamera() {
        if (mapCamera != null) {
            mMap.animateCamera(mapCamera);
            if (mMarker != null) {
                mMap.removeMarker(mMarker);
                mMarker = null;
            }
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    public static boolean verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (shareBitmap != null) {
            ShareDialog.showDialog(this.getActivity(), shareBitmap);
            shareBitmap = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        mMapView.onResume();
        initMap();
    }

    @Override
    public void onStart() {
        super.onStart();
//        removeFromSuperview(mMapView);
//        ((ViewGroup)mLayout.findViewById(R.id.footmark_map_contain)).addView(mMapView);
//        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
//        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mMapView.onPause();
        removeMap();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        mMapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mMapView.onDestroy();
//        removeFromSuperview(mMapView);
        EventBus.getDefault().unregister(this);
    }

    private void removeFromSuperview(View view) {
        ViewParent parent = view.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        mMapView.onSaveInstanceState(outState);
    }
}
