package com.szip.sportwatch.BLE;

import com.inuker.bluetooth.library.BluetoothClient;
import com.szip.sportwatch.MyApplication;


/**
 * Created by PettySion on 2016/8/27.
 */
public class ClientManager {

    private static BluetoothClient mClient;

    public static BluetoothClient getClient() {
        if (mClient == null) {
            synchronized (ClientManager.class) {
                if (mClient == null) {
                    mClient = new BluetoothClient(MyApplication.getInstance());
                }
            }
        }
        return mClient;
    }
}
