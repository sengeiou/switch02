package com.szip.jswitch.Activity.bodyFat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.necer.utils.CalendarUtil;
import com.szip.jswitch.Activity.BaseActivity;
import com.szip.jswitch.Activity.LoginActivity;
import com.szip.jswitch.Activity.userInfo.UserInfoActivity;
import com.szip.jswitch.DB.LoadDataUtil;
import com.szip.jswitch.DB.SaveDataUtil;
import com.szip.jswitch.DB.dbModel.BodyFatData;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.Util.StatusBarCompat;
import com.szip.jswitch.View.BodyFatLevelProgress;
import com.szip.jswitch.View.BodyFatTable;
import com.szip.jswitch.View.MyAlerDialog;
import com.szip.jswitch.View.MyTextView;
import com.vtrump.vtble.VTComUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static com.szip.jswitch.MyApplication.FILE;

public class BodyFatActivity extends BaseActivity implements IBodyFatView {

    private IBodyFatPresenter iBodyFatPresenter;

    private int dataSize = 7;
    private ArrayList<BodyFatData> bodyFatDataList;
    private BodyFatData bodyFatData;

    private TextView timeTv,ageTv,scoreTv,shapeTv,weightTv,fatFreeTv,fatTv,muscleWeightTv,proteinWeightTv,waterTv,bmiDataTv,bmrDataTv
            ,bodyFatDataTv,muscleDataTv,proteinDataTv,skeletalDataTv,subcutaneousDataTv,boneDataTv,stateTv;
    private MyTextView bmiTv,bmrTv,obesityTv,visceralTv,bodyFatTv,muscleTv,proteinTv,skeletalTv,subcutaneousTv,boneTv;
    private ImageView shapeIv;

    private int[] shapeStr = {R.string.recessive,R.string.overweight,R.string.sports_obesity,R.string.lack_of_exercise,R.string.standard,R.string.standard_sport,
            R.string.lean_type,R.string.lean_athletic,R.string.sports_bodybuilding};
    private int[] shapeImage = {R.mipmap.recessiveobesty,R.mipmap.chubby,R.mipmap.overweightinexercise,R.mipmap.lackofexercise,
            R.mipmap.standard,R.mipmap.standardsport,R.mipmap.leantype,R.mipmap.leansports,R.mipmap.athleticfitness};

    private BodyFatLevelProgress bodyFatLevelProgress;
    private BodyFatTable bodyFatTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_body_fat);
        StatusBarCompat.translucentStatusBar(this,true);
        setAndroidNativeLightStatusBar(this,true);
        initView();
        initEvent();
        iBodyFatPresenter = new BodyFatOkPresenterImp(getApplicationContext(),this);
        bodyFatDataList = LoadDataUtil.newInstance().getBodyFat(Calendar.getInstance().getTimeInMillis()/1000,dataSize);
        bodyFatData = LoadDataUtil.newInstance().getLastBodyFat();
        checkPermission();
        refreshView(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iBodyFatPresenter.disconnectDevice();
    }

    private void checkPermission() {
        /**
         * 获取权限·
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        100);
            }else {
                iBodyFatPresenter.initBle();
            }
        }else {
            iBodyFatPresenter.initBle();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            int code = grantResults[0];
            if (!(code == PackageManager.PERMISSION_GRANTED)){
                showToast(getString(R.string.permissionErrorForLocation));
            }else {
                iBodyFatPresenter.initBle();
            }
        }
    }

    @Override
    public void initBleFinish(boolean bleEnable) {
        iBodyFatPresenter.startScan(MyApplication.getInstance().getUserInfo());
    }

    @Override
    public void updateView() {
        bodyFatDataList = LoadDataUtil.newInstance().getBodyFat(Calendar.getInstance().getTimeInMillis()/1000,dataSize);
        bodyFatData = bodyFatDataList.get(bodyFatDataList.size()-1);
        refreshView(false);
    }

    @Override
    public void updateState(String state) {
        stateTv.setText(state);
    }

    @Override
    public void showTipDialog(float weight) {
        MyAlerDialog.getSingle().showAlerDialogForBodyFat(getString(R.string.tip), getString(R.string.new_body_fat_data), weight, false,
                new MyAlerDialog.AlerDialogOnclickListener() {
                    @Override
                    public void onDialogTouch(boolean flag) {
                        if (flag){
                            iBodyFatPresenter.saveData();
                        }
                    }
                },BodyFatActivity.this).show();
    }


    private void refreshView(boolean isTouchTable) {
        if (bodyFatData!=null&&bodyFatData.weight!=0){
            timeTv.setText(DateUtil.getStringDateFromSecond(bodyFatData.time,"MM-dd HH:mm"));
            ageTv.setText(String.format("%d",(int)bodyFatData.ageOfBody));
            scoreTv.setText(String.format("%d",bodyFatData.score));
            if (MyApplication.getInstance().getUserInfo().getUnit() == 0){
                weightTv.setText(String.format("%.1fKG",bodyFatData.weight));
                fatFreeTv.setText(String.format("%.1fKG",bodyFatData.fatFreeBodyWeight));
                fatTv.setText(String.format("%.1fKG",bodyFatData.weightOfFat));
                muscleWeightTv.setText(String.format("%.1fKG",bodyFatData.weightOfMuscle));
                proteinWeightTv.setText(String.format("%.1fKG",bodyFatData.weightOfProtein));
                waterTv.setText(String.format("%.1fKG",bodyFatData.weightOfWater));
            }else {
                weightTv.setText(String.format("%.1flb", VTComUtils.kg2Lb(bodyFatData.weight)));
                fatFreeTv.setText(String.format("%.1flb",VTComUtils.kg2Lb(bodyFatData.fatFreeBodyWeight)));
                fatTv.setText(String.format("%.1flb",VTComUtils.kg2Lb(bodyFatData.weightOfFat)));
                muscleWeightTv.setText(String.format("%.1flb",VTComUtils.kg2Lb(bodyFatData.weightOfMuscle)));
                proteinWeightTv.setText(String.format("%.1flb",VTComUtils.kg2Lb(bodyFatData.weightOfProtein)));
                waterTv.setText(String.format("%.1flb",VTComUtils.kg2Lb(bodyFatData.weightOfWater)));
            }
            bmiDataTv.setText(String.format("%.1f",bodyFatData.bmi));
            bmrDataTv.setText(String.format("%.1fcal",bodyFatData.bmr));
            bodyFatDataTv.setText(String.format("%.1f%%",bodyFatData.ratioOfFat));
            muscleDataTv.setText(String.format("%.1f%%",bodyFatData.ratioOfMuscle));
            proteinDataTv.setText(String.format("%.1f%%",bodyFatData.ratioOfProtein));
            skeletalDataTv.setText(String.format("%.1f%%",bodyFatData.ratioOfSkeletalMuscle));
            subcutaneousDataTv.setText(String.format("%.1f%%",bodyFatData.ratioOfSubcutaneousFat));
            boneDataTv.setText(String.format("%.1f%%",bodyFatData.weightOfBone));

//            shapeIv.setImageResource(shapeImage[bodyFatData.bodyShape-1]);
//            shapeTv.setText(getString(shapeStr[bodyFatData.bodyShape-1]));

            bmiTv.setStyle(MathUitl.getBodyFatStateIndex(bodyFatData.bmiRange,bodyFatData.bmi));
            bmrTv.setStyle(MathUitl.getBodyFatStateIndex(bodyFatData.bmrRange,bodyFatData.bmr));
            obesityTv.setStyle(bodyFatData.obesityLevel);
            visceralTv.setStyle(MathUitl.getBodyFatStateIndex(bodyFatData.levelOfVisceralFatRange,bodyFatData.levelOfVisceralFat));
            bodyFatTv.setStyle(MathUitl.getBodyFatStateIndex(bodyFatData.ratioOfFatRange,bodyFatData.ratioOfFat));
            muscleTv.setStyle(MathUitl.getBodyFatStateIndex(bodyFatData.ratioOfMuscleRange,bodyFatData.ratioOfMuscle));
            proteinTv.setStyle(MathUitl.getBodyFatStateIndex(bodyFatData.ratioOfProteinRange,bodyFatData.ratioOfProtein));
            skeletalTv.setStyle(MathUitl.getBodyFatStateIndex(bodyFatData.ratioOfSkeletalMuscleRange,bodyFatData.ratioOfSkeletalMuscle));
            subcutaneousTv.setStyle(MathUitl.getBodyFatStateIndex(bodyFatData.ratioOfSubcutaneousFatRange,bodyFatData.ratioOfSubcutaneousFat));
            boneTv.setStyle(MathUitl.getBodyFatStateIndex(bodyFatData.weightOfBoneRange,bodyFatData.weightOfBone));
            bodyFatLevelProgress.setRadio(bodyFatData.weightRange,bodyFatData.weight);
        }
        if (!isTouchTable)
            bodyFatTable.setBodyFatDataList(bodyFatDataList);
    }

    private void initView() {
        timeTv = findViewById(R.id.timeTv);
        ageTv = findViewById(R.id.ageTv);
        scoreTv = findViewById(R.id.scoreTv);
        shapeTv = findViewById(R.id.shapeTv);
        weightTv = findViewById(R.id.weightTv);
        fatFreeTv = findViewById(R.id.fatFreeTv);
        fatTv = findViewById(R.id.fatTv);
        muscleWeightTv = findViewById(R.id.muscleWeightTv);
        proteinWeightTv = findViewById(R.id.proteinWeightTv);
        waterTv = findViewById(R.id.waterTv);
        bmiDataTv = findViewById(R.id.bmiDataTv);
        bmrDataTv = findViewById(R.id.bmrDataTv);
        bodyFatDataTv = findViewById(R.id.bodyFatDataTv);
        muscleDataTv = findViewById(R.id.muscleDataTv);
        proteinDataTv = findViewById(R.id.proteinDataTv);
        skeletalDataTv = findViewById(R.id.skeletalDataTv);
        subcutaneousDataTv = findViewById(R.id.subcutaneousDataTv);
        boneDataTv = findViewById(R.id.boneDataTv);
        bmiTv = findViewById(R.id.bmiTv);
        bmrTv = findViewById(R.id.bmrTv);obesityTv = findViewById(R.id.obesityTv);
        visceralTv = findViewById(R.id.visceralTv);
        bodyFatTv = findViewById(R.id.bodyFatTv);
        muscleTv = findViewById(R.id.muscleTv);
        proteinTv = findViewById(R.id.proteinTv);
        skeletalTv = findViewById(R.id.skeletalTv);
        subcutaneousTv = findViewById(R.id.subcutaneousTv);
        boneTv = findViewById(R.id.boneTv);
        shapeIv = findViewById(R.id.shapeIv);
        bodyFatLevelProgress = findViewById(R.id.weightLp);
        bodyFatTable = findViewById(R.id.bodyFatTable);
        stateTv = findViewById(R.id.stateTv);
        if(MyApplication.getInstance().getProductId()!=1)
            findViewById(R.id.userIv).setVisibility(View.GONE);
    }

    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(listener);
        findViewById(R.id.lastSevenRb).setOnClickListener(listener);
        findViewById(R.id.lastThirtyRb).setOnClickListener(listener);
        findViewById(R.id.stateTv).setOnClickListener(listener);
        findViewById(R.id.userIv).setOnClickListener(listener);
        bodyFatTable.setOnTouchListener(new BodyFatTable.OnTouchListener() {
            @Override
            public void onPosition(int index) {
                if (bodyFatDataList!=null&&bodyFatDataList.get(index).weight!=0){
                    bodyFatData = bodyFatDataList.get(index);
                    refreshView(true);
                }
            }
        });
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.backIv:
                    if (MyApplication.getInstance().getProductId()!=1) {
                        finish();
                    } else {
                        MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.logoutTip), null, null,
                                false, new MyAlerDialog.AlerDialogOnclickListener() {
                                    @Override
                                    public void onDialogTouch(boolean flag) {
                                        if (flag){
                                            MathUitl.saveStringData(BodyFatActivity.this,"token",null).commit();
                                            Intent intent = new Intent();
                                            intent.setClass(BodyFatActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                },BodyFatActivity.this);
                    }
                    break;
                case R.id.userIv:
                    if (MyApplication.getInstance().getUserInfo().getPhoneNumber()==null&&MyApplication.getInstance().getUserInfo().getEmail()==null)
                        showToast(getString(R.string.visiter));
                    else
                        startActivity(new Intent(BodyFatActivity.this, UserInfoActivity.class));
                    break;
                case R.id.lastSevenRb:
                    dataSize = 7;
                    bodyFatDataList = LoadDataUtil.newInstance().getBodyFat(Calendar.getInstance().getTimeInMillis()/1000,dataSize);
                    refreshView(false);
                    break;
                case R.id.lastThirtyRb:
                    dataSize = 31;
                    bodyFatDataList = LoadDataUtil.newInstance().getBodyFat(Calendar.getInstance().getTimeInMillis()/1000,dataSize);
                    refreshView(false);
                    break;
                case R.id.stateTv:
                    iBodyFatPresenter.startScan(MyApplication.getInstance().getUserInfo());
                    break;
            }
        }
    };
}