package com.muyu.mapnote.map.search;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.google.gson.JsonObject;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.controller.ActivityController;

public class SearchPlaceController extends ActivityController {
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private BaseActivity mActivity;
    private MapboxMap mMap;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";

    public SearchPlaceController(MapboxMap map) {
        mMap = map;
    }

    @Override
    public void onCreate(BaseActivity activity) {
        mActivity = activity;
        initSearch();
    }

    private void initSearch() {
        addUserLocations();
        // Add the symbol layer icon to map for future use
        Bitmap icon = BitmapFactory.decodeResource(
                mActivity.getResources(), R.drawable.blue_marker);
        mMap.addImage(symbolIconId, icon);

        // Create an empty GeoJSON source using the empty feature collection
        setUpSource();

        // Set up a new symbol layer for displaying the searched location's feature coordinates
        setupLayer();
    }

    public CarmenFeature home;
    public CarmenFeature work;
    private void addUserLocations() {
        home = CarmenFeature.builder().text("Mapbox SF Office")
                .geometry(Point.fromLngLat(-122.399854, 37.7884400))
                .placeName("85 2nd St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Mapbox DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    public void toSearhMode() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken())
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .addInjectedFeature(home)
                        .addInjectedFeature(work)
                        .build(PlaceOptions.MODE_CARDS))
                .build(mActivity);
        mActivity.startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }

    private void setUpSource() {
        GeoJsonSource geoJsonSource = new GeoJsonSource(geojsonSourceLayerId);
        mMap.addSource(geoJsonSource);
    }

    private void setupLayer() {
        SymbolLayer selectedLocationSymbolLayer = new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId);
        selectedLocationSymbolLayer.withProperties(PropertyFactory.iconImage(symbolIconId));
        mMap.addLayer(selectedLocationSymbolLayer);
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
            GeoJsonSource source = mMap.getSourceAs(geojsonSourceLayerId);
            if (source != null) {
                source.setGeoJson(featureCollection);
            }

            // Move map camera to the selected location
            CameraPosition newCameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lng))
                    .zoom(14)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), 4000);
        }
    }
}
