package com.szip.sportwatch.Contorller.Fragment.ReportFragment.heart;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.BloodOxygenReportActivity;
import com.szip.sportwatch.Contorller.BloodPressureReportActivity;
import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.Contorller.HeartReportActivity;
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
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2019/12/18.
 */

public class HeartWeekFragment extends BaseFragment implements View.OnClickListener{

    private ReportView reportView;
    private TextView averageTv,maxTv,minTv;
    private ReportDataBean reportDataBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_heart_week;
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
        reportView.setReportDate(((HeartReportActivity)getActivity()).reportDate);
        reportView.addData(reportDataBean.getDrawDataBeans());

        if (reportDataBean.getValue()!=0)
            averageTv.setText(reportDataBean.getValue()+40+"");
        else
            averageTv.setText("--");
        if (reportDataBean.getValue1()!=0)
            maxTv.setText(reportDataBean.getValue1()+40+"");
        else
            maxTv.setText("--");
        if (reportDataBean.getValue2()!=0)
            minTv.setText(reportDataBean.getValue2()+40+"");
        else
            minTv.setText("--");

        if (DateUtil.getTimeOfToday()==((HeartReportActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((HeartReportActivity)getActivity()).reportDate-6*24*60*60,"yyyy/MM/dd")+
                    "~"+getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(
                    DateUtil.getStringDateFromSecond(
                            ((HeartReportActivity)getActivity()).reportDate-6*24*60*60,"yyyy/MM/dd")+
                            "~"
                            +DateUtil.getStringDateFromSecond(
                            ((HeartReportActivity)getActivity()).reportDate,"yyyy/MM/dd"
                    ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getHeartWithWeek(((HeartReportActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportView = getView().findViewById(R.id.tableView);
        reportView.setReportDate(0);
        averageTv = getView().findViewById(R.id.averageTv);
        maxTv = getView().findViewById(R.id.maxTv);
        minTv = getView().findViewById(R.id.minTv);
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
                if (((HeartReportActivity)getActivity()).reportDate==DateUtil.getTimeOfToday())
                    showToast(getString(R.string.tomorrow));
                else{
                    ((HeartReportActivity)getActivity()).reportDate+=24*60*60;
                    EventBus.getDefault().post(new UpdateReport());
                }

                break;
            case R.id.leftIv:
                ((HeartReportActivity)getActivity()).reportDate-=24*60*60;
                EventBus.getDefault().post(new UpdateReport());
                break;
        }
    }
}
