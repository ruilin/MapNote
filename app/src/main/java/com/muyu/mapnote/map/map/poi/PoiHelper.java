package com.muyu.mapnote.map.map.poi;

import android.content.Context;
import android.location.Location;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.R;
import com.muyu.minimalism.framework.app.BaseApplication;
import com.muyu.minimalism.view.Msg;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;

public class PoiHelper {

    public final static byte POI_TYPE_TARGET = 0;
    public final static byte POI_TYPE_SEARCH_FIRST = 1;
    public final static byte POI_TYPE_SEARCH_OTHER = 2;

    public static Marker showPoi(MapboxMap map, String title, String snippet, LatLng point, byte poiType) {
        IconFactory iconFactory = IconFactory.getInstance(BaseApplication.getInstance());
        Icon icon = iconFactory.fromResource(R.drawable.yellow_marker);
        switch (poiType) {
            case POI_TYPE_TARGET:
                icon = iconFactory.fromResource(R.drawable.map_default_map_marker);
                break;
            case POI_TYPE_SEARCH_FIRST:
                icon = iconFactory.fromResource(R.drawable.yellow_marker);
                break;
            case POI_TYPE_SEARCH_OTHER:
                icon = iconFactory.fromResource(R.drawable.blue_marker);
                break;
        }
        return map.addMarker(new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(snippet)
                .icon(icon)
        );
    }

    public static void getLocationInfo(Context context, Location location, final OnLocationInfo callback) {
        if (callback == null) {
            return;
        }
        TencentSearch tencentSearch = new TencentSearch(context);
        //用户还可以通过Geo2AddressParam.coord_type(CoordTypeEnum type)
        //这个方法指定传入的坐标类型以获取正确的结果
        //此接口支持的坐标类型请参考接口文档
        Geo2AddressParam param = new Geo2AddressParam().location(new com.tencent.lbssearch.object.Location()
                .lat((float) location.getLatitude()).lng((float) location.getLongitude()));
        //设置此参数可以返回坐标附近的POI，默认为false,不返回
        param.get_poi(true);
        tencentSearch.geo2address(param, new HttpResponseListener() {
            @Override
            public void onSuccess(int i, BaseObject baseObject) {
                if (baseObject.isStatusOk() && baseObject instanceof Geo2AddressResultObject) {
                    Geo2AddressResultObject result = (Geo2AddressResultObject) baseObject;
                    callback.onLocationInfoCallback(result.result.formatted_addresses.recommend);
                }
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }
        });
    }

    public interface OnLocationInfo {
        void onLocationInfoCallback(String info);
    }
}
