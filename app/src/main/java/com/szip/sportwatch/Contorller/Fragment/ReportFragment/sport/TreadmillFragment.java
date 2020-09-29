package com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.View.SportReportView;

import java.util.Locale;

public class TreadmillFragment extends BaseFragment {

    private TextView timeTv,dataTv,sportTimeTv,kcalTv,heartTv, averageTv1, averageTv2;
    private SportReportView tableView1, tableView2;
    private SportData sportData;

    public TreadmillFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_treadmill;
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
        kcalTv = getView().findViewById(R.id.kcalTv);
        heartTv = getView().findViewById(R.id.heartTv);
        averageTv1 = getView().findViewById(R.id.averageTv1);
        averageTv2 = getView().findViewById(R.id.averageTv2);
        tableView1 = getView().findViewById(R.id.tableView1);
        tableView2 = getView().findViewById(R.id.tableView2);
    }

    private void initData() {
        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        dataTv.setText(sportData.step+"");

        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f", MathUitl.metric2Miles(sportData.calorie)));
        heartTv.setText(sportData.heart+"");
        averageTv1.setText(sportData.heart+"");
        averageTv2.setText(sportData.stride+"");
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        tableView1.addData(sportData.heartArray.equals("")?null:sportData.heartArray.split(","));
        tableView2.addData(sportData.heartArray.equals("")?null:sportData.strideArray.split(","));
    }
}
