package com.szip.jswitch.Activity.main;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TabHost;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTabHost;

import com.mediatek.wearable.WearableManager;
import com.szip.jswitch.BuildConfig;
import com.szip.jswitch.Model.HttpBean.CheckUpdateBean;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.JsonGenericsSerializator;
import com.szip.jswitch.Util.LocationUtil;
import com.szip.jswitch.Util.LogUtil;
import com.szip.jswitch.View.HostTabView;
import com.szip.jswitch.View.MyAlerDialog;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;

public class MainPresenterImpl implements IMainPrisenter{

    private IMainView iMainView;
    private Context context;
    private IGetLocation iGetLocation;
    private LocationManager locationManager;

    public MainPresenterImpl(IMainView iMainView, Context context) {
        this.iMainView = iMainView;
        this.context = context;
    }

    @Override
    public void checkBluetoochState() {
        //判断蓝牙状态
        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        if (!blueadapter.isEnabled()) {
            Intent bleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(bleIntent);
        }
    }

    @Override
    public void checkUpdata() {
        if (iMainView!=null)
            iMainView.checkVersionFinish();
//        if(!BuildConfig.FLAVORS.equals("")){
//            boolean isInstalled = isInstalled(BuildConfig.FLAVORS,context);
//
//            if (isInstalled){
//                try {
//                    String ver = context.getPackageManager().getPackageInfo("com.szip.jswitch",
//                            0).versionName;
//                    HttpMessgeUtil.getInstance().postForCheckUpdate(ver, new GenericsCallback<CheckUpdateBean>(new JsonGenericsSerializator()) {
//                        @Override
//                        public void onError(Call call, Exception e, int id) {
//                            if (iMainView!=null)
//                                iMainView.checkVersionFinish();
//                        }
//
//                        @Override
//                        public void onResponse(final CheckUpdateBean response, int id) {
//                            if (response.getCode() == 200){
//                                if (response.getData().getNewVersion()!=null){//有更新
//                                    if (MyApplication.getInstance().isNewVersion()){//之前已经提示过
//                                        MyApplication.getInstance().setNewVersion(true);
//                                    }else {//还未弹框提示过
//                                        MyAlerDialog.getSingle().showAlerDialog(context.getString(R.string.tip), context.getString(R.string.newVersion),
//                                                context.getString(R.string.confirm), context.getString(R.string.cancel), false, new MyAlerDialog.AlerDialogOnclickListener() {
//                                                    @Override
//                                                    public void onDialogTouch(boolean flag) {
//                                                        if (flag){
//                                                            MyApplication.getInstance().setNewVersion(false);
//                                                            try {
//                                                                Uri uri = Uri.parse("market://details?id=com.szip.jswitch");
//                                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                                                                intent.setPackage(BuildConfig.FLAVORS);
//                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                                context.startActivity(intent);
//                                                            } catch (Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                        }else {
//                                                            MyApplication.getInstance().setNewVersion(true);
//                                                        }
//                                                    }
//                                                },context);
//                                    }
//                                }else {//无更新
//                                    MyApplication.getInstance().setNewVersion(false);
//                                }
//                                if (iMainView!=null)
//                                    iMainView.checkVersionFinish();
//                            }
//                        }
//                    });
//
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }else {
//            if (iMainView!=null)
//                iMainView.checkVersionFinish();
//        }
    }

    private boolean isInstalled(@NonNull String packageName, Context context) {
        if ("".equals(packageName) || packageName.length() <= 0) {
            return false;
        }

        PackageInfo packageInfo;

        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void checkGPSState() {
        MyApplication.getInstance().checkGpsState(context);
        if (iMainView!=null){
            iMainView.checkGPSFinish();
        }
    }

    @Override
    public void initBle() {
        if (MyApplication.getInstance().getUserInfo().getDeviceCode()!=null){//已绑定
            //连接设备
            LogUtil.getInstance().logd("SZIP******","state = "+ WearableManager.getInstance().getConnectState());
            if (MainService.getInstance().getState()==WearableManager.STATE_NONE){
                WearableManager.getInstance().scanDevice(true);
            }else if (MainService.getInstance().getState() == WearableManager.STATE_CONNECT_LOST||
                    MainService.getInstance().getState() == WearableManager.STATE_LISTEN){
                if (MyApplication.getInstance().isMtk()){
                    BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(MyApplication.getInstance().getUserInfo().getDeviceCode());
                    WearableManager.getInstance().setRemoteDevice(device);
                }
                MainService.getInstance().startConnect();
            }
        }
        if (iMainView!=null){
            iMainView.initBleFinish();
        }
    }

    @Override
    public void getLoction(LocationManager locationManager,IGetLocation iGetLocation) {
        if (iGetLocation!=null)
            LocationUtil.getInstance().getLocation(locationManager,true,myListener,locationListener);
        this.locationManager = locationManager;
        this.iGetLocation = iGetLocation;
    }

    @Override
    public void setViewDestory() {
        iMainView = null;
        iGetLocation = null;
    }

    @Override
    public void checkNotificationState() {
        if (!isNotificationListenerActived())
            showNotifiListnerPrompt();
    }

    @Override
    public void initHost(FragmentTabHost fragmentTabHost) {

        final ArrayList<HostTabView> mTableItemList = new ArrayList<>();
        //添加tab
        mTableItemList.add(new HostTabView(R.mipmap.tab_icon_health,R.mipmap.tab_icon_health_pre,R.string.healthy, HealthyFragment.class,context));
        mTableItemList.add(new HostTabView(R.mipmap.tab_icon_sport,R.mipmap.tab_icon_sport_pre,R.string.sport, SportFragment.class,context));
        mTableItemList.add(new HostTabView(R.mipmap.tab_icon_my,R.mipmap.tab_icon_my_pre,R.string.mine, MineFragment.class,context));


        //去掉分割线
        fragmentTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0; i<mTableItemList.size(); i++) {
            HostTabView tabItem = mTableItemList.get(i);
            //实例化一个TabSpec,设置tab的名称和视图
            TabHost.TabSpec tabSpec = fragmentTabHost.newTabSpec(tabItem.getTitleString()).setIndicator(tabItem.getView());
            fragmentTabHost.addTab(tabSpec,tabItem.getFragmentClass(),null);

            //给Tab按钮设置背景
            fragmentTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#00ffffff"));

            //默认选中第一个tab
            if(i == 0) {
                tabItem.setChecked(true);
            }
        }

        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                //重置Tab样式
                for (int i = 0; i< mTableItemList.size(); i++) {
                    HostTabView tabitem = mTableItemList.get(i);
                    if (tabId.equals(tabitem.getTitleString())) {
                        tabitem.setChecked(true);
                    }else {
                        tabitem.setChecked(false);
                    }
                }
            }
        });

        if (iMainView!=null)
            iMainView.initHostFinish(mTableItemList);
    }

    private boolean isNotificationListenerActived() {
        String packageName = context.getPackageName();
        String strListener = Settings.Secure.getString(context.getContentResolver(),
                "enabled_notification_listeners");
        return strListener != null
                && strListener
                .contains(packageName);
    }

    private void showNotifiListnerPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.notificationlistener_prompt_title);
        builder.setMessage(R.string.notificationlistener_prompt_content);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Go to notification listener settings
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.startActivity(new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });
        builder.create().show();
    }


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (iGetLocation!=null){
                iGetLocation.onLocation(location);
            }
            locationManager.removeUpdates(locationListener);
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
}
