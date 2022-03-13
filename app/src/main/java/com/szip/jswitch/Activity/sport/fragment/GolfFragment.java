package com.szip.jswitch.Activity.sport.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.DB.dbModel.SportData;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.View.SportReportView;

import java.util.Locale;

public class GolfFragment extends BaseFragment {

    private TextView timeTv,dataTv,sportTimeTv,stepTv,heartTv,averageTv1;
    private SportReportView tableView1;
    private SportData sportData;

    public GolfFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_golf;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initView() {
        timeTv = getView().findViewById(R.id.timeTv);
        dataTv = getView().findViewById(R.id.dataTv);
        sportTimeTv = getView().findViewById(R.id.sportTimeTv);
        stepTv = getView().findViewById(R.id.stepTv);
        averageTv1 = getView().findViewById(R.id.averageTv1);
        heartTv = getView().findViewById(R.id.heartTv);
        tableView1 = getView().findViewById(R.id.tableView1);
    }

    private void initData() {
        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        dataTv.setText(sportData.step+"");
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        stepTv.setText(sportData.step+"");
        heartTv.setText(sportData.heart+"");
        averageTv1.setText(sportData.heart+"");
        tableView1.addData(sportData.getHeartArray().equals("")?null:sportData.getHeartArray().split(","));
    }
}
