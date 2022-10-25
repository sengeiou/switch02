package com.szip.jswitch.Fragment.ReportFragment.animalHeat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.jswitch.Activity.report.ReportActivity;
import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.DB.LoadDataUtil;
import com.szip.jswitch.Model.EvenBusModel.UpdateReport;
import com.szip.jswitch.Model.ReportDataBean;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.View.ReportView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Locale;

public class AnimalYearFragment extends BaseFragment implements View.OnClickListener{

    private ReportView reportView;
    private TextView averageTv,reachTv;
    private ReportDataBean reportDataBean;
    private int month;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_animal_heat_year;
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
        reportView.setReportDate(((ReportActivity)getActivity()).reportDate);
        reportView.addData(reportDataBean.getDrawDataBeans());
        if (reportDataBean.getValue()!=0){
            if (reportDataBean.getValue()!=0){
                if (MyApplication.getInstance().getUserInfo().getTempUnit()==0){
                    averageTv.setText(String.format(Locale.ENGLISH,"%.1f℃",(reportDataBean.getValue()+340)/10f));
                    reachTv.setText(String.format(Locale.ENGLISH,"%.1f℃",(reportDataBean.getValue1()+340)/10f));
                }else {
                    averageTv.setText(String.format(Locale.ENGLISH,"%.1f℉", MathUitl.c2f((reportDataBean.getValue()+340)/10f)));
                    reachTv.setText(String.format(Locale.ENGLISH,"%.1f℉", MathUitl.c2f((reportDataBean.getValue1()+340)/10f)));
                }
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(((ReportActivity)getActivity()).reportDate*1000);
        calendar.add(Calendar.MONTH,-11);
        long start = calendar.getTimeInMillis()/1000;
        ((TextView)getView().findViewById(R.id.dateTv)).setText(
                DateUtil.getStringDateFromSecond(
                        start,"yyyy/MM")+ "~" +DateUtil.getStringDateFromSecond(
                        ((ReportActivity)getActivity()).reportDate,"yyyy/MM"
                ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getAnimalHeatWithYear(((ReportActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportView = getView().findViewById(R.id.tableView1);
        reportView.setReportDate(0);
        if (MyApplication.getInstance().getUserInfo().getTempUnit()==0){
            reportView.setF(false);
        }else {
            reportView.setF(true);
        }

        averageTv = getView().findViewById(R.id.averageTv1);
        reachTv = getView().findViewById(R.id.reachTv);
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
                calendar.setTimeInMillis(((ReportActivity)getActivity()).reportDate*1000);
                if (calendar.get(Calendar.MONTH)==month)
                    showToast(getString(R.string.tomorrow));
                else{
                    calendar.add(Calendar.MONTH,1);
                    ((ReportActivity)getActivity()).reportDate = calendar.getTimeInMillis()/1000;
                    EventBus.getDefault().post(new UpdateReport());
                }
            }
            break;
            case R.id.leftIv:
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(((ReportActivity)getActivity()).reportDate*1000);
                calendar.add(Calendar.MONTH,-1);
                ((ReportActivity)getActivity()).reportDate = calendar.getTimeInMillis()/1000;
                EventBus.getDefault().post(new UpdateReport());
                break;
        }
    }
}
