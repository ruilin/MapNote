package com.muyu.mapnote.map.search;

import android.app.Activity;
import android.util.Log;

import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.controller.SubController;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.Location;
import com.tencent.lbssearch.object.param.Address2GeoParam;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.result.SearchResultObject;

public class MapSearchController extends SubController {
    private MapSearchProvider mProvider;


    @Override
    public void onAttached(BaseActivity activity) {
        searchPoi(activity, "广州");
    }

    /**
     * poi检索
     */
    protected void searchPoi(BaseActivity activity, String keyWord) {
        TencentSearch tencentSearch = new TencentSearch(activity);
        //城市搜索
        SearchParam.Region region = new SearchParam.Region()
                .poi("北京")//设置搜索城市
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
            }

            @Override
            public void onSuccess(int arg0, BaseObject arg1) {
                // TODO Auto-generated method stub
                if (arg1 == null) {
                    return;
                }
                SearchResultObject obj = (SearchResultObject) arg1;
                if(obj.data == null){
                    return;
                }
                String result = "搜索poi\n";
                for(SearchResultObject.SearchResultData data : obj.data){
                    Log.v("SearchDemo","title:"+data.title + ";" + data.address);
                    result += data.address+"\n";
                }
                Log.e("xxx", result);
            }
        });
    }

    @Override
    public void onDetached() {
        super.onDetached();
    }
}
