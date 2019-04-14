package com.muyu.mapnote.map.navigation.location;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.muyu.mapnote.app.MapApplication;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.muyu.minimalism.utils.GpsUtils;
import com.muyu.minimalism.utils.Logs;
import com.tencent.tauth.Tencent;

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

    public static Location toLocation(com.tencent.lbssearch.object.Location tcLocation) {
        if (tcLocation != null) {
            Location location = new Location("tencent");
            location.setTime(System.currentTimeMillis());

            GpsUtils.Gps gps = GpsUtils.gcj_To_Gps84(tcLocation.lat, tcLocation.lng);
            location.setLatitude(gps.getWgLat());
            location.setLongitude(gps.getWgLon());
            location.setAltitude(1.0f);
            location.setSpeed(0.0f);
            location.setBearing(0.0f);
            location.setAccuracy(1.0f);
            return location;
        } else {
            return null;
        }
    }

    public static Location toLocation(AMapLocation amapLocation) {
        if (amapLocation != null) {
            Location location = new Location("AMap");
            location.setTime(amapLocation.getTime());
            location.setLatitude(amapLocation.getLatitude());
            location.setLongitude(amapLocation.getLongitude());
            location.setAltitude(amapLocation.getAltitude());
            location.setSpeed(amapLocation.getSpeed());
            location.setBearing(amapLocation.getBearing());
            location.setAccuracy(amapLocation.getGpsAccuracyStatus());
            return location;
        } else {
            return null;
        }
    }

    public static LatLng getChinaLatlng(double lat, double lng) {
        if (isInChina(lat, lng)) {
            GpsUtils.Gps gps = GpsUtils.gcj_To_Gps84(lat, lng);
            return new LatLng(gps.getWgLat(), gps.getWgLon());
        } else {
            return new LatLng(lat, lng);
        }
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

    public Location getLastLocationCheckChina() {
        if (mLocationClient != null) {
            AMapLocation location = mLocationClient.getLastKnownLocation();
            if (location != null) {
                LatLng latLng = getChinaLatlng(location.getLatitude(), location.getLongitude());
                location.setLatitude(latLng.getLatitude());
                location.setLongitude(latLng.getLongitude());
                return toLocation(location);
            }
        }
        return null;
    }

    public boolean isLocationFresh() {
        Location location = getLastLocation();
        if (location != null) {
            long timeInterval = System.currentTimeMillis() - location.getTime();
            // 3个小时内有效
//            if (timeInterval  < 3 * 60 * 60 * 1000) {
                return true;
//            }
        }
        return false;
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
                    Logs.d("定位失败：" + errorCode);
                    return;
                }
                final double lat = aMapLocation.getLatitude();
                final double lng = aMapLocation.getLongitude();
                if (CoordinateConverter.isAMapDataAvailable(lat, lng)) {
                    // 在中国
                    GpsUtils.Gps point = GpsUtils.gcj_To_Gps84(lat, lng);
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
        mLocationOption.setInterval(3000);
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
            GpsUtils.Gps point = GpsUtils.gcj_To_Gps84(lat, lng);
            newLat = point.getWgLat();
            newLng = point.getWgLon();
        }
        return new double[]{newLat, newLng};
    }

    /**
     * 根据用户的起点和终点经纬度计算两点间距离，此距离为相对较短的距离，单位米。
     *
     * @param 起点的坐标
     * @param 终点的坐标
     * @return
     */
    public static double calculateLineDistance(double sLat, double sLng, double eLat, double eLng) {
        double d1 = 0.01745329251994329D;
        double d2 = sLng;
        double d3 = sLat;
        double d4 = eLng;
        double d5 = eLat;
        d2 *= d1;
        d3 *= d1;
        d4 *= d1;
        d5 *= d1;
        double d6 = Math.sin(d2);
        double d7 = Math.sin(d3);
        double d8 = Math.cos(d2);
        double d9 = Math.cos(d3);
        double d10 = Math.sin(d4);
        double d11 = Math.sin(d5);
        double d12 = Math.cos(d4);
        double d13 = Math.cos(d5);
        double[] arrayOfDouble1 = new double[3];
        double[] arrayOfDouble2 = new double[3];
        arrayOfDouble1[0] = (d9 * d8);
        arrayOfDouble1[1] = (d9 * d6);
        arrayOfDouble1[2] = d7;
        arrayOfDouble2[0] = (d13 * d12);
        arrayOfDouble2[1] = (d13 * d10);
        arrayOfDouble2[2] = d11;
        double d14 = Math.sqrt((arrayOfDouble1[0] - arrayOfDouble2[0]) * (arrayOfDouble1[0] - arrayOfDouble2[0])
                + (arrayOfDouble1[1] - arrayOfDouble2[1]) * (arrayOfDouble1[1] - arrayOfDouble2[1])
                + (arrayOfDouble1[2] - arrayOfDouble2[2]) * (arrayOfDouble1[2] - arrayOfDouble2[2]));

        return (Math.asin(d14 / 2.0D) * 12742001.579854401D);
    }

    public interface OnLocationListener {
        void onLocationUpdate(final Location location);
    }
}
