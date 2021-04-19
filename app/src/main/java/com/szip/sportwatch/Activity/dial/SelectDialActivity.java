package com.szip.sportwatch.Activity.dial;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.szip.sportwatch.Activity.BaseActivity;
import com.szip.sportwatch.Activity.diy.DIYActivity;
import com.szip.sportwatch.BLE.BleClient;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.Model.EvenBusModel.UpdateView;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SelectDialActivity extends BaseActivity implements ISelectDialView{


    private ISelectDialPresenter iSelectDialPresenter;
    private int pictureId = -1;
    private int clock = -1;
    private ImageView dialIv,changeIv;
    private int progress = 0;
    private boolean isSendPic = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (MyApplication.getInstance().isMtk()) {
            setContentView(R.layout.activity_select_dial);
            iSelectDialPresenter = new SelectDialPresenterImpl(getApplicationContext(),this);
        } else{
            setContentView(R.layout.activity_select_dial06);
            iSelectDialPresenter = new SelectDialPresenterImpl06(getApplicationContext(),this);
        }
        StatusBarCompat.translucentStatusBar(this,true);
        setAndroidNativeLightStatusBar(this,true);
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
                if (!ProgressHudModel.newInstance().isShow()){
                    ProgressHudModel.newInstance().show(SelectDialActivity.this,getString(R.string.loading),
                            getString(R.string.connect_error),10000);
                    if (MyApplication.getInstance().isMtk())
                        EXCDController.getInstance().initDialInfo();
                    else
                        BleClient.getInstance().writeForSendPicture(0,clock,0,0,new byte[0]);
                }
            }
        });
    }

    private void initView() {
        setTitleText(getString(R.string.face));
        RecyclerView dialRv = findViewById(R.id.dialRv);
        changeIv = findViewById(R.id.changeIv);
        iSelectDialPresenter.getViewConfig(dialRv);
    }

    /**
     * 更新数据显示
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdataView(UpdateView updateView){
        if(updateView.getState().equals("0")){//进度+1
            progress++;
            iSelectDialPresenter.sendDial(-1,-1);
            ProgressHudModel.newInstance().setProgress(progress);
        }else if (updateView.getState().equals("1")){//完成
            isSendPic = false;
            progress = 0;
            ProgressHudModel.newInstance().diss();
            showToast(getString(R.string.diyDailOK));
        }else if (updateView.getState().equals("2")){//失败
            if (isSendPic){
                isSendPic = false;
                progress = 0;
                ProgressHudModel.newInstance().diss();
                showToast(getString(R.string.diyDailError));
            }
        }else {
            isSendPic = true;
            progress = 0;
            ProgressHudModel.newInstance().diss();
            iSelectDialPresenter.sendDial(pictureId,clock);
        }
    }

    @Override
    public void setView(boolean isCircle, int id,int pictureId,int clock) {
        if (isCircle){
            changeIv.setImageResource(R.mipmap.change_watch_c);
            dialIv = findViewById(R.id.dialIv_c);
        }else {
            dialIv = findViewById(R.id.dialIv_r);
        }
        this.pictureId = pictureId;
        this.clock = clock;
        dialIv.setImageResource(id);
    }

    @Override
    public void setDialView(int dialId, int pictureId, int clock) {
        if (dialId==-1){
            startActivity(new Intent(SelectDialActivity.this, DIYActivity.class));
            finish();
        }else {
            this.pictureId = pictureId;
            this.clock = clock;
            dialIv.setImageResource(dialId);
        }
    }

    @Override
    public void setDialProgress(int max) {
        ProgressHudModel.newInstance().showWithPie(this,getString(R.string.diyDailing),max,
                getString(R.string.diyDailError),100*1000);
    }
}
