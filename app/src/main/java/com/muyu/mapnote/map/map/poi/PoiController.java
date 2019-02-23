package com.muyu.mapnote.map.map.poi;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.muyu.mapnote.R;
import com.muyu.mapnote.map.map.MapPluginController;
import com.muyu.mapnote.map.map.moment.MomentMarker;
import com.muyu.mapnote.map.map.moment.MomentPoi;
import com.muyu.mapnote.note.MomentPopupView;
import com.tencent.lbssearch.object.result.SearchResultObject;

import java.util.ArrayList;
import java.util.List;

public class PoiController extends MapPluginController {
    private MarkerViewManager markerViewManager;
    private MapboxMap mMap;
    private MapView mMapView;
    private ArrayList<Marker> searchResult = new ArrayList<>();

    @Override
    protected void onMapCreated(MapboxMap map, MapView mapView) {
        mMap = map;
        mMapView = mapView;
        init();

//        SearchHelper.searchPoiByKeyWord(getActivity(), "娱乐", new SearchHelper.OnSearchKeyWordCallback() {
//            @Override
//            public void onSearchKeyWordCallback(List<SearchResultObject.SearchResultData> result) {
//                showPoiList(result);
//            }
//        });
    }

    private void init() {
        mMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                View view = PoiController.this.getActivity().getLayoutInflater().inflate(R.layout.marker_info_search, null);
                ((TextView) view.findViewById(R.id.marker_info_search_title)).setText(marker.getTitle());
                ((TextView) view.findViewById(R.id.marker_info_search_content)).setText(marker.getSnippet());
                ImageView imgView = view.findViewById(R.id.marker_info_search_img);
                Icon icon = marker.getIcon();
                Bitmap bitmap = null;
                if (icon != null) {
//                    bitmap = icon.getBitmap();
                }
                if (bitmap != null) {
                    imgView.setImageBitmap(icon.getBitmap());
                    imgView.setVisibility(View.VISIBLE);
                } else {
                    imgView.setVisibility(View.GONE);
                }
                return view;
            }
        });

        markerViewManager = new MarkerViewManager(mMapView, mMap);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker instanceof MomentMarker) {
            MomentPoi poi = ((MomentMarker) marker).getMomentPoi();
//            DetailActivity.startDetailPage(getActivity(), ((MomentMarker)marker).getMomentPoi().id);
            new MomentPopupView(getActivity(), poi).show(getActivity().getCurrentFocus());
            return false;
        }
        return super.onMarkerClick(marker);
    }

    @Override
    public void onDetached() {
        super.onDetached();
        if (markerViewManager != null) {
            markerViewManager.onDestroy();
        }
    }

    private void showPoiList(List<SearchResultObject.SearchResultData> data) {
        if (searchResult != null) {
            for (Marker poi : searchResult)
                mMap.removeMarker(poi);
            searchResult.clear();
        }
        boolean isFirst = true;
        for (SearchResultObject.SearchResultData item : data) {
            byte poiType = PoiManager.POI_TYPE_SEARCH_OTHER;
            if (isFirst) {
                poiType = PoiManager.POI_TYPE_SEARCH_FIRST;
                isFirst = false;
            }
            Marker poi = PoiManager.createPoi(mMap, item.title, item.category, new LatLng(item.location.lat, item.location.lng), poiType);
            searchResult.add(poi);
        }
    }
}
