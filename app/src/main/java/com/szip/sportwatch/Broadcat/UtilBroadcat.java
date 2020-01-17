package com.szip.sportwatch.Broadcat;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


public class UtilBroadcat extends BroadcastReceiver {
    private IntentFilter mIntentFilter;
    private Context context;
    public UtilBroadcat(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.MEDIA_BUTTON".equals(intent.getAction())){
            Log.d("SZIP******","收到广播");
        }
    }

    private IntentFilter getmIntentFilter() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("clockIsComing");
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
