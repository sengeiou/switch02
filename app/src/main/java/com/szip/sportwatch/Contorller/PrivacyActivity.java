package com.szip.sportwatch.Contorller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.StatusBarCompat;

public class PrivacyActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_privacy);
        StatusBarCompat.translucentStatusBar(this,true);
        initView();

    }

    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.privacy1));
        findViewById(R.id.rightIv).setVisibility(View.GONE);
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
