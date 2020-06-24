package com.szip.sportwatch;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mediatek.wearable.WearableManager;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.szip.sportwatch.Broadcat.UtilBroadcat;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Interface.HttpCallbackWithUserInfo;
import com.szip.sportwatch.Model.HttpBean.UserInfoBean;
import com.szip.sportwatch.Model.HttpBean.WeatherBean;
import com.szip.sportwatch.Model.UserInfo;
import com.szip.sportwatch.Model.WeatherModel;
import com.szip.sportwatch.Notification.IgnoreList;
import com.szip.sportwatch.Notification.MyNotificationReceiver;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.FileUtil;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.TopExceptionHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.ArrayRes;


/**
 * Created by Administrator on 2019/11/28.
 */

public class MyApplication extends Application implements HttpCallbackWithUserInfo{

    private SharedPreferences sharedPreferences;
    private int mFinalCount;
    static public String FILE = "sportWatch";

    private UserInfo userInfo;
    private boolean isRun = true;

    /**
     * 启动状态 0：登录状态 1：未登录状态 2：登录过期状态
     * */
    private int startState = 0;


    private static MyApplication mInstance;
    private boolean camerable;//能否使用照相机

    private int updownTime;
    private Thread updownDataThread;//上传数据的线程

    public static MyApplication getInstance(){
        return mInstance;
    }

    private ArrayList<WeatherBean.Condition> weatherModel;
    private String city;

    private String deviceNum;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Heal","onCreate");

        mInstance = this;
        FlowManager.init(this);

        /**
         * 注册广播
         * */
        UtilBroadcat broadcat = new UtilBroadcat(getApplicationContext());
        broadcat.onRegister();

        /**
         * 把log上传到云端
         * */
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));

        //初始化文件存储
        FileUtil.getInstance().initFile();

        //注册网络回调
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithUserInfo(this);

        //初始化不推送的应用
        initIgnoreList();

        /**
         * 拿去本地缓存的数据
         * */
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
        //获取上次退出之后剩余的倒计时上传时间
        updownTime = sharedPreferences.getInt("updownTime",3600);

        camerable = sharedPreferences.getBoolean("camera",false);

        //判断登录状态
        String token = sharedPreferences.getString("token",null);
        if (token!=null){//已登录
            HttpMessgeUtil.getInstance(this).setToken(token);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRun){
                        try {
                            Log.d("SZIP******","GET USER1");
                            HttpMessgeUtil.getInstance(MyApplication.this).getForGetInfo();
                            Thread.sleep(2000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }



        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mFinalCount++;
                //如果mFinalCount ==1，说明是从后台到前台
                Log.e("onActivityStarted", mFinalCount + "");
                if (mFinalCount == 1) {
                    //说明从后台回到了前台
                    Log.i("SZIP******", " 返回到了 前台");
                    if(sharedPreferences==null)
                        sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
                    //判断登录状态
                    String token = sharedPreferences.getString("token",null);
                    if (token==null){//未登录
                        startState = 1;
                    }else {//已登录
                        startState = 0;
                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mFinalCount--;
                //如果mFinalCount ==0，说明是前台到后台

                Log.i("onActivityStopped", mFinalCount + "");
                if (mFinalCount == 0) {
                    //说明从前台回到了后台
                    Log.i("SZIP******", " 切换到了 后台");
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });





        String packageName = getPackageName();
        String strListener = Settings.Secure.getString(this.getContentResolver(),
                "enabled_notification_listeners");
        if (strListener != null
                && strListener
                .contains(packageName)) {
            ComponentName localComponentName = new ComponentName(this, MyNotificationReceiver.class);
            PackageManager localPackageManager = this.getPackageManager();
            localPackageManager.setComponentEnabledSetting(localComponentName, 2, 1);
            localPackageManager.setComponentEnabledSetting(localComponentName, 1, 1);
        }

       startUpdownThread();
    }

    /**
     * 倒计时累计一个小时就上传一次数据到云端
     * */
    private void startUpdownThread(){

        updownDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                        updownTime--;
                        if (updownTime == 0)
                            break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (getUserInfo().getDeviceCode()!=null){
                    try {
                        String datas = MathUitl.getStringWithJson(getSharedPreferences(FILE,MODE_PRIVATE));
                        HttpMessgeUtil.getInstance(MyApplication.this).postForUpdownReportData(datas);
                        updownTime = 3600;
                        startUpdownThread();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updownDataThread.start();
    }


    private void initIgnoreList() {
        HashSet<String> exclusionList = IgnoreList.getInstance().getExclusionList();
        List<PackageInfo> packagelist = getPackageManager().getInstalledPackages(0);

        for (PackageInfo packageInfo : packagelist) {
            if (packageInfo != null) {
                // Whether this package should be exclude;
                if (exclusionList.contains(packageInfo.packageName)) {
                    continue;
                }
                // Add app name
                String appName = packageInfo.packageName;
                // Add to package list
                if (MathUitl.isSystemApp(packageInfo.applicationInfo)) {
                    IgnoreList.getInstance().addIgnoreItem(appName);
                }else {
                    if (!(appName.equals("com.tencent.mm")||appName.equals("com.tencent.mobileqq")))
                        IgnoreList.getInstance().addIgnoreItem(appName);
                }
            }
        }
        IgnoreList.getInstance().saveIgnoreList();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        MathUitl.saveInfoData(this,userInfo).commit();
    }

    public int getStartState() {
        return startState;
    }

    public boolean isCamerable() {
        return camerable;
    }

    public void setCamerable(boolean camerable) {
        this.camerable = camerable;
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("camera",camerable).commit();
    }

    @Override
    public void onUserInfo(UserInfoBean userInfoBean) {
        Log.d("SZIP******","GET USER");
        isRun = false;
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithUserInfo(null);
        if (userInfoBean.getCode() == 401){//登录过期
            startState = 2;
        }else {//保存用户信息
            setUserInfo(userInfoBean.getData());
        }
    }

    public int getUpdownTime() {
        return updownTime;
    }

    public ArrayList<WeatherBean.Condition> getWeatherModel() {
        if (weatherModel==null){
            String weather = sharedPreferences.getString("weatherList",null);
            if (weather == null)
                return null;
            else {
                Gson gson = new Gson();
                ArrayList<WeatherBean.Condition> bean = gson.fromJson(weather, new TypeToken<ArrayList<WeatherBean.Condition>>(){}.getType());
                return bean;
            }
        }else
            return weatherModel;
    }

    public String getCity() {
        if (city==null){
            String city1 = sharedPreferences.getString("city",null);
            return city1;
        }
        return city;
    }

    public void setWeatherModel(WeatherBean weatherBean) {
        try {
            Gson gson=new Gson();
            this.weatherModel = weatherBean.getData().getForecasts();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            JSONArray array = null;
            array = new JSONArray(gson.toJson(weatherModel));
            Log.d("SZIP******", "weather = "+array.toString());
            editor.putString("weatherList",array.toString());
            editor.putString("city",weatherBean.getData().getLocation().getCity());
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("deviceNum",deviceNum);
        editor.commit();
    }



    public boolean getSportVisiable(){
        if (deviceNum==null){
            deviceNum = sharedPreferences.getString("deviceNum",null);
            if (deviceNum==null)
                return true;
        }

        if (deviceNum.equals("249")||deviceNum.equals("242")){
            return false;
        }else {
            return true;
        }
    }
}
