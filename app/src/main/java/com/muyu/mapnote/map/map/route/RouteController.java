package com.muyu.mapnote.map.map.route;

import android.view.View;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.map.MapController;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.muyu.mapnote.note.PublishActivity;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.utils.Logs;
import com.muyu.minimalism.view.BottomMenu;
import com.muyu.minimalism.view.Msg;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteController extends MapPluginController implements View.OnClickListener {
    private static final byte ROUTE_TYPE_DRIVE = 0;
    private static final byte ROUTE_TYPE_CYCLE = 1;
    private static final byte ROUTE_TYPE_WALK = 2;

    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private NavigationRoute route;

    private View view;
    private View loading;
    @Override
    protected void onMapCreated(MapController map) {
        super.onMapCreated(map);

        View layout = map.getLayout();
        view = layout.findViewById(R.id.map_view_route);
        layout.findViewById(R.id.view_route_nav).setOnClickListener(this);
        layout.findViewById(R.id.view_route_cancel).setOnClickListener(this);
        loading = layout.findViewById(R.id.view_route_loading);
        loading.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
    }

    /**
     *
     * @param origin
     * @param destination
     * @param type route_type
     */
    public void route(Point origin, Point destination, byte type) {
        loading.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);

        String profile = DirectionsCriteria.PROFILE_DEFAULT_USER;
        switch (type) {
            case ROUTE_TYPE_DRIVE:
                profile = DirectionsCriteria.PROFILE_DRIVING;
                break;
            case ROUTE_TYPE_CYCLE:
                profile = DirectionsCriteria.PROFILE_CYCLING;
                break;
            case ROUTE_TYPE_WALK:
                profile = DirectionsCriteria.PROFILE_WALKING;
                break;
        }
        route = NavigationRoute.builder(getActivity())
                .accessToken(Mapbox.getAccessToken())
                .profile(profile)
                .origin(origin)
                .destination(destination)
                .build();

        route.getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                try {

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
                        navigationMapRoute = new NavigationMapRoute(null, getMapView(), getMapboxMap(), R.style.NavigationMapRoute);
                    }
                    navigationMapRoute.addRoute(currentRoute);

                    route = null;
                    loading.post(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    error();
                    return;
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Logs.e("Error: " + throwable.getMessage());
                error();
            }
        });
        //navigationMapRoute.removeRoute();
    }

    private void error() {
        route = null;

        loading.post(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                Msg.show("路线规划失败");
            }
        });
    }

    public void route(Point origin, Point destination) {
        if (LocationHelper.calculateLineDistance(origin.latitude(), origin.longitude(), destination.latitude(), destination.longitude()) > 3000000) {
            Msg.show("距离太远");
            return;
        }
        BottomMenu.show(getActivity(), new String[]{"驾车", "骑行", "步行"}, new BottomMenu.OnItemClickedListener() {
            @Override
            public void OnItemClicked(int position) {
                switch (position) {
                    case 0:
                        route(origin, destination, ROUTE_TYPE_DRIVE);
                        break;
                    case 1:
                        route(origin, destination, ROUTE_TYPE_CYCLE);
                        break;
                    case 2:
                        route(origin, destination, ROUTE_TYPE_WALK);
                        break;
                }
            }
        });
    }

    public void startNavigation() {
        boolean simulateRoute = false;  // 模拟导航
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(currentRoute)
                .shouldSimulateRoute(simulateRoute)
                .build();
        // Call this method with Context from within an Activity
        NavigationLauncher.startNavigation(getActivity(), options);

    }

    @Override
    public void onClick(View v) {
        if (loading.getVisibility() == View.VISIBLE) {
            return;
        }
        switch (v.getId()) {
            case R.id.view_route_nav:
                startNavigation();
                break;
            case R.id.view_route_cancel:
                if (navigationMapRoute != null) {
                    navigationMapRoute.removeRoute();
                }
                view.setVisibility(View.GONE);
                break;
        }
    }
}
