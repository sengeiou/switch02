package com.szip.sportwatch;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Interface.HttpCallbackWithUserInfo;
import com.szip.sportwatch.Model.HttpBean.UserInfoBean;
import com.szip.sportwatch.Model.UserInfo;
import com.szip.sportwatch.Notification.IgnoreList;
import com.szip.sportwatch.Notification.MyNotificationReceiver;
import com.szip.sportwatch.Util.FileUtil;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.TopExceptionHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;


/**
 * Created by Administrator on 2019/11/28.
 */

public class MyApplication extends Application implements HttpCallbackWithUserInfo{

    private SharedPreferences sharedPreferences;
    private int mFinalCount;
    public static boolean isBackground = false;
    static public String FILE = "sportWatch";

    private UserInfo userInfo;
    private boolean isRun = true;

    /**
     * 启动状态 0：登录状态 1：未登录状态 2：登录过期状态
     * */
    private int startState = 0;


    private static MyApplication mInstance;
    private boolean camerable;//能否使用照相机

    public static MyApplication getInstance(){
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Heal","onCreate");

        mInstance = this;
        FlowManager.init(this);
        LoadDataUtil.newInstance().initCalendarPoint();
        /**
         * 把log上传到云端
         * */
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));

        //初始化文件存储
        FileUtil.getInstance().initFile(getExternalFilesDir(null).getPath());

        //注册网络回调
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithUserInfo(this);

        //初始化不推送的应用
        initIgnoreList();

        /**
         * 拿去本地缓存的数据
         * */
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
        //判断登录状态
        String token = sharedPreferences.getString("token",null);
        camerable = sharedPreferences.getBoolean("camera",false);
//        HttpMessgeUtil.getInstance(this).setUrl(sharedPreferences.getBoolean("isTest",false));
        if (token==null){//未登录
            startState = 1;
        }else {//已登录
            startState = 0;
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
                    MyApplication.isBackground = false;
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

                if (mFinalCount == 0) {
                    //说明从前台回到了后台
                    MyApplication.isBackground = true;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
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
}
