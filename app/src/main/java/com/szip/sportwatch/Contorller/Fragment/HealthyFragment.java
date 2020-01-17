package com.szip.sportwatch.Contorller.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.BloodOxygenReportActivity;
import com.szip.sportwatch.Contorller.BloodPressureReportActivity;
import com.szip.sportwatch.Contorller.EcgListActivity;
import com.szip.sportwatch.Contorller.HeartReportActivity;
import com.szip.sportwatch.Contorller.SleepReportActivity;
import com.szip.sportwatch.Contorller.StepReportActivity;
import com.szip.sportwatch.Contorller.UserInfoActivity;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Model.HealthyDataModel;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ViewUtil;
import com.szip.sportwatch.View.ColorArcProgressBar;
import com.szip.sportwatch.View.HealthyProgressView;

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
    private HealthyProgressView sleepPv,sbpPv,dbpPv,heartPv,bloodOxygenPv;

    /**
     * 健康数据相关控件
     * */
    private TextView stepRadioTv,planStepTv,stepTv,distanceTv,kcalTv;
    private TextView sleepDataTv;
    private TextView heartDataTv;
    private TextView bloodDataTv;
    private TextView bloodODataTv;

    /**
     * 最近一次健康数据的model
     * */
    private HealthyDataModel healthyDataModel;

    private ViewUtil viewUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_healthy;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();
        initView();
        initEvent();

    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        stepPb = getView().findViewById(R.id.stepPb);
        sleepPv = getView().findViewById(R.id.sleepPv);
        sbpPv = getView().findViewById(R.id.sbpPv);
        dbpPv = getView().findViewById(R.id.dbpPv);
        heartPv = getView().findViewById(R.id.heartPv);
        bloodOxygenPv = getView().findViewById(R.id.bloodOxygenPv);

        stepRadioTv = getView().findViewById(R.id.stepRadioTv);
        planStepTv = getView().findViewById(R.id.planStepTv);
        stepTv = getView().findViewById(R.id.stepTv);
        distanceTv = getView().findViewById(R.id.distanceTv);
        kcalTv = getView().findViewById(R.id.kcalTv);

        sleepDataTv = getView().findViewById(R.id.sleepDataTv);

        heartDataTv = getView().findViewById(R.id.heartDataTv);

        bloodDataTv = getView().findViewById(R.id.bloodDataTv);

        bloodODataTv = getView().findViewById(R.id.bloodODataTv);
    }

    private void initEvent() {
        getView().findViewById(R.id.pictureIv).setOnClickListener(this);
        getView().findViewById(R.id.stepRl).setOnClickListener(this);
        getView().findViewById(R.id.sleepRl).setOnClickListener(this);
        getView().findViewById(R.id.heartRl).setOnClickListener(this);
        getView().findViewById(R.id.bloodPressureRl).setOnClickListener(this);
        getView().findViewById(R.id.bloodOxygenRl).setOnClickListener(this);
        getView().findViewById(R.id.ecgRl).setOnClickListener(this);
    }

    @SuppressLint("StringFormatInvalid")
    private void initData() {
        viewUtil = new ViewUtil(getContext());
        healthyDataModel = LoadDataUtil.newInstance().getHealthyDataLast(DateUtil.getTimeOfToday());

        if (app.getUserInfo()!=null){
            stepPb.setMaxValues(app.getUserInfo().getStepsPlan());
            stepPb.setCurrentValues(healthyDataModel.getStepsData());
            stepTv.setText(healthyDataModel.getStepsData()+"");
            distanceTv.setText(String.format("%.2f",healthyDataModel.getDistanceData()/10000f));
            kcalTv.setText(String.format("%.1f",healthyDataModel.getKcalData()/10f));
            planStepTv.setText(String.format(getString(R.string.planStep),app.getUserInfo().getStepsPlan()));
            stepRadioTv.setText(String.format("%.1f%%",healthyDataModel.getStepsData()/(float)app.getUserInfo().getStepsPlan()*100));
        }
        if (healthyDataModel.getAllSleepData()!=0){
            sleepDataTv.setText(String.format("%.1fh/%.1fh",healthyDataModel.getAllSleepData()/60f,app.getUserInfo().getSleepPlan()/60f));
            sleepPv.setSleepData(healthyDataModel.getAllSleepData()/(float)app.getUserInfo().getSleepPlan(),
                    healthyDataModel.getAllSleepData(),healthyDataModel.getLightSleepData());
            viewUtil.setSleepView(healthyDataModel.getAllSleepData(),getView().findViewById(R.id.sleepStateTv));

        }
        if (healthyDataModel.getHeartData()!=0){
            heartDataTv.setText(healthyDataModel.getHeartData()+"Bpm");
            viewUtil.setHeartView(healthyDataModel.getHeartData(), getView().findViewById(R.id.heartStateTv),
                    getView().findViewById(R.id.heartPv));
        }

        if (healthyDataModel.getSbpData()!=0){
            bloodDataTv.setText(String.format("%d/%dmmHg",healthyDataModel.getSbpData(),healthyDataModel.getDbpData()));
            viewUtil.setBloodPressureView(healthyDataModel.getSbpData(),healthyDataModel.getDbpData(),
                    getView().findViewById(R.id.bloodStateTv),getView().findViewById(R.id.sbpPv),getView().findViewById(R.id.dbpPv));
        }

        if (healthyDataModel.getBloodOxygenData()!=0){
            bloodODataTv.setText(healthyDataModel.getBloodOxygenData()+"%");
            viewUtil.setBloodOxygenView(healthyDataModel.getBloodOxygenData(),getView().findViewById(R.id.bloodOStateTv),
                    getView().findViewById(R.id.bloodOxygenPv));
        }

        if (app.getUserInfo()!=null)
            ((ImageView)getView().findViewById(R.id.pictureIv)).setImageResource(app.getUserInfo().getSex()==1?R.mipmap.my_head_male_36:
                    R.mipmap.my_head_female_36);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pictureIv:
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
            case R.id.ecgRl:
                startActivity(new Intent(getActivity(), EcgListActivity.class));
                break;
        }
    }
}
