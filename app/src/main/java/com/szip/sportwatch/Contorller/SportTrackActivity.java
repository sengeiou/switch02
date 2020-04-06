package com.szip.sportwatch.Contorller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.StatusBarCompat;

public class SportTrackActivity extends BaseActivity implements View.OnClickListener {


    private ImageView pictureIv;
    private TextView nameTv,timeTv,distanceTv,speedTv,calorieTv,sportTimeTv;
    private long time;
    private int distance,speed,calorie,sportTime;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sport_track);
        app = (MyApplication) getApplicationContext();
        initData(getIntent());
        initView();
        initEvent();
    }

    private void initData(Intent intent) {
        time = intent.getLongExtra("time",0);
        distance = intent.getIntExtra("distance",0);
        speed = intent.getIntExtra("speed",0);
        calorie = intent.getIntExtra("calorie",0);
        sportTime = intent.getIntExtra("sportTime",0);
    }

    private void initView() {
        StatusBarCompat.translucentStatusBar(SportTrackActivity.this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(R.string.track);
        findViewById(R.id.rightIv).setVisibility(View.GONE);
        pictureIv = findViewById(R.id.pictureIv);
        nameTv = findViewById(R.id.nameTv);
        timeTv = findViewById(R.id.timeTv);
        distanceTv = findViewById(R.id.distanceTv);
        speedTv = findViewById(R.id.speedTv);
        calorieTv = findViewById(R.id.calorieTv);
        sportTimeTv = findViewById(R.id.sportTimeTv);


        if (app.getUserInfo().getAvatar()!=null)
            Glide.with(this).load(app.getUserInfo().getAvatar()).into(pictureIv);
        else
            pictureIv.setImageResource(app.getUserInfo().getSex()==1?R.mipmap.my_head_male_52:R.mipmap.my_head_female_52);

        nameTv.setText(app.getUserInfo().getUserName());
        timeTv.setText(DateUtil.getStringDateFromSecond(time,"MM/dd HH:mm:ss"));
        if (app.getUserInfo().getUnit().equals("metric")){
            distanceTv.setText(String.format("%.1f",distance/10f));
            ((TextView)findViewById(R.id.unitTv)).setText("m");
        } else{
            distanceTv.setText(String.format("%.2f", MathUitl.metric2Miles(distance/10)));
            ((TextView)findViewById(R.id.unitTv)).setText("Mi");
        }
        speedTv.setText(String.format("%02d'%02d''",speed/60,speed%60));
        calorieTv.setText(calorie+"");
        sportTimeTv.setText(String.format("%02d:%02d:%02d",sportTime/3600, sportTime%3600/60,sportTime%3600%60));
    }

    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                finish();
                break;
        }
    }
}
