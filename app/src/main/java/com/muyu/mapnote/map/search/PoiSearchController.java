package com.muyu.mapnote.map.search;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.minimalism.framework.util.MLog;
import com.muyu.minimalism.framework.util.Msg;

public class PoiSearchController extends MapPluginController {
    private PlaceDetectionClient mPlaceDetectionClient;

    @Override
    protected void onMapCreated(MapboxMap map, MapView mapView) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity());
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                try {
                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        MLog.e(String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        Msg.showDebug("xxx" + placeLikelihood.getPlace().getName());
                    }
                    likelyPlaces.release();
                } catch (Exception e) {
                    MLog.e(e.toString());
                }
            }
        });
    }
}
