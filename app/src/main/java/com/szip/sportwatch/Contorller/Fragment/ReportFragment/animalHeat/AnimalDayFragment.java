package com.szip.sportwatch.Contorller.Fragment.ReportFragment.animalHeat;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.szip.sportwatch.Adapter.BloodAdapter;
import com.szip.sportwatch.Contorller.AnimalHeatActivity;
import com.szip.sportwatch.Contorller.AnimalHeatActivity;
import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Interface.OnPageViewScorllAble;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.Model.ReportDataBean;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.View.ReportScorllView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AnimalDayFragment extends BaseFragment implements View.OnClickListener, OnPageViewScorllAble {

    private ReportScorllView reportScorllView;

    private AnimalHeatActivity activity;

    private ListView listView;
    private BloodAdapter adapter;

    private ReportDataBean reportDataBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_animal_heat_day;
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
        if (DateUtil.getTimeOfToday()==((AnimalHeatActivity)getActivity()).reportDate)
            ((TextView)getView().findViewById(R.id.dateTv)).setText(getString(R.string.today));
        else
            ((TextView)getView().findViewById(R.id.dateTv)).setText(DateUtil.getStringDateFromSecond(
                    ((AnimalHeatActivity)getActivity()).reportDate,"yyyy/MM/dd"
            ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getAnimalHeatWithDay(((AnimalHeatActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportScorllView = getView().findViewById(R.id.reportView);
        reportScorllView.setOnPageViewScorllAble(this);
        listView = getView().findViewById(R.id.dataList);
        adapter = new BloodAdapter(reportDataBean.getDrawDataBeans(),2,getContext());
        listView.setAdapter(adapter);
    }

    @Override
    public void onScroll(boolean scrollAble) {
        if (activity!=null)
            activity.setViewPagerScroll(scrollAble);
    }

    public void setActivity(AnimalHeatActivity activity) {
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
                if (((AnimalHeatActivity)getActivity()).reportDate==DateUtil.getTimeOfToday())
                    showToast(getString(R.string.tomorrow));
                else{
                    ((AnimalHeatActivity)getActivity()).reportDate+=24*60*60;
                    EventBus.getDefault().post(new UpdateReport());
                }
                break;
            case R.id.leftIv:
                ((AnimalHeatActivity)getActivity()).reportDate-=24*60*60;
                EventBus.getDefault().post(new UpdateReport());
                break;
        }
    }
}
