package com.szip.sportwatch.Contorller;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.sportwatch.Adapter.DialAdapter;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Model.EvenBusModel.UpdateView;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.ScreenCapture;
import com.szip.sportwatch.Util.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SelectDialActivity extends BaseActivity {

    private RecyclerView dialRv;
    private DialAdapter dialAdapter;
    private int[] dials_r = new int[]{R.mipmap.nowwatch_1,R.mipmap.nowwatch_2,R.mipmap.nowwatch_3,R.mipmap.nowwatch_4,
            R.mipmap.nowwatch_5,R.mipmap.nowwatch_6,R.mipmap.nowwatch_7,R.mipmap.nowwatch_8,R.mipmap.nowwatch_9,
            R.mipmap.nowwatch_10,R.mipmap.nowwatch_11,R.mipmap.nowwatch_12,R.mipmap.nowwatch_13,
            R.mipmap.nowwatch_15,R.mipmap.nowwatch_16,R.mipmap.nowwatch_17,R.mipmap.nowwatch_18,R.mipmap.nowwatch_28};
    private int[] jpg_r = new int[]{R.raw.bg1,R.raw.bg2,R.raw.bg3,R.raw.bg4,R.raw.bg5,R.raw.bg6,R.raw.bg7,R.raw.bg8,
            R.raw.bg9,R.raw.bg10,R.raw.bg11,R.raw.bg12,R.raw.bg13,R.raw.bg14,R.raw.bg15,R.raw.bg16,R.raw.bg17,R.raw.bg33};

    private int[] dials_c = new int[]{R.mipmap.nowwatch_c_1,R.mipmap.nowwatch_c_2,R.mipmap.nowwatch_c_3,R.mipmap.nowwatch_c_4,
            R.mipmap.nowwatch_c_5,R.mipmap.nowwatch_c_6,R.mipmap.nowwatch_c_7,R.mipmap.nowwatch_c_8,R.mipmap.nowwatch_c_9,
            R.mipmap.nowwatch_c_10,R.mipmap.nowwatch_c_11,R.mipmap.nowwatch_c_12,R.mipmap.nowwatch_c_13,R.mipmap.nowwatch_c_14,
            R.mipmap.nowwatch_c_15,R.mipmap.nowwatch_c_16,R.mipmap.nowwatch_c_17,R.mipmap.nowwatch_c_18};
    private int[] jpg_c = new int[]{R.raw.bg5,R.raw.bg7,R.raw.bg22,R.raw.bg15,R.raw.bg23,R.raw.bg20,R.raw.bg21,R.raw.bg19,
            R.raw.bg24,R.raw.bg25,R.raw.bg32,R.raw.bg26,R.raw.bg27,R.raw.bg28,R.raw.bg29,R.raw.bg30,R.raw.bg31,R.raw.bg10};

    private int[] clock_r = new int[]{1,2,3,4,5,6,7,8,9,8,10,11,11,12,11,13,14,28};
    private int[] clock_c = new int[]{15,7,16,11,17,11,11,11,18,19,20,21,22,23,24,25,26,27};

    private int pos = 0;
    private ImageView dialIv_r,dialIv_c;

    private int PAGENUM = 8000;//分包长度
    private int progress = 0;

    private boolean isCircle = false;

    private boolean isSendPic = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_select_dial);
        StatusBarCompat.translucentStatusBar(this,true);
        isCircle = ((MyApplication)getApplicationContext()).isCirlce();
        EventBus.getDefault().register(this);
        initView();
        initEvent();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!isSendPic){
            progress = 0;
            ProgressHudModel.newInstance().diss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void initEvent() {
        dialAdapter.setOnItemClickListener(new DialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position== (isCircle?dials_c.length:dials_r.length))
                    startActivity(new Intent(SelectDialActivity.this, DIYActivity.class));
                else{
                    pos = position;
                    if (isCircle) {
                        dialIv_c.setImageResource(isCircle?dials_c[position]:dials_r[position]);
                    } else {
                        dialIv_r.setImageResource(isCircle?dials_c[position]:dials_r[position]);
                    }
                }

            }
        });

        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.rightIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 保存成功
                ProgressHudModel.newInstance().show(SelectDialActivity.this,getString(R.string.loading),
                        getString(R.string.connect_error),10000);
                EXCDController.getInstance().initDialInfo();
            }
        });
    }

    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.face));
        dialRv = findViewById(R.id.dialRv);
        dialRv.setLayoutManager(new GridLayoutManager(this, 3));
        dialAdapter = new DialAdapter(isCircle?dials_c:dials_r);
        dialRv.setAdapter(dialAdapter);
        dialRv.setHasFixedSize(true);
        dialRv.setNestedScrollingEnabled(false);

        dialIv_r = findViewById(R.id.dialIv_r);
        dialIv_c = findViewById(R.id.dialIv_c);
        if (isCircle)
            ((ImageView)findViewById(R.id.changeIv)).setImageResource(R.mipmap.change_watch_c);
        if (isCircle) {
            dialIv_c.setImageResource(isCircle?dials_c[0]:dials_r[0]);
        } else {
            dialIv_r.setImageResource(isCircle?dials_c[0]:dials_r[0]);
        }

    }

    /**
     * 更新数据显示
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdataView(UpdateView updateView){
        if(updateView.getState().equals("0")){//进度+1
            progress++;
            ProgressHudModel.newInstance().setProgress(progress);
        }else if (updateView.getState().equals("1")){//完成
            isSendPic = false;
            progress = 0;
            ProgressHudModel.newInstance().diss();
            showToast(getString(R.string.diyDailOK));
        }else if (updateView.getState().equals("2")){//失败
            isSendPic = false;
            progress = 0;
            showToast(getString(R.string.diyDailError));
        }else {
            if (!isSendPic){
                isSendPic = true;
                ProgressHudModel.newInstance().diss();
                byte[] datas = ScreenCapture.imageToByte(SelectDialActivity.this, isCircle?jpg_c[pos]:jpg_r[pos]);
                int num = datas.length/PAGENUM;
                num = datas.length%PAGENUM==0?num:num+1;

                ProgressHudModel.newInstance().showWithPie(SelectDialActivity.this,getString(R.string.diyDailing),num+7,
                        getString(R.string.diyDailError),5*60*1000);

                if (datas.length<=PAGENUM){
                    EXCDController.getInstance().writeForSendImage(datas,1,num,isCircle?clock_c[pos]:clock_r[pos],
                            MathUitl.getClockStyle(isCircle?clock_c[pos]:clock_r[pos]));
                }else {
                    byte[] newDatas;
                    for (int i=0;i<datas.length;i+=PAGENUM){
                        int len = (datas.length-i>PAGENUM)?PAGENUM:(datas.length-i);
                        newDatas = new byte[len];
                        System.arraycopy(datas,i,newDatas,0,len);
                        EXCDController.getInstance().writeForSendImage(newDatas,i/PAGENUM+1,num,isCircle?clock_c[pos]:clock_r[pos],
                                MathUitl.getClockStyle(isCircle?clock_c[pos]:clock_r[pos]));
                    }
                }
                for (int i=0;i<7;i++){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progress++;
                            ProgressHudModel.newInstance().setProgress(progress);
                        }
                    },500*(i+1));
                }
            }
        }
    }
}
