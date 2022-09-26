package com.szip.jswitch.Activity.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szip.jswitch.Activity.gpsSport.GpsActivity;
import com.szip.jswitch.Activity.main.IGetLocation;
import com.szip.jswitch.Activity.main.MainActivity;
import com.szip.jswitch.Activity.sport.SportDataListActivity;
import com.szip.jswitch.Activity.userInfo.UserInfoActivity;
import com.szip.jswitch.BLE.EXCDController;
import com.szip.jswitch.DB.LoadDataUtil;
import com.szip.jswitch.DB.dbModel.SportData;
import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.View.CircularImageView;
import com.szip.jswitch.View.MyAlerDialog;

import java.util.Locale;

/**
 * Created by Administrator on 2019/12/1.
 */

public class SportFragment extends BaseFragment implements View.OnClickListener{

    private CircularImageView pictureIv;
    private LinearLayout heartLl,distanceLl;
    private RelativeLayout backRl,startRl;
    private TextView userNameTv,calorieTv,sportTimeTv,heartTv,distanceTv1,runTv,walkTv;
    private ImageView sportTypeIv,sportIv,gpsIv;
    private MyApplication app;

    private int sportType = 2; //2:跑步运动 1:徒步运动
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sport;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();
        Log.i("data******","sport");
        checkPermission();
        initView();
        initEvent();
    }

    /**
     * 获取权限
     * */
    private void checkPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("data******","state = "+getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)+"Build.VERSION.SDK_INT = "+Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                ||getActivity().checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                    MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tipForPrivacy), getString(R.string.getPrivacy), getString(R.string.confirm),
                            getString(R.string.cancel), false, new MyAlerDialog.AlerDialogOnclickListener() {
                                @Override
                                public void onDialogTouch(boolean flag) {
                                    if (flag){
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACTIVITY_RECOGNITION},
                                                102);
                                    }
                                }
                            }, getActivity());
                }
            }else {

                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tipForPrivacy), getString(R.string.getPrivacy), getString(R.string.confirm),
                            getString(R.string.cancel), false, new MyAlerDialog.AlerDialogOnclickListener() {
                                @Override
                                public void onDialogTouch(boolean flag) {
                                    if (flag){
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                101);
                                    }
                                }
                            }, getActivity());
                }
            }

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
        ((MainActivity)getActivity()).getLocation(iGetLocation);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).getLocation(null);
    }

    private void initEvent() {
        getView().findViewById(R.id.pictureIv).setOnClickListener(this);
        getView().findViewById(R.id.userNameTv).setOnClickListener(this);
        getView().findViewById(R.id.historyIv).setOnClickListener(this);
        runTv.setOnClickListener(this);
        walkTv.setOnClickListener(this);
        startRl.setOnClickListener(this);
    }

    private void initView() {
        gpsIv = getView().findViewById(R.id.gpsIv);
        runTv = getView().findViewById(R.id.runTv);
        walkTv = getView().findViewById(R.id.walkTv);
        startRl = getView().findViewById(R.id.startRl);
        sportIv = getView().findViewById(R.id.sportIv);
        backRl = getView().findViewById(R.id.backRl);
        heartLl = getView().findViewById(R.id.heartLl);
        distanceLl = getView().findViewById(R.id.distanceLl);
        pictureIv = getView().findViewById(R.id.pictureIv);
        sportTypeIv = getView().findViewById(R.id.sportTypeIv);
        userNameTv = getView().findViewById(R.id.userNameTv);
        calorieTv = getView().findViewById(R.id.buttonFirstTv);
        sportTimeTv = getView().findViewById(R.id.buttonSecondTv);
        heartTv = getView().findViewById(R.id.heartTv);
        distanceTv1 = getView().findViewById(R.id.distanceTv1);
    }

    /**
     * 初始化数据
     * */
    private void initData() {
        SportData sportData = LoadDataUtil.newInstance().getBestSportData();
        int typeId = R.mipmap.sport_bg_walk;
        int backId = R.drawable.sport_bg_green_light;
        switch (sportData.type){
            case 1:
                backId =  R.drawable.sport_bg_green_light;
                typeId = R.mipmap.sport_bg_walk;
                break;
            case 2:
                backId =  R.drawable.sport_bg_orange;
                typeId = R.mipmap.sport_bg_outrun;
                break;
            case 3:
                backId =  R.drawable.sport_bg_green;
                typeId = R.mipmap.sport_bg_treadmill;
                break;
            case 4:
                backId =  R.drawable.sport_bg_green;
                typeId = R.mipmap.sport_bg_mountain;
                break;
            case 5:
                backId =  R.drawable.sport_bg_blue_light;
                typeId = R.mipmap.sport_bg_marathon;
                break;
            case 6:
                backId =  R.drawable.sport_bg_purple;
                typeId = R.mipmap.sport_bg_trainrun;
                break;
            case 9:
                backId =  R.drawable.sport_bg_blue_light;
                typeId = R.mipmap.sport_bg_badminton;
                break;
            case 10:
                backId =  R.drawable.sport_bg_purple;
                typeId = R.mipmap.sport_bg_basketball;
                break;
            case 11:
                backId =  R.drawable.sport_bg_orange;
                typeId = R.mipmap.sport_bg_bike;
                break;
            case 12:
                backId =  R.drawable.sport_bg_blue_light;
                typeId = R.mipmap.sport_bg_skii;
                break;
            case 16:
                backId =  R.drawable.sport_bg_red;
                typeId = R.mipmap.sport_bg_pingpong;
                break;
            case 17:
                backId =  R.drawable.sport_bg_green;
                typeId = R.mipmap.sport_bg_football;
                break;
            case 18:
                backId =  R.drawable.sport_bg_blue_light;
                typeId = R.mipmap.sport_bg_swim;
                break;
            case 19:
                backId =  R.drawable.sport_bg_red;
                typeId = R.mipmap.sport_bg_climb;
                break;
            case 20:
                backId =  R.drawable.sport_bg_green_light;
                typeId = R.mipmap.sport_bg_boating;
                break;
            case 22:
                backId =  R.drawable.sport_bg_red;
                typeId = R.mipmap.sport_bg_surfing;
                break;
        }
        backRl.setBackground(getResources().getDrawable(backId));
        sportTypeIv.setImageResource(typeId);

        if (sportData.distance!=0){
            heartLl.setVisibility(View.GONE);
            distanceLl.setVisibility(View.VISIBLE);
            if (app.getUserInfo().getUnit()==0){
                distanceTv1.setText(String.format(Locale.ENGLISH,"%.2f",((sportData.distance+5)/10)/100f));
                ((TextView)getView().findViewById(R.id.deistanceUnitTv)).setText(getString(R.string.distance)+"km)");
            } else{
                distanceTv1.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.km2Miles(sportData.distance)));
                ((TextView)getView().findViewById(R.id.deistanceUnitTv)).setText(getString(R.string.distance)+"Mi)");
            }
        }else {
            heartLl.setVisibility(View.VISIBLE);
            distanceLl.setVisibility(View.GONE);
            heartTv.setText(sportData.heart+"");
        }

        calorieTv.setText(String.format(Locale.ENGLISH,"%.1f", sportData.calorie/1000f));
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600, sportData.sportTime%3600/60,sportData.sportTime%3600%60));

        if (app.getUserInfo().getAvatar()!=null)
            Glide.with(this).load(app.getUserInfo().getAvatar()).into(pictureIv);
        else
            pictureIv.setImageResource(R.mipmap.head);
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
            case R.id.runTv:
                sportType = 2;
                runTv.setTextColor(getResources().getColor(R.color.rayblue));
                walkTv.setTextColor(getResources().getColor(R.color.gray));
                sportIv.setImageResource(R.mipmap.sport_btnicon_outrun);
                break;
            case R.id.walkTv:
                sportType = 1;
                runTv.setTextColor(getResources().getColor(R.color.gray));
                walkTv.setTextColor(getResources().getColor(R.color.rayblue));
                sportIv.setImageResource(R.mipmap.sport_btnicon_walk);
                break;
            case R.id.startRl:
                if (MainService.getInstance().getState()!=3){
                    Intent intent = new Intent(getActivity(), GpsActivity.class);
                    intent.putExtra("sportType",sportType);
                    startActivity(intent);
                }else {
                    if (MyApplication.getInstance().isSyncSport()){
                        EXCDController.getInstance().writeForStartSport(sportType);
                    }else {
                        Intent intent = new Intent(getActivity(), GpsActivity.class);
                        intent.putExtra("sportType",sportType);
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    private IGetLocation iGetLocation = new IGetLocation() {
        @Override
        public void onLocation(Location location) {
            if (location!=null){
                float acc = location.getAccuracy();
                if (acc>=29){
                    gpsIv.setImageResource(R.mipmap.sport_icon_gps_1);
                }else if (acc>=15){
                    gpsIv.setImageResource(R.mipmap.sport_icon_gps_2);
                }else {
                    gpsIv.setImageResource(R.mipmap.sport_icon_gps_3);
                }
            }
        }
    };
}
