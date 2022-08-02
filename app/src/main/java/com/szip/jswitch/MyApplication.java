package com.szip.jswitch;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mediatek.wearable.WearableManager;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.szip.jswitch.BLE.EXCDController;
import com.szip.jswitch.Broadcat.UtilBroadcat;
import com.szip.jswitch.Activity.LoginActivity;
import com.szip.jswitch.DB.LoadDataUtil;
import com.szip.jswitch.DB.SaveDataUtil;
import com.szip.jswitch.DB.dbModel.NotificationData;
import com.szip.jswitch.DB.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.jswitch.Model.HttpBean.WeatherBean;
import com.szip.jswitch.Model.UserInfo;
import com.szip.jswitch.Notification.MyNotificationReceiver;
import com.szip.jswitch.Notification.NotificationView;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.FileUtil;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.LogUtil;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.Util.MusicUtil;
import com.szip.jswitch.Util.ProgressHudModel;
import com.szip.jswitch.View.MyAlerDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2019/11/28.
 */

public class MyApplication extends Application{

    private SharedPreferences sharedPreferences;
    private int mFinalCount;
    static public String FILE = "mycandy";

    private UserInfo userInfo;
    private String city;
    private String deviceNum;
    private ArrayList<WeatherBean.Condition> weatherModel;

    private static MyApplication mInstance;
    private boolean camerable;//能否使用照相机
    private boolean heartSwitch;//能否使用照相机

    private int updownTime;
    private Thread updownDataThread;//上传数据的线程

    public static MyApplication getInstance(){
        return mInstance;
    }

    private boolean isSyncSport =false;
    private boolean isFirst = true;
    private String BtMac;

    private BluetoothAdapter btAdapt;

    private boolean isNewVersion = false;

    private String privatePath;

    private SportWatchAppFunctionConfigDTO sportWatchAppFunctionConfigDTO;

    private int productId = 0;//用于判断用户用的是什么产品，0：手表 1:体重秤 2：两者都用

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setBtMac(final String btMac) {
        if (BtMac==null||!btMac.split(":")[0].equals(BtMac.split(":")[0])){
            String[] buff = btMac.split(":");
            BtMac = String.format("%02X:%02X:%02X:%02X:%02X:%02X",Integer.valueOf(buff[0],16),Integer.valueOf(buff[1],16),
                    Integer.valueOf(buff[2],16),Integer.valueOf(buff[3],16),Integer.valueOf(buff[4],16)
                    ,Integer.valueOf(buff[5],16));
            Log.d("SZIP******","MAC = "+BtMac);
        }
        if (BtMac.equals("00:00:00:00:00:00"))
            return;
        if (btAdapt == null)
            btAdapt = BluetoothAdapter.getDefaultAdapter();
        try {
            if (BtMac!=null) {
                BluetoothDevice btDev = btAdapt.getRemoteDevice(BtMac);
                Boolean returnValue = false;
                if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
                    //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);
                    Method createBondMethod = BluetoothDevice.class
                            .getMethod("createBond");
                    Log.d("SZIP******", "开始配对");
                    returnValue = (Boolean) createBondMethod.invoke(btDev);
                }
            }
        }catch (IllegalArgumentException e){
            Log.e("SZIP******",e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Heal","onCreate");

        mInstance = this;
        FlowManager.init(this);
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this,true);
        /**
         * 注册广播
         * */
        UtilBroadcat broadcat = new UtilBroadcat(getApplicationContext());
        broadcat.onRegister();

        /**
         * 把测试版的log导出到本地，上线的渠道版不需要导出
         * */
        if(BuildConfig.FLAVORS.equals(""))
            LogUtil.getInstance().init(this);

        /**
         * 初始化音乐控制器
         * */
        MusicUtil.getSingle().init(getApplicationContext());

        //初始化文件存储
        privatePath = getExternalFilesDir(null).getPath()+"/";
        FileUtil.getInstance().initFile(this);
        //注册网络回调
        HttpMessgeUtil.getInstance().init(this);
        //初始化通知栏
        NotificationView.getInstance().init(this);

        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            //只在Android O之上需要渠道，这里的第一个参数要和下面的channelId一样
            NotificationChannel notificationChannel = new NotificationChannel("0103", "MyCandy", NotificationManager.IMPORTANCE_HIGH);
            //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，通知才能正常弹出
            notificationChannel.setShowBadge(false);
            manager.createNotificationChannel(notificationChannel);
        }
        /**
         * 拿去本地缓存的数据
         * */
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);

        productId = sharedPreferences.getInt("productId",0);
        sportWatchAppFunctionConfigDTO = LoadDataUtil.newInstance().getDeviceConfig(sharedPreferences.getString("deviceName",null));
        isSyncSport = sharedPreferences.getBoolean("isSyncSport",false);
        //获取上次退出之后剩余的倒计时上传时间
        updownTime = sharedPreferences.getInt("updownTime",600);
        //获取手机缓存的远程拍照状态
        camerable = sharedPreferences.getBoolean("camera",false);
        heartSwitch = sharedPreferences.getBoolean("heartSwitch",false);
        //获取手机缓存的自动更新信息
        isNewVersion = sharedPreferences.getBoolean("version",false);
        initNotifyList();

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
                    if(isFirst){
                        isFirst = false;
                    }else {
                        if (isMtk()&&WearableManager.getInstance().getConnectState()==WearableManager.STATE_CONNECTED){
                            EXCDController.getInstance().writeForEnableSend(1);
                        }
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
                    if (isMtk()&&WearableManager.getInstance().getConnectState()==WearableManager.STATE_CONNECTED){
                        EXCDController.getInstance().writeForEnableSend(0);
                    }
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

//       startUpdownThread();
    }


    public boolean checkFaceType(int width,int height){
        String[] faceStr = getFaceType().split("\\*");
        int faceWidth = Integer.valueOf(faceStr[0]);
        int faceHeight = Integer.valueOf(faceStr[1]);
        if (width!=faceWidth||height!=faceHeight)
            return false;
        return true;
    }


    public String getPrivatePath() {
        return privatePath;
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
                        if (updownTime%20==0)
                            getSharedPreferences(FILE,MODE_PRIVATE).edit().putInt("updownTime",updownTime).commit();
                        updownTime--;
                        if (updownTime == 0)
                            break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (userInfo!=null&&userInfo.getDeviceCode()!=null){
                    try {
                        String datas = MathUitl.getStringWithJson(getSharedPreferences(FILE,MODE_PRIVATE));
                        HttpMessgeUtil.getInstance().postForUpdownReportData(datas);
                        updownTime = 600;
//                        startUpdownThread();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updownDataThread.start();
    }


    private void initNotifyList() {

        List<NotificationData> list = new ArrayList<>();
        list.add(new NotificationData("message", R.mipmap.cp_icon_empty, getString(R.string.message), true));
        list.add(new NotificationData("com.tencent.mm", R.mipmap.cp_icon_empty, getString(R.string.wechat), true));
        list.add(new NotificationData("com.tencent.mobileqq", R.mipmap.cp_icon_empty, getString(R.string.qq), true));
        list.add(new NotificationData("com.facebook.katana", R.mipmap.cp_icon_empty, getString(R.string.facebook), true));
        list.add(new NotificationData("com.facebook.orca", R.mipmap.cp_icon_empty, getString(R.string.facebook_message), true));
        list.add(new NotificationData("com.twitter.android", R.mipmap.cp_icon_empty, getString(R.string.twitter), true));
        list.add(new NotificationData("com.whatsapp", R.mipmap.cp_icon_empty, getString(R.string.whatsApp), true));
        list.add(new NotificationData("com.instagram.android", R.mipmap.cp_icon_empty, getString(R.string.instagram), true));
        list.add(new NotificationData("com.skype.rover", R.mipmap.cp_icon_empty, "Skype", true));
        list.add(new NotificationData("com.linkedin.android", R.mipmap.cp_icon_empty, "Linkedin", true));
        list.add(new NotificationData("jp.naver.line.android", R.mipmap.cp_icon_empty, "Line", true));
        list.add(new NotificationData("com.snapchat.android", R.mipmap.cp_icon_empty, "Snapchat", true));
        list.add(new NotificationData("com.pinterest", R.mipmap.cp_icon_empty, "Pinterest", true));
        list.add(new NotificationData("com.google.android.apps.plus", R.mipmap.cp_icon_empty, "Google+", true));
        list.add(new NotificationData("com.tumblr", R.mipmap.cp_icon_empty, "Tumblr", true));
        list.add(new NotificationData("com.viber.voip", R.mipmap.cp_icon_empty, "Viber", true));
        list.add(new NotificationData("com.vkontakte.android", R.mipmap.cp_icon_empty, "Vkontakte", true));
        list.add(new NotificationData("org.telegram.messenger", R.mipmap.cp_icon_empty, "Telegram", true));
        list.add(new NotificationData("com.zhiliaoapp.musically", R.mipmap.cp_icon_empty, "Tiktok", true));
        SaveDataUtil.newInstance().saveNotificationList(list);

    }

    public void checkGpsState(final Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!(gps || network)) {
            MyAlerDialog.getSingle().showAlerDialog(context.getString(R.string.tip), context.getString(R.string.checkGPS),
                    context.getString(R.string.confirm), context.getString(R.string.cancel),
                    false, new MyAlerDialog.AlerDialogOnclickListener() {
                        @Override
                        public void onDialogTouch(boolean flag) {
                            if (flag){
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(intent);
                            }
                        }
                    },context);
        }
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
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

    public boolean isHeartSwitch() {
        return heartSwitch;
    }

    public void setHeartSwitch(boolean heartSwitch) {
        this.heartSwitch = heartSwitch;
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("heartSwitch",heartSwitch).commit();
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
        return sharedPreferences.getString("city",null);
    }

    public float getElevation(){
        return sharedPreferences.getFloat("elevation",1f);
    }

    public void setWeatherModel(WeatherBean weatherBean) {
        try {
            Gson gson=new Gson();
            this.weatherModel = weatherBean.getData().getForecasts();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            JSONArray array = null;
            array = new JSONArray(gson.toJson(weatherModel));
            editor.putString("weatherList",array.toString());
            editor.putString("city",weatherBean.getData().getLocation().getCity());
            editor.putFloat("elevation",weatherBean.getData().getLocation().getElevation());
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

    public String getDeviceNum() {
        if (deviceNum==null){
            deviceNum = sharedPreferences.getString("deviceNum",null);
            if (deviceNum==null)
                return "0";
            else
                return deviceNum;
        }else
            return deviceNum;
    }


    public void setDeviceConfig(String deviceName) {
        Log.d("DATA******", "deviceName = " + deviceName);
        sharedPreferences.edit().putString("deviceName",deviceName).commit();
        sportWatchAppFunctionConfigDTO = LoadDataUtil.newInstance().getDeviceConfig(deviceName);
    }

    public boolean isMtk() {
        return  sportWatchAppFunctionConfigDTO==null?false:sportWatchAppFunctionConfigDTO.getUseMtkConnect()==1;
    }

    public boolean isCircle() {
        return sportWatchAppFunctionConfigDTO==null?false:sportWatchAppFunctionConfigDTO.getScreenType()==0;
    }

    public String getDialGroupId() {
        return sportWatchAppFunctionConfigDTO==null?"0":Integer.toString(sportWatchAppFunctionConfigDTO.getWatchPlateGroupId());
    }

    public String getFaceType() {
        return  sportWatchAppFunctionConfigDTO==null?"320*385":sportWatchAppFunctionConfigDTO.getScreen();
    }





    public boolean isSyncSport() {
        return isSyncSport;
    }


    public boolean isNewVersion() {
        return isNewVersion;
    }

    public void setNewVersion(boolean newVersion) {
        isNewVersion = newVersion;
        if (sharedPreferences!=null)
            sharedPreferences.edit().putBoolean("version",newVersion).commit();

    }



    public void setDialUrl(String url){
        sharedPreferences.edit().putString("dialUrl",url).commit();
    }

    public String getDiadUrl(){
        return sharedPreferences.getString("dialUrl","");
    }

    public boolean getSportVisiable(){
        if (deviceNum==null){
            deviceNum = sharedPreferences.getString("deviceNum",null);
            if (deviceNum==null)
                return true;
        }
        return LoadDataUtil.newInstance().getSportConfig(Integer.valueOf(deviceNum));
    }

    public void tokenTimeOut(){
        SharedPreferences sharedPreferences ;
        ProgressHudModel.newInstance().diss();

        sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",null);
        editor.commit();
        if (MainService.getInstance()!=null)
            MainService.getInstance().stopConnect();
        MathUitl.showToast(this,getString(R.string.tokenTimeOut));
        Intent intentmain=new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentmain);
    }
}
