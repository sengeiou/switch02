package com.szip.sportwatch.Contorller.Fragment.ReportFragment.sleep;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.BloodOxygenReportActivity;
import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.Contorller.HeartReportActivity;
import com.szip.sportwatch.Contorller.SleepReportActivity;
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
import java.util.Locale;
import java.util.Random;

/**
 * Created by Administrator on 2019/12/18.
 */

public class SleepYearFragment extends BaseFragment implements View.OnClickListener{

    private ReportView reportView;
    private ReportDataBean reportDataBean;
    private TextView allSleepTv,averageSleepTv;
    private int month;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sleep_year;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        initEvent();
        initData();
        initView();
        updateView();
        month = Calendar.getInstance().get(Calendar.MONTH);
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
        reportView.setReportDate(((SleepReportActivity)getActivity()).reportDate);
        reportView.addData(reportDataBean.getDrawDataBeans());
        allSleepTv.setText(String.format(Locale.ENGLISH,"%.1fh",reportDataBean.getValue()/60f));
        averageSleepTv.setText(String.format(Locale.ENGLISH,"%.1fh",reportDataBean.getValue1()/60f));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(((SleepReportActivity)getActivity()).reportDate*1000);
        calendar.add(Calendar.MONTH,-11);
        long start = calendar.getTimeInMillis()/1000;
        ((TextView)getView().findViewById(R.id.dateTv)).setText(
                DateUtil.getStringDateFromSecond(
                        start,"yyyy/MM")+ "~" +DateUtil.getStringDateFromSecond(
                        ((SleepReportActivity)getActivity()).reportDate,"yyyy/MM"
                ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getSleepWithYear(((SleepReportActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportView = getView().findViewById(R.id.tableView);
        reportView.setReportDate(0);
        allSleepTv = getView().findViewById(R.id.allSleepTv);
        averageSleepTv = getView().findViewById(R.id.averageSleepTv);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReport(UpdateReport updateReport){
        initData();
        updateView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rightIv:{
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(((SleepReportActivity)getActivity()).reportDate*1000);
                if (calendar.get(Calendar.MONTH)==month)
                    showToast(getString(R.string.tomorrow));
                else{
                    calendar.add(Calendar.MONTH,1);
                    ((SleepReportActivity)getActivity()).reportDate = calendar.getTimeInMillis()/1000;
                    EventBus.getDefault().post(new UpdateReport());
                }
            }
                break;
            case R.id.leftIv:
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(((SleepReportActivity)getActivity()).reportDate*1000);
                calendar.add(Calendar.MONTH,-1);
                ((SleepReportActivity)getActivity()).reportDate = calendar.getTimeInMillis()/1000;
                EventBus.getDefault().post(new UpdateReport());
                break;
        }
    }
}
