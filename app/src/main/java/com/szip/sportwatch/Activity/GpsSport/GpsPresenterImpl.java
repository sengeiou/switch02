package com.szip.sportwatch.Activity.GpsSport;

import android.content.Context;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.amap.api.maps.AMap;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.Fragment.CalendarFragment;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.LocationUtil;

import java.util.Timer;
import java.util.TimerTask;

public class GpsPresenterImpl implements IGpsPresenter {
    private Context context;
    private IGpsView iGpsView;

    private Location preLocation;
    private LocationManager locationManager;

    private boolean firstTime = true;




    private int time = 0;
    private int preTime = 0;
    private float distance = 0;
    private float preDistance = 0;
    private float calorie = 0;
    private int speed = 0;
    private long preTime1 = 0;
    private float preDistance1 = 0;
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

    public GpsPresenterImpl(Context context, IGpsView iGpsView,LocationManager locationManager) {
        this.context = context;
        this.iGpsView = iGpsView;
        this.locationManager = locationManager;
    }

    @Override
    public void startLocationService() {
        if (firstTime){
            if (iGpsView!=null)
                iGpsView.startCountDown();
            firstTime = false;
        }else {
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
        if(locationManager!=null){
            locationManager.removeUpdates(locationListener);
            if (timer!=null){
                timer.cancel();
                timerTask.cancel();
            }
            if (iGpsView!=null)
                iGpsView.stopRun();
        }
    }

    @Override
    public void finishLocationService() {
        if(locationManager!=null){
            locationManager.removeUpdates(locationListener);
            if (distance-preDistance1>200){
                speedStr.append(","+getInstantaneousSpeed((distance-preDistance1)/(time-preTime)));
            }
//            Log.d("LOCATION******","本次运动统计到的数据：\n运动时长 = "+String.format("%02d:%02d:%02d",time/60/60,time/60%60,time%60)+
//                    "\n运动配速数组 = "+speedStr.toString()+"\n平均速度数组 = "+speedPerHour.toString()+"\n经度数组 = "+latStr.toString()+
//                    "\n纬度数组 = "+lngStr.toString()+"\n运动里程 = "+distance+"\n卡路里 = "+calorie+"\n步频数组 = "+strideStr.toString());
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
        mapFragment = new MapFragment(speed,distance,calorie);
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

    private GpsStatus.Listener myListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int i) {

        }
    };


    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            if (preLocation!=null){
                long subTime=(System.currentTimeMillis()-preTime1)/1000;
                float d = preLocation.distanceTo(location);
                float v = (subTime==0)?0:(d/subTime);
                speed = getInstantaneousSpeed(v);
                distance+=d;
                calorie+=getCalorie(d);
                if (distance-preDistance1>1000){
                    speedStr.append(String.format(",%d",time-preTime));
                    preDistance1+=1000;
                    preTime = time;
                }
                latStr.append(String.format(",%d",(int)((location.getLatitude()-lat)*1000000)));
                lngStr.append(String.format(",%d",(int)((location.getLongitude()-lng)*1000000)));
                if (iGpsView!=null)
                    iGpsView.upDateRunData(speed,distance,calorie);
                if (mapFragment!=null&&!mapFragment.isHidden()){
                    mapFragment.setData(speed,distance,calorie);
                    Log.d("LOCATION******","fragment 没有隐藏");
                }else {
                    Log.d("LOCATION******","fragment 已经隐藏");
                }
            }else {
                lat = location.getLatitude();
                lng = location.getLongitude();
                latStr.append(String.format(",%d",(int)(lat*1000000)));
                lngStr.append(String.format(",%d",(int)(lng*1000000)));
            }
            preLocation=location;
            preTime1=System.currentTimeMillis();
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
