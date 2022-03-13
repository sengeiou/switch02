package com.szip.jswitch.Activity.sport.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.DB.dbModel.SportData;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.View.SportReportView;

import java.util.Locale;

public class BasketballFragment extends BaseFragment {

    private TextView timeTv,sportTimeTv,kcalTv,averageTv1;
    private SportReportView tableView1;
    private SportData sportData;

    private String[] heartArray = new String[0];

    public BasketballFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_basketball;
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
        tableView1 = getView().findViewById(R.id.tableView1);
    }

    private void initData() {
        heartArray = sportData.getHeartArray().split(",");
        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f", sportData.calorie/1000f));
        averageTv1.setText(sportData.heart+"");
        tableView1.addData(heartArray);

        if (sportData.heart==0)
            getView().findViewById(R.id.heartLl).setVisibility(View.GONE);
    }
}
