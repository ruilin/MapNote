package com.muyu.mapnote.map.map.poi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
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
    private static Hashtable<String, MomentPoi> mMomentPoiMap = new Hashtable<>();

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

    public static Marker createPoi(MapboxMap map, Poi poi) {
        byte poiType = PoiManager.POI_TYPE_SEARCH_OTHER;
//        if (isFirst) {
//            poiType = PoiHelper.POI_TYPE_SEARCH_FIRST;
//            isFirst = false;
//        }
        Marker marker = createPoi(map, poi.title, poi.address, new LatLng(poi.lat, poi.lng), poiType);
        mKeywordPoiMap.put(poi.title, marker);
        return marker;
    }

//    public static void showMoment(Context context, MapboxMap map, MomentPoi poi) {
//        IconFactory iconFactory = IconFactory.getInstance(BaseApplication.getInstance());
//        Icon icon = iconFactory.fromResource(R.drawable.green_marker);
////        Marker marker = map.addMarker(new MarkerOptions()
////                .position(new LatLng(poi.lat, poi.lng))
////                .title(poi.title)
////                .snippet(poi.address)
////                .icon(icon)
////        );
//        MomentMarkerOptions options = new MomentMarkerOptions();
//        options.position(new LatLng(poi.lat, poi.lng))
//                .title(poi.nickname)
//                .snippet("……")
//                .icon(icon);
//        options.momentPoi(poi);
//        MomentMarker marker = (MomentMarker)map.addMarker(options);
//        if (poi.pictureUrlLiat.size() > 0) {
//            Glide.with(context).asBitmap().load(poi.pictureUrlLiat.get(0)).into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                    Bitmap smallBitmap = BitmapUtils.changeBitmapSize(resource, 100, 100);
//                    Bitmap circle = CanvasUtils.drawCircleBitmap(smallBitmap);
//                    marker.setIcon(iconFactory.fromBitmap(circle));
//                }
//            });
//        }
//        mMomentPoiMap.put(poi.id, marker);
//    }

    public static MomentPoi getMomentPoi(String id) {
        MomentPoi poi = mMomentPoiMap.get(id);
        return poi;
    }

    public static List<String> getMomentLayerIdList() {
        ArrayList<String> list = new ArrayList<>();
        for(Iterator<Map.Entry<String, MomentPoi>> iterator = mMomentPoiMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, MomentPoi> entry = iterator.next();
            list.add(entry.getKey());
        }
        return list;
    }

    public static MomentPoi searchMomentByScreenPoint(MapboxMap map, PointF screenPoint) {
        for(Iterator<Map.Entry<String, MomentPoi>> iterator = mMomentPoiMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, MomentPoi> entry = iterator.next();
            List<Feature> features = map.queryRenderedFeatures(screenPoint, "layer_" + entry.getKey());
            if (!features.isEmpty()) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static void removePoiByType(MapboxMap map, byte type) {
        switch (type) {
            case POI_TYPE_TARGET:
                break;
            case POI_TYPE_SEARCH_FIRST:
                break;
            case POI_TYPE_SEARCH_OTHER:
                for(Iterator<Map.Entry<String, Marker>> iterator = mKeywordPoiMap.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<String, Marker> entry = iterator.next();
                    map.removeMarker(entry.getValue());
                }
                break;
            case POI_TYPE_MOMENT:
                for(Iterator<Map.Entry<String, MomentPoi>> iterator = mMomentPoiMap.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<String, MomentPoi> entry = iterator.next();
                    Style style = map.getStyle();
                    removeMarker(style, entry.getKey());
                }
                break;
        }
    }

    public static void removePoiByKey(MapboxMap map, String key) {
        map.removeMarker(mKeywordPoiMap.get(key));
    }

    public static void showMoment(BaseActivity activity, Style style, MomentPoi poi) {
        Glide.with(activity).asBitmap().load(poi.pictureUrlLiat.get(0)).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Bitmap smallBitmap = BitmapUtils.changeBitmapSize(resource, 100, 100);
                Bitmap circle = CanvasUtils.drawCircleBitmap(smallBitmap);
                String layerId = addMarker(style, poi, circle);
                mMomentPoiMap.put(poi.id, poi);
            }
        });
    }

    private static void removeMarker(Style style, String id) {
        String sourceId = "source_" + id;
        String imageId = "icon_" + id;
        String layerId = "layer_" + id;

        style.removeLayer(layerId);
        style.removeImage(imageId);
        style.removeSource(sourceId);
    }

    public synchronized static String addMarker(Style style, MomentPoi poi, Bitmap bitmap) {
//        List<Feature> markerCoordinates = new ArrayList<>();
//        markerCoordinates.add(Feature.fromGeometry(Point.fromLngLat(poi.lat, poi.lng)));
        String sourceId = "source_" + poi.id;
        String imageId = "icon_" + poi.id;
        String layerId = "layer_" + poi.id;

        removeMarker(style, poi.id);

        style.addSource(new GeoJsonSource(sourceId,
                FeatureCollection.fromFeature(Feature.fromGeometry(Point.fromLngLat(poi.lng, poi.lat)))));

        // 添加资源图片到地图
        style.addImage(imageId, bitmap);

        // Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
        // middle of the icon being fixed to the coordinate point.
        style.addLayerAt(new SymbolLayer(layerId, sourceId)
                .withProperties(PropertyFactory.iconImage(imageId),
                        iconOffset(new Float[]{0f, 0f})),
                style.getLayers().size() - 5
        );
        return layerId;
    }
}
