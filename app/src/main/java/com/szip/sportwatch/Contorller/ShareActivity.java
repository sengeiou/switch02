package com.szip.sportwatch.Contorller;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ScreenCapture;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.MyAlerDialog;

import java.util.Locale;

public class ShareActivity extends BaseActivity {

    private long time;
    private int value,value1,value2,value3;
    private boolean isFirst = true;


    private LinearLayout layout;
    private TextView timeTv,stateTv,valueTv,valueTv1,valueTv2,valueTv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_share);
        Intent intent = getIntent();
        int flag = intent.getIntExtra("flag",0);
        time = intent.getLongExtra("time",0);
        value = intent.getIntExtra("value",0);
        value1 = intent.getIntExtra("value1",0);
        value2 = intent.getIntExtra("value2",0);
        value3 = intent.getIntExtra("value3",0);

        initView(flag);
        checkPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst)
            finish();
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
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        initData();
                    }
                }, 100);
            }
        }else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    initData();
                }
            }, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            int code = grantResults[0];
            if (code == PackageManager.PERMISSION_GRANTED){
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        initData();
                    }
                }, 100);
            }else {
                showToast(getString(R.string.shareFailForPermission));
                ShareActivity.this.finish();
            }
        }
    }

    private void initView(int flag) {
        StatusBarCompat.translucentStatusBar(ShareActivity.this,true);
        layout = findViewById(R.id.backgroundRl);
        timeTv = findViewById(R.id.timeTv);
        stateTv = findViewById(R.id.stateTv);
        valueTv = findViewById(R.id.valueTv1);
        valueTv1 = findViewById(R.id.valueTv2);
        valueTv2 = findViewById(R.id.valueTv3);
        valueTv3 = findViewById(R.id.valueTv4);

        switch (flag){
            case 0:
                layout.setBackgroundResource(R.mipmap.sport_share);
                timeTv.setTextColor(getResources().getColor(R.color.white));
                timeTv.setText(DateUtil.getStringDateFromSecond(time,"yyyy-MM-dd"));

                valueTv.setTextColor(getResources().getColor(R.color.white));
                valueTv.setText(MathUitl.initText(String.format(Locale.ENGLISH,"%dsteps",value),0,null,null));

                stateTv.setTextColor(getResources().getColor(R.color.white));
                if (value1>value)
                    stateTv.setText(getString(R.string.planNO));
                else
                    stateTv.setText(getString(R.string.planOK));

                valueTv1.setTextColor(getResources().getColor(R.color.white));
                valueTv1.setText(MathUitl.initText(String.format(Locale.ENGLISH,getString(R.string.planStepShare),value1),
                        0,":","steps"));

                valueTv2.setTextColor(getResources().getColor(R.color.white));
                valueTv2.setText(MathUitl.initText(String.format(Locale.ENGLISH,getString(R.string.distanceShare),value2/1000f),0,
                        ":","Km"));

                valueTv3.setTextColor(getResources().getColor(R.color.white));
                valueTv3.setText(MathUitl.initText(String.format(Locale.ENGLISH,getString(R.string.calurieShare),value3/10f),0,
                        ":","Kcal"));
                break;
            case 1:
                layout.setBackground(getResources().getDrawable(R.mipmap.sleep));
                timeTv.setTextColor(getResources().getColor(R.color.white));
                timeTv.setText(DateUtil.getStringDateFromSecond(time,"yyyy-MM-dd"));

                valueTv.setTextColor(getResources().getColor(R.color.white));
                valueTv.setText(MathUitl.initText(String.format(Locale.ENGLISH,"%02dh%02dmin",value/60,value%60),1,null,null));

                stateTv.setTextColor(getResources().getColor(R.color.white));
                if (value1>value)
                    stateTv.setText(getString(R.string.planNO));
                else
                    stateTv.setText(getString(R.string.planOK));

                valueTv1.setTextColor(getResources().getColor(R.color.white));
                valueTv1.setText(MathUitl.initText(String.format(Locale.ENGLISH,getString(R.string.planSleep),value1/60f),1,":","h"));

                valueTv2.setTextColor(getResources().getColor(R.color.white));
                valueTv2.setText(MathUitl.initText(String.format(Locale.ENGLISH,getString(R.string.deepSleepShare),value2/60,value2%60),1,
                        ":",null));

                valueTv3.setTextColor(getResources().getColor(R.color.white));
                valueTv3.setText(MathUitl.initText(String.format(Locale.ENGLISH,getString(R.string.lightSleepShare),value3/60,value3%60),1,
                        ":",null));
                break;
            case 2:
                layout.setBackground(getResources().getDrawable(R.mipmap.heart));
                timeTv.setTextColor(getResources().getColor(R.color.black));
                timeTv.setText(DateUtil.getStringDateFromSecond(time,"yyyy-MM-dd"));

                valueTv.setTextColor(getResources().getColor(R.color.black));
                valueTv.setText(MathUitl.initText(String.format(Locale.ENGLISH,"%dbpm",value),2,null,null));

                stateTv.setTextColor(getResources().getColor(R.color.black));
                stateTv.setText(getString(R.string.averageHeart));

                valueTv1.setTextColor(getResources().getColor(R.color.black));
                valueTv1.setText(MathUitl.initText(String.format(Locale.ENGLISH,getString(R.string.testTimes),value1),2,":",null));

                valueTv2.setTextColor(getResources().getColor(R.color.black));
                valueTv2.setText(MathUitl.initText(String.format(Locale.ENGLISH,getString(R.string.maxHeartShare),value2),2,":","bpm"));

                valueTv3.setTextColor(getResources().getColor(R.color.black));
                valueTv3.setText(MathUitl.initText(String.format(Locale.ENGLISH,getString(R.string.minHeartShare),value3),2,":","bpm"));
                break;
            case 3:
                layout.setBackground(getResources().getDrawable(R.mipmap.bp));
                timeTv.setTextColor(getResources().getColor(R.color.white));
                timeTv.setText(DateUtil.getStringDateFromSecond(time,"yyyy-MM-dd"));

                valueTv.setTextColor(getResources().getColor(R.color.white));
                valueTv.setText(MathUitl.initText(String.format("%d/%dmmHg",value,value1),3,null,null));

                stateTv.setTextColor(getResources().getColor(R.color.white));
                stateTv.setText(getString(R.string.bloodPressureData));

                valueTv1.setTextColor(getResources().getColor(R.color.white));
                if (value2==0)
                    valueTv1.setText(MathUitl.initText(String.format(getString(R.string.testValue)+" "+getString(R.string.normal)),3,
                            ":",null));
                else if (value2 == 1)
                    valueTv1.setText(MathUitl.initText(String.format(getString(R.string.testValue)+" "+getString(R.string.flat)),3,
                            ":",null));
                else
                    valueTv1.setText(MathUitl.initText(String.format(getString(R.string.testValue)+" "+getString(R.string.higher)),3,
                            ":",null));

                valueTv2.setTextColor(getResources().getColor(R.color.white));
                valueTv2.setText(MathUitl.initText(String.format(getString(R.string.testTimes),value3),3,":",null));

                break;
            case 4:
                layout.setBackground(getResources().getDrawable(R.mipmap.bo));
                timeTv.setTextColor(getResources().getColor(R.color.rayblue));
                timeTv.setText(DateUtil.getStringDateFromSecond(time,"yyyy-MM-dd"));

                valueTv.setTextColor(getResources().getColor(R.color.rayblue));
                valueTv.setText(MathUitl.initText(String.format("%d%%",value),4,null,null));

                stateTv.setTextColor(getResources().getColor(R.color.rayblue));
                stateTv.setText(getString(R.string.bloodOxygenData));

                valueTv1.setTextColor(getResources().getColor(R.color.rayblue));
                valueTv1.setText(MathUitl.initText(String.format(getString(R.string.testValue)+" "+(value1==0?getString(R.string.normal):getString(R.string.flat))),
                        4,":",null));

                valueTv2.setTextColor(getResources().getColor(R.color.rayblue));
                valueTv2.setText(MathUitl.initText(String.format(getString(R.string.testTimes),value2),4,":",null));

                valueTv3.setTextColor(getResources().getColor(R.color.rayblue));
                valueTv3.setText(MathUitl.initText(String.format(getString(R.string.passTimeData),value3/10f),4,":",null));
                break;
        }
    }

    private void initData() {
        // TODO Auto-generated method stub
        String filePath = ScreenCapture.getBitmap
                (ShareActivity.this, layout);
        isFirst = false;
//        shareShow(filePath);
//        Intent intent = new Intent();
//        intent.putExtra("filePath", filePath);
//        setResult(101, intent);
//        finish();
    }
}
