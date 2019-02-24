package com.muyu.mapnote.map.map;

import android.support.annotation.NonNull;
import android.view.View;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.plugins.localization.MapLocale;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.services.android.navigation.ui.v5.NavigationContract;
import com.muyu.mapnote.R;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;

public class MapSettings {

    public static void initMapStyle(MapboxMap map, MapView view, Style.OnStyleLoaded listener) {

        // UI设置
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setCompassEnabled(true);             //指南针
        uiSettings.setTiltGesturesEnabled(false);        //设置是否可以调整地图倾斜角
        uiSettings.setRotateGesturesEnabled(false);     //设置是否可以旋转地图
        uiSettings.setAttributionEnabled(false);        //设置是否显示那个提示按钮
        uiSettings.setLogoEnabled(false);               //隐藏logo


        // 本地化
        map.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                LocalizationPlugin localizationPlugin = new LocalizationPlugin(view, map, style);
                localizationPlugin.setMapLanguage(MapLocale.SIMPLIFIED_CHINESE);
                listener.onStyleLoaded(style);
            }
        });
    }
}
