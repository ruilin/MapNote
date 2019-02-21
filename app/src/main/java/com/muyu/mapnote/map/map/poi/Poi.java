package com.muyu.mapnote.map.map.poi;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.muyu.mapnote.map.navigation.location.LocationHelper;
import com.tencent.lbssearch.object.result.SearchResultObject;

public class Poi implements Parcelable {

    public String id = "";
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

    public Poi() {
    }

    protected Poi(Parcel in) {
        id = in.readString();
        title = in.readString();
        address = in.readString();
        tel = in.readString();
        category = in.readString();
        type = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(title);
        out.writeString(address);
        out.writeString(tel);
        out.writeString(category);
        out.writeString(type);
        out.writeDouble(lat);
        out.writeDouble(lng);
    }

    public static final Creator<Poi> CREATOR = new Creator<Poi>() {
        @Override
        public Poi createFromParcel(Parcel in) {
            return new Poi(in);
        }

        @Override
        public Poi[] newArray(int size) {
            return new Poi[size];
        }
    };
}
