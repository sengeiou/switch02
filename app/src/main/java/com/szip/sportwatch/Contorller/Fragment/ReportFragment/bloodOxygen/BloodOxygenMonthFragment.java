package com.szip.sportwatch.Contorller.Fragment.ReportFragment.bloodOxygen;

import android.os.Bundle;
import android.widget.TextView;

import com.mediatek.wearable.WearableManager;
import com.szip.sportwatch.Contorller.BloodOxygenReportActivity;
import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Model.DrawDataBean;
import com.szip.sportwatch.Model.EvenBusModel.ConnectState;
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

public class BloodOxygenMonthFragment extends BaseFragment{

    private ReportView reportView;
    private TextView averageTv,reachTv;
    private ReportDataBean reportDataBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blood_oxygen_month;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
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

    private void updateView() {
        reportView.setReportDate(((BloodOxygenReportActivity)getActivity()).reportDate);
        reportView.addData(reportDataBean.getDrawDataBeans());
        reportView.setReportDate(((BloodOxygenReportActivity)getActivity()).reportDate);
        averageTv.setText(reportDataBean.getValue()+70+"%");
        reachTv.setText(String.format("%.1f%%",reportDataBean.getValue1()/10f));
        if (DateUtil.getTimeOfToday()==((BloodOxygenReportActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((BloodOxygenReportActivity)getActivity()).reportDate-27*24*60*60,"yyyy/MM/dd")+
                    "~"+getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(
                    DateUtil.getStringDateFromSecond(
                            ((BloodOxygenReportActivity)getActivity()).reportDate-27*24*60*60,"yyyy/MM/dd")+
                            "~"
                    +DateUtil.getStringDateFromSecond(
                    ((BloodOxygenReportActivity)getActivity()).reportDate,"yyyy/MM/dd"
            ));

    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getBloodOxygenWithMonth(((BloodOxygenReportActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportView = getView().findViewById(R.id.tableView);
        reportView.setReportDate(0);
        averageTv = getView().findViewById(R.id.averageTv);
        reachTv = getView().findViewById(R.id.reachTv);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReport(UpdateReport updateReport){
        initData();
        updateView();
    }
}
