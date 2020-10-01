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
import com.szip.sportwatch.View.SportSpeedView;

import java.util.Locale;

public class ClimbFragment extends BaseFragment {
    private TextView timeTv,dataTv,heightTv,unitTv,heartTv,averageTv1;
    private SportReportView tableView1;
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
        dataTv = getView().findViewById(R.id.dataTv);
        heightTv = getView().findViewById(R.id.heightTv);
        unitTv = getView().findViewById(R.id.unitTv);
        heartTv = getView().findViewById(R.id.heartTv);
        averageTv1 = getView().findViewById(R.id.averageTv1);
        tableView1 = getView().findViewById(R.id.tableView1);
    }

    private void initData() {
        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        dataTv.setText(sportData.step+"");
        if (MyApplication.getInstance().getUserInfo().getUnit().equals("metric")){
            heightTv.setText(String.format(Locale.ENGLISH,"%.2f",sportData.distance/1000f));
            unitTv.setText("km");
        } else{
            heightTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
            unitTv.setText("Mi");
        }
        heartTv.setText(sportData.heart+"");
        averageTv1.setText(sportData.heart+"");
        tableView1.addData(sportData.heartArray.equals("")?null:sportData.heartArray.split(","));
    }

}
