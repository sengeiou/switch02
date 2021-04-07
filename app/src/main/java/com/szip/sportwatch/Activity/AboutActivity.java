package com.szip.sportwatch.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.MyAlerDialog;

public class AboutActivity extends BaseActivity {

    private TextView versionTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_about);
        StatusBarCompat.translucentStatusBar(AboutActivity.this,true);
        setAndroidNativeLightStatusBar(this,true);
        versionTv = findViewById(R.id.versionTv);

        String ver;
        try {
            ver = getPackageManager().getPackageInfo("com.szip.sportwatch",
                    0).versionName;
            versionTv.setText("V" + ver);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.privacyRl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, PrivacyActivity.class));
            }
        });
        findViewById(R.id.feedbackRl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, FeedbackActivity.class));
            }
        });

        findViewById(R.id.userNameRl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyApplication.getInstance().isNewVersion()){
                    MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.newVersion), getString(R.string.confirm), getString(R.string.cancel),
                            false, new MyAlerDialog.AlerDialogOnclickListener() {
                                @Override
                                public void onDialogTouch(boolean flag) {
                                    if (flag){
                                        MyApplication.getInstance().setNewVersion(false);
                                        MainService.getInstance().downloadFirmsoft(MyApplication.getInstance().getVersionUrl(),"iSmarport.apk");
                                    }
                                }
                            },AboutActivity.this);
                }
            }
        });

        if(MyApplication.getInstance().isNewVersion()){
            findViewById(R.id.updateView).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.updateView).setVisibility(View.GONE);
        }
    }
}
