package com.szip.jswitch.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.jswitch.BuildConfig;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.StatusBarCompat;
import com.szip.jswitch.View.MyAlerDialog;

public class AboutActivity extends BaseActivity {

    private TextView versionTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_about);
        StatusBarCompat.translucentStatusBar(this,true);
        setAndroidNativeLightStatusBar(this,true);
        versionTv = findViewById(R.id.versionTv);

        String ver;
        try {
            ver = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID,
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
                                        try {
                                            Uri uri = Uri.parse("market://details?id=com.szip.jswitch");
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            intent.setPackage(BuildConfig.FLAVORS);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
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
