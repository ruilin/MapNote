package com.muyu.mapnote.map.navigation.route;

import android.app.Activity;
import android.util.Log;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.Logs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public enum RouteHelper {
    INSTANCE;

    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;

    public void getRoute(MapPluginController controller, Point origin, Point destination) {
        NavigationRoute route = NavigationRoute.builder(controller.getActivity())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build();

        route.getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Logs.d("Response code: " + response.code());
                if (response.body() == null) {
                    Logs.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Logs.e("No routes found");
                    return;
                }

                currentRoute = response.body().routes().get(0);

                // Draw the route on the map
                if (navigationMapRoute != null) {
                    navigationMapRoute.removeRoute();
                } else {
                    navigationMapRoute = new NavigationMapRoute(null, controller.getMapView(), controller.getMapboxMap(), R.style.NavigationMapRoute);
                }
                navigationMapRoute.addRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Logs.e("Error: " + throwable.getMessage());
            }
        });
        //navigationMapRoute.removeRoute();
    }

    public void startNavigation(BaseActivity activity) {
        boolean simulateRoute = false;  // 模拟导航
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(currentRoute)
                .shouldSimulateRoute(simulateRoute)
                .build();
        // Call this method with Context from within an Activity
        NavigationLauncher.startNavigation(activity, options);

    }
}
