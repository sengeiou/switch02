package com.szip.sportwatch.Contorller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.szip.sportwatch.Interface.HttpCallbackWithBase;
import com.szip.sportwatch.Model.HttpBean.BaseApi;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.MyAlerDialog;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class RegisterActivity extends BaseActivity implements View.OnClickListener,HttpCallbackWithBase{

    private boolean isPhone;

    /**
     * 国家与地区以及国家代码
     * */
    private TextView countryTv,countryCodeTv,countryTipTv;

    /**
     * 用户名
     * */
    private EditText userEt;
    private TextView userTipTv;

    /**
     * 验证码相关控件
     * */
    private EditText verifyCodeEt;
    private TextView sendTv,verifyCodeTipTv;
    private Timer timer;
    private int time;

    private Context mContext;
    private SharedPreferences sharedPreferencesp;

    private int flagForEt;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    String getCodeAgain = getString(R.string.send);
                    time--;
                    if (time <= 0){
                        timer.cancel();
                        sendTv.setEnabled(true);
                        sendTv.setText(getCodeAgain);
                        sendTv.setTextColor(getResources().getColor(R.color.rayblue));
                    }else {
                        sendTv.setText(time+"s");
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        mContext = this;
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(null);
    }

    /**
     * 初始化界面
     * */
    private void initView() {
        StatusBarCompat.translucentStatusBar(RegisterActivity.this,true);
        countryTv = findViewById(R.id.countryTv);
        countryCodeTv = findViewById(R.id.countryCodeTv);
        countryTipTv = findViewById(R.id.countryTipTv);

        userEt = findViewById(R.id.userEt);
        userTipTv = findViewById(R.id.userTipTv);

        verifyCodeEt = findViewById(R.id.verifyCodeEt);
        verifyCodeTipTv = findViewById(R.id.verifyCodeTipTv);
        sendTv = findViewById(R.id.sendTv);


    }
    /**
     * 初始化事件
     * */
    private void initEvent() {
        sendTv.setOnClickListener(this);
        findViewById(R.id.countryRl).setOnClickListener(this);
        findViewById(R.id.nextBtn).setOnClickListener(this);
        findViewById(R.id.backIv).setOnClickListener(this);
        userEt.addTextChangedListener(watcher);
        userEt.setOnFocusChangeListener(focusChangeListener);
        verifyCodeEt.addTextChangedListener(watcher);
        verifyCodeEt.setOnFocusChangeListener(focusChangeListener);
    }

    /**
     * 开始倒计时
     * */
    private void startTimer(){
        try {
            HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(this);
            if (!MathUitl.isNumeric(userEt.getText().toString()))
                HttpMessgeUtil.getInstance(mContext).getVerificationCode("2","","",
                        userEt.getText().toString());
            else
                HttpMessgeUtil.getInstance(mContext).getVerificationCode("1","00"+countryCodeTv.getText().toString().substring(1),
                        userEt.getText().toString(),"");

        } catch (IOException e) {
            e.printStackTrace();
        }
        sendTv.setTextColor(getResources().getColor(R.color.gray));
        sendTv.setEnabled(false);
        time = 60;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(100);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,1000,1000);
    }

    /**
     * 点击事件监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.countryRl:
                CityPicker.getInstance()
                        .setFragmentManager(getSupportFragmentManager())
                        .enableAnimation(true)
                        .setAnimationStyle(R.style.CustomAnim)
                        .setLocatedCity(null)
                        .setHotCities(null)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                countryTv.setText(data == null ? "" :  data.getName());
                                countryCodeTv.setText("+"+data.getCode().substring(2));
                                countryTv.setTextColor(getResources().getColor(R.color.rayblue));
                                countryCodeTv.setTextColor(getResources().getColor(R.color.rayblue));
                                countryTipTv.setTextColor(getResources().getColor(R.color.gray));
                            }
                            @Override
                            public void onLocate() {

                            }
                        })
                        .show();
                break;
            case R.id.sendTv:
                if (countryCodeTv.getText().toString().equals("")){
                    showToast(getString(R.string.choseCountry));
                }else if (userEt.getText().toString().equals("")){
                    showToast(getString(R.string.phoneOrEmail));
                }else {
                    if (!MathUitl.isNumeric(userEt.getText().toString())){
                        if (!MathUitl.isEmail(userEt.getText().toString()))
                            showToast(getString(R.string.enterRightEmail));
                        else
                            startTimer();
                    } else
                        startTimer();
                }
                break;
            case R.id.backIv:
                finish();
                break;
            case R.id.nextBtn:
                if (countryCodeTv.getText().toString().equals("")){
                    showToast(getString(R.string.choseCountry));
                }else if (userEt.getText().toString().equals("")){
                    showToast(getString(R.string.phoneOrEmail));
                } else if (verifyCodeEt.getText().toString().equals("")){
                    showToast(getString(R.string.enterVerification));
                } else{
                    MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.privacyTip), getString(R.string.confirm),
                            getString(R.string.cancel), false, new MyAlerDialog.AlerDialogOnclickListener() {
                                @Override
                                public void onDialogTouch(boolean flag) {
                                    try {
                                        if (!MathUitl.isNumeric(userEt.getText().toString())){//邮箱
                                            if (MathUitl.isEmail(userEt.getText().toString())){
                                                ProgressHudModel.newInstance().show(RegisterActivity.this,
                                                        getString(R.string.waitting),getString(R.string.httpError),10000);
                                                HttpMessgeUtil.getInstance(mContext).postCheckVerifyCode("2","","",userEt.getText().toString(),
                                                        verifyCodeEt.getText().toString());
                                                isPhone = false;
                                            }
                                            else
                                                showToast(getString(R.string.enterRightEmail));
                                        }else {//电话
                                            ProgressHudModel.newInstance().show(RegisterActivity.this,
                                                    getString(R.string.waitting),getString(R.string.httpError),10000);
                                            HttpMessgeUtil.getInstance(mContext).postCheckVerifyCode("1","00"+countryCodeTv.getText().toString().substring(1),
                                                    userEt.getText().toString(),"", verifyCodeEt.getText().toString());
                                            isPhone = true;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },this);

                }
                break;
        }
    }

    /**
     * 输入框键入监听器
     * */
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String data = s.toString();

            switch (flagForEt){
                case 0:
                    if (TextUtils.isEmpty(data)){
                        userTipTv.setTextColor(getResources().getColor(R.color.rayblue));
                    }else {
                        userTipTv.setTextColor(getResources().getColor(R.color.gray));
                    }
                    break;
                case 1:
                    if (TextUtils.isEmpty(data)){
                        verifyCodeTipTv.setTextColor(getResources().getColor(R.color.rayblue));
                    }else {
                        verifyCodeTipTv.setTextColor(getResources().getColor(R.color.gray));
                    }
                    break;

            }
        }
    };

    /**
     * 输入框焦点监听
     * */
    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.userEt:
                    if (hasFocus){
                        flagForEt = 0;
                    }
                    break;
                case R.id.verifyCodeEt:
                    if (hasFocus){
                        flagForEt = 1;
                    }
                    break;
            }
        }
    };

    /**
     * 注册网络请求回调
     * */
    @Override
    public void onCallback(BaseApi baseApi, int id) {
        ProgressHudModel.newInstance().diss();
        Intent intent = new Intent();
        intent.setClass(mContext,SetPasswordActivity.class);
        intent.putExtra("isPhone",isPhone);
        intent.putExtra("countryCode","00"+countryCodeTv.getText().toString().substring(1));
        intent.putExtra("user",userEt.getText().toString());
        intent.putExtra("verifyCode", verifyCodeEt.getText().toString());
        startActivity(intent);
    }
}
