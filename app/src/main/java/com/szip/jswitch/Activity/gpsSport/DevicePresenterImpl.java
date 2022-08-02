package com.szip.jswitch.Activity.gpsSport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.szip.jswitch.BLE.EXCDController;
import com.szip.jswitch.DB.SaveDataUtil;
import com.szip.jswitch.DB.dbModel.SportData;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.Util.LocationUtil;
import com.szip.jswitch.View.MyAlerDialog;

import java.util.Timer;
import java.util.TimerTask;

public class DevicePresenterImpl implements IGpsPresenter{

    private Context context;
    private IGpsView iGpsView;

    private Location preLocation;
    private LocationManager locationManager;

    private boolean firstTime = true;

    private int time = 0;//运动时长
    private int uiTime = 0;//上次更新UI的时间，用于定时更新ui
    private float distance = 0;//两个点之间的距离
    private float calorie = 0;
    private int speed = 0;
    private StringBuffer latStr = new StringBuffer();
    private StringBuffer lngStr = new StringBuffer();
    private double lat;
    private double lng;

    private DialogFragment mapFragment;

    private Timer timer;
    private TimerTask timerTask;

    private DeviceDataBroadcase deviceDataBroadcase;
    private boolean isStart = false;


    public DevicePresenterImpl(Context context, IGpsView iGpsView) {
        this.context = context;
        this.iGpsView = iGpsView;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    @Override
    public void startLocationService() {
        if (firstTime){
            if (iGpsView!=null)
                iGpsView.startCountDown();
            firstTime = false;
            deviceDataBroadcase = new DeviceDataBroadcase();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.szip.update.sport");
            intentFilter.addAction("com.szip.control.sport");
            context.registerReceiver(deviceDataBroadcase,intentFilter);
            start();
        }else {
            if (isStart&&locationManager!=null) {
                if (MainService.getInstance().getState()==3)
                    EXCDController.getInstance().writeForControlSport(2);
            }
            isStart = true;
        }
    }

    private void start(){
        LocationUtil.getInstance().getLocation(locationManager,false,myListener,locationListener);
        initTask();
        timer.schedule(timerTask,0,1000);
        if (iGpsView!=null)
            iGpsView.startRun();
    }


    @Override
    public void stopLocationService() {
        if (MainService.getInstance().getState()==3)
            EXCDController.getInstance().writeForControlSport(1);
    }

    private void stop(){
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
        if (MainService.getInstance().getState()==3)
            EXCDController.getInstance().writeForControlSport(0);
    }

    private void initTask(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time++;
                if (iGpsView!=null)
                    iGpsView.upDateTime(time);
            }
        };
    }

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
    float acc = 15;
    private void updateWithNewLocation(Location location) {

        Log.d("LOCATION******",location.toString());
        if (location != null) {
            acc = location.getAccuracy();
            if (preLocation!=null){//非第一次获取经纬度
                if (time-uiTime>4&&location.getLatitude()!=0&&location.getLongitude()!=0){
                    latStr.append(String.format(",%d",(int)((location.getLatitude()-lat)*1000000)));//第二次开始的经纬度数据只保存相对第一次的增减量，不需要全部保存
                    lngStr.append(String.format(",%d",(int)((location.getLongitude()-lng)*1000000)));
                    preLocation=location;
                }
            }else {//第一次获取到经纬度
                lat = location.getLatitude();
                lng = location.getLongitude();
                latStr.append(String.format(",%d",(int)(lat*1000000)));//把第一组经纬度*1000000存到数据库备用
                lngStr.append(String.format(",%d",(int)(lng*1000000)));
                preLocation=location;
            }
            if (iGpsView!=null)
                iGpsView.updateLocation(location);
        }
    }

    private GpsStatus.Listener myListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int i) {

        }
    };




    private void getSportData(String[] sport){
        if (iGpsView!=null){
            if(locationManager!=null){
                locationManager.removeUpdates(locationListener);
                if (timer!=null){
                    timer.cancel();
                    timerTask.cancel();
                }
            }
            if (sport==null){
                iGpsView.saveRun(null);
                return;
            }
            int type = Integer.valueOf(sport[0]);
            long time = DateUtil.getTimeScope(sport[1],"yyyy|MM|dd|HH|mm|ss");
            int sportTime = Integer.valueOf(sport[2]);
            int distance = Integer.valueOf(sport[3]);
            int calorie = Integer.valueOf(sport[4])*1000;
            int speed = Integer.valueOf(sport[5]);
            int heart = Integer.valueOf(sport[10]);
            int stride = Integer.valueOf(sport[7]);
            SportData sportData = new SportData(time,sportTime,distance,calorie,speed,type,heart,stride);
            if (!lngStr.toString().equals("")){//经度数组
                sportData.lngArray = lngStr.toString().substring(1);
            }
            if (!latStr.toString().equals("")){//纬度数组
                sportData.latArray = latStr.toString().substring(1);
            }

            Log.i("szip******","lat = "+latStr);
            Log.i("SZIP******","lng = "+lngStr);
            if (stride!=0){
                sportData.step = (int)((sportTime/60f)*stride);
            }
            iGpsView.saveRun(sportData);
        }

    }

//    @Override
//    public void openMap(FragmentManager fragmentManager) {
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        final Fragment prev = fragmentManager.findFragmentByTag("MAP");
//        if (prev != null){
//            ft.remove(prev).commit();
//            ft = fragmentManager.beginTransaction();
//        }
//        ft.addToBackStack(null);
//        if (context.getResources().getConfiguration().locale.getCountry().equals("CN")){
//            mapFragment = new GaoDeMapFragment(speed,distance,calorie,preLocation);
//            mapFragment.show(ft, "MAP");
//        }else {
//            mapFragment = new GoogleMapFragment(speed,distance,calorie,preLocation);
//            mapFragment.show(ft, "MAP");
//        }
//    }

    @Override
    public void setViewDestory() {
        iGpsView = null;
        context.unregisterReceiver(deviceDataBroadcase);
    }

    private class DeviceDataBroadcase extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case "com.szip.update.sport":{
                    distance = Integer.valueOf(intent.getStringExtra("distance"))/10f;
                    calorie = Integer.valueOf(intent.getStringExtra("calorie"))/10f;
                    speed = (int)(time/(distance/1000f));
                    if (iGpsView!=null)
                        iGpsView.upDateRunData(speed,distance,calorie,acc);
                    if (mapFragment !=null&&!mapFragment.isHidden()){
                        if (context.getResources().getConfiguration().locale.getCountry().equals("CN"))
                            ((GaoDeMapFragment)mapFragment).setData(speed,distance,calorie,acc);
                        else
                            ((GoogleMapFragment)mapFragment).setData(speed,distance,calorie,acc);
                    }
                }
                    break;
                case "com.szip.control.sport":
                    String cmd = intent.getStringExtra("cmd");
                    if (cmd.equals("pause")){
                        stop();
                    }else if (cmd.equals("start")){
                        start();
                    }else if (cmd.equals("stop")){
                        String index = intent.getStringExtra("index");
                        if (MainService.getInstance().getState()==3)
                            EXCDController.getInstance().writeForSportGPS(index);
                    }else if (cmd.equals("save")){
                        String data = intent.getStringExtra("data");
                        getSportData(data.split(","));
                    }else if (cmd.equals("finish")){
                        getSportData(null);
                    }
                    break;
            }
        }
    }
}
