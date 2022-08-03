package com.szip.jswitch.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.szip.jswitch.Adapter.NotificationAdapter;
import com.szip.jswitch.DB.LoadDataUtil;
import com.szip.jswitch.DB.dbModel.NotificationData;
import com.szip.jswitch.Interface.OnSmsStateListener;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.LogUtil;
import com.szip.jswitch.Util.StatusBarCompat;

import java.util.List;

public class NotificationActivity extends BaseActivity {

    private ListView switchList;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_notification);
        StatusBarCompat.translucentStatusBar(NotificationActivity.this,true);
        setAndroidNativeLightStatusBar(this,true);
        initView();
        initData();
    }

    private void initData() {
        List<NotificationData> list = LoadDataUtil.newInstance().getNotificationList();
        notificationAdapter.setNotificationDatas(list);
    }

    private void initView() {
        setTitleText(getString(R.string.notification));
        switchList = findViewById(R.id.switchList);
        notificationAdapter = new NotificationAdapter(getApplicationContext(),onSmsStateListener);
        switchList.setAdapter(notificationAdapter);

        findViewById(R.id.rightIv).setVisibility(View.GONE);
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private OnSmsStateListener onSmsStateListener = new OnSmsStateListener() {
        @Override
        public void onSmsStateChange(boolean check) {
            if (check) {
                LogUtil.getInstance().logd("DATA******","进入回调");
                checkPermission();
            } else {
                MainService.getInstance().stopSmsService();
            }
        }
    };

    private void checkPermission() {
        /**
         * 获取权限·
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED){
                LogUtil.getInstance().logd("DATA******","申请权限");
                requestPermissions(new String[]{Manifest.permission.READ_SMS},
                        100);
            }else {
                LogUtil.getInstance().logd("DATA******","申请已经打开");
                MainService.getInstance().startSmsService();
            }
        }else {
            MainService.getInstance().startSmsService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            int code = grantResults[0];
            if (code == PackageManager.PERMISSION_GRANTED){
                MainService.getInstance().startSmsService();
            }else {
                showToast(getString(R.string.permissionErrorForSMS));
                notificationAdapter.setSmsError();
            }
        }
    }
}