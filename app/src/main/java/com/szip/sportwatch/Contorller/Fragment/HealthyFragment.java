package com.szip.sportwatch.Contorller.Fragment;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szip.sportwatch.BLE.BleClient;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.Contorller.AnimalHeatActivity;
import com.szip.sportwatch.Contorller.BloodOxygenReportActivity;
import com.szip.sportwatch.Contorller.BloodPressureReportActivity;
import com.szip.sportwatch.Contorller.EcgListActivity;
import com.szip.sportwatch.Contorller.HeartReportActivity;
import com.szip.sportwatch.Contorller.SleepReportActivity;
import com.szip.sportwatch.Contorller.StepReportActivity;
import com.szip.sportwatch.Contorller.UserInfoActivity;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.DB.dbModel.HealthyConfig;
import com.szip.sportwatch.DB.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.sportwatch.Interface.MyListener;
import com.szip.sportwatch.Model.EvenBusModel.ConnectState;
import com.szip.sportwatch.Model.HealthyDataModel;
import com.szip.sportwatch.Model.HttpBean.WeatherBean;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.JsonGenericsSerializator;
import com.szip.sportwatch.Util.LocationUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ViewUtil;
import com.szip.sportwatch.View.CircularImageView;
import com.szip.sportwatch.View.ColorArcProgressBar;
import com.szip.sportwatch.View.HealthyProgressView;
import com.szip.sportwatch.View.PullToRefreshLayout;
import com.zhy.http.okhttp.callback.GenericsCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;
import static com.szip.sportwatch.MyApplication.FILE;

/**
 * Created by Administrator on 2019/12/1.
 */

public class HealthyFragment extends BaseFragment implements View.OnClickListener{

    private MyApplication app;
    /**
     * 计步圈
     * */
    private ColorArcProgressBar stepPb;
    /**
     * 健康游标控件
     * */
    private HealthyProgressView sleepPv;

    /**
     * 健康数据相关控件
     * */
    private TextView stepRadioTv,planStepTv,stepTv,distanceTv,kcalTv;
    private TextView sleepDataTv;
    private TextView heartDataTv;
    private TextView bloodDataTv;
    private TextView bloodODataTv;
    private TextView animalHeatDataTv;
    private TextView ecgDataTv;
    private TextView tempTv,conditionTv;
    private ImageView weatherIv;

    /**
     * 最近一次健康数据的model
     * */
    private HealthyDataModel healthyDataModel;

    private ViewUtil viewUtil;

    private BluetoothAdapter btAdapt;

    private HealthyConfig healthyConfig;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_healthy;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();

        initView();
        initEvent();
        initWeather();
    }

    private void initWeather() {
        updataWeatherView();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(FILE,MODE_PRIVATE);
        long time = sharedPreferences.getLong("weatherTime",0);
        if (Calendar.getInstance().getTimeInMillis()-time>60*60*1000){
            Log.d("LOCATION******","开始定位");
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationUtil.getInstance().getLocation(locationManager,myListener,locationListener);
        }
    }


    private void updataWeatherView() {
        if(app.getWeatherModel()!=null&&app.getCity()!=null){
            tempTv.setText(String.format(Locale.ENGLISH,"%d~%d℃",(int)app.getWeatherModel().get(0).getLow(),(int)app.getWeatherModel().get(0).getHigh()));
            Glide.with(getActivity()).load(app.getWeatherModel().get(0).getIconUrl()).into(weatherIv);
            conditionTv.setText(app.getCity()+" "+app.getWeatherModel().get(0).getText());
            conditionTv.setSelected(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (app.isMtk())
            EXCDController.getInstance().writeForCheckVersion();
        else
            BleClient.getInstance().writeForGetDeviceState();
        initData();
        if (conditionTv!=null)
            conditionTv.setSelected(true);



        if (btAdapt == null)
            btAdapt = BluetoothAdapter.getDefaultAdapter();
        try {
            if (app.getBtMac()!=null) {
                BluetoothDevice btDev = btAdapt.getRemoteDevice(app.getBtMac());
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
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        ((PullToRefreshLayout) getView().findViewById(R.id.refresh_view))
                .setOnRefreshListener(new MyListener());
        healthyConfig = LoadDataUtil.newInstance().getConfig(Integer.valueOf(app.getDeviceNum()));
        if (healthyConfig!=null){
            if(healthyConfig.sleep!=1){
                getView().findViewById(R.id.sleepRl).setVisibility(View.GONE);
            }else {
                getView().findViewById(R.id.sleepRl).setVisibility(View.VISIBLE);
            }

            if(healthyConfig.heartRate!=1){
                getView().findViewById(R.id.heartRl).setVisibility(View.GONE);
            }else {
                getView().findViewById(R.id.heartRl).setVisibility(View.VISIBLE);
            }

            if(healthyConfig.bloodPressure!=1){
                getView().findViewById(R.id.bloodPressureRl).setVisibility(View.GONE);
            }else {
                getView().findViewById(R.id.bloodPressureRl).setVisibility(View.VISIBLE);
            }

            if(healthyConfig.bloodOxygen!=1){
                getView().findViewById(R.id.bloodOxygenRl).setVisibility(View.GONE);
            }else {
                getView().findViewById(R.id.bloodOxygenRl).setVisibility(View.VISIBLE);
            }

            if(healthyConfig.ecg!=1){
                getView().findViewById(R.id.ecgRl).setVisibility(View.GONE);
            }else {
                getView().findViewById(R.id.ecgRl).setVisibility(View.VISIBLE);
            }

            if(healthyConfig.temperature!=1){
                getView().findViewById(R.id.animalHeatRl).setVisibility(View.GONE);
            }else {
                getView().findViewById(R.id.animalHeatRl).setVisibility(View.VISIBLE);
            }
        }

        stepPb = getView().findViewById(R.id.stepPb);
        sleepPv = getView().findViewById(R.id.sleepPv);

        stepRadioTv = getView().findViewById(R.id.stepRadioTv);
        planStepTv = getView().findViewById(R.id.planStepTv);
        stepTv = getView().findViewById(R.id.stepTv);
        distanceTv = getView().findViewById(R.id.dataTv);
        kcalTv = getView().findViewById(R.id.kcalTv);

        sleepDataTv = getView().findViewById(R.id.sleepDataTv);

        heartDataTv = getView().findViewById(R.id.heartDataTv);

        bloodDataTv = getView().findViewById(R.id.bloodDataTv);

        bloodODataTv = getView().findViewById(R.id.bloodODataTv);

        animalHeatDataTv = getView().findViewById(R.id.animalHeatDataTv);

        ecgDataTv = getView().findViewById(R.id.ecgDataTv);

        weatherIv = getView().findViewById(R.id.weatherIv);

        conditionTv = getView().findViewById(R.id.conditionTv);

        tempTv = getView().findViewById(R.id.tempTv);
    }

    private void initEvent() {
        getView().findViewById(R.id.pictureIv).setOnClickListener(this);
        getView().findViewById(R.id.weatherIv).setOnClickListener(this);
        getView().findViewById(R.id.stepRl).setOnClickListener(this);
        getView().findViewById(R.id.sleepRl).setOnClickListener(this);
        getView().findViewById(R.id.heartRl).setOnClickListener(this);
        getView().findViewById(R.id.bloodPressureRl).setOnClickListener(this);
        getView().findViewById(R.id.bloodOxygenRl).setOnClickListener(this);
        getView().findViewById(R.id.animalHeatRl).setOnClickListener(this);
        getView().findViewById(R.id.ecgRl).setOnClickListener(this);
        getView().findViewById(R.id.weatherLl).setOnClickListener(this);
    }

    @SuppressLint("StringFormatInvalid")
    private void initData() {
        viewUtil = new ViewUtil(getContext());
        healthyDataModel = LoadDataUtil.newInstance().getHealthyDataLast(DateUtil.getTimeOfToday());

        if (app.getUserInfo()!=null){
            stepPb.setMaxValues(app.getUserInfo().getStepsPlan());
            stepPb.setCurrentValues(healthyDataModel.getStepsData());
            stepTv.setText(healthyDataModel.getStepsData()+"");
            if (app.getUserInfo().getUnit().equals("metric")){
                distanceTv.setText(String.format(Locale.ENGLISH,"%.1f",healthyDataModel.getDistanceData()/10f));
                ((TextView)getView().findViewById(R.id.unitTv)).setText("m");
            } else{
                distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",MathUitl.metric2Miles(healthyDataModel.getDistanceData()/10)));
                ((TextView)getView().findViewById(R.id.unitTv)).setText("Mi");
            }
            kcalTv.setText(String.format(Locale.ENGLISH,"%.1f",healthyDataModel.getKcalData()/10f));
            planStepTv.setText(String.format(Locale.ENGLISH,getString(R.string.planStep),app.getUserInfo().getStepsPlan()));
            stepRadioTv.setText(String.format(Locale.ENGLISH,"%.1f%%",healthyDataModel.getStepsData()/(float)app.getUserInfo().getStepsPlan()*100));
        }

        if (healthyDataModel.getAllSleepData()!=0){
            sleepDataTv.setText(String.format(Locale.ENGLISH,"%.1fh/%.1fh",healthyDataModel.getAllSleepData()/60f,app.getUserInfo().getSleepPlan()/60f));
        }else{
            sleepDataTv.setText(String.format(Locale.ENGLISH,"%.1fh/%.1fh",0f,app.getUserInfo().getSleepPlan()/60f));
        }
        sleepPv.setSleepData(healthyDataModel.getAllSleepData()/(float)app.getUserInfo().getSleepPlan(),
                healthyDataModel.getAllSleepData(),healthyDataModel.getLightSleepData());
        viewUtil.setSleepView(healthyDataModel.getAllSleepData(),getView().findViewById(R.id.sleepStateTv));

        if (healthyDataModel.getHeartData()!=0){
            heartDataTv.setText(healthyDataModel.getHeartData()+"Bpm");
        }else {
            heartDataTv.setText("--Bpm");
        }
        viewUtil.setHeartView(healthyDataModel.getHeartData(), getView().findViewById(R.id.heartStateTv),
                getView().findViewById(R.id.heartPv));


        if (healthyDataModel.getSbpData()!=0){
            bloodDataTv.setText(String.format(Locale.ENGLISH,"%d/%dmmHg",healthyDataModel.getSbpData(),healthyDataModel.getDbpData()));
        }else {
            bloodDataTv.setText("--/--mmHg");
        }
        viewUtil.setBloodPressureView(healthyDataModel.getSbpData(),healthyDataModel.getDbpData(),
                getView().findViewById(R.id.bloodStateTv),getView().findViewById(R.id.sbpPv),getView().findViewById(R.id.dbpPv));


        if (healthyDataModel.getBloodOxygenData()!=0){
            bloodODataTv.setText(healthyDataModel.getBloodOxygenData()+"%");
        }else {
            bloodODataTv.setText("--%");
        }
        viewUtil.setBloodOxygenView(healthyDataModel.getBloodOxygenData(),getView().findViewById(R.id.bloodOStateTv),
                getView().findViewById(R.id.bloodOxygenPv));


        if (healthyDataModel.getAnimalHeatData()!=0){
            animalHeatDataTv.setText(String.format(Locale.ENGLISH,"%.1f℃",healthyDataModel.getAnimalHeatData()/10f));
        }else {
            animalHeatDataTv.setText("--℃");
        }
        viewUtil.setAnimalHeatView(healthyDataModel.getAnimalHeatData(),getView().findViewById(R.id.animalHeatStateTv),
                getView().findViewById(R.id.animalHeatPv));


        if (healthyDataModel.getEcgData()!=0){
            ecgDataTv.setText(healthyDataModel.getEcgData()+"Bpm");
        }else {
            ecgDataTv.setText("--Bpm");
        }


        if (app.getUserInfo().getAvatar()!=null)
            Glide.with(this).load(app.getUserInfo().getAvatar()).into( ((CircularImageView)getView().findViewById(R.id.pictureIv)));
        else
            ((CircularImageView)getView().findViewById(R.id.pictureIv)).setImageResource(app.getUserInfo().getSex()==1?R.mipmap.my_head_male_52:
                    R.mipmap.my_head_female_52);
    }


    /**
     * 更新数据显示
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdataView(ConnectState connectBean){
            initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pictureIv:
                if (app.getUserInfo().getPhoneNumber()==null&&app.getUserInfo().getEmail()==null)
                    showToast(getString(R.string.visiter));
                else
                    startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.stepRl:
                startActivity(new Intent(getActivity(), StepReportActivity.class));
                break;
            case R.id.sleepRl:
                startActivity(new Intent(getActivity(), SleepReportActivity.class));
                break;
            case R.id.heartRl:
                startActivity(new Intent(getActivity(), HeartReportActivity.class));
                break;
            case R.id.bloodPressureRl:
                startActivity(new Intent(getActivity(), BloodPressureReportActivity.class));
                break;
            case R.id.bloodOxygenRl:
                startActivity(new Intent(getActivity(), BloodOxygenReportActivity.class));
                break;
            case R.id.animalHeatRl:
                startActivity(new Intent(getActivity(), AnimalHeatActivity.class));
                break;
            case R.id.ecgRl:
                startActivity(new Intent(getActivity(), EcgListActivity.class));
                break;
            case R.id.weatherLl:
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                LocationUtil.getInstance().getLocation(locationManager,myListener,locationListener);
                getView().findViewById(R.id.weatherLl).setClickable(false);
                break;
        }
    }


    private LocationManager locationManager;
    private GpsStatus.Listener myListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int i) {

        }
    };


    //监听GPS位置改变后得到新的经纬度
    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            if (location != null) {
                //获取国家，省份，城市的名称
                Log.e("location******", location.toString());

                try {
                    HttpMessgeUtil.getInstance(getActivity()).getWeather(location.getLatitude()+"", location.getLongitude()+"",
                            new GenericsCallback<WeatherBean>(new JsonGenericsSerializator()) {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public void onResponse(final WeatherBean response, int id) {
                                    if (response.getCode()==200){
                                        app.setWeatherModel(response);
                                        if (getActivity()!=null){
                                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(FILE,MODE_PRIVATE);
                                            sharedPreferences.edit().putLong("weatherTime",Calendar.getInstance().getTimeInMillis()).commit();
                                            if (EventBus.getDefault().isRegistered(HealthyFragment.this)){
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getView().findViewById(R.id.weatherLl).setClickable(true);
                                                        updataWeatherView();
                                                        EXCDController.getInstance().writeForUpdateWeather(app.getWeatherModel(),app.getCity());
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                locationManager.removeUpdates(locationListener);
            }
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
}
