package com.szip.sportwatch.Contorller.Fragment.ReportFragment.step;

import android.os.Bundle;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.Contorller.SleepReportActivity;
import com.szip.sportwatch.Contorller.StepReportActivity;
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
 * Created by Administrator on 2019/12/16.
 */

public class StepDayFragment extends BaseFragment {

    private ReportView reportView;
    private ReportDataBean reportDataBean;
    private TextView kcalTv,distanceTv,allStepTv;
    private long time = DateUtil.getTimeOfToday();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_day;
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
        reportView.addData(reportDataBean.getDrawDataBeans());
        allStepTv.setText(reportDataBean.getValue()+"");
        kcalTv.setText(String.format("%.1f",reportDataBean.getValue2()/10f));
        distanceTv.setText(String.format("%.2f",reportDataBean.getValue1()/10000f));
        if (DateUtil.getTimeOfToday()==((StepReportActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((StepReportActivity)getActivity()).reportDate,"yyyy/MM/dd"
            ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().
                getStepWithDay(((StepReportActivity)getActivity()).reportDate);

    }

    private void initView() {
        reportView = getView().findViewById(R.id.tableView);
        reportView.setReportDate(0);
        allStepTv = getView().findViewById(R.id.allStepTv);
        kcalTv = getView().findViewById(R.id.kcalTv);
        distanceTv = getView().findViewById(R.id.distanceTv);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReport(UpdateReport updateReport){
        initData();
        updateView();
    }
}
