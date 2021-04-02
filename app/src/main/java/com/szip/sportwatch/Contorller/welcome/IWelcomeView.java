package com.szip.sportwatch.Contorller.welcome;

public interface IWelcomeView {

    void initBleFinish();
    void initDeviceConfigFinish();
    void initUserinfoFinish(boolean isNeedLogin);
    void checkPrivacyResult(boolean comfirm);
}
