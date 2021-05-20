package com.szip.sportwatch.Activity.GpsSport;

import android.location.LocationManager;

import androidx.fragment.app.FragmentManager;

public interface IGpsPresenter {
    void startLocationService();
    void stopLocationService();
    void finishLocationService();
    void openMap(FragmentManager fragmentManager);




}
