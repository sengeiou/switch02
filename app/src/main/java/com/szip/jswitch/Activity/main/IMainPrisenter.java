package com.szip.jswitch.Activity.main;

import android.location.LocationManager;

import androidx.fragment.app.FragmentTabHost;

public interface IMainPrisenter {

    //检查蓝牙状态
    void checkBluetoochState();
    //检查应用更新
    void checkUpdata();
    //检查GPS状态
    void checkGPSState();
    //检查通知状态
    void checkNotificationState();
    //初始化工具栏
    void initHost(FragmentTabHost fragmentTabHost);
    //蓝牙重连
    void initBle();
    //开启GPS定位
    void getLoction(LocationManager locationManager,IGetLocation iGetLocation);

    void setViewDestory();
}
