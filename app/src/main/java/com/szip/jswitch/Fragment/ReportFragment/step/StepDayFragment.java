package com.szip.jswitch.Fragment.ReportFragment.step;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.Activity.report.ReportActivity;
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

import java.util.Locale;

/**
 * Created by Administrator on 2019/12/16.
 */

public class StepDayFragment extends BaseFragment implements View.OnClickListener{

    private ReportView reportView;
    private ReportDataBean reportDataBean;
    private TextView kcalTv,distanceTv,allStepTv;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_step_day;
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
        reportView.addData(reportDataBean.getDrawDataBeans());
        allStepTv.setText(reportDataBean.getValue()+"");
        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f",reportDataBean.getValue2()/1000f));
        if (((MyApplication)getActivity().getApplicationContext()).getUserInfo().getUnit()==0){
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",(reportDataBean.getValue1()+55)/100/100f));
            ((TextView)getView().findViewById(R.id.unitTv)).setText("km");
        } else{
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",MathUitl.km2Miles(reportDataBean.getValue1()/10)));
            ((TextView)getView().findViewById(R.id.unitTv)).setText("mile");
        }
        if (DateUtil.getTimeOfToday()==((ReportActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((ReportActivity)getActivity()).reportDate,"yyyy/MM/dd"
            ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().
                getStepWithDay(((ReportActivity)getActivity()).reportDate);

    }

    private void initView() {
        reportView = getView().findViewById(R.id.tableView1);
        reportView.setReportDate(0);
        allStepTv = getView().findViewById(R.id.allStepTv);
        kcalTv = getView().findViewById(R.id.kcalTv);
        distanceTv = getView().findViewById(R.id.dataTv);
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
