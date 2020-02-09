package com.szip.sportwatch.Contorller.Fragment.ReportFragment.bloodPressure;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.BloodPressureReportActivity;
import com.szip.sportwatch.Contorller.BloodPressureReportActivity;
import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Model.DrawDataBean;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.Model.ReportDataBean;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.View.ReportView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2019/12/18.
 */

public class BloodPressureYearFragment extends BaseFragment implements View.OnClickListener{

    private ReportView reportView;
    private TextView averageSbpTv,averageDbpTv;
    private ReportDataBean reportDataBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blood_pressure_year;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        initEvent();
        initData();
        initView();
        updateView();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initEvent() {
        getView().findViewById(R.id.leftIv).setOnClickListener(this);
        getView().findViewById(R.id.rightIv).setOnClickListener(this);
    }

    private void updateView() {
        reportView.setReportDate(((BloodPressureReportActivity)getActivity()).reportDate);
        reportView.addData(reportDataBean.getDrawDataBeans());
        averageSbpTv.setText(reportDataBean.getValue()+45+"mmHg");
        averageDbpTv.setText(reportDataBean.getValue1()+45+"mmHg");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(((BloodPressureReportActivity)getActivity()).reportDate*1000);
        calendar.add(Calendar.MONTH,-11);
        long start = calendar.getTimeInMillis()/1000;
        ((TextView)getView().findViewById(R.id.dateTv)).setText(
                DateUtil.getStringDateFromSecond(
                        start,"yyyy/MM")+ "~" +DateUtil.getStringDateFromSecond(
                        ((BloodPressureReportActivity)getActivity()).reportDate,"yyyy/MM"
                ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getBloodPressureWithYear(((BloodPressureReportActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportView = getView().findViewById(R.id.tableView);
        reportView.setReportDate(0);
        averageSbpTv = getView().findViewById(R.id.sbpTv);
        averageDbpTv = getView().findViewById(R.id.dbpTv);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReport(UpdateReport updateReport){
        initData();
        updateView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rightIv:
                ((BloodPressureReportActivity)getActivity()).reportDate+=24*60*60;
                updateView();
                break;
            case R.id.leftIv:
                ((BloodPressureReportActivity)getActivity()).reportDate-=24*60*60;
                updateView();
                break;
        }
    }
}
