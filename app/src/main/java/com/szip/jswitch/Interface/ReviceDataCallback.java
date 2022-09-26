package com.szip.jswitch.Interface;

/**
 * Created by Administrator on 2019/12/28.
 */

public interface ReviceDataCallback {
    void checkVersion(boolean stepNum,boolean deltaStepNum,boolean sleepNum,boolean deltaSleepNum,
                      boolean heart,boolean bloodPressure,boolean bloodOxygen,boolean ecg,boolean animalHeat,String deviceNum,int elc);

    void getStepsForDay(String [] stepsForday);
    void getSteps(String [] steps);
    void getSleepForDay(String [] sleepForday);
    void getSleep(String [] sleep);
    void getHeart(String [] heart);
    void getBloodPressure(String [] bloodPressure);
    void getBloodOxygen(String [] bloodOxygen);
    void getAnimalHeat(String [] animalHeat);
    void getEcg(String [] ecg);
    void getSport(String [] sport);
    void findPhone(int flag);
    void startSport(String flag);
    void endSport(String flag);
    void getSportData(String distance,String speed,String calorie);
}
