package com.szip.sportwatch.Contorller.Fragment.ReportFragment.bloodPressure;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.szip.sportwatch.Adapter.BloodAdapter;
import com.szip.sportwatch.Contorller.BloodOxygenReportActivity;
import com.szip.sportwatch.Contorller.BloodPressureReportActivity;
import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Interface.OnPageViewScorllAble;
import com.szip.sportwatch.Model.DrawDataBean;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.Model.ReportDataBean;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.View.ReportScorllView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2019/12/18.
 */

public class BloodPressureDayFragment extends BaseFragment implements OnPageViewScorllAble, View.OnClickListener {

    private ReportScorllView reportScorllView;

    private BloodPressureReportActivity activity;

    private ListView listView;
    private BloodAdapter adapter;

    private ReportDataBean reportDataBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blood_pressure_day;
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
        reportScorllView.addData(reportDataBean.getDrawDataBeans());
        adapter.setDrawDataBeans(reportDataBean.getDrawDataBeans());
        if (DateUtil.getTimeOfToday()==((BloodPressureReportActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((BloodPressureReportActivity)getActivity()).reportDate,"yyyy/MM/dd"
            ));
    }

    public void setActivity(BloodPressureReportActivity activity) {
        this.activity = activity;
    }


    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getBloodPressureWithDay(((BloodPressureReportActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportScorllView = getView().findViewById(R.id.reportView);
        reportScorllView.setOnPageViewScorllAble(this);
        listView = getView().findViewById(R.id.dataList);
        adapter = new BloodAdapter(reportDataBean.getDrawDataBeans(),getContext());
        listView.setAdapter(adapter);
    }

    @Override
    public void onScroll(boolean scrollAble) {
        if (activity !=null)
            activity.setViewPagerScroll(scrollAble);
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
                ((BloodPressureReportActivity)getActivity()).reportDate+=24*60*60;
                initData();
                updateView();
                break;
            case R.id.leftIv:
                ((BloodPressureReportActivity)getActivity()).reportDate-=24*60*60;
                initData();
                updateView();
                break;
        }
    }
}
