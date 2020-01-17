package com.szip.sportwatch.Contorller.Fragment.ReportFragment.heart;

import android.os.Bundle;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2019/12/18.
 */

public class HeartYearFragment extends BaseFragment{

    private ReportView reportView;
    private TextView averageTv,maxTv,minTv;
    private ReportDataBean reportDataBean;
    private long time = DateUtil.getTimeOfToday();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_heart_year;
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
        reportView.setReportDate(((HeartReportActivity)getActivity()).reportDate);
        reportView.addData(reportDataBean.getDrawDataBeans());
        averageTv.setText(reportDataBean.getValue()+45+"");
        maxTv.setText(reportDataBean.getValue1()+45+"");
        minTv.setText(reportDataBean.getValue2()+45+"");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(((HeartReportActivity)getActivity()).reportDate*1000);
        calendar.add(Calendar.MONTH,-11);
        long start = calendar.getTimeInMillis()/1000;
        ((TextView)getView().findViewById(R.id.dateTv)).setText(
                DateUtil.getStringDateFromSecond(
                        start,"yyyy/MM")+ "~" +DateUtil.getStringDateFromSecond(
                        ((HeartReportActivity)getActivity()).reportDate,"yyyy/MM"
                ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getHeartWithYear(((HeartReportActivity)getActivity()).reportDate);
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
}
