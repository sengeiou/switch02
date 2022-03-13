package com.szip.jswitch.Activity.bodyFat;

import com.szip.jswitch.Model.UserInfo;

public interface IBodyFatPresenter {
    void initBle();
    void startScan(UserInfo userInfo);
    void disconnectDevice();
    void saveData();
}
