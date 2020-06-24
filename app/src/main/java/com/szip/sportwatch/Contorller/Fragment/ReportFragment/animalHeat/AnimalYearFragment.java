package com.szip.sportwatch.Contorller.Fragment.ReportFragment.animalHeat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.AnimalHeatActivity;
import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.Model.ReportDataBean;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.View.ReportView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

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
        reportView.setReportDate(((AnimalHeatActivity)getActivity()).reportDate);
        reportView.addData(reportDataBean.getDrawDataBeans());
        if (reportDataBean.getValue()!=0)
            averageTv.setText(String.format("%.1f℃",(reportDataBean.getValue()+340)/10f));
        if (reportDataBean.getValue()!=0)
            reachTv.setText(String.format("%.1f℃",(reportDataBean.getValue1()+340)/10f));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(((AnimalHeatActivity)getActivity()).reportDate*1000);
        calendar.add(Calendar.MONTH,-11);
        long start = calendar.getTimeInMillis()/1000;
        ((TextView)getView().findViewById(R.id.dateTv)).setText(
                DateUtil.getStringDateFromSecond(
                        start,"yyyy/MM")+ "~" +DateUtil.getStringDateFromSecond(
                        ((AnimalHeatActivity)getActivity()).reportDate,"yyyy/MM"
                ));
    }

    private void initData() {
        reportDataBean = LoadDataUtil.newInstance().getAnimalHeatWithYear(((AnimalHeatActivity)getActivity()).reportDate);
    }

    private void initView() {
        reportView = getView().findViewById(R.id.tableView);
        reportView.setReportDate(0);
        averageTv = getView().findViewById(R.id.averageTv);
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
                calendar.setTimeInMillis(((AnimalHeatActivity)getActivity()).reportDate*1000);
                if (calendar.get(Calendar.MONTH)==month)
                    showToast(getString(R.string.tomorrow));
                else{
                    calendar.add(Calendar.MONTH,1);
                    ((AnimalHeatActivity)getActivity()).reportDate = calendar.getTimeInMillis()/1000;
                    EventBus.getDefault().post(new UpdateReport());
                }
            }
            break;
            case R.id.leftIv:
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(((AnimalHeatActivity)getActivity()).reportDate*1000);
                calendar.add(Calendar.MONTH,-1);
                ((AnimalHeatActivity)getActivity()).reportDate = calendar.getTimeInMillis()/1000;
                EventBus.getDefault().post(new UpdateReport());
                break;
        }
    }
}
