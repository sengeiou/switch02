package com.szip.sportwatch.Contorller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.mediatek.wearable.WearableManager;
import com.necer.utils.CalendarUtil;
import com.szip.sportwatch.Interface.HttpCallbackWithLogin;
import com.szip.sportwatch.Model.HttpBean.LoginBean;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;

import java.io.IOException;
import java.util.Calendar;

import static com.szip.sportwatch.MyApplication.FILE;

public class LoginActivity extends BaseActivity implements View.OnClickListener,HttpCallbackWithLogin{

    /**
     * 用户名密码
     * */
    private TextView userTipTv,passwordTipTv;
    private EditText userEt,passwordEt;
    /**
     * 国家以及国家编号
     * */
    private TextView countryTv,countryCodeTv,countryTipTv;

    private Context mContext;

    private SharedPreferences sharedPreferencesp;

    private int flagForEt;

    private int touchTimes = 0;//连续点击次数
    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        mContext = this;
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithLogin(this);//注册网络回调监听
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithLogin(null);//注销网络回调监听
    }

    /**
     * 初始化视图
     * */
    private void initView() {
        StatusBarCompat.translucentStatusBar(LoginActivity.this,true);
        userTipTv = findViewById(R.id.userTipTv);
        passwordTipTv = findViewById(R.id.passwordTipTv);
        countryCodeTv = findViewById(R.id.countryCodeTv);
        countryTipTv = findViewById(R.id.countryTipTv);
        userEt = findViewById(R.id.userEt);
        passwordEt = findViewById(R.id.passwordEt);
        countryTv = findViewById(R.id.countryTv);
    }

    /**
     * 初始化事件
     * */
    private void initEvent() {
        findViewById(R.id.loginBtn).setOnClickListener(this);
        findViewById(R.id.forgetTv).setOnClickListener(this);
        findViewById(R.id.registerTv).setOnClickListener(this);
        findViewById(R.id.countryRl).setOnClickListener(this);
        findViewById(R.id.iconIv).setOnClickListener(this);
        userEt.addTextChangedListener(watcher);
        userEt.setOnFocusChangeListener(focusChangeListener);
        passwordEt.addTextChangedListener(watcher);
        passwordEt.setOnFocusChangeListener(focusChangeListener);
        ((CheckBox)findViewById(R.id.lawsCb)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String psd = passwordEt.getText().toString();
                if (isChecked){
                    passwordEt.setInputType(0x90);
                }else {
                    passwordEt.setInputType(0x81);
                }
                passwordEt.setSelection(psd.length());
            }
        });
    }

    /**
     * 点击监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn:
                if (countryCodeTv.getText().toString().equals("")){
                    showToast(getString(R.string.choseCountry));
                }else if (userEt.getText().toString().equals("")){
                    showToast(getString(R.string.phoneOrEmail));
                }else if (passwordEt.getText().toString().equals("")){
                    showToast(getString(R.string.enterPassword));
                }else {
                    try {
                        if (userEt.getText().toString().contains("@")){//邮箱
                            if (MathUitl.isEmail(userEt.getText().toString())){//如果是邮箱登录，判断邮箱格式是否正确
                                ProgressHudModel.newInstance().show(mContext,getString(R.string.logging),getString(R.string.httpError),10000);
                                HttpMessgeUtil.getInstance(mContext).postLogin("2","","",
                                        userEt.getText().toString(),passwordEt.getText().toString());
                            }else {
                                showToast(getString(R.string.enterRightEmail));
                            }
                        }else {//电话
                            ProgressHudModel.newInstance().show(mContext,getString(R.string.logging),getString(R.string.httpError),10000);
                            HttpMessgeUtil.getInstance(LoginActivity.this).postLogin("1","00"+countryCodeTv.getText().toString().substring(1),
                                    userEt.getText().toString(), "",passwordEt.getText().toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.forgetTv:
                startActivity(new Intent(mContext, ForgetPasswordActivity.class));
                break;
            case R.id.registerTv:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
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
                        }).show();
                break;
            case R.id.iconIv:
                long secondTime = System.currentTimeMillis();
                if (firstTime==0||secondTime-firstTime>2000){
                    firstTime = System.currentTimeMillis();
                    touchTimes = 1;
                }else{
                    firstTime = System.currentTimeMillis();
                    touchTimes++;
                }

                if (touchTimes == 10){

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
                        passwordTipTv.setTextColor(getResources().getColor(R.color.rayblue));
                    }else {
                        passwordTipTv.setTextColor(getResources().getColor(R.color.gray));
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
                case R.id.passwordEt:
                    if (hasFocus){
                        flagForEt = 1;
                    }
                    break;
            }
        }
    };

    /**
     * 登录网络请求回调
     * */
    @Override
    public void onLogin(LoginBean loginBean) {
        ProgressHudModel.newInstance().diss();
        HttpMessgeUtil.getInstance(mContext).setToken(loginBean.getData().getToken());
        if (sharedPreferencesp == null)
            sharedPreferencesp = getSharedPreferences(FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesp.edit();
        editor.putString("token",loginBean.getData().getToken());
        editor.putString("phone",loginBean.getData().getUserInfo().getPhoneNumber());
        editor.putString("mail",loginBean.getData().getUserInfo().getEmail());
        ((MyApplication)getApplicationContext()).setUserInfo(loginBean.getData().getUserInfo());
        editor.putString("password",passwordEt.getText().toString());
        if (loginBean.getData().getUserInfo().getDeviceCode()== null){//如果未绑定手环，跳到绑定页面
            startActivity(new Intent(mContext, SeachingActivity.class));
        }else {

            try {
                HttpMessgeUtil.getInstance(mContext).getForDownloadReportData(Calendar.getInstance().getTimeInMillis()/1000+"",30+"");
            } catch (IOException e) {
                e.printStackTrace();
            }

            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(loginBean.getData().getUserInfo().getDeviceCode());
            WearableManager.getInstance().setRemoteDevice(device);
            MainService.getInstance().startConnect();
            startActivity(new Intent(mContext,MainActivity.class));
        }
        editor.commit();
        finish();
    }
}
