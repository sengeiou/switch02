package com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport;

import android.os.Bundle;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.View.SportReportView;

import java.util.Locale;

public class MountainFragment extends BaseFragment {

    private TextView timeTv,dataTv,distanceTv,unitTv,averageTv1,averageTv2,sportTimeTv, heightTv, heightUnitTv;
    private SportReportView tableView1,tableView2;
    private SportData sportData;


    public MountainFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mountain;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initView() {
        timeTv = getView().findViewById(R.id.timeTv);
        dataTv = getView().findViewById(R.id.dataTv);
        distanceTv = getView().findViewById(R.id.distanceTv);
        unitTv = getView().findViewById(R.id.unitTv);
        sportTimeTv = getView().findViewById(R.id.sportTimeTv);
        heightTv = getView().findViewById(R.id.heightTv);
        heightUnitTv = getView().findViewById(R.id.heightUnitTv);
        averageTv1 = getView().findViewById(R.id.averageTv1);
        averageTv2 = getView().findViewById(R.id.averageTv2);
        tableView1 = getView().findViewById(R.id.tableView1);
        tableView2 = getView().findViewById(R.id.tableView2);
    }

    private void initData() {
        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        dataTv.setText(sportData.step+"");
        if (MyApplication.getInstance().getUserInfo().getUnit().equals("metric")){
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",sportData.distance/1000f));
            heightTv.setText(sportData.height+"");
            unitTv.setText("km");
            heightUnitTv.setText("m");
        } else{
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
            heightTv.setText(sportData.height+"");
            unitTv.setText("Mi");
            heightUnitTv.setText("Mi");
        }
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));

        averageTv1.setText(sportData.heart+"");
        averageTv2.setText(sportData.altitude+"");
        tableView1.addData(sportData.heartArray.equals("")?null:sportData.heartArray.split(","));
        tableView2.addData(sportData.altitudeArray.equals("")?null:sportData.altitudeArray.split(","));
    }
}
