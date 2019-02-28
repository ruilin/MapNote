package com.muyu.mapnote.map.map.poi;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.okayapi.OkMomentItem;
import com.muyu.mapnote.map.map.moment.MomentMarker;
import com.muyu.mapnote.map.map.moment.MomentMarkerOptions;
import com.muyu.mapnote.map.map.moment.MomentPoi;
import com.muyu.minimalism.framework.app.BaseApplication;
import com.muyu.minimalism.utils.bitmap.BitmapUtils;
import com.muyu.minimalism.utils.GpsUtils;
import com.muyu.minimalism.utils.bitmap.CanvasUtils;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class PoiManager {

    public final static byte POI_TYPE_TARGET = 0;
    public final static byte POI_TYPE_SEARCH_FIRST = 1;
    public final static byte POI_TYPE_SEARCH_OTHER = 2;
    public final static byte POI_TYPE_MOMENT = 3;
    public final static byte POI_TYPE_FOOTMARK = 4;

    private static Hashtable<String, Marker> mKeywordPoiMap = new Hashtable<>();
    private static Hashtable<String, MomentMarker> mMomentPoiMap = new Hashtable<>();

    public static MomentPoi toMomentPoi(OkMomentItem item) {
        MomentPoi poi = new MomentPoi();
        poi.id = String.valueOf(item.id);
        poi.address = item.moment_nickname;
        poi.title = item.moment_content;
        poi.content = item.moment_content;
        poi.nickname = item.moment_nickname;
        poi.createtime = item.moment_createtime;
        poi.like = item.moment_like;
        poi.place = item.moment_place;
        GpsUtils.Gps gps = GpsUtils.gcj_To_Gps84(item.moment_lat, item.moment_lng);
        poi.lat = gps.getWgLat();
        poi.lng = gps.getWgLon();
        if (item.moment_picture1 != null) {
            poi.pictureUrlLiat.add(item.moment_picture1);
        }
        if (item.moment_picture2 != null) {
            poi.pictureUrlLiat.add(item.moment_picture2);
        }
        if (item.moment_picture3 != null) {
            poi.pictureUrlLiat.add(item.moment_picture3);
        }
        if (item.moment_picture4 != null) {
            poi.pictureUrlLiat.add(item.moment_picture4);
        }
        return poi;
    }

    public static Marker createPoi(MapboxMap map, String title, String snippet, LatLng point, byte poiType) {
        IconFactory iconFactory = IconFactory.getInstance(BaseApplication.getInstance());
        Icon icon = iconFactory.fromResource(R.drawable.yellow_marker);
        switch (poiType) {
            case POI_TYPE_TARGET:
                icon = iconFactory.fromResource(R.drawable.map_default_map_marker);
                break;
            case POI_TYPE_SEARCH_FIRST:
                icon = iconFactory.fromResource(R.drawable.blue_marker);
                break;
            case POI_TYPE_SEARCH_OTHER:
                icon = iconFactory.fromResource(R.drawable.yellow_marker);
                break;
            case POI_TYPE_MOMENT:
                icon = iconFactory.fromResource(R.drawable.green_marker);
                break;
            case POI_TYPE_FOOTMARK:
                icon = iconFactory.fromResource(R.drawable.green_marker);
                break;
        }
        return map.addMarker(new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(snippet)
                .icon(icon)
        );
    }

    public static void createPoi(MapboxMap map, Poi poi) {
        byte poiType = PoiManager.POI_TYPE_SEARCH_OTHER;
//        if (isFirst) {
//            poiType = PoiHelper.POI_TYPE_SEARCH_FIRST;
//            isFirst = false;
//        }
        Marker marker = createPoi(map, poi.title, poi.address, new LatLng(poi.lat, poi.lng), poiType);
        mKeywordPoiMap.put(poi.title, marker);
    }

    public static void showMoment(Context context, MapboxMap map, MomentPoi poi) {
        IconFactory iconFactory = IconFactory.getInstance(BaseApplication.getInstance());
        Icon icon = iconFactory.fromResource(R.drawable.green_marker);
//        Marker marker = map.addMarker(new MarkerOptions()
//                .position(new LatLng(poi.lat, poi.lng))
//                .title(poi.title)
//                .snippet(poi.address)
//                .icon(icon)
//        );
        MomentMarkerOptions options = new MomentMarkerOptions();
        options.position(new LatLng(poi.lat, poi.lng))
                .title(poi.nickname)
                .snippet("……")
                .icon(icon);
        options.momentPoi(poi);
        MomentMarker marker = (MomentMarker)map.addMarker(options);
        if (poi.pictureUrlLiat.size() > 0) {
            Glide.with(context).asBitmap().load(poi.pictureUrlLiat.get(0)).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Bitmap smallBitmap = BitmapUtils.changeBitmapSize(resource, 100, 100);
                    Bitmap circle = CanvasUtils.drawCircleBitmap(smallBitmap);
                    marker.setIcon(iconFactory.fromBitmap(circle));
                }
            });
        }
        mMomentPoiMap.put(poi.id, marker);
    }

    public static MomentPoi getMomentPoi(String id) {
        MomentMarker marker = mMomentPoiMap.get(id);
        if (marker != null) {
            return marker.getMomentPoi();
        } else {
            return null;
        }
    }

    public static void removePoiByType(MapboxMap map, byte type) {
        switch (type) {
            case POI_TYPE_TARGET:
                break;
            case POI_TYPE_SEARCH_FIRST:
                break;
            case POI_TYPE_SEARCH_OTHER:
                for(Iterator<Map.Entry<String, Marker>> iterator = mKeywordPoiMap.entrySet().iterator(); iterator.hasNext();){
                    Map.Entry<String, Marker> entry = iterator.next();
                    map.removeMarker(entry.getValue());
                }
                break;
            case POI_TYPE_MOMENT:
                for(Iterator<Map.Entry<String, MomentMarker>> iterator = mMomentPoiMap.entrySet().iterator(); iterator.hasNext();){
                    Map.Entry<String, MomentMarker> entry = iterator.next();
                    map.removeMarker(entry.getValue());
                }
                break;
        }
    }

    public static void removePoiByKey(MapboxMap map, String key) {
        map.removeMarker(mKeywordPoiMap.get(key));
    }
}
