package com.muyu.mapnote.map.activity;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.plugins.localization.MapLocale;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;

public class MainActivity extends AppCompatActivity implements PermissionsListener, LocationHelper.OnLocationListener {
    private String TAG = MainActivity.class.getSimpleName();
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private LocationLayerPlugin locationPlugin;
    private Location originLocation;
    boolean isFirst = true;

    private Marker destinationMarker;
    private LatLng destinationCoord;
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_main);
        isFirst = true;

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            MainActivity.this.mapboxMap = mapboxMap;
            initNavigation();
            initUi();
            enableLocationPlugin();
        });

        permissionsManager = new PermissionsManager(this);
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }

    private void initUi() {
        // 本地化
        LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, mapboxMap);
        localizationPlugin.matchMapLanguageWithDeviceDefault();
        localizationPlugin.setMapLanguage(new MapLocale(MapLocale.SIMPLIFIED_CHINESE));
        localizationPlugin.setCameraToLocaleCountry();

        // UI设置
        UiSettings uiSettings = mapboxMap.getUiSettings();
        uiSettings.setCompassEnabled(true);             //指南针
        uiSettings.setTiltGesturesEnabled(true);        //设置是否可以调整地图倾斜角
        uiSettings.setRotateGesturesEnabled(true);      //设置是否可以旋转地图
        uiSettings.setAttributionEnabled(false);        //设置是否显示那个提示按钮
        uiSettings.setLogoEnabled(false);               //隐藏logo

        Layer mapText = mapboxMap.getLayer("country-label-lg");
        if (mapText != null) {
            mapText.setProperties(textField("{name_zh}"));
        }
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


        findViewById(R.id.main_bt_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean simulateRoute = false;  // 模拟导航
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build();

                // Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(MainActivity.this, options);
            }
        });
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
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

//    private void enableLocationPlugin() {
//        // Check if permissions are enabled and if not request
//        if (PermissionsManager.areLocationPermissionsGranted(this)) {
//            // Create an instance of LOST location engine
//            //获取定位引擎并激活
//            locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
//            locationEngine.activate();
////            mapboxMap.setMyLocationEnabled(true);
////            mapboxMap.setOnMyLocationChangeListener(new MapboxMap.OnMyLocationChangeListener() {
////                @Override
////                public void onMyLocationChange(@Nullable Location location) {
////                    if (isFirst) {
////                        isFirst = false;
////                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 15), 1000);
////                    }
////                }
////            });
////            MyLocationViewSettings locationSettings = mapboxMap.getMyLocationViewSettings();
//////            locationSettings.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.positioning), new int[]{20, 20, 20, 20});
//////            locationSettings.setForegroundTintColor(ContextCompat.getColor(this, R.color.colorPrimary));//设置定位图标中心圆的颜色
//////            locationSettings.setAccuracyTintColor(ContextCompat.getColor(this,R.color.colorAccent));//设置定位图标范围框的颜色
////            locationSettings.setAccuracyAlpha(30);//设置范围框的透明度
//////            locationSettings.setTilt(30);//设置倾斜角
////
////            TrackingSettings trackingSettings = mapboxMap.getTrackingSettings();
////            // 让地图始终以定位点为中心，无法滑动
////            trackingSettings.setDismissAllTrackingOnGesture(false);
////            // 启用位置和方位跟踪
////            trackingSettings.setMyLocationTrackingMode(MyLocationTracking.TRACKING_NONE);
////            trackingSettings.setMyBearingTrackingMode(MyBearingTracking.COMPASS);
////
////
////
//            UiSettings uiSettings = mapboxMap.getUiSettings();
//////            uiSettings.setCompassEnabled(false);//隐藏指南针
//////            uiSettings.setLogoEnabled(false);//隐藏logo
//////            uiSettings.setTiltGesturesEnabled(true);//设置是否可以调整地图倾斜角
//////            uiSettings.setRotateGesturesEnabled(true);//设置是否可以旋转地图
//////            uiSettings.setAttributionEnabled(false);//设置是否显示那个提示按钮
//
//            enableLocation(true);
//        } else {
//            permissionsManager = new PermissionsManager(this);
//            permissionsManager.requestLocationPermissions(this);
//        }
//    }

    //设置定位
//    private void enableLocation(boolean enable) {
//        if (enable) {
//            LocationEngineListener locationEngineListener = new LocationEngineListener() {
//                @Override
//                public void onConnected() {
//                    //连接到定位服务，不需要操作
//                }
//
//                @Override
//                public void onLocationChanged(Location location) {
//                    if (location != null) {
//                        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 15), 1000);
//                        locationEngine.removeLocationEngineListener(this);
//                        Log.e("xxx", "xxx> " + location.getLatitude() + " " + location.getLongitude());
//                    }
//
//                }
//            };
//            //设置监听器
//            locationEngine.addLocationEngineListener(locationEngineListener);
//        } else {
//        }
//        //添加或移除定位图层
////        mapboxMap.setMyLocationEnabled(enable);
//    }


    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, mapboxMap);
            locationPlugin.setLocationLayerEnabled(true);
            locationPlugin.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        LocationHelper.INSTANCE.init();
        LocationHelper.INSTANCE.start();
        LocationHelper.INSTANCE.addListener(this);
        Location lastLocation = LocationHelper.INSTANCE.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        }
    }

    private void setCameraPosition(Location location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            Toast.makeText(this, "定位授权失败.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        LocationHelper.INSTANCE.start();
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationHelper.INSTANCE.stop();
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onLocationUpdate(Location location) {
        originLocation = location;
        if (isFirst) {
            setCameraPosition(location);
            isFirst = false;
        }
        locationPlugin.forceLocationUpdate(location);
    }
}
