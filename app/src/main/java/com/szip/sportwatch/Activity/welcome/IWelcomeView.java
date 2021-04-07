package com.szip.sportwatch.Activity.welcome;

public interface IWelcomeView {

    void initBleFinish();
    void initDeviceConfigFinish();
    void initUserinfoFinish(boolean isNeedLogin);
    void checkPrivacyResult(boolean comfirm);
}
