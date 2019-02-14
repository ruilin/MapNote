//package com.muyu.mapnote.map.search;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//
//import com.google.android.gms.location.places.PlaceDetectionClient;
//import com.google.android.gms.location.places.PlaceLikelihood;
//import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.mapbox.mapboxsdk.maps.MapView;
//import com.mapbox.mapboxsdk.maps.MapboxMap;
//import com.muyu.mapnote.map.map.MapPluginController;
//import com.muyu.minimalism.utils.Logs;
//
//public class PoiSearchController extends MapPluginController {
//    private final static int M_MAX_ENTRIES = 10;
//    private PlaceDetectionClient mPlaceDetectionClient;
//
//    @Override
//    protected void onMapCreated(MapboxMap map, MapView mapView) {
//
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity());
//        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
//        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                if (task.isSuccessful() && task.getResult() != null) {
//                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
//
//                    // Set the count, handling cases where less than 5 entries are returned.
//                    int count;
//                    if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
//                        count = likelyPlaces.getCount();
//                    } else {
//                        count = M_MAX_ENTRIES;
//                    }
//
//                    int i = 0;
//                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                        // Build a list of likely places to show the user.
//                        String mLikelyPlaceNames = (String) placeLikelihood.getPlace().getName();
//                        String mLikelyPlaceAddresses = (String) placeLikelihood.getPlace()
//                                .getAddress();
//                        String mLikelyPlaceAttributions = (String) placeLikelihood.getPlace()
//                                .getAttributions();
//                        LatLng mLikelyPlaceLatLngs = placeLikelihood.getPlace().getLatLng();
//                        Logs.d("Google place search: " + mLikelyPlaceNames + " : " + mLikelyPlaceAttributions + " : " + mLikelyPlaceAddresses);
//                        i++;
//                        if (i > (count - 1)) {
//                            break;
//                        }
//                    }
//                    likelyPlaces.release();
//                } else {
//                    Logs.e("Google place search error: " + task.getException());
//                }
//            }
//        });
//    }
//}
