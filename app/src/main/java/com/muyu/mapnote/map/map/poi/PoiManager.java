package com.muyu.mapnote.map.map.poi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngQuad;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.ImageSource;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.okayapi.OkMomentItem;
import com.muyu.mapnote.footmark.FootmarkFragment;
import com.muyu.mapnote.map.map.moment.MomentMarker;
import com.muyu.mapnote.map.map.moment.MomentMarkerOptions;
import com.muyu.mapnote.map.map.moment.MomentPoi;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.app.BaseApplication;
import com.muyu.minimalism.utils.StringUtils;
import com.muyu.minimalism.utils.bitmap.BitmapUtils;
import com.muyu.minimalism.utils.GpsUtils;
import com.muyu.minimalism.utils.bitmap.CanvasUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

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
        poi.headimg = item.moment_url;
//        if (!StringUtils.isEmpty(item.ext_data)) {
//            JsonObject obj = new JsonParser().parse(item.ext_data).getAsJsonObject();
//            poi.headimg = obj.get("headimg").getAsString();
//        }

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
                icon = iconFactory.fromResource(R.mipmap.ic_foot_select);
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


    private static final String ID_IMAGE_SOURCE = "animated_image_source";
    private static final String ID_IMAGE_LAYER = "animated_image_layer";

//    private void setMarkerLayer(BaseActivity activity, MapboxMap map, final List<MomentPoi> poiList) {
//        if (map != null && poiList.size() > 0) {
//            map.getStyle(new Style.OnStyleLoaded() {
//                @Override
//                public void onStyleLoaded(@NonNull Style style) {
//                    List<Feature> markerCoordinates = new ArrayList<>();
//                    for (int i = poiList.size() - 1; i >= 0; --i) {
//                        MomentPoi poi = poiList.get(i);
//                        markerCoordinates.add(Feature.fromGeometry(Point.fromLngLat(poi.lat, poi.lng)));
//                    }
//                    style.addSource(new GeoJsonSource("marker-source",
//                            FeatureCollection.fromFeatures(markerCoordinates)));
//                    new LatLngQuad.
////                    style.addSource(new ImageSource(ID_IMAGE_SOURCE, ));
//
//                    // 添加资源图片到地图
//                    style.addImage("my-marker-image", BitmapFactory.decodeResource(
//                            activity.getResources(), R.drawable.map_default_map_marker));
//
//                    // Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
//                    // middle of the icon being fixed to the coordinate point.
//                    style.addLayer(new SymbolLayer("marker-layer", "marker-source")
//                            .withProperties(PropertyFactory.iconImage("my-marker-image"),
//                                    iconOffset(new Float[]{0f, 0f}))
//                    );
//
//                    // Add the selected marker source and layer
//                    style.addSource(new GeoJsonSource("selected-marker"));
//
//                    // Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
//                    // middle of the icon being fixed to the coordinate point.
//                    style.addLayer(new SymbolLayer("selected-marker-layer", "selected-marker")
//                            .withProperties(PropertyFactory.iconImage("my-marker-image"),
//                                    iconOffset(new Float[]{0f, 0f})));
//                }
//            });
//        }
//    }
}
