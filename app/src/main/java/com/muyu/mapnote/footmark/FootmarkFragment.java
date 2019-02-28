package com.muyu.mapnote.footmark;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.okayapi.OkException;
import com.muyu.mapnote.app.okayapi.OkMoment;
import com.muyu.mapnote.app.okayapi.OkMomentItem;
import com.muyu.mapnote.app.okayapi.callback.MomentListCallback;
import com.muyu.mapnote.map.MapOptEvent;
import com.muyu.mapnote.map.map.MapSettings;
import com.muyu.mapnote.map.map.poi.PoiManager;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.muyu.mapnote.note.DetailActivity;
import com.muyu.minimalism.framework.app.BaseFragment;
import com.muyu.minimalism.utils.StringUtils;
import com.muyu.minimalism.view.recyclerview.CommonRecyclerAdapter;
import com.muyu.minimalism.view.recyclerview.CommonViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class FootmarkFragment extends BaseFragment implements OnMapReadyCallback {

    private FootmarkViewModel mViewModel;
    private View mLayout;
    private MapView mMapView;
    private MapboxMap mMap;
    private Marker mMarker;
    private int oldSelected = -1;
    private SwipeRefreshLayout mRefreshView;
    private TextView emptyView;
    public static FootmarkFragment newInstance() {
        return new FootmarkFragment();
    }
    private CommonRecyclerAdapter adapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_footmark, container, false);
        emptyView = mLayout.findViewById(R.id.foot_empty);
        return mLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FootmarkViewModel.class);
        mViewModel.getMyMoment().observe(this, new Observer<ArrayList<OkMomentItem>>() {
            @Override
            public void onChanged(@Nullable ArrayList<OkMomentItem> okMomentItems) {
                emptyView.setVisibility(okMomentItems.size() > 0 ? View.GONE : View.VISIBLE);

                adapter.setDataList(okMomentItems);

                if (mMap != null && !okMomentItems.isEmpty()) {
                    if (okMomentItems.size() > 1) {
                        PolylineOptions opt = new PolylineOptions();
                        List<OkMomentItem> list = mViewModel.getMyMoment().getValue();
                        for (OkMomentItem item : list) {
                            opt.add(LocationHelper.getChinaLatlng(item.moment_lat, item.moment_lng));
                        }
                        opt.color(FootmarkFragment.this.getActivity().getResources().getColor(R.color.colorPrimary));
                        opt.width(4);
                        mMap.addPolyline(opt);
                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .includes(opt.getPoints())
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                    } else {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(okMomentItems.get(0).moment_lat, okMomentItems.get(0).moment_lng), 13));
                    }
                    LatLng latlng = LocationHelper.getChinaLatlng(okMomentItems.get(0).moment_lat, okMomentItems.get(0).moment_lng);
                    mark(latlng);
                }
            }
        });


        RecyclerView listView = getView().findViewById(R.id.footmark_list);

        adapter = new CommonRecyclerAdapter<OkMomentItem>(getActivity(),
                mViewModel.getMyMoment().getValue(),
                R.layout.item_footmark) {

            @Override
            public void bindData(CommonViewHolder holder, OkMomentItem poi, int position) {
                View view = holder.itemView;
                view.setSelected(true);
                if (!StringUtils.isEmpty(poi.moment_picture1)) {
                    Glide.with(getActivity()).load(poi.moment_picture1).into((ImageView) view.findViewById(R.id.moment_pupup_iv));
                }
                TextView tv = view.findViewById(R.id.footmark_content);
                tv.setText(poi.moment_content);
                tv = view.findViewById(R.id.footmark_username);
                tv.setText(poi.moment_nickname);
                tv = view.findViewById(R.id.footmark_pupup_like);
                tv.setText(String.valueOf(poi.moment_like));

                view.findViewById(R.id.footmark_detail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DetailActivity.startDetailPage(FootmarkFragment.this.baseActivity(), String.valueOf(poi.id));
                        view.setSelected(true);
                        oldSelected = position;
                    }
                });
                holder.setCommonClickListener(new CommonViewHolder.onItemCommonClickListener() {
                    @Override
                    public void onItemClickListener(int position) {
                        LatLng latlng = LocationHelper.getChinaLatlng(poi.moment_lat, poi.moment_lng);
                        mark(latlng);
                        double zoom = mMap.getCameraPosition().zoom;
                        if (zoom < 12) {
                            zoom = 12;
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
                    }

                    @Override
                    public void onItemLongClickListener(int position) {

                    }
                });
            }
        };
        listView.setAdapter(adapter);

        mMapView = mLayout.findViewById(R.id.footmark_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        /** refresh */
        mRefreshView = mLayout.findViewById(R.id.footmark_refresh);
        mRefreshView.setColorSchemeColors(getResources().getColor(R.color.orange),
                                        getResources().getColor(R.color.colorPrimary),
                                        getResources().getColor(R.color.tomato));
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapOptEvent(MapOptEvent event) {
        switch (event.eventId) {
            case MapOptEvent.MAP_EVENT_DATA_UPDATE:
                update();
                break;
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mMap = mapboxMap;
        mMap.setMaxZoomPreference(15);
        MapSettings.initMapStyle(mapboxMap, mMapView, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

            }
        });
        update();
        mRefreshView.setRefreshing(true);
    }

    private void mark(LatLng latlng) {
        if (mMarker != null) {
            mMap.removeMarker(mMarker);
        }
        mMarker = PoiManager.createPoi(mMap, "", "", latlng, PoiManager.POI_TYPE_FOOTMARK);
    }

    public void update() {
        OkMoment.getMyMoment(new MomentListCallback() {
            @Override
            public void onSuccess(ArrayList<OkMomentItem> list) {
                mViewModel.getMyMoment().postValue(list);
                mRefreshView.setRefreshing(false);
            }

            @Override
            public void onFail(OkException e) {
                mRefreshView.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
