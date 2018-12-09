package com.muyu.mapnote.map.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
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
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.muyu.mapnote.R;
import com.muyu.mapnote.framework.controller.ActivityController;
import com.muyu.mapnote.framework.controller.BaseActivity;
import com.muyu.mapnote.map.navigation.location.LocationHelper;

import java.util.List;

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

    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private MapView mapView;
    private View mLayout;
    private SupportMapFragment mMapFragment;

    private OnMapEventListener mListener;

    public MapController(OnMapEventListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onCreate(BaseActivity activity) {
        mActivity = activity;
        mLayout = mActivity.findViewById(R.id.map_content);
        permissionsManager = new PermissionsManager(this);
        if (!PermissionsManager.areLocationPermissionsGranted(mActivity)) {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(mActivity);
        }

        // 默认设置
        MapboxMapOptions options = new MapboxMapOptions();
        options.maxZoomPreference(19);
        options.styleUrl(Style.MAPBOX_STREETS);
        LatLng paris = new LatLng(52.5173,13.3889);
        options.camera(new CameraPosition.Builder()
                .target(paris)
                .zoom(2.2)
                .build());
        // Create map fragment
        mMapFragment = SupportMapFragment.newInstance(options);
        // Add map fragment to parent container
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.map_content, mMapFragment, "com.mapbox.map");
        transaction.commit();

        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapView = (MapView) mMapFragment.getView();
        initNavigation();
        initUi();
        enableLocationPlugin();
        mListener.onMapCreated(mapboxMap, mapView);
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

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(mActivity)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, mapboxMap);
            locationPlugin.setLocationLayerEnabled(true);
            locationPlugin.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(mActivity);
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
        if (!LocationHelper.isInChina(location.getLatitude(), location.getLongitude())) {
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

//            String json = data.getStringExtra(PlaceConstants.RETURNING_CARMEN_FEATURE);
//            try {
//
//                JsonObject jsonObj = (JsonObject) new JsonParser().parse(json);
//                JsonObject geoObj = jsonObj.getAsJsonObject("geometry");
//                if (geoObj != null) {
//                    JsonArray coorArr = geoObj.getAsJsonArray("coordinates");
//                    if (coorArr != null) {
//                        double[] newCoor = LocationHelper.checkChineseCoor((coorArr.get(1)).getAsDouble(), (coorArr.get(0)).getAsDouble());
//                        coorArr.set(1, new JsonPrimitive(newCoor[0]));
//                        coorArr.set(0, new JsonPrimitive(newCoor[1]));
//                    }
//                }
//                JsonArray centerObj = jsonObj.getAsJsonArray("center");
//                if (centerObj != null) {
//                    double[] newCoor = LocationHelper.checkChineseCoor((centerObj.get(1)).getAsDouble(), (centerObj.get(0)).getAsDouble());
//                    centerObj.set(1, new JsonPrimitive(newCoor[0]));
//                    centerObj.set(0, new JsonPrimitive(newCoor[1]));
//                }
//                json = jsonObj.toString();
//            } catch (JsonParseException e) {
//                e.printStackTrace();
//            }
//            CarmenFeature selectedCarmenFeature = CarmenFeature.fromJson(json);

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            double lat = ((Point) selectedCarmenFeature.geometry()).latitude();
            double lng = ((Point) selectedCarmenFeature.geometry()).longitude();
//            if (CoordinateConverter.isAMapDataAvailable(lat, lng)) {
//                Log.e("xxx", ">>>  " + lat + ",  " + lng);
//            }

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(
                    new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())});

            // Retrieve and update the source designated for showing a selected location's symbol layer icon
            GeoJsonSource source = mapboxMap.getSourceAs(geojsonSourceLayerId);
            if (source != null) {
                source.setGeoJson(featureCollection);
            }

            // Move map camera to the selected location
            CameraPosition newCameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng))
                    .zoom(14)
                    .build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), 4000);
        }
    }
}
