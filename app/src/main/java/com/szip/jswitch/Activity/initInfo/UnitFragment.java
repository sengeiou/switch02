package com.szip.jswitch.Activity.initInfo;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.Model.HttpBean.BaseApi;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.JsonGenericsSerializator;
import com.szip.jswitch.Util.ProgressHudModel;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;

import okhttp3.Call;


public class UnitFragment extends BaseFragment implements View.OnClickListener {
    private MyApplication app;
    private RadioGroup unitRg,tempRg;

    private int unit = 0;//单位制式
    private int temp = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_unit;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();
        initView();
        initEvent();
    }

    private void initView() {
        unitRg = getView().findViewById(R.id.unitRg);
        tempRg = getView().findViewById(R.id.tempRg);
        unit = app.getUserInfo().getUnit();
        if (app.getUserInfo().getUnit()==0){
            unitRg.check(R.id.metricRb);
        }else {
            unitRg.check(R.id.britishRb);
        }

        temp = app.getUserInfo().getTempUnit();
        if (app.getUserInfo().getTempUnit()==0){
            tempRg.check(R.id.cRb);
        }else {
            tempRg.check(R.id.fRb);
        }
    }

    private void initEvent() {
        getView().findViewById(R.id.nextBtn).setOnClickListener(this);
        getView().findViewById(R.id.metricTv).setOnClickListener(this);
        getView().findViewById(R.id.britishTv).setOnClickListener(this);
        getView().findViewById(R.id.metricRb).setOnClickListener(this);
        getView().findViewById(R.id.britishRb).setOnClickListener(this);
        getView().findViewById(R.id.cTv).setOnClickListener(this);
        getView().findViewById(R.id.cRb).setOnClickListener(this);
        getView().findViewById(R.id.fTv).setOnClickListener(this);
        getView().findViewById(R.id.fRb).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.metricTv:
            case R.id.metricRb:
                unitRg.check(R.id.metricRb);
                unit = 0;
                break;
            case R.id.britishTv:
            case R.id.britishRb:
                unitRg.check(R.id.britishRb);
                unit = 1;
                break;
            case R.id.cTv:
            case R.id.cRb:
                tempRg.check(R.id.cRb);
                temp = 0;
                break;
            case R.id.fTv:
            case R.id.fRb:
                tempRg.check(R.id.fRb);
                temp = 1;
                break;
            case R.id.nextBtn:
                ProgressHudModel.newInstance().show(getActivity(),getString(R.string.waitting),getString(R.string.httpError),
                        3000);
                try {
                    HttpMessgeUtil.getInstance().postForSetUnit(unit+"",temp+"",callback);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    private GenericsCallback<BaseApi> callback = new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(BaseApi response, int id) {
            ProgressHudModel.newInstance().diss();
            if (response.getCode()==200){
                app.getUserInfo().setUnit(unit);
                app.getUserInfo().setTempUnit(temp);
                ((InitInfoActivity)getActivity()).infoPage();
            }else {
                showToast(response.getMessage());
            }
        }
    };
}
