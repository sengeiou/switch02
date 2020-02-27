package com.szip.sportwatch.Contorller;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.StatusBarCompat;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private TextView versionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_about);
        StatusBarCompat.translucentStatusBar(AboutActivity.this,true);
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
    }
}
