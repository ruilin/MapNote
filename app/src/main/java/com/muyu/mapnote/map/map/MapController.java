package com.muyu.mapnote.map.map;

import android.graphics.PointF;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.plugins.localization.MapLocale;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.map.location.LocationController;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.muyu.mapnote.map.search.PoiSearchController;
import com.muyu.mapnote.map.map.poi.MapSearchController;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.controller.ActivityController;
import com.muyu.minimalism.framework.controller.SubController;
import com.muyu.minimalism.util.MLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;

public class MapController extends ActivityController implements PermissionsListener, OnMapReadyCallback {
    private static final String TAG = MapController.class.getSimpleName();
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private FragmentActivity mActivity;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LocationLayerPlugin locationPlugin;
    private Location originLocation;
    boolean isFirst = true;

    private Marker destinationMarker;
    private LatLng destinationCoord;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;

    private View mLayout;
    private MapView mapView;
    private SupportMapFragment mMapFragment;

    private OnMapEventListener mListener;

    /* 插件 */
    private PoiSearchController mPoiSearchController = new PoiSearchController();
    private MapSearchController mMapSearchController = new MapSearchController();
    private LocationController mLocationController = new LocationController();

    public MapController(OnMapEventListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onCreate(BaseActivity activity) {
        mActivity = activity;
        mLayout = mActivity.findViewById(R.id.map_content);

        addController(activity, mPoiSearchController);
        addController(activity, mMapSearchController);
        addController(activity, mLocationController);

        // 默认设置
        MapboxMapOptions options = new MapboxMapOptions();
        options.maxZoomPreference(19);
        options.styleUrl(Style.MAPBOX_STREETS);
        LatLng paris = new LatLng(52.5173,13.3889);
        options.camera(new CameraPosition.Builder()
                .target(paris)
                .zoom(2.4)
                .build());
        // Create map fragment
        mMapFragment = SupportMapFragment.newInstance(options);
        // Add map fragment to parent container
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.map_content, mMapFragment, "com.muyu.map");
        transaction.commit();

        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onDestroy() {
        LocationHelper.INSTANCE.stop();
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        ArrayList<SubController> pluginList = MapController.this.getSubControllers();
        for (SubController plugin : pluginList) {
            if (plugin instanceof MapPluginController) {
                ((MapPluginController) plugin).onRequestPermissionsResult(granted);
            }
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapView = (MapView) mMapFragment.getView();
        initNavigation();
        initUi();
//        enableLocationPlugin();

        mListener.onMapCreated(mapboxMap, mapView);

        for (SubController controller : getSubControllers()) {
            ((MapPluginController) controller).onMapCreated(mapboxMap, mapView);
        }

        permissionsManager = new PermissionsManager(this);
        if (!PermissionsManager.areLocationPermissionsGranted(mActivity)) {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(mActivity);
        } else {
            onPermissionResult(true);
        }
    }

    private void initUi() {
        // 本地化
        try {
            LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, mapboxMap);
//            localizationPlugin.matchMapLanguageWithDeviceDefault();
            localizationPlugin.setMapLanguage(MapLocale.SIMPLIFIED_CHINESE);
            // 镜头转移到所在国家
            // localizationPlugin.setCameraToLocaleCountry();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }


        // UI设置
        UiSettings uiSettings = mapboxMap.getUiSettings();
        uiSettings.setCompassEnabled(true);             //指南针
        uiSettings.setTiltGesturesEnabled(false);        //设置是否可以调整地图倾斜角
        uiSettings.setRotateGesturesEnabled(false);     //设置是否可以旋转地图
        uiSettings.setAttributionEnabled(false);        //设置是否显示那个提示按钮
        uiSettings.setLogoEnabled(false);               //隐藏logo

        Layer mapText = mapboxMap.getLayer("country-label-lg");
        if (mapText != null) {
            mapText.setProperties(textField("{name_zh}"));
        }

        /**
         * Setting Map Events
         */

        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                final PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);
                ArrayList<SubController> pluginList = MapController.this.getSubControllers();
                for (SubController plugin : pluginList) {
                    if (plugin instanceof MapPluginController) {
                        ((MapPluginController) plugin).onMapClick(point, screenPoint);
                    }
                }
            }
        });

        mapboxMap.addOnCameraMoveStartedListener(new MapboxMap.OnCameraMoveStartedListener() {

            private final String[] REASONS = {"REASON_API_GESTURE", "REASON_DEVELOPER_ANIMATION", "REASON_API_ANIMATION"};

            @Override
            public void onCameraMoveStarted(int reason) {
                String string = String.format(Locale.US, "OnCameraMoveStarted: %s", REASONS[reason - 1]);
                MLog.d(string);
            }
        });

        mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                MLog.d("onCameraMove");
            }
        });

        mapboxMap.addOnCameraMoveCancelListener(new MapboxMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                MLog.d("onCameraMoveCanceled");
            }
        });

        mapboxMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                MLog.d("onCameraIdle");
            }
        });

        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                ArrayList<SubController> pluginList = MapController.this.getSubControllers();
                for (SubController plugin : pluginList) {
                    if (plugin instanceof MapPluginController) {
                        ((MapPluginController) plugin).onMarkerClick(marker);
                    }
                }
                return false;
            }
        });
    }

    private void initNavigation() {
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                if (destinationMarker != null) {
                    mapboxMap.removeMarker(destinationMarker);
                }
                destinationCoord = point;
                destinationMarker = mapboxMap.addMarker(new MarkerOptions()
                        .position(destinationCoord)
                );

                if (originLocation != null) {
                    LatLng originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
                    Point destinationPosition = Point.fromLngLat(destinationCoord.getLongitude(), destinationCoord.getLatitude());
                    Point originPosition = Point.fromLngLat(originCoord.getLongitude(), originCoord.getLatitude());
                    getRoute(originPosition, destinationPosition);
                }
            }
        });


        Button btn = mLayout.findViewById(R.id.main_bt_nav);
        if (btn != null) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean simulateRoute = false;  // 模拟导航
                    NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                            .directionsRoute(currentRoute)
                            .shouldSimulateRoute(simulateRoute)
                            .build();

                    // Call this method with Context from within an Activity
                    NavigationLauncher.startNavigation(mActivity, options);
                }
            });
        }
    }

    public void processLocation() {
        ArrayList<SubController> pluginList = MapController.this.getSubControllers();
        for (SubController plugin : pluginList) {
            if (plugin instanceof MapPluginController) {
                if (((MapPluginController) plugin).onLocationClick())
                    return;
            }
        }
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(mActivity)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);
                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
