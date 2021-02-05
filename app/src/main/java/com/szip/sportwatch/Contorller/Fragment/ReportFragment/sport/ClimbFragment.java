package com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.View.SportReportView;
import com.szip.sportwatch.View.SportSpeedView;

import java.util.Locale;

public class ClimbFragment extends BaseFragment {
    private TextView timeTv,kcalTv,sportTimeTv,averageTv1,averageTv2;
    private SportReportView tableView1, tableView2;
    private SportData sportData;

    public ClimbFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_climb;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initView() {
        timeTv = getView().findViewById(R.id.timeTv);
        sportTimeTv = getView().findViewById(R.id.sportTimeTv);
        kcalTv = getView().findViewById(R.id.kcalTv);
        averageTv1 = getView().findViewById(R.id.averageTv1);
        averageTv2 = getView().findViewById(R.id.averageTv2);
        tableView1 = getView().findViewById(R.id.tableView1);
        tableView2 = getView().findViewById(R.id.tableView2);
    }

    private void initData() {
        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f", sportData.calorie/1000f));
        averageTv1.setText(sportData.heart+"");
        tableView1.addData(sportData.getHeartArray().equals("")?null:sportData.getHeartArray().split(","));
        averageTv2.setText(sportData.height+"");
        tableView2.addData(sportData.getAltitudeArray().equals("")?null:sportData.getAltitudeArray().split(","));

    }

}
