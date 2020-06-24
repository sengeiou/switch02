package com.szip.sportwatch.Contorller;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.StatusBarCompat;

public class SportTrackActivity extends BaseActivity implements View.OnClickListener {


    private ImageView pictureIv,bgIv,typeIv;
    private TextView nameTv,timeTv,distanceTv,speedTv,calorieTv,sportTimeTv,typeTv,heartTv,strideTv;
    private MyApplication app;
    private long time;
    private int sportTime;
    private int distance;
    private int calorie;
    private int speed;
    private int type;
    private int heart;
    private int stride;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sport_track);
        app = (MyApplication) getApplicationContext();
        time = getIntent().getLongExtra("time",0);
        sportTime = getIntent().getIntExtra("sportTime",0);
        distance = getIntent().getIntExtra("distance",0);
        calorie = getIntent().getIntExtra("calorie",0);
        speed = getIntent().getIntExtra("speed",0);
        type = getIntent().getIntExtra("type",0);
        heart = getIntent().getIntExtra("heart",0);
        stride = getIntent().getIntExtra("stride",0);
        initView();
        initEvent();
    }


    private void initView() {
        StatusBarCompat.translucentStatusBar(SportTrackActivity.this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(R.string.track);
        ((ImageView)findViewById(R.id.rightIv)).setImageResource(R.mipmap.report_icon_share);
        pictureIv = findViewById(R.id.pictureIv);
        nameTv = findViewById(R.id.nameTv);
        timeTv = findViewById(R.id.timeTv);
        distanceTv = findViewById(R.id.distanceTv);
        speedTv = findViewById(R.id.speedTv);
        calorieTv = findViewById(R.id.calorieTv);
        typeTv = findViewById(R.id.typeTv);
        heartTv = findViewById(R.id.heartTv);
        strideTv = findViewById(R.id.strideTv);
        bgIv = findViewById(R.id.bgIv);
        sportTimeTv = findViewById(R.id.sportTimeTv);
        typeIv = findViewById(R.id.typeIv);



        if (app.getUserInfo().getAvatar()!=null)
            Glide.with(this).load(app.getUserInfo().getAvatar()).into(pictureIv);
        else
            pictureIv.setImageResource(app.getUserInfo().getSex()==1?R.mipmap.my_head_male_52:R.mipmap.my_head_female_52);

        nameTv.setText(app.getUserInfo().getUserName());
        timeTv.setText(DateUtil.getStringDateFromSecond(time,"MM/dd HH:mm:ss"));

        switch (type){
            case 1:{//走路
            }
            break;
            case 2://跑步
            case 5:
            case 6:
            case 7:
            case 3:{//室内跑步
                typeTv.setText(getString(R.string.run));
                if (app.getUserInfo().getUnit().equals("metric")){
                    distanceTv.setText(String.format("%.1f",distance/10f));
                    ((TextView)findViewById(R.id.unitTv)).setText("m");
                } else{
                    distanceTv.setText(String.format("%.2f", MathUitl.metric2Miles(distance/10)));
                    ((TextView)findViewById(R.id.unitTv)).setText("Mi");
                }
                speedTv.setText(String.format("%02d'%02d''",speed/60,speed%60));
                strideTv.setText(stride+"");
                bgIv.setImageResource(R.mipmap.sport_bg_run);
                typeIv.setImageResource(R.mipmap.sport_pic_run);
            }
            break;
            case 4:{//登山
                typeTv.setText(getString(R.string.mountain));
                if (app.getUserInfo().getUnit().equals("metric")){
                    distanceTv.setText(String.format("%.1f",distance/10f));
                    ((TextView)findViewById(R.id.unitTv)).setText("m");
                } else{
                    distanceTv.setText(String.format("%.2f", MathUitl.metric2Miles(distance/10)));
                    ((TextView)findViewById(R.id.unitTv)).setText("Mi");
                }
                speedTv.setText(String.format("%02d'%02d''",speed/60,speed%60));
                strideTv.setText(stride+"");
                bgIv.setImageResource(R.mipmap.sport_bg_mountain);
                typeIv.setImageResource(R.mipmap.sport_pic_mountain);
            }
            break;
            case 8:{//跳绳

            }
            break;
            case 9:{//羽毛球

            }
            break;
            case 10:{//篮球
                typeTv.setText(getString(R.string.basket));
                findViewById(R.id.distanceRl).setVisibility(View.GONE);
                findViewById(R.id.strideRl).setVisibility(View.GONE);
                findViewById(R.id.speedRl).setVisibility(View.GONE);
                bgIv.setImageResource(R.mipmap.sport_bg_basketball);
                typeIv.setImageResource(R.mipmap.sport_pic_basketball);
            }
            break;
            case 11:{//骑行
                typeTv.setText(getString(R.string.bike));
                findViewById(R.id.distanceRl).setVisibility(View.GONE);
                findViewById(R.id.strideRl).setVisibility(View.GONE);
                findViewById(R.id.speedRl).setVisibility(View.GONE);
                bgIv.setImageResource(R.mipmap.sport_bg_bike);
                typeIv.setImageResource(R.mipmap.sport_pic_bike);
            }
            break;
            case 12:{//滑冰

            }
            break;
            case 13:{//健身房

            }
            break;
            case 14:{//瑜伽

            }
            break;
            case 15:{//网球

            }
            break;
            case 16:{//乒乓球
                typeTv.setText(getString(R.string.pingpong));
                findViewById(R.id.distanceRl).setVisibility(View.GONE);
                findViewById(R.id.strideRl).setVisibility(View.GONE);
                findViewById(R.id.speedRl).setVisibility(View.GONE);
                bgIv.setImageResource(R.mipmap.sport_bg_pingpang);
                typeIv.setImageResource(R.mipmap.sport_pic_pingpang);
            }
            break;
            case 17:{//足球
                typeTv.setText(getString(R.string.football));
                findViewById(R.id.distanceRl).setVisibility(View.GONE);
                findViewById(R.id.strideRl).setVisibility(View.GONE);
                findViewById(R.id.speedRl).setVisibility(View.GONE);
                bgIv.setImageResource(R.mipmap.sport_bg_football);
                typeIv.setImageResource(R.mipmap.sport_pic_football);
            }
            break;
            case 18:{//游泳

            }
            break;
        }

        calorieTv.setText(calorie+"");
        sportTimeTv.setText(String.format("%02d:%02d:%02d",sportTime/3600, sportTime%3600/60,sportTime%3600%60));
        heartTv.setText(heart+"");
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
