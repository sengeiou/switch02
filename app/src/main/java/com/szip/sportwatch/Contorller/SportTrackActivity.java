package com.szip.sportwatch.Contorller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szip.sportwatch.Contorller.Fragment.BaseFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.BasketballFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.BikeFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.BoatFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.ClimbFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.FootballFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.GolfFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.MarathonFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.MountainFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.OnfootFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.PingpangFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.RunFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.SkiiFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.SurfingFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sport.TreadmillFragment;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.StatusBarCompat;

import java.util.Locale;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SportTrackActivity extends BaseActivity implements View.OnClickListener {

    /**
     * Fragment操作相关
     * */
    private FragmentManager fm;
    private FragmentTransaction transaction;

    private SportData sportData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sport_track);
        sportData = (SportData) getIntent().getBundleExtra("data").getSerializable("sport");
        initView();
        initEvent();
    }


    private void initView() {
        StatusBarCompat.translucentStatusBar(SportTrackActivity.this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(R.string.track);
        ((ImageView)findViewById(R.id.rightIv)).setImageResource(R.mipmap.report_icon_share);
        BaseFragment fragment = null;
        switch (sportData.type){
            case 1:
                fragment = new OnfootFragment(sportData);
                break;
            case 2:
            case 6:
                fragment = new RunFragment(sportData);
                break;
            case 3:
                fragment = new TreadmillFragment(sportData);
                break;
            case 4:
                fragment = new MountainFragment(sportData);
                break;
            case 5:
                fragment = new MarathonFragment(sportData);
                break;
            case 10:
                fragment = new BasketballFragment();
                break;
            case 11:
                fragment = new BikeFragment(sportData);
                break;
            case 12:
                fragment = new SkiiFragment(sportData);
                break;
            case 16:
                fragment = new PingpangFragment();
                break;
            case 17:
                fragment = new FootballFragment();
                break;
            case 19:
                fragment = new ClimbFragment(sportData);
                break;
            case 20:
                fragment = new BoatFragment(sportData);
                break;
            case 21:
                fragment = new GolfFragment(sportData);
                break;
            case 22:
                fragment = new SurfingFragment(sportData);
                break;
        }
        fm = getSupportFragmentManager();
        transaction =  fm.beginTransaction();
        transaction.replace(R.id.fragment,fragment);
        transaction.commit();

    }

    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(this);
        findViewById(R.id.rightIv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                finish();
                break;
            case R.id.rightIv:
                checkPermission();
                break;
        }
    }

    private void checkPermission() {
        /**
         * 获取权限·
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }else {
                shareShow(findViewById(R.id.reportLl));
            }
        }else {
            shareShow(findViewById(R.id.reportLl));
        }
    }
}
