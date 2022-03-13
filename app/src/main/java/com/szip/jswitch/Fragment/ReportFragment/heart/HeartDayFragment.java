package com.szip.jswitch.Fragment.ReportFragment.heart;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.jswitch.Activity.report.ReportActivity;
import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.DB.LoadDataUtil;
import com.szip.jswitch.Model.EvenBusModel.UpdateReport;
import com.szip.jswitch.Model.ReportDataBean;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.View.ReportView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2019/12/18.
 */

public class HeartDayFragment extends BaseFragment implements View.OnClickListener {

    private ReportView reportView;
    private TextView averageTv,maxTv,minTv;
    private ReportDataBean reportDataBean;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_heart_day;
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
        if (reportDataBean!=null) {
            reportView.addData(reportDataBean.getDrawDataBeans());
            averageTv.setText(reportDataBean.getValue()+"");
            maxTv.setText(reportDataBean.getValue1()+40+"");
            minTv.setText(reportDataBean.getValue2()+40+"");
        }else {
            reportView.addData(null);
            averageTv.setText("--");
            maxTv.setText("--");
            minTv.setText("--");
        }
        if (DateUtil.getTimeOfToday()==((ReportActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((ReportActivity)getActivity()).reportDate,"yyyy/MM/dd"
            ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getHeartWithDay(((ReportActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportView = getView().findViewById(R.id.tableView1);
        reportView.setReportDate(0);
        averageTv = getView().findViewById(R.id.averageTv1);
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
