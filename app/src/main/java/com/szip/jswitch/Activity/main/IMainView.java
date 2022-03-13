package com.szip.jswitch.Activity.main;

import android.location.Location;

import com.szip.jswitch.View.HostTabView;

import java.util.ArrayList;

public interface IMainView {
    void checkVersionFinish();
    void checkGPSFinish();
    void initBleFinish();
    void initHostFinish(ArrayList<HostTabView> hostTabViews);
}
