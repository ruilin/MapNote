package com.muyu.mapnote.map.map.poi;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.view.Msg;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.result.SearchResultObject;

import java.util.ArrayList;
import java.util.List;

public class MapSearchController extends MapPluginController {
    private MarkerViewManager markerViewManager;
    private MapSearchProvider mProvider;
    private MapboxMap mMap;
    private MapView mMapView;
    private ArrayList<Marker> searchResult = new ArrayList<>();

    @Override
    protected void onMapCreated(MapboxMap map, MapView mapView) {
        mMap = map;
        mMapView = mapView;
        init();
        searchPoi(getActivity(), "娱乐");
    }

    private void init() {
        mMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                View view = MapSearchController.this.getActivity().getLayoutInflater().inflate(R.layout.marker_info_search, null);
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
        Msg.showDebug(marker.getTitle());
        return super.onMarkerClick(marker);
    }

    /**
     * poi检索
     */
    public void searchPoi(BaseActivity activity, String keyWord) {
        TencentSearch tencentSearch = new TencentSearch(activity);
        //城市搜索
        SearchParam.Region region = new SearchParam.Region()
                .poi("广州")//设置搜索城市
                .autoExtend(true);//设置搜索范围不扩大
        //圆形范围搜索
        Location location1 = new Location().lat(39.984154f).lng(116.307490f);
        SearchParam.Nearby nearBy = new SearchParam.Nearby().point(location1).r(1000);
        //矩形搜索，这里的范围是故宫
        Location location2 = new Location().lat(39.913127f).lng(116.392164f);
        Location location3 = new Location().lat(39.923034f).lng(116.402078f);
        SearchParam.Rectangle rectangle = new SearchParam.Rectangle().point(location2, location3);

        //filter()方法可以设置过滤类别，
        //search接口还提供了排序方式、返回条目数、返回页码具体用法见文档，
        //同时也可以参考官网的webservice对应接口的说明
        SearchParam searchParam = new SearchParam().keyword(keyWord).boundary(region);
        tencentSearch.search(searchParam, new HttpResponseListener() {

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                Log.e("xxx", arg1);
                arg2.printStackTrace();
            }

            @Override
            public void onSuccess(int arg0, BaseObject arg1) {
                // TODO Auto-generated method stub
                if (arg1 == null) {
                    return;
                }
                SearchResultObject obj = (SearchResultObject) arg1;
                if(obj.data == null) {
                    return;
                }
                String result = "搜索poi\n";
                for(SearchResultObject.SearchResultData data : obj.data) {
                    Log.v("SearchDemo","title:"+data.title + ";" + data.address);
                    result += data.title + " category:" + data.category + " type:" + data.type +"\n";
                }
                showPoiList(obj.data);
                Log.e("xxx", result);
            }
        });
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
            byte poiType = PoiHelper.POI_TYPE_SEARCH_OTHER;
            if (isFirst) {
                poiType = PoiHelper.POI_TYPE_SEARCH_FIRST;
                isFirst = false;
            }
            Marker poi = PoiHelper.showPoi(mMap, item.title, item.category, new LatLng(item.location.lat, item.location.lng), poiType);
            searchResult.add(poi);
        }
    }
}
