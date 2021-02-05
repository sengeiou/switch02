package com.szip.sportwatch.Contorller;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.mediatek.leprofiles.LocalBluetoothLEManager;
import com.mediatek.wearable.WearableManager;
import com.szip.sportwatch.Model.HttpBean.CheckUpdateBean;
import com.szip.sportwatch.Model.UserInfo;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.FileUtil;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.JsonGenericsSerializator;
import com.szip.sportwatch.Util.LogUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.View.MyAlerDialog;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;

import static com.szip.sportwatch.MyApplication.FILE;

public class WelcomeActivity extends BaseActivity implements Runnable{

    /**
     * 延时线程
     * */
    private Thread thread;
    private int time = 2;

    private int sportWatchCode = 100;

    /**
     * 轻量级文件
     * */
    private SharedPreferences sharedPreferences;
    private boolean isFirst;
    private MyApplication app;

    private boolean isNotificationDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        app = (MyApplication)getApplicationContext();

//        Toast.makeText(this, FileUtil.getInstance().getPath(),Toast.LENGTH_SHORT).show();
        /**
         * 拿去本地缓存的数据
         * */
        if (sharedPreferences == null)
            sharedPreferences = getSharedPreferences(FILE,MODE_PRIVATE);
        isFirst = sharedPreferences.getBoolean("isFirst",true);
        app.setUserInfo(MathUitl.loadInfoData(sharedPreferences));
        app.getDeviceConfig();
        if (isFirst){
            MyAlerDialog.getSingle().showAlerDialogWithPrivacy(getString(R.string.privacy1), getString(R.string.privacyTip), null, null, false,
                    new MyAlerDialog.AlerDialogOnclickListener() {
                        @Override
                        public void onDialogTouch(boolean flag) {
                            if (flag){
                                sharedPreferences.edit().putBoolean("isFirst",false).commit();
                                /**
                                 * 获取权限·
                                 * */
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED
                                            || checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED
                                            || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                                            || checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED
                                            || checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED
                                            ||checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS,
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS,
                                                        Manifest.permission.ACCESS_FINE_LOCATION},
                                                sportWatchCode);
                                    }else {
                                        initBLE();
                                        if (!isNotificationListenerActived()) {
                                            showNotifiListnerPrompt();
                                        }else {
                                            initData();
                                        }
                                    }
                                }else {
                                    initBLE();
                                    if (!isNotificationListenerActived()) {
                                        showNotifiListnerPrompt();
                                    }else {
                                        initData();
                                    }
                                }
                            }else{
                                finish();
                            }
                        }
                    },this);
        } else {
            /**
             * 获取权限·
             * */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED
                        || checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED
                        || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                        || checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED
                        || checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED
                        ||checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            sportWatchCode);
                }else {
                    initBLE();
                    if (!isNotificationListenerActived()) {
                        showNotifiListnerPrompt();
                    }else {
                        initData();
                    }
                }
            }else {
                initBLE();
                if (!isNotificationListenerActived()) {
                    showNotifiListnerPrompt();
                }else {
                    initData();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNotificationDialog)
            initData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private boolean isNotificationListenerActived() {
        String packageName = getPackageName();
        String strListener = Settings.Secure.getString(this.getContentResolver(),
                "enabled_notification_listeners");
        return strListener != null
                && strListener
                .contains(packageName);
    }


    private void showNotifiListnerPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notificationlistener_prompt_title);
        builder.setMessage(R.string.notificationlistener_prompt_content);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initData();
                dialog.dismiss();
            }
        });
        // Go to notification listener settings
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                isNotificationDialog = true;
            }
        });
        builder.create().show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == sportWatchCode){
            int code = grantResults[0];
            int code1 = grantResults[1];
            int code2= grantResults[2];
            int code3= grantResults[3];
            int code4= grantResults[4];
            int code5= grantResults[5];
            if (code == PackageManager.PERMISSION_GRANTED&&code1 == PackageManager.PERMISSION_GRANTED
                    &&code2 == PackageManager.PERMISSION_GRANTED&&code3 == PackageManager.PERMISSION_GRANTED
                    &&code4 == PackageManager.PERMISSION_GRANTED&&code5 == PackageManager.PERMISSION_GRANTED){
                initBLE();
                if (!isNotificationListenerActived()) {
                    showNotifiListnerPrompt();
                }else {
                    initData();
                }
            }else {
                WelcomeActivity.this.finish();
            }
        }
    }

    private void initBLE() {
        LocalBluetoothLEManager.getInstance().init(this, 511);
        boolean isSuccess = WearableManager.getInstance().init(true, getApplicationContext(), "we had", R.xml.wearable_config);
        //切换成GATT模式
        if (WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP)
            WearableManager.getInstance().switchMode();
        if (!MainService.isMainServiceActive()) {
            getApplicationContext().startService(
                    new Intent(getApplicationContext(), MainService.class));
        }
        LogUtil.getInstance().logd("SZIP******","初始化蓝牙");
    }

    private void initData() {
        thread = new Thread(WelcomeActivity.this);
        thread.start();
    }


    @Override
    public void run() {
        try {
            while (time != 0){
                Thread.sleep(2000);
                time = time -1;
            }
                //TODO 此处放引导页
                if (app.getStartState() == 0){//已登录
                    Intent in = new Intent();
                    in.setClass(WelcomeActivity.this, MainActivity.class);
                    startActivity(in);
                    finish();
                }else if (app.getStartState() == 1){//未登录
                    Intent in = new Intent();
                    in.setClass(WelcomeActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                }else {//登陆过期
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(getString(R.string.tokenTimeout));
                        }
                    });
                    Intent in = new Intent();
                    in.setClass(WelcomeActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
