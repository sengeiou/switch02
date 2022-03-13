package com.szip.jswitch.Activity.welcome;

public interface IWelcomeView {

    void initBleFinish();
    void initDeviceConfigFinish();
    void initUserinfoFinish(boolean isNeedLogin);
    void checkPrivacyResult(boolean comfirm);
}
