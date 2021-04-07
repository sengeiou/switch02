package com.szip.sportwatch.Activity.Fragment.ReportFragment.bloodOxygen;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.szip.sportwatch.Adapter.BloodAdapter;
import com.szip.sportwatch.Activity.BloodOxygenReportActivity;
import com.szip.sportwatch.Activity.Fragment.BaseFragment;
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
import java.util.Collections;

/**
 * Created by Administrator on 2019/12/18.
 */

public class BloodOxygenDayFragment extends BaseFragment implements OnPageViewScorllAble, View.OnClickListener {

    private ReportScorllView reportScorllView;

    private BloodOxygenReportActivity activity;

    private ListView listView;
    private BloodAdapter adapter;

    private ReportDataBean reportDataBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blood_oxygen_day;
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
        ArrayList<DrawDataBean> list = reportDataBean.getDrawDataBeans();
        Collections.sort(list);
        adapter.setDrawDataBeans(list);
        if (DateUtil.getTimeOfToday()==((BloodOxygenReportActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((BloodOxygenReportActivity)getActivity()).reportDate,"yyyy/MM/dd"
            ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getBloodOxygenWithDay(((BloodOxygenReportActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportScorllView = getView().findViewById(R.id.reportView);
        reportScorllView.setOnPageViewScorllAble(this);
        listView = getView().findViewById(R.id.dataList);
        adapter = new BloodAdapter(reportDataBean.getDrawDataBeans(),1,getContext());
        listView.setAdapter(adapter);
    }

    @Override
    public void onScroll(boolean scrollAble) {
        if (activity!=null)
            activity.setViewPagerScroll(scrollAble);
    }

    public void setActivity(BloodOxygenReportActivity activity) {
        this.activity = activity;
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
                if (((BloodOxygenReportActivity)getActivity()).reportDate==DateUtil.getTimeOfToday())
                    showToast(getString(R.string.tomorrow));
                else{
                    ((BloodOxygenReportActivity)getActivity()).reportDate+=24*60*60;
                    EventBus.getDefault().post(new UpdateReport());
                }
                break;
            case R.id.leftIv:
                ((BloodOxygenReportActivity)getActivity()).reportDate-=24*60*60;
                EventBus.getDefault().post(new UpdateReport());
                break;
        }
    }
}
