package com.szip.sportwatch.Contorller;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.szip.sportwatch.Interface.HttpCallbackWithBase;
import com.szip.sportwatch.Model.HttpBean.BaseApi;
import com.szip.sportwatch.Model.UserInfo;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.BLE.EXCDController;

import java.io.IOException;

public class UnitSelectActivity extends BaseActivity implements View.OnClickListener,HttpCallbackWithBase{

    private MyApplication app;
    private RadioGroup unitRg;

    private String unit;//单位制式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_unit_select);
        app = (MyApplication) getApplicationContext();
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithBase(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithBase(null);
    }

    private void initView() {
        StatusBarCompat.translucentStatusBar(UnitSelectActivity.this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.unit));
        unitRg = findViewById(R.id.unitRg);
        if (app.getUserInfo().getUnit().equals("metric")){
            unit = "metric";
            unitRg.check(R.id.metricRb);
        }else {
            unit = "british";
            unitRg.check(R.id.britishRb);
        }


    }

    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(this);
        findViewById(R.id.rightIv).setOnClickListener(this);
        findViewById(R.id.metricTv).setOnClickListener(this);
        findViewById(R.id.britishTv).setOnClickListener(this);
        findViewById(R.id.metricRb).setOnClickListener(this);
        findViewById(R.id.britishRb).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                finish();
                break;
            case R.id.metricTv:
            case R.id.metricRb:
                unitRg.check(R.id.metricRb);
                unit = "metric";
                break;
            case R.id.britishTv:
            case R.id.britishRb:
                unitRg.check(R.id.britishRb);
                unit = "british";
                break;
            case R.id.rightIv:
                Log.d("SZIP******","UNIT = "+unit+" ;unit = "+app.getUserInfo().getUnit());
                if (unit.equals(app.getUserInfo().getUnit())){
                    showToast(getString(R.string.saved));
                    finish();
                }else {//如果制式发生变化，则清空原来的数据
                    ProgressHudModel.newInstance().show(UnitSelectActivity.this,getString(R.string.waitting),getString(R.string.httpError),
                            3000);
                    UserInfo info = app.getUserInfo();
                    try {
                        HttpMessgeUtil.getInstance(this).postForSetUnit(unit);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    @Override
    public void onCallback(BaseApi baseApi, int id) {
        ProgressHudModel.newInstance().diss();
        showToast(getString(R.string.saved));
        app.getUserInfo().setUnit(unit);
        app.getUserInfo().setHeight("");
        app.getUserInfo().setWeight("");
        MathUitl.saveInfoData(UnitSelectActivity.this,app.getUserInfo()).commit();
        if (MainService.getInstance().getConnectState()!=3){
            showToast(getString(R.string.syceError));
        }else {
            EXCDController.getInstance().writeForSetUnit(app.getUserInfo());
        }
        finish();
    }
}
