package com.muyu.mapnote.map.map;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.plugins.localization.MapLocale;
import com.mapbox.mapboxsdk.style.layers.Layer;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;

public class MapSettings {

    public static void initMapStyle(MapboxMap map, MapView view) {
        // 本地化
        try {
            LocalizationPlugin localizationPlugin = new LocalizationPlugin(view, map);
//            localizationPlugin.matchMapLanguageWithDeviceDefault();
            localizationPlugin.setMapLanguage(MapLocale.SIMPLIFIED_CHINESE);
            // 镜头转移到所在国家
            // localizationPlugin.setCameraToLocaleCountry();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        // UI设置
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setCompassEnabled(true);             //指南针
        uiSettings.setTiltGesturesEnabled(false);        //设置是否可以调整地图倾斜角
        uiSettings.setRotateGesturesEnabled(false);     //设置是否可以旋转地图
        uiSettings.setAttributionEnabled(false);        //设置是否显示那个提示按钮
        uiSettings.setLogoEnabled(false);               //隐藏logo

        Layer mapText = map.getLayer("country-label-lg");
        if (mapText != null) {
            mapText.setProperties(textField("{name_zh}"));
        }
    }
}
