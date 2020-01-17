package com.szip.sportwatch.Contorller.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.sportwatch.Contorller.SportDataListActivity;
import com.szip.sportwatch.Contorller.UserInfoActivity;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;

/**
 * Created by Administrator on 2019/12/1.
 */

public class SportFragment extends BaseFragment implements View.OnClickListener{

    private ImageView pictureIv;
    private TextView userNameTv,distanceTv,speedTv,calorieTv,sportTimeTv;

    private MyApplication app;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sport;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();
        initView();
        initEvent();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initEvent() {
        getView().findViewById(R.id.pictureIv).setOnClickListener(this);
        getView().findViewById(R.id.userNameTv).setOnClickListener(this);
        getView().findViewById(R.id.historyIv).setOnClickListener(this);
    }

    private void initView() {
        pictureIv = getView().findViewById(R.id.pictureIv);
        userNameTv = getView().findViewById(R.id.userNameTv);
        distanceTv = getView().findViewById(R.id.distanceTv);
        speedTv = getView().findViewById(R.id.speedTv);
        calorieTv = getView().findViewById(R.id.calorieTv);
        sportTimeTv = getView().findViewById(R.id.sportTimeTv);
    }

    /**
     * 初始化数据
     * */
    private void initData() {
        SportData sportData = LoadDataUtil.newInstance().getBestSportData();
        if (sportData!=null){
            distanceTv.setText(String.format("%.2f",sportData.distance/1000f));
            speedTv.setText(String.format("%02d'%02d''",sportData.speed/60,sportData.speed%60));
            calorieTv.setText(sportData.calorie+"");
            sportTimeTv.setText(String.format("%02d:%02d:%02d",sportData.sportTime/3600, sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        }
        if (app.getUserInfo()!=null){
            userNameTv.setText(app.getUserInfo().getUserName());
            pictureIv.setImageResource(app.getUserInfo().getSex()==1?R.mipmap.my_head_male_52:R.mipmap.my_head_female_52);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pictureIv:
            case R.id.userNameTv:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.historyIv:
                startActivity(new Intent(getActivity(), SportDataListActivity.class));
                break;
        }
    }
}
