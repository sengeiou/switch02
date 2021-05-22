package com.szip.sportwatch.Activity.GpsSport;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.Util.LocationUtil;

import java.util.Timer;
import java.util.TimerTask;

public class GpsPresenterImpl implements IGpsPresenter {
    private Context context;
    private IGpsView iGpsView;

    private Location preLocation;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;

    private LocationManager locationManager;




    private boolean firstTime = true;

    private int time = 0;//运动时长
    private int uiTime = 0;//上次更新UI的时间，用于定时更新ui
    private int speedTime = 0;//记录一公里配速的时长
    private float distance = 0;//两个点之间的距离
    private float preDistance = 0;//30秒之内移动的距离，用于计算平均速度
    private float calorie = 0;
    private int speed = 0;
    private long preTime = 0;//上一次定位的时间，用于计算两次定位之间的瞬时速度
    private float preDistance1 = 0;//整数公里数，用于计算1公里的配速
    private StringBuffer speedStr = new StringBuffer();
    private StringBuffer speedPerHour = new StringBuffer();
    private StringBuffer strideStr = new StringBuffer();
    private StringBuffer latStr = new StringBuffer();
    private StringBuffer lngStr = new StringBuffer();
    private double lat;
    private double lng;

    private MapFragment mapFragment;

    private Timer timer;
    private TimerTask timerTask;

    public GpsPresenterImpl(Context context, IGpsView iGpsView) {
        this.context = context;
        this.iGpsView = iGpsView;
        initLocationService();
    }
    public GpsPresenterImpl(Context context, IGpsView iGpsView,LocationManager locationManager) {
        this.context = context;
        this.iGpsView = iGpsView;
        this.locationManager = locationManager;
    }

    private void initLocationService() {
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //声明AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(false);
        mLocationOption.setMockEnable(false);
        mLocationOption.setLocationCacheEnable(false);
        mLocationClient.setLocationOption(mLocationOption);
    }

    @Override
    public void startLocationService() {
        if (firstTime){
            if (iGpsView!=null)
                iGpsView.startCountDown();
            firstTime = false;
        }else {
            if (mLocationClient!=null) {
                mLocationClient.startLocation();
                initTask();
                timer.schedule(timerTask,0,1000);
                if (iGpsView!=null)
                    iGpsView.startRun();
            }

            if (locationManager!=null) {
                LocationUtil.getInstance().getLocation(locationManager,myListener,locationListener);
                initTask();
                timer.schedule(timerTask,0,1000);
                if (iGpsView!=null)
                    iGpsView.startRun();
            }
        }
    }

    @Override
    public void stopLocationService(){
        if(mLocationClient!=null){
            mLocationClient.stopLocation();
            if (timer!=null){
                timer.cancel();
                timerTask.cancel();
                timer = null;
                timerTask = null;
            }
            if (iGpsView!=null)
                iGpsView.stopRun();
        }

        if(locationManager!=null){
            locationManager.removeUpdates(locationListener);
            if (timer!=null){
                timer.cancel();
                timerTask.cancel();
                timer = null;
                timerTask = null;
            }
            if (iGpsView!=null)
                iGpsView.stopRun();
        }
    }

    @Override
    public void finishLocationService() {
        if(mLocationClient!=null){
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            if (timer!=null){
                timer.cancel();
                timerTask.cancel();
            }
            if (distance-preDistance1>200){
                speedStr.append(","+getInstantaneousSpeed((distance-preDistance1)/(time- speedTime)));
            }
            if (iGpsView!=null)
                iGpsView.saveRun(getSportData());
        }

        if(locationManager!=null){
            locationManager.removeUpdates(locationListener);
            if (timer!=null){
                timer.cancel();
                timerTask.cancel();
            }
            if (distance-preDistance1>200){
                speedStr.append(","+getInstantaneousSpeed((distance-preDistance1)/(time- speedTime)));
            }
            if (iGpsView!=null)
                iGpsView.saveRun(getSportData());
        }
    }

    @Override
    public void openMap(FragmentManager fragmentManager) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        final Fragment prev = fragmentManager.findFragmentByTag("MAP");
        if (prev != null){
            ft.remove(prev).commit();
            ft = fragmentManager.beginTransaction();
        }
        ft.addToBackStack(null);
        mapFragment = new MapFragment(speed,distance,calorie,preLocation);
        mapFragment.show(ft, "MAP");
    }

    private void initTask(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time++;
                if (iGpsView!=null)
                    iGpsView.upDateTime(time);
                /**
                 * 每30秒采集一次平均速度的数据
                 * */
                if (time!=0&&time%30==0){
                    speedPerHour.append(String.format(",%d",(int)((distance-preDistance)/30f*3.6f*10)));
                    strideStr.append(String.format(",%d",getStride(distance-preDistance)));
                    preDistance = distance;
                }
            }
        };
    }

    //声明定位回调监听器
    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            updateWithNewLocation(aMapLocation);

        }
    };

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(LocationUtil.getInstance().getGaoLocation(location,context));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private GpsStatus.Listener myListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int i) {

        }
    };

    private void updateWithNewLocation(Location location) {
        Log.d("LOCATION******",location.toString());
        if (location != null) {
            if (preLocation!=null){
                if (time-uiTime>4&&location.getLatitude()!=0&&location.getLongitude()!=0){
                    latStr.append(String.format(",%d",(int)((location.getLatitude()-lat)*1000000)));
                    lngStr.append(String.format(",%d",(int)((location.getLongitude()-lng)*1000000)));
                    long subTime=(System.currentTimeMillis()- preTime)/1000;
                    float d = AMapUtils.calculateLineDistance(new LatLng(preLocation.getLatitude(),preLocation.getLongitude()),
                            new LatLng(location.getLatitude(),location.getLongitude()));
                    float v = (subTime==0)?0:(d/subTime);
                    speed = getInstantaneousSpeed(v);
                    distance+=d;
                    calorie+=getCalorie(d);
                    if (iGpsView!=null)
                        iGpsView.upDateRunData(speed,distance,calorie);
                    if (mapFragment!=null&&!mapFragment.isHidden()){
                        mapFragment.setData(speed,distance,calorie);
                    }
                    if (distance-preDistance1>1000){
                        speedStr.append(String.format(",%d",time- speedTime));
                        preDistance1+=1000;
                        speedTime = time;
                    }
                    preLocation=location;
                    preTime =System.currentTimeMillis();
                    uiTime = time;
                }
            }else {
                lat = location.getLatitude();
                lng = location.getLongitude();
                latStr.append(String.format(",%d",(int)(lat*1000000)));
                lngStr.append(String.format(",%d",(int)(lng*1000000)));
                preLocation=location;
                preTime =System.currentTimeMillis();
            }
            if (mapFragment!=null&&!mapFragment.isHidden()){
                mapFragment.setLocation(location);
            }
        }
    }

    private int getStride(float d){
        float k = MyApplication.getInstance().getUserInfo().getHeight()*0.45f/100;
        return (int)(d/k*2);
    }

    private float getCalorie(float d) {
        float calorie = MyApplication.getInstance().getUserInfo().getWeight()*1.036f*(d/1000);
        return calorie;
    }

    private int getInstantaneousSpeed(float v){
        float speed = (v == 0)?0:(1000/v);
        return (int)speed;
    }

    private SportData getSportData(){
        SportData sportData = new SportData();
        if(time>30){
            sportData.type = 2;
            sportData.time = System.currentTimeMillis()/1000;
            sportData.sportTime = time;
            sportData.calorie = (int)(calorie*1000);
            sportData.distance = (int)distance;
            if (!strideStr.toString().equals("")) {
                sportData.strideArray = strideStr.toString().substring(1);
                sportData.stride = getAverageData(sportData.strideArray);
            }
            if (!speedPerHour.toString().equals("")){
                sportData.speedPerHourArray = speedPerHour.toString().substring(1);
                sportData.speedPerHour = getAverageData(sportData.speedPerHourArray);
            }
            if (!speedStr.toString().equals("")){
                sportData.speedArray = speedStr.toString().substring(1);
                sportData.speed = getAverageData(sportData.speedArray);
            }
            if (!lngStr.toString().equals("")){
                sportData.lngArray = lngStr.toString().substring(1);
            }
            if (!latStr.toString().equals("")){
                sportData.latArray = latStr.toString().substring(1);
            }
        }
        return sportData;
    }

    private int getAverageData(String data){
        int sum = 0;
        int allData = 0;
        String [] datas = data.split(",");
        for (String str:datas){
            if (Integer.valueOf(str)!=0){
                allData+=Integer.valueOf(str);
                sum++;
            }
        }
        if (sum!=0)
            return allData/sum;
        else
            return 0;
    }
}
