package com.muyu.mapnote.map.search;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.controller.ActivityController;
import com.muyu.minimalism.utils.Msg;

public class GoogleSearchHelper extends ActivityController {

    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 99;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private GoogleApiClient mGoogleApiClient;
    private BaseActivity mActivity;
    private MapboxMap mMap;

    public GoogleSearchHelper(MapboxMap map) {
        mMap = map;
    }

    @Override
    public void onCreate(BaseActivity activity) {
        mActivity = activity;
    }

//    private void getLocationList() {
//        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        //创建GoogleAPIClient实例
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
//                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                        @Override
//                        public void onConnected(@Nullable Bundle bundle) {
//
//                        }
//
//                        @Override
//                        public void onConnectionSuspended(int i) {
//
//                        }
//                    })
//                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
//                        @Override
//                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//                        }
//                    })
//                    .addApi(Places.GEO_DATA_API)
//                    .addApi(Places.PLACE_DETECTION_API)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//
//    }

    public void toSearhMode() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                            .setFilter(mActivity.typeFilter)
                            .build(mActivity);
            mActivity.startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Msg.showDebug(e.toString());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Msg.showDebug(e.toString());
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == mActivity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(mActivity, data);
                Log.i("dd", "Place: " + place.getName());

                double lat = place.getLatLng().latitude;
                double lng = place.getLatLng().longitude;

                // Move map camera to the selected location
                CameraPosition newCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(lat, lng))
                        .zoom(14)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), 4000);

                Bitmap icon = BitmapFactory.decodeResource(
                        mActivity.getResources(), R.drawable.blue_marker);
                mMap.addImage(symbolIconId, icon);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(mActivity, data);
                // TODO: Handle the error.
                Log.i("ee", status.getStatusMessage());

            } else if (resultCode == mActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}
