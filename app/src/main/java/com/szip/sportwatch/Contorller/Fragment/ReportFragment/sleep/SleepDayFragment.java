package com.szip.sportwatch.Contorller.Fragment.ReportFragment.sleep;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.BloodOxygenReportActivity;
import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.Contorller.HeartReportActivity;
import com.szip.sportwatch.Contorller.SleepReportActivity;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.Model.ReportDataBean;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.View.ReportView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2019/12/18.
 */

public class SleepDayFragment extends BaseFragment implements View.OnClickListener{
    private ReportView reportView;
    private ReportDataBean reportDataBean;
    private TextView deepTv,lightTv,allSleepTv;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sleep_day;
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
        if (reportDataBean!=null){
            reportView.setSleepState(reportDataBean.getValue(),reportDataBean.getValue1()+reportDataBean.getValue2());
            reportView.addData(reportDataBean.getDrawDataBeans());
            allSleepTv.setText(String.format("%2dh%02dmin",(reportDataBean.getValue1()+reportDataBean.getValue2())/60,
                    (reportDataBean.getValue1()+reportDataBean.getValue2())%60));
            deepTv.setText(String.format("%2dh%02dmin",reportDataBean.getValue1()/60,reportDataBean.getValue1()%60));
            lightTv.setText(String.format("%2dh%02dmin",reportDataBean.getValue2()/60,reportDataBean.getValue2()%60));
        }else {
            reportView.setSleepState(0,0);
            reportView.addData(null);
            allSleepTv.setText("--h--min");
            deepTv.setText("--h--min");
            lightTv.setText("--h--min");
        }
        if (DateUtil.getTimeOfToday()==((SleepReportActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((SleepReportActivity)getActivity()).reportDate,"yyyy/MM/dd"
            ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getSleepWithDay(((SleepReportActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportView = getView().findViewById(R.id.tableView);
        reportView.setReportDate(0);
        allSleepTv = getView().findViewById(R.id.allSleepTv);
        deepTv = getView().findViewById(R.id.deepTv);
        lightTv = getView().findViewById(R.id.lightTv);
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
                if (((SleepReportActivity)getActivity()).reportDate==DateUtil.getTimeOfToday())
                    showToast(getString(R.string.tomorrow));
                else{
                    ((SleepReportActivity)getActivity()).reportDate+=24*60*60;
                    initData();
                    updateView();
                }

                break;
            case R.id.leftIv:
                ((SleepReportActivity)getActivity()).reportDate-=24*60*60;
                initData();
                updateView();
                break;
        }
    }

}
