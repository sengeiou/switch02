package com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport;

import android.os.Bundle;
import android.widget.ImageView;
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

public class RunFragment extends BaseFragment {

    private TextView timeTv,dataTv,distanceTv,unitTv,kcalTv,heartTv,averageTv, averageTv3, averageTv2;
    private SportReportView tableView1, tableView2;
    private SportData sportData;
    private SportSpeedView sportSpeed;

    public RunFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_run;
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
        kcalTv = getView().findViewById(R.id.kcalTv);
        heartTv = getView().findViewById(R.id.heartTv);
        averageTv = getView().findViewById(R.id.averageTv1);
        averageTv3 = getView().findViewById(R.id.averageTv3);
        averageTv2 = getView().findViewById(R.id.averageTv2);
        tableView1 = getView().findViewById(R.id.tableView1);
        tableView2 = getView().findViewById(R.id.tableView2);
        sportSpeed = getView().findViewById(R.id.sportSpeed);

        if(sportData.type==6){
            getView().findViewById(R.id.bgRl).setBackground(getView().getResources().getDrawable(R.drawable.sport_bg_purple));
            ((ImageView)getView().findViewById(R.id.bgIv)).setImageResource(R.mipmap.sport_bg_trainrun);
        }
    }

    private void initData() {
        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        dataTv.setText(sportData.step+"");
        if (MyApplication.getInstance().getUserInfo().getUnit().equals("metric")){
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",sportData.distance/1000f));
            unitTv.setText("km");
        } else{
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
            unitTv.setText("Mi");
        }
        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f", MathUitl.metric2Miles(sportData.calorie)));
        heartTv.setText(sportData.heart+"");
        averageTv.setText(sportData.heart+"");
        averageTv3.setText(sportData.speed+"");
        averageTv2.setText(sportData.stride+"");
        tableView1.addData(sportData.heartArray.equals("")?null:sportData.heartArray.split(","));
        tableView2.addData(sportData.strideArray.equals("")?null:sportData.strideArray.split(","));
        sportSpeed.addData(sportData.speedArray.equals("")?null:sportData.speedArray.split(","));
    }
}
