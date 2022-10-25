package com.szip.jswitch.Activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.szip.jswitch.BuildConfig;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.StatusBarCompat;

public class PrivacyActivity extends BaseActivity {

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_privacy);
        StatusBarCompat.translucentStatusBar(this,true);
        setAndroidNativeLightStatusBar(this,true);
        initView();

    }

    private void initView() {
        setTitleText(getString(R.string.privacy1));
        findViewById(R.id.rightIv).setVisibility(View.GONE);
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = findViewById(R.id.webview);
        if(getResources().getConfiguration().locale.getLanguage().equals("zh")){
            if (BuildConfig.APP_NAME.equals("mycandy")){
                webView.loadUrl("https://cloud.znsdkj.com:8443/file/contract/mycandy/statement.html");
            }else if (BuildConfig.APP_NAME.equals("switch essentials")){
                webView.loadUrl("https://cloud.znsdkj.com:8443/file/contract/Switch%20essentials/statement-zh.html");
            }


        } else{
            if (BuildConfig.APP_NAME.equals("mycandy")){
                webView.loadUrl("https://cloud.znsdkj.com:8443/file/contract/mycandy/statement-en.html");
            }else if (BuildConfig.APP_NAME.equals("switch essentials")){
                webView.loadUrl("https://cloud.znsdkj.com:8443/file/contract/Switch%20essentials/statement-en.html");
            }

        }
        webView.getSettings().setJavaScriptEnabled(true);
    }
}
