package com.szip.jswitch.Activity.sport;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.szip.jswitch.Activity.BaseActivity;
import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.Activity.sport.fragment.BadmintonFragment;
import com.szip.jswitch.Activity.sport.fragment.BasketballFragment;
import com.szip.jswitch.Activity.sport.fragment.BikeFragment;
import com.szip.jswitch.Activity.sport.fragment.BoatFragment;
import com.szip.jswitch.Activity.sport.fragment.ClimbFragment;
import com.szip.jswitch.Activity.sport.fragment.FootballFragment;
import com.szip.jswitch.Activity.sport.fragment.GolfFragment;
import com.szip.jswitch.Activity.sport.fragment.MarathonFragment;
import com.szip.jswitch.Activity.sport.fragment.MountainFragment;
import com.szip.jswitch.Activity.sport.fragment.OnfootFragment;
import com.szip.jswitch.Activity.sport.fragment.PingpangFragment;
import com.szip.jswitch.Activity.sport.fragment.RunFragment;
import com.szip.jswitch.Activity.sport.fragment.SkiiFragment;
import com.szip.jswitch.Activity.sport.fragment.SurfingFragment;
import com.szip.jswitch.Activity.sport.fragment.SwimFragment;
import com.szip.jswitch.Activity.sport.fragment.TreadmillFragment;
import com.szip.jswitch.DB.dbModel.SportData;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.StatusBarCompat;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SportTrackActivity extends BaseActivity implements View.OnClickListener {

    /**
     * Fragment操作相关
     * */
    private FragmentManager fm;
    private FragmentTransaction transaction;

    private SportData sportData;

    private BaseFragment fragment = null;

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
        setAndroidNativeLightStatusBar(this,true);
        setTitleText(getString(R.string.track));
        ((ImageView)findViewById(R.id.rightIv)).setImageResource(R.mipmap.report_icon_share);
        Log.d("SZIP******","TYPE = "+sportData.type);
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
            case 9:
                fragment = new BadmintonFragment(sportData);
                break;
            case 10:
                fragment = new BasketballFragment(sportData);
                break;
            case 11:
                fragment = new BikeFragment(sportData);
                break;
            case 12:
                fragment = new SkiiFragment(sportData);
                break;
            case 16:
                fragment = new PingpangFragment(sportData);
                break;
            case 17:
                fragment = new FootballFragment(sportData);
                break;
            case 18:
                fragment = new SwimFragment(sportData);
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
            default:
                fragment = new RunFragment(sportData);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        /**
         * 获取权限·
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }else {
                shareShowLong((ScrollView)fragment.getView().findViewById(R.id.scrollId));
            }
        }else {
            shareShowLong((ScrollView) fragment.getView().findViewById(R.id.scrollId));
        }
    }
}
