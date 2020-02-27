package com.szip.sportwatch.Contorller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.szip.sportwatch.Interface.HttpCallbackWithBase;
import com.szip.sportwatch.Interface.HttpCallbackWithLogin;
import com.szip.sportwatch.Model.HttpBean.BaseApi;
import com.szip.sportwatch.Model.HttpBean.LoginBean;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;

import java.io.IOException;

import static com.szip.sportwatch.MyApplication.FILE;

public class SetPasswordActivity extends BaseActivity implements View.OnClickListener,HttpCallbackWithBase,HttpCallbackWithLogin {

    /**
     * 密码
     * */
    private EditText passwordEt,confirmPasswordEt;
    private TextView passwordTipTv,confirmPasswordTipTv;

    private boolean isPhone;
    private String user;
    private String countryCode;
    private String verifyCode;

    private Context mContext;

    private SharedPreferences sharedPreferencesp;

    private int flagForEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_set_password);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        initData(intent);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithLogin(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(null);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithLogin(null);
    }

    private void initData(Intent intent) {
        isPhone = intent.getBooleanExtra("isPhone",false);
        user = intent.getStringExtra("user");
        countryCode = intent.getStringExtra("countryCode");
        verifyCode = intent.getStringExtra("verifyCode");
    }

    private void initView() {
        StatusBarCompat.translucentStatusBar(SetPasswordActivity.this,true);
        passwordEt = findViewById(R.id.passwordEt);
        passwordTipTv = findViewById(R.id.passwordTipTv);

        confirmPasswordEt = findViewById(R.id.confirmPasswordEt);
        confirmPasswordTipTv = findViewById(R.id.confirmPasswordTipTv);
    }

    private void initEvent() {

        findViewById(R.id.registerBtn).setOnClickListener(this);
        findViewById(R.id.backIv).setOnClickListener(this);

        passwordEt.addTextChangedListener(watcher);
        passwordEt.setOnFocusChangeListener(focusChangeListener);
        confirmPasswordEt.addTextChangedListener(watcher);
        confirmPasswordEt.setOnFocusChangeListener(focusChangeListener);

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
        ((CheckBox)findViewById(R.id.lawsCb1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String psd = confirmPasswordEt.getText().toString();
                if (isChecked){
                    confirmPasswordEt.setInputType(0x90);
                }else {
                    confirmPasswordEt.setInputType(0x81);
                }
                confirmPasswordEt.setSelection(psd.length());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerBtn:
                try {
                    if (passwordEt.getText().toString().equals("")){
                        showToast(getString(R.string.enterPassword));
                    } else if (!passwordEt.getText().toString().equals(confirmPasswordEt.getText().toString())){
                        showToast(getString(R.string.passwordUnSame));
                    }else if (isPhone){
                        HttpMessgeUtil.getInstance(mContext).postRegister("1",countryCode,user,"",
                                verifyCode, passwordEt.getText().toString());
                    }else {
                        HttpMessgeUtil.getInstance(mContext).postRegister("2","","",user,
                                verifyCode, passwordEt.getText().toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.backIv:
                finish();
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
                        passwordTipTv.setTextColor(getResources().getColor(R.color.rayblue));
                    }else {
                        passwordTipTv.setTextColor(getResources().getColor(R.color.gray));
                    }
                    break;
                case 1:
                    if (TextUtils.isEmpty(data)){
                        confirmPasswordTipTv.setTextColor(getResources().getColor(R.color.rayblue));
                    }else {
                        confirmPasswordTipTv.setTextColor(getResources().getColor(R.color.gray));
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
                case R.id.passwordEt:
                    if (hasFocus){
                        flagForEt = 0;
                    }
                    break;
                case R.id.confirmPasswordEt:
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
        try {
            if (isPhone){
                HttpMessgeUtil.getInstance(mContext).postLogin("1",countryCode,user,"",
                        passwordEt.getText().toString());
            }else {
                HttpMessgeUtil.getInstance(mContext).postLogin("2","","",user,
                        passwordEt.getText().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        editor.commit();
        Intent intentmain=new Intent(mContext,SeachingActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentmain);
    }
}
