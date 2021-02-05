package com.szip.sportwatch.Contorller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.mediatek.wearable.WearableManager;
import com.szip.sportwatch.Contorller.Fragment.HealthyFragment;
import com.szip.sportwatch.Contorller.Fragment.MineFragment;
import com.szip.sportwatch.Contorller.Fragment.SportFragment;
import com.szip.sportwatch.Model.HttpBean.CheckUpdateBean;
import com.szip.sportwatch.Model.UpdateSportView;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.Notification.NotificationView;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.JsonGenericsSerializator;
import com.szip.sportwatch.Util.LogUtil;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.HostTabView;
import com.szip.sportwatch.View.MyAlerDialog;
import com.szip.sportwatch.View.MyToastView;
import com.zhy.http.okhttp.callback.GenericsCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTabHost;

import okhttp3.Call;

public class MainActivity extends BaseActivity{

    private ArrayList<HostTabView> mTableItemList;
    private MyApplication app;
    private RelativeLayout layout;
    private boolean isVisiable = false;
    private long firstime = 0;
    /**
     * 淡入
     * */
    private AlphaAnimation alphaAnimation  = new AlphaAnimation(0f, 1f);
    /**
     * 淡出
     * */
    private AlphaAnimation alphaAnimation1  = new AlphaAnimation(1f, 0f);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        StatusBarCompat.translucentStatusBar(MainActivity.this,true);
        app = (MyApplication) getApplicationContext();
        layout = findViewById(R.id.layout);

        //判断蓝牙状态
        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        if (!blueadapter.isEnabled()) {
            Intent bleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(bleIntent);
        }

        checkUpdate();
        initBLE();
        initAnimation();
        initTabData();
        initHost();
        mTableItemList.get(1).setView(app.getSportVisiable());

    }

    private void checkUpdate() {
        try {
            String ver = getPackageManager().getPackageInfo("com.szip.sportwatch",
                    0).versionName;
            HttpMessgeUtil.getInstance(this).postForCheckUpdate(ver, new GenericsCallback<CheckUpdateBean>(new JsonGenericsSerializator()) {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(final CheckUpdateBean response, int id) {
                    if (response.getCode() == 200){
                        if (response.getData().getNewVersion()!=null){//有更新
                            if (app.isNewVersion()){//之前已经提示过
                                app.setNewVersion(true);
                                app.setVersionUrl(response.getData().getNewVersion().getUrl());
                            }else {//还未弹框提示过
                                MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.newVersion), getString(R.string.confirm), getString(R.string.cancel),
                                        false, new MyAlerDialog.AlerDialogOnclickListener() {
                                            @Override
                                            public void onDialogTouch(boolean flag) {
                                                if (flag){
                                                    app.setNewVersion(false);
                                                    MainService.getInstance().downloadFirmsoft(response.getData().getNewVersion()
                                                            .getUrl(),"iSmarport.apk");
                                                }else {
                                                    app.setNewVersion(true);
                                                    app.setVersionUrl(response.getData().getNewVersion().getUrl());
                                                }
                                            }
                                        },MainActivity.this);
                            }
                        }else {//无更新
                            app.setNewVersion(false);
                        }
                        isLocServiceEnable(MainActivity.this);
                    }
                }
            });

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            LogUtil.getInstance().logd("SZIP******","IOException = "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void initBLE() {
        if (app.getUserInfo().getDeviceCode()!=null){//已绑定
            //连接设备
            LogUtil.getInstance().logd("SZIP******","state = "+WearableManager.getInstance().getConnectState());
            if (MainService.getInstance().getState()==0){
                MainService.getInstance().setConnectAble(true);
                WearableManager.getInstance().scanDevice(true);
            }else if (MainService.getInstance().getState() == 1||MainService.getInstance().getState() == 5){
                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(app.getUserInfo().getDeviceCode());
                WearableManager.getInstance().setRemoteDevice(device);
                MainService.getInstance().startConnect();
            }
        }
    }

    private void isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!(gps || network)) {
            MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.checkGPS), getString(R.string.confirm), getString(R.string.cancel),
                    false, new MyAlerDialog.AlerDialogOnclickListener() {
                        @Override
                        public void onDialogTouch(boolean flag) {
                            if (flag){
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        }
                    },MainActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (app.isCamerable()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    requestPermissions(new String[]{Manifest.permission.CAMERA,
                            }, 101);
                }
            }
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.getInstance().logd("SZIP******","MAIN DESTROY");
        MainService.getInstance().stopConnect();
    }

    public void MyFinish(){
        LogUtil.getInstance().logd("SZIP******","MAIN DESTROY");
        MainService.getInstance().stopConnect();
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateSport(UpdateSportView updateSportView){
        mTableItemList.get(1).setView(app.getSportVisiable());
    }

    /**
     * 初始化标签
     * */
    private void initTabData() {
        mTableItemList = new ArrayList<>();
        //添加tab
        mTableItemList.add(new HostTabView(R.mipmap.tab_icon_health,R.mipmap.tab_icon_health_pre,R.string.healthy, HealthyFragment.class,this));
        mTableItemList.add(new HostTabView(R.mipmap.tab_icon_sport,R.mipmap.tab_icon_sport_pre,R.string.sport, SportFragment.class,this));
        mTableItemList.add(new HostTabView(R.mipmap.tab_icon_my,R.mipmap.tab_icon_my_pre,R.string.mine, MineFragment.class,this));
    }

    FragmentTabHost fragmentTabHost;
    /**
     * 初始化选项卡视图
     * */
    private void initHost() {
        //实例化FragmentTabHost对象
        fragmentTabHost = findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);

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
    }
    
    /**
     * 初始化动画
     * */
    private void initAnimation() {
        alphaAnimation.setDuration(500);//设置动画持续时间
        alphaAnimation.setRepeatCount(0);//设置重复次数
        alphaAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态

        alphaAnimation1.setDuration(500);//设置动画持续时间
        alphaAnimation1.setRepeatCount(0);//设置重复次数
        alphaAnimation1.setFillAfter(true);//动画执行完后是否停留在执行完的状态
    }


    /**
     * 中间自定义弹窗
     * */
    public void showMyToast(final String msg){
        if (!isVisiable){
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

            final LinearLayout linearLayout = MyToastView.getInstance(this).showToast(msg);
            linearLayout.startAnimation(alphaAnimation);
            layout.addView(linearLayout,layoutParams);
            isVisiable = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    linearLayout.startAnimation(alphaAnimation1);
                    layout.removeView(linearLayout);
                    isVisiable = false;
                }
            },2000);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101){
            int code = grantResults[0];
            if (!(code == PackageManager.PERMISSION_GRANTED)){
                showToast(getString(R.string.permissionErrorForCamare));
                app.setCamerable(false);
            }
        }
    }

    /**
     * 双击退出
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondtime = System.currentTimeMillis();
            if (secondtime - firstime > 3000) {
                Toast.makeText(this, getString(R.string.touchAgain),
                        Toast.LENGTH_SHORT).show();
                firstime = System.currentTimeMillis();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
