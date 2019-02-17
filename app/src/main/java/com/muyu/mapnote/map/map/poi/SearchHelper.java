package com.muyu.mapnote.map.map.poi;

import android.content.Context;
import android.util.Log;

import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.lbssearch.object.result.SearchResultObject;

import java.util.List;

public class SearchHelper {

    /**
     * 通过关键字检索poi
     */
    public static void searchPoiByKeyWord(BaseActivity activity, String keyWord, final OnSearchKeyWordCallback callback) {
        TencentSearch tencentSearch = new TencentSearch(activity);
        SearchParam searchParam;

        android.location.Location gps = LocationHelper.INSTANCE.getLastLocation();
        if (gps != null) {
            //圆形范围搜索
            Location location1 = new Location().lat((float) gps.getLatitude()).lng((float) gps.getLongitude());
            SearchParam.Nearby nearBy = new SearchParam.Nearby()
                    .point(location1)
                    .r(100000); // 单位：米
            //矩形搜索，这里的范围是故宫
            Location location2 = new Location().lat(39.913127f).lng(116.392164f);
            Location location3 = new Location().lat(39.923034f).lng(116.402078f);
            SearchParam.Rectangle rectangle = new SearchParam.Rectangle().point(location2, location3);

            searchParam = new SearchParam()
                    .keyword(keyWord)
                    .page_index(0)
                    .boundary(nearBy);
        } else {
            //城市搜索
            SearchParam.Region region = new SearchParam.Region()
                    .poi("广州")//设置搜索城市
                    .autoExtend(true);//设置搜索范围扩大
            //filter()方法可以设置过滤类别，
            //search接口还提供了排序方式、返回条目数、返回页码具体用法见文档，
            //同时也可以参考官网的webservice对应接口的说明
            searchParam = new SearchParam()
                    .keyword(keyWord)
                    .boundary(region);
        }
        searchParam.page_size(20);
        tencentSearch.search(searchParam, new HttpResponseListener() {

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                if (arg2 != null)
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
                callback.onSearchKeyWordCallback(obj.data);
            }
        });
    }

    /**
     * 通过经纬度搜索地理信息
     * @param context
     * @param location
     * @param callback
     */
    public static void searchLocationInfo(Context context, android.location.Location location, final OnSearchLocationCallback callback) {
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
        param.get_poi(false);
        tencentSearch.geo2address(param, new HttpResponseListener() {
            @Override
            public void onSuccess(int i, BaseObject baseObject) {
                if (baseObject.isStatusOk() && baseObject instanceof Geo2AddressResultObject) {
                    Geo2AddressResultObject result = (Geo2AddressResultObject) baseObject;
                    callback.onSearchLocationCallback(result.result.formatted_addresses.recommend);
                }
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
            }
        });
    }

    public interface OnSearchLocationCallback {
        void onSearchLocationCallback(String info);
    }

    public interface OnSearchKeyWordCallback {
        void onSearchKeyWordCallback(List<SearchResultObject.SearchResultData> result);
    }
}
