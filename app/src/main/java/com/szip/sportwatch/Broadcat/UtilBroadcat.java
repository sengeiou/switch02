package com.szip.sportwatch.Broadcat;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.mediatek.wearable.WearableManager;
import com.szip.sportwatch.Service.MainService;


public class UtilBroadcat extends BroadcastReceiver {
    private IntentFilter mIntentFilter;
    private Context context;
    public UtilBroadcat(Context context) {
        this.context = context;
    }

    public UtilBroadcat() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.d("aaa", "STATE_OFF 手机蓝牙关闭");
                    Intent bleIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    bleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(bleIntent);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.d("aaa", "STATE_TURNING_OFF 手机蓝牙正在关闭");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.d("aaa", "STATE_ON 手机蓝牙开启");
                    MainService.getInstance().stopConnect();
                    WearableManager.getInstance().scanDevice(true);
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.d("aaa", "STATE_TURNING_ON 手机蓝牙正在开启");
                    break;
            }
        }
    }

    private IntentFilter getmIntentFilter() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return mIntentFilter;
    }

    public void onRegister() {
        context.registerReceiver(this, getmIntentFilter());
    }

    public void unRegister() {
        context.unregisterReceiver(this);
    }
}
