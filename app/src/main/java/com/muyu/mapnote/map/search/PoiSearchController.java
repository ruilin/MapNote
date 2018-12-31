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
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.controller.SubController;
import com.muyu.minimalism.framework.util.Msg;

public class PoiSearchController extends SubController {
    private PlaceDetectionClient mPlaceDetectionClient;
    private BaseActivity mActivity;

    @Override
    public void onAttached(BaseActivity activity) {
        mActivity = activity;

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mPlaceDetectionClient = Places.getPlaceDetectionClient(mActivity);
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                try {
                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        Log.i("xxx", String.format("Place '%s' has likelihood: %g",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        Msg.showDebug("" + placeLikelihood.getPlace().getName());
                    }
                    likelyPlaces.release();
                } catch (Exception e) {
                    Msg.showDebug(e.toString());
                }
            }
        });
    }
}
