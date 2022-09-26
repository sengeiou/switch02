package com.szip.jswitch.Broadcat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.mediatek.wearable.WearableManager;
import com.szip.jswitch.BLE.BleClient;
import com.szip.jswitch.BuildConfig;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.LogUtil;

import java.util.Calendar;


public class UtilBroadcat extends BroadcastReceiver {
    private IntentFilter mIntentFilter;
    private Context context;
    public UtilBroadcat(Context context) {
        this.context = context;
    }
    private long time;
    public UtilBroadcat() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    LogUtil.getInstance().logd("aaa", "STATE_OFF 手机蓝牙关闭");
                    Intent bleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    bleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(bleIntent);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    LogUtil.getInstance().logd("aaa", "STATE_TURNING_OFF 手机蓝牙正在关闭");
                    break;
                case BluetoothAdapter.STATE_ON:
                    LogUtil.getInstance().logd("aaa", "STATE_ON 手机蓝牙开启");
                    if (MainService.getInstance()!=null)
                        MainService.getInstance().stopConnect();
                    WearableManager.getInstance().scanDevice(true);
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    LogUtil.getInstance().logd("aaa", "STATE_TURNING_ON 手机蓝牙正在开启");
                    break;
            }
        }else if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")){
            LogUtil.getInstance().logd("SZIP******","关机");
            WearableManager.getInstance().disconnect();
        }else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device.getType()==1&&MainService.getInstance()!=null&&MainService.getInstance().getState()!=3
                    &&MainService.getInstance().getState()!=2){
                WearableManager.getInstance().scanDevice(true);
            }
            LogUtil.getInstance().logd("SZIP******","蓝牙连接 type = "+device.getType()+" ;address = "+device.getAddress());
        }else if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            long priTime = Calendar.getInstance().getTimeInMillis();
            if (priTime-time>50){
                time = priTime;
                if (MainService.getInstance()!=null&&MainService.getInstance().getState()==3&&!MyApplication.getInstance().isMtk()){
                    BleClient.getInstance().writeForSendVolume();
                }

            }

        }else if (intent.getAction().equals("android.intent.action.LOCALE_CHANGED")){
            HttpMessgeUtil.getInstance().init(context);
        }
    }

    private IntentFilter getmIntentFilter() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        mIntentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
        mIntentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        return mIntentFilter;
    }

    public void onRegister() {
        context.registerReceiver(this, getmIntentFilter());
    }

    public void unRegister() {
        context.unregisterReceiver(this);
    }
}
