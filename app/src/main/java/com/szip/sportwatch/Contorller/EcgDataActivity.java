package com.szip.sportwatch.Contorller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.WH_ECGView;

import java.util.ArrayList;
import java.util.Locale;

public class EcgDataActivity extends BaseActivity{

    private WH_ECGView ecgView,ecgView1;
    private TextView nameTv,averageTv,maxTv,minTv,timeTv;
    private String nameStr;
    private int average,max,min;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_ecg_data);

        Intent intent = getIntent();
        nameStr = intent.getStringExtra("name");
        average = intent.getIntExtra("average",0);
        max = intent.getIntExtra("max",0);
        min = intent.getIntExtra("min",0);
        time = intent.getLongExtra("time",0);
        initView();
        initEvent();

    }

    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        StatusBarCompat.translucentStatusBar(EcgDataActivity.this,true);
        final int data[] = new int[]{0,0,0,15,30,15,0,-15,180,-45,0,10,0,15,30,45,30,15,0,0,0,
                0,0,0,15,30,15,0,-15,180,-45,0,10,0,15,30,45,30,15,0,0,0,
                0,0,0,15,30,15,0,-15,180,-45,0,10,0,15,30,45,30,15,0,0,0,
                0,0,0,15,30,15,0,-15,180,-45,0,10,0,15,30,45,30,15,0,0,0,
                0,0,0,15,30,15,0,-15,180,-45,0,10,0,15,30,45,30,15,0,0,0,
                0,0,0,15,30,15,0,-15,180,-45,0,10,0,15,30,45,30,15,0,0,0,
                0,0,0,15,30,15,0,-15,180,-45,0,10,0,15,30,45,30,15,0,0,0};

        ecgView = findViewById(R.id.ecg_data_ecgView);
        ecgView1 = findViewById(R.id.ecg_data_ecgView1);
        ecgView.addData(data);
        ecgView1.addData(data);

        nameTv = findViewById(R.id.nameTv);
        nameTv.setText(nameStr);
        averageTv = findViewById(R.id.averageTv);
        averageTv.setText(String.format(Locale.ENGLISH,"%dBpm",average));
        maxTv = findViewById(R.id.maxTv);
        maxTv.setText(String.format("%dBpm",max));
        minTv = findViewById(R.id.minTv);
        minTv.setText(String.format(Locale.ENGLISH,"%dBpm",min));
        timeTv = findViewById(R.id.timeTv);
        timeTv.setText(DateUtil.getStringDateFromSecond(time,"MM/dd HH:mm:ss"));
    }
}
