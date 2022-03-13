package com.szip.jswitch.BLE;

import android.os.Looper;

import com.mediatek.wearableProfiles.WearableClientProfile;
import com.mediatek.wearableProfiles.WearableClientProfileManager;

public class WearableClientProfileRegister {
    public WearableClientProfileRegister() {
    }

    public static final void registerWearableClientProfile(WearableClientProfile var0, Looper var1) {
        WearableClientProfileManager.getWearableClientProfileManager().registerWearableClientProfile(var0, var1);
    }

    public static final void unRegisterWearableClientProfile(WearableClientProfile var0) {
        WearableClientProfileManager.getWearableClientProfileManager().unRegisterWearableClientProfile(var0);
    }
}
