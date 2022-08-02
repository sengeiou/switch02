package com.szip.jswitch.Activity.gpsSport;


import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.szip.jswitch.Activity.BaseActivity;
import com.szip.jswitch.Activity.sport.SportTrackActivity;
import com.szip.jswitch.BLE.EXCDController;
import com.szip.jswitch.DB.SaveDataUtil;
import com.szip.jswitch.DB.dbModel.SportData;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.MapUtilGaodeImp;
import com.szip.jswitch.Util.MapUtilGoogleImp;
import com.szip.jswitch.Util.StatusBarCompat;
import com.szip.jswitch.View.MyAlerDialog;
import com.szip.jswitch.View.PulldownUpdateView;

public class GpsActivity extends BaseActivity implements IGpsView, OnMapReadyCallback {

    private TextView distanceTv,speedTv,timeTv,calorieTv,countDownTv,distanceMapTv,speedMapTv,calorieMapTv;
    private View switchView;
    private ImageView lockIv,mapIv,switchIv,gpsIv,gpsMapIv;
    private RelativeLayout switchRl,finishRl,mapRl;
    private FrameLayout lockFl,startTimeFl;
    private RelativeLayout updateRl;

    private IGpsPresenter isportPresenter;

    private long countDownTime = 3;

    private int sportType = 0;

    private MapView mapView;
    private com.amap.api.maps.MapView gaodeView;
    private IMapUtil iMapUtil;
    private Bundle bundle;

    private PulldownUpdateView updateView;

    private ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1f, 1f,
            1f, 50f, 50f);
    private ScaleAnimation touchAnimation = new ScaleAnimation(1f, 0.9f, 1f,
            0.9f, 50f, 50f);

    private AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
    private AlphaAnimation alphaAnimation1 = new AlphaAnimation(1,0);
    private long firstime = 0;
    private boolean started = false;

    private Sensor stepCounter;

    /** Called when the activity is first created. */

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_gps);
        bundle = savedInstanceState;
        initView();
        initEvent();
        initAnimation();
        sportType = getIntent().getIntExtra("sportType",2);
        stepCounter = ((SensorManager)getSystemService(SENSOR_SERVICE)).getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(sportType==-1){
            isportPresenter = new DevicePresenterImpl(getApplicationContext(),this);
        }else {
            if (stepCounter==null)
                isportPresenter = new GpsPresenterImpl(getApplicationContext(),this, sportType);
            else
                isportPresenter = new StepPresenterImpl(getApplicationContext(),stepCounter,this,sportType);
        }
        isportPresenter.startLocationService();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (iMapUtil!=null)
            iMapUtil.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iMapUtil!=null)
            iMapUtil.onDestroy();
        isportPresenter.stopLocationService();
        isportPresenter.setViewDestory();
    }



    private void initView() {
        StatusBarCompat.translucentStatusBar(this,true);
        setAndroidNativeLightStatusBar(this,true);
        updateView = findViewById(R.id.updateView);
        distanceTv = findViewById(R.id.distanceTv);
        speedTv = findViewById(R.id.speedTv);
        timeTv = findViewById(R.id.timeTv);
        calorieTv = findViewById(R.id.calorieTv);
        distanceMapTv = findViewById(R.id.distanceMapTv);
        speedMapTv = findViewById(R.id.speedMapTv);
        calorieMapTv = findViewById(R.id.calorieMapTv);
        lockIv = findViewById(R.id.lockIv);
        mapIv = findViewById(R.id.mapIv);
        gpsMapIv = findViewById(R.id.gpsMapIv);
        mapRl = findViewById(R.id.mapRl);
        switchIv = findViewById(R.id.switchIv);
        switchRl = findViewById(R.id.switchRl);
        finishRl = findViewById(R.id.finishRl);
        updateRl = findViewById(R.id.updateRl);
        lockFl = findViewById(R.id.lockFl);
        startTimeFl = findViewById(R.id.startTimeFl);
        countDownTv = findViewById(R.id.countDownTv);
        switchView = findViewById(R.id.switchView);
        gpsIv = findViewById(R.id.gpsIv);

        mapView = findViewById(R.id.googleMap);
        gaodeView = findViewById(R.id.gaodeMap);

        if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
            gaodeView.setVisibility(View.VISIBLE);
            iMapUtil = new MapUtilGaodeImp(gaodeView);
            iMapUtil.onCreate(bundle);
            iMapUtil.setUpMap();
        } else {
            mapView.setVisibility(View.VISIBLE);
            mapView.onCreate(bundle);
            mapView.onResume();

            try {
                MapsInitializer.initialize(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
            if (ConnectionResult.SUCCESS != errorCode) {
                GooglePlayServicesUtil.getErrorDialog(errorCode, GpsActivity.this, 0).show();
            } else {
                mapView.getMapAsync(this);
            }
        }
    }

    private void initEvent() {
        updateView.setListener(pulldownListener);
        lockIv.setOnClickListener(onClickListener);
        findViewById(R.id.backIv).setOnClickListener(onClickListener);
        mapIv.setOnClickListener(onClickListener);
        switchRl.setOnClickListener(onClickListener);
        finishRl.setOnClickListener(onClickListener);
    }

    /**
     * 初始化动画
     * */
    private void initAnimation() {
        touchAnimation.setDuration(50);//设置动画持续时间
        touchAnimation.setRepeatCount(0);//设置重复次数
        touchAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setDuration(200);
        alphaAnimation.setRepeatCount(0);
        alphaAnimation1.setDuration(200);
        alphaAnimation1.setRepeatCount(0);
        scaleAnimation.setDuration(1000);//设置动画持续时间
        scaleAnimation.setRepeatCount(3);//设置重复次数
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startTimeFl.setVisibility(View.GONE);
                if (isportPresenter !=null)
                    isportPresenter.startLocationService();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (countDownTime==1)
                    countDownTv.setText("GO!");
                else
                    countDownTv.setText(String.valueOf(--countDownTime));
            }
        });
    }

    /**
     * 控件下拉监听
     * */
    private PulldownUpdateView.PulldownListener pulldownListener = new PulldownUpdateView.PulldownListener() {
        @Override
        public void updateNow() {
            lockFl.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.lockIv:
                    updateRl.setVisibility(View.VISIBLE);
                    lockFl.setVisibility(View.VISIBLE);
                    break;
                case R.id.mapIv:
                    mapRl.startAnimation(alphaAnimation);
                    mapRl.setVisibility(View.VISIBLE);
                    break;
                case R.id.backIv:
                    mapRl.startAnimation(alphaAnimation1);
                    mapRl.setVisibility(View.GONE);
                    break;
                case R.id.switchRl:
                    switchRl.startAnimation(touchAnimation);
                    if (switchRl.getTag().equals("start")){
                        if (isportPresenter !=null)
                            isportPresenter.startLocationService();
                    }else {
                        if (isportPresenter !=null)
                            isportPresenter.stopLocationService();
                    }
                    break;
                case R.id.finishRl:
                    MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.runFinsh), getString(R.string.confirm), getString(R.string.cancel), false,
                            new MyAlerDialog.AlerDialogOnclickListener() {
                                @Override
                                public void onDialogTouch(boolean flag) {
                                    if (flag){
                                        switchRl.startAnimation(touchAnimation);
                                        if (isportPresenter !=null)
                                            isportPresenter.finishLocationService();
                                    }
                                }
                            },GpsActivity.this);

                    break;
            }
        }
    };

    @Override
    public void startCountDown() {
        started = true;
        startTimeFl.setVisibility(View.VISIBLE);
        updateRl.setVisibility(View.GONE);
        countDownTv.startAnimation(scaleAnimation);
    }

    @Override
    public void startRun() {
        switchView.setBackgroundResource(R.drawable.bg_circle_white);
        switchIv.setImageResource(R.mipmap.sport_icon_stop);
        switchRl.setTag("");
        finishRl.setVisibility(View.GONE);
    }

    @Override
    public void stopRun() {
        switchView.setBackgroundResource(R.drawable.bg_circle_green);
        switchIv.setImageResource(R.mipmap.sport_icon_continue);
        switchRl.setTag("start");
        finishRl.setVisibility(View.VISIBLE);
    }


    @Override
    public void saveRun(final SportData sportData) {
        if (sportData!=null){
            if (sportData.time>30){
                SaveDataUtil.newInstance().saveSportData(sportData);
                Intent intent = new Intent(GpsActivity.this, SportTrackActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("sport",sportData);
                intent.putExtra("data",bundle);
                startActivity(intent);
            }else {
                showToast(getString(R.string.runShort));
            }
        }
        finish();
    }

    @Override
    public void upDateTime(final int time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeTv.setText(String.format("%02d:%02d:%02d",time/60/60,time/60%60,time%60));
            }
        });
    }

    @Override
    public void upDateRunData(int speed, float distance, float calorie,float acc) {
        speedTv.setText(String.format("%d'%d''",speed/60,speed%60));
        distanceTv.setText(String.format("%.2f",distance/1000));
        calorieTv.setText(String.format("%.1f",calorie));
        if (acc>=29){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_1);
            gpsMapIv.setImageResource(R.mipmap.sport_icon_gps_1);
        }else if (acc>=15){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_2);
            gpsMapIv.setImageResource(R.mipmap.sport_icon_gps_1);
        }else {
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_3);
            gpsMapIv.setImageResource(R.mipmap.sport_icon_gps_1);
        }
    }

    /**
     * 双击退出
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(mapRl.getVisibility()==View.VISIBLE){
                mapRl.startAnimation(alphaAnimation1);
                mapRl.setVisibility(View.GONE);
                return true;
            }else {
                long secondtime = System.currentTimeMillis();
                if (secondtime - firstime > 3000) {
                    Toast.makeText(this, getString(R.string.touchAgain1),
                            Toast.LENGTH_SHORT).show();
                    firstime = System.currentTimeMillis();
                    return true;
                } else {
                    //todo 发送数据结束到手表端
//                if(MyApplication.getInstance().isMtk()&& MainService.getInstance().getState()==3)
//                    EXCDController.getInstance().writeForControlSport(3);
                    finish();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void updateLocation(Location location) {
        iMapUtil.setLocation(location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        iMapUtil = new MapUtilGoogleImp(googleMap);
        iMapUtil.setUpMap();
    }
}