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

public class AnimalMonthFragment extends BaseFragment implements View.OnClickListener{
    private ReportView reportView;
    private TextView averageTv,reachTv;
    private ReportDataBean reportDataBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_animal_heat_month;
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
        reportView.setReportDate(((ReportActivity)getActivity()).reportDate);
        reportView.addData(reportDataBean.getDrawDataBeans());
        reportView.setReportDate(((ReportActivity)getActivity()).reportDate);
        if (reportDataBean.getValue()!=0){
            if (MyApplication.getInstance().getUserInfo().getTempUnit()==0){
                averageTv.setText(String.format("%.1f℃",(reportDataBean.getValue()+340)/10f));
                reachTv.setText(String.format("%.1f℃",(reportDataBean.getValue1()+340)/10f));
            }else {
                averageTv.setText(String.format("%.1f℉", MathUitl.c2f((reportDataBean.getValue()+340)/10f)));
                reachTv.setText(String.format("%.1f℉", MathUitl.c2f((reportDataBean.getValue1()+340)/10f)));
            }
        }

        if (DateUtil.getTimeOfToday()==((ReportActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((ReportActivity)getActivity()).reportDate-27*24*60*60,"yyyy/MM/dd")+
                    "~"+getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(
                    DateUtil.getStringDateFromSecond(
                            ((ReportActivity)getActivity()).reportDate-27*24*60*60,"yyyy/MM/dd")+
                            "~"
                            +DateUtil.getStringDateFromSecond(
                            ((ReportActivity)getActivity()).reportDate,"yyyy/MM/dd"
                    ));

    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getAnimalHeatWithMonth(((ReportActivity)getActivity()).reportDate);
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
            case R.id.rightIv:
                if (((ReportActivity)getActivity()).reportDate==DateUtil.getTimeOfToday())
                    showToast(getString(R.string.tomorrow));
                else{
                    ((ReportActivity)getActivity()).reportDate+=24*60*60;
                    EventBus.getDefault().post(new UpdateReport());
                }

                break;
            case R.id.leftIv:
                ((ReportActivity)getActivity()).reportDate-=24*60*60;
                EventBus.getDefault().post(new UpdateReport());
                break;
        }
    }

}
