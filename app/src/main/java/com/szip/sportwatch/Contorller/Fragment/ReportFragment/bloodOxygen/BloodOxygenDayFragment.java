package com.szip.sportwatch.Contorller.Fragment.ReportFragment.bloodOxygen;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.szip.sportwatch.Adapter.BloodAdapter;
import com.szip.sportwatch.Contorller.BloodOxygenReportActivity;
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
import java.util.Random;

/**
 * Created by Administrator on 2019/12/18.
 */

public class BloodOxygenDayFragment extends BaseFragment implements OnPageViewScorllAble{

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
        reportScorllView.addData(reportDataBean.getDrawDataBeans());
        adapter.setDrawDataBeans(reportDataBean.getDrawDataBeans());
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
        adapter = new BloodAdapter(reportDataBean.getDrawDataBeans(),getContext());
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
}
