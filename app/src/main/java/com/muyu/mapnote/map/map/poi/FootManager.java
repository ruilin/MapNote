package com.muyu.mapnote.map.map.poi;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.muyu.mapnote.R;
import com.muyu.mapnote.app.MapApplication;
import com.muyu.mapnote.app.okayapi.OkMomentItem;
import com.muyu.mapnote.map.map.moment.MomentPoi;
import com.muyu.minimalism.framework.app.BaseActivity;
import com.muyu.minimalism.framework.app.BaseApplication;
import com.muyu.minimalism.utils.GpsUtils;
import com.muyu.minimalism.utils.MathUtils;
import com.muyu.minimalism.utils.SPUtils;
import com.muyu.minimalism.utils.bitmap.BitmapUtils;
import com.muyu.minimalism.utils.bitmap.CanvasUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class FootManager {

    private static Hashtable<String, Marker> mFootPoiMap = new Hashtable<>();
    private static final String KEY = "footrecord_";


    public static void init(MapboxMap mapboxMap) {
        mFootPoiMap.clear();
        Map<String, ?> sp = SPUtils.getAll(MapApplication.getInstance());
        for (Map.Entry<String, ?> item : sp.entrySet()) {
            if (item.getKey().startsWith(KEY)) {
                Location location = SPUtils.get(item.getKey(), new Location(""));
                if (location != null) {
                    addFootMarker(mapboxMap, location);
                }
            }
        }
    }

    public static Marker createPoi(MapboxMap map, String title, String snippet, LatLng point) {
        IconFactory iconFactory = IconFactory.getInstance(BaseApplication.getInstance());
        Icon icon = iconFactory.fromResource(R.drawable.marker_foot);
        return map.addMarker(new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(snippet)
                .icon(icon)
        );
    }

    public static void removePoiByType(MapboxMap map) {
        for(Iterator<Map.Entry<String, Marker>> iterator = mFootPoiMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Marker> entry = iterator.next();
            map.removeMarker(entry.getValue());
        }
    }

    public static String getId(Location location) {
        return KEY + MathUtils.round(location.getLatitude(), 5) + "_" + MathUtils.round(location.getLongitude(), 5);
    }

    public static boolean addFootMarker(MapboxMap map, Location location) {
        String id = getId(location);
        if (mFootPoiMap.isEmpty() || mFootPoiMap.get(id) == null) {
            map.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
//                    addMarker(style, id, location.getLatitude(), location.getLongitude(), BitmapFactory.decodeResource(activity.getResources(), R.drawable.marker_foot));
                    Marker marker = createPoi(map, "标记", "纬度:" + MathUtils.round(location.getLatitude(), 5) + ",经度:" + MathUtils.round(location.getLongitude(), 5), new LatLng(location.getLatitude(), location.getLongitude()));
                    mFootPoiMap.put(id, marker);
                    SPUtils.put(id, location);
                }
            });
            return true;
        }
        return false;
    }

    public static void removeFootMarker(MapboxMap map, Location location) {
        String id = getId(location);
        if (!mFootPoiMap.isEmpty() && mFootPoiMap.get(id) != null) {
            map.removeMarker(mFootPoiMap.get(id));
            mFootPoiMap.remove(id);
            SPUtils.remove(MapApplication.getInstance(), id);
        }
    }
}
