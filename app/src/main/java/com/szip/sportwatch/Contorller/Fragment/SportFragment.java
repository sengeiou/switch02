package com.szip.sportwatch.Contorller.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szip.sportwatch.Contorller.SportDataListActivity;
import com.szip.sportwatch.Contorller.UserInfoActivity;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.View.CircularImageView;

import java.io.File;

import androidx.core.content.FileProvider;

/**
 * Created by Administrator on 2019/12/1.
 */

public class SportFragment extends BaseFragment implements View.OnClickListener{

    private CircularImageView pictureIv;
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

        if (app.getUserInfo().getUnit().equals("metric")){
            distanceTv.setText(String.format("%.1f",sportData.distance/10f));
            ((TextView)getView().findViewById(R.id.unitTv)).setText("m");
        } else{
            distanceTv.setText(String.format("%.2f", MathUitl.metric2Miles(sportData.distance/10)));
            ((TextView)getView().findViewById(R.id.unitTv)).setText("Mi");
        }
        speedTv.setText(String.format("%02d'%02d''",sportData.speed/60,sportData.speed%60));
        calorieTv.setText(sportData.calorie+"");
        sportTimeTv.setText(String.format("%02d:%02d:%02d",sportData.sportTime/3600, sportData.sportTime%3600/60,sportData.sportTime%3600%60));


        if (app.getUserInfo().getAvatar()!=null)
            Glide.with(this).load(app.getUserInfo().getAvatar()).into(pictureIv);
        else
            pictureIv.setImageResource(app.getUserInfo().getSex()==1?R.mipmap.my_head_male_52:R.mipmap.my_head_female_52);
        userNameTv.setText(app.getUserInfo().getUserName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pictureIv:
            case R.id.userNameTv:
                if (app.getUserInfo().getPhoneNumber()==null&&app.getUserInfo().getEmail()==null)
                    showToast(getString(R.string.visiter));
                else
                    startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.historyIv:
                startActivity(new Intent(getActivity(), SportDataListActivity.class));
                break;
        }
    }
}
