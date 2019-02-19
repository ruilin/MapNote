package com.muyu.mapnote.map.map.poi;

import android.location.Location;

import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.tencent.lbssearch.object.result.SearchResultObject;

public class Poi {

    public String id;
    public String title;
    public String address;
    public String tel;
    public String category;
    public String type;
    public double lat;
    public double lng;

    public static Poi toPoi(SearchResultObject.SearchResultData tcPoi) {
        Poi poi = new Poi();
        poi.id = tcPoi.id;
        poi.title = tcPoi.title;
        poi.address = tcPoi.address;
        poi.tel = tcPoi.tel;
        poi.category = tcPoi.category;
        poi.type = tcPoi.type;
        Location location = LocationHelper.toLocation(tcPoi.location);
        poi.lat = location.getLatitude();
        poi.lng = location.getLongitude();
        return poi;
    }
}
