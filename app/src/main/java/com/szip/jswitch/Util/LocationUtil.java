package com.szip.jswitch.Util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.szip.jswitch.MyApplication;

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


    @RequiresApi(api = Build.VERSION_CODES.N)
    public Location getLocation(LocationManager myLocationManager, boolean needNetwork, GpsStatus.Listener myListener, LocationListener locationListener) {
        //获取位置管理服务

        //查找服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //定位精度: 最高
        criteria.setAltitudeRequired(false); //海拔信息：不需要
        criteria.setBearingRequired(false); //方位信息: 不需要
        criteria.setCostAllowed(true);  //是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_HIGH); //耗电量: 低功耗

        Location gpsLocation = null;
        Location netLocation = null;

        if (ActivityCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        myLocationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
            @Override
            public void onStarted() {
                super.onStarted();
            }
        });

        if (gpsIsOpen(myLocationManager)) {
            myLocationManager.requestLocationUpdates("gps", 1000, 1, locationListener);
            gpsLocation = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (needNetwork&&netWorkIsOpen(myLocationManager)) {
            myLocationManager.requestLocationUpdates("network", 2000, 1, locationListener);
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

    public Location getGaoLocation(Location location, Context context){
        if (context.getResources().getConfiguration().locale.getCountry().equals("CN")){
            LatLng mark = null;
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            //初始化坐标转换类
            CoordinateConverter converter = new CoordinateConverter(context);
            converter.from(CoordinateConverter.CoordType.GPS);
            //设置需要转换的坐标
            try {
                converter.coord(new LatLng(latitude,longitude));
                mark=converter.convert();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mark!=null){
                location.setLatitude(mark.latitude);
                location.setLongitude(mark.longitude);
            }
        }
        return location;
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
