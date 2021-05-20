package com.szip.sportwatch.Util;

import android.annotation.SuppressLint;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class LocationUtil {

    private static LocationUtil locationUtil;

    public static LocationUtil getInstance() {
        if (locationUtil == null) {
            synchronized (LocationUtil.class) {
                if (locationUtil == null) {
                    locationUtil = new LocationUtil();
                }
            }
        }
        return locationUtil;
    }

    @SuppressLint("MissingPermission")
    public Location getLocation(LocationManager myLocationManager, GpsStatus.Listener myListener, LocationListener locationListener) {
        //获取位置管理服务

        //查找服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //定位精度: 最高
        criteria.setAltitudeRequired(false); //海拔信息：不需要
        criteria.setBearingRequired(false); //方位信息: 不需要
        criteria.setCostAllowed(true);  //是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_HIGH); //耗电量: 低功耗
//        String provider = myLocationManager.getBestProvider(criteria, true); //获取GPS信息
//        myLocationManager.requestLocationUpdates(provider,2000,5,locationListener);
//        Log.e("provider", provider);
//        List<String> list = myLocationManager.getAllProviders();
//        Log.e("provider", list.toString());
//
        Location gpsLocation = null;
        Location netLocation = null;
        myLocationManager.addGpsStatusListener(myListener);
        if (netWorkIsOpen(myLocationManager)) {
            //2000代表每2000毫秒更新一次，5代表每5秒更新一次
            myLocationManager.requestLocationUpdates("network", 2000, 5, locationListener);
            netLocation = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (gpsIsOpen(myLocationManager)) {
            myLocationManager.requestLocationUpdates("gps", 2000, 5, locationListener);
            gpsLocation = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (gpsLocation == null && netLocation == null) {
            return null;
        }
        if (gpsLocation != null && netLocation != null) {
            if (gpsLocation.getTime() < netLocation.getTime()) {
                gpsLocation = null;
                return netLocation;
            } else {
                netLocation = null;
                return gpsLocation;
            }
        }
        if (gpsLocation == null) {
            return netLocation;
        } else {
            return gpsLocation;
        }
    }

    @SuppressLint("MissingPermission")
    public Location getLocationOnlyGPS(LocationManager myLocationManager, GpsStatus.Listener myListener, LocationListener locationListener) {
        //获取位置管理服务

        //查找服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); //定位精度: 最高
        criteria.setAltitudeRequired(true); //海拔信息：需要
        criteria.setBearingRequired(false); //方位信息: 不需要
        criteria.setCostAllowed(true);  //是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_LOW); //耗电量: 低功耗
//
        Location gpsLocation = null;
        myLocationManager.addGpsStatusListener(myListener);

        if (gpsIsOpen(myLocationManager)) {
            String provider = myLocationManager.getBestProvider(criteria, true); //获取GPS信息
            myLocationManager.requestLocationUpdates(provider,2000,5,locationListener);
            gpsLocation = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return gpsLocation;
    }

    private boolean gpsIsOpen(LocationManager myLocationManager) {
        boolean isOpen = true;
        if (!myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//没有开启GPS
            isOpen = false;
        }
        return isOpen;
    }

    private boolean netWorkIsOpen(LocationManager myLocationManager) {
        boolean netIsOpen = true;
        if (!myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {//没有开启网络定位
            netIsOpen = false;
        }
        return netIsOpen;
    }
}
