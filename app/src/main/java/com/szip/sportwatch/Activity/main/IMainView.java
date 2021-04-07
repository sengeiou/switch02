package com.szip.sportwatch.Activity.main;

import com.szip.sportwatch.View.HostTabView;

import java.util.ArrayList;

public interface IMainView {
    void checkVersionFinish();
    void checkGPSFinish();
    void initBleFinish();
    void initHostFinish(ArrayList<HostTabView> hostTabViews);
}
