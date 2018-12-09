package com.muyu.mapnote.map.navigation.location;

import android.location.Location;

import com.muyu.mapnote.app.MapApplication;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.muyu.mapnote.framework.util.PositionUtil;
import com.muyu.mapnote.framework.util.Msg;

import java.util.ArrayList;

public enum LocationHelper {
    INSTANCE;

    //声明mlocationClient对象
    public AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;


    private ArrayList<OnLocationListener> mListeners;

    LocationHelper() {
        mListeners = new ArrayList<>();
    }

    public Location toLocation(AMapLocation amapLocation) {
        Location location = new Location("AMap");
        location.setTime(amapLocation.getTime());
        location.setLatitude(amapLocation.getLatitude());
        location.setLongitude(amapLocation.getLongitude());
        location.setAltitude(amapLocation.getAltitude());
        location.setSpeed(amapLocation.getSpeed());
        location.setBearing(amapLocation.getBearing());
        location.setAccuracy(amapLocation.getGpsAccuracyStatus());
        return location;
    }

    public void start() {
        //mLocationClient为第二步初始化过的LocationClient对象
        //调用LocationClient的start()方法，便可发起定位请求
        if (mLocationClient != null && !mLocationClient.isStarted())
            mLocationClient.startLocation();
    }

    public void stop() {
        if (mLocationClient != null && mLocationClient.isStarted())
            mLocationClient.stopLocation();
    }

    public void addListener(OnLocationListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public boolean removeListener(OnLocationListener listener) {
        return mListeners.remove(listener);
    }

    public Location getLastLocation() {
        if (mLocationClient != null)
            return toLocation(mLocationClient.getLastKnownLocation());
        return null;
    }

    public void init() {
        mLocationClient = new AMapLocationClient(MapApplication.getInstance());
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                //定位回调监听器
                int errorCode = aMapLocation.getErrorCode();
                if (errorCode != AMapLocation.LOCATION_SUCCESS) {
                    Msg.show("定位失败：" + errorCode);
                    return;
                }
                final double lat = aMapLocation.getLatitude();
                final double lng = aMapLocation.getLongitude();
                if (CoordinateConverter.isAMapDataAvailable(lat, lng)) {
                    // 在中国
                    PositionUtil.Gps point = PositionUtil.gcj_To_Gps84(lat, lng);
                    aMapLocation.setLatitude(point.getWgLat());
                    aMapLocation.setLongitude(point.getWgLon());
                }
                Location location = toLocation(aMapLocation);
                for (OnLocationListener l : mListeners) {
                    l.onLocationUpdate(location);
                }
            }
        });
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mLocationClient.startLocation();
    }

    public static boolean isInChina(double latitude, double longitude) {
        CoordinateConverter converter  = new CoordinateConverter(MapApplication.getInstance());
        //返回true代表当前位置在大陆、港澳地区，反之不在。
        boolean isAMapDataAvailable = converter.isAMapDataAvailable(latitude,longitude);
        //第一个参数为纬度，第二个为经度，纬度和经度均为高德坐标系。
        return isAMapDataAvailable;
    }

    public static double[] checkChineseCoor(double lat, double lng) {
        double newLat = lat;
        double newLng = lng;
        if (CoordinateConverter.isAMapDataAvailable(lat, lng)) {
            PositionUtil.Gps point = PositionUtil.gcj_To_Gps84(lat, lng);
            newLat = point.getWgLat();
            newLng = point.getWgLon();
        }
        return new double[]{newLat, newLng};
    }

    public interface OnLocationListener {
        void onLocationUpdate(final Location location);
    }
}
