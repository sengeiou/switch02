package com.szip.jswitch.Activity.gpsSport;

import com.szip.jswitch.DB.dbModel.SportData;

public interface IGpsView {
    void startCountDown();
    void startRun();
    void stopRun();
    void saveRun(final SportData sportData);
    void upDateTime(int time);
    void upDateRunData(int speed,float distance,float calorie,float acc);
}
