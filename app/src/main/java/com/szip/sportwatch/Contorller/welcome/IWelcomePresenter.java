package com.szip.sportwatch.Contorller.welcome;

import android.content.Context;

public interface IWelcomePresenter {
    void checkPrivacy(Context context);
    void initBle(Context context);
    void initDeviceConfig();
    void initUserInfo(Context context);
}
