package com.szip.jswitch.Activity.bodyFat;

public interface IBodyFatView {
    void initBleFinish(boolean bleEnable);
    void updateView();
    void updateState(String state);
    void showTipDialog(float weight,int type);
}
