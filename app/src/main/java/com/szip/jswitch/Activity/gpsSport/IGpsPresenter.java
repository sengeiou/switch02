package com.szip.jswitch.Activity.gpsSport;

import androidx.fragment.app.FragmentManager;

public interface IGpsPresenter {
    void startLocationService();
    void stopLocationService();
    void finishLocationService();
//    void openMap(FragmentManager fragmentManager);
    void setViewDestory();



}
