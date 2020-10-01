package com.szip.sportwatch.Model.HttpBean;

import com.szip.sportwatch.DB.dbModel.AnimalHeatData;
import com.szip.sportwatch.DB.dbModel.BloodOxygenData;
import com.szip.sportwatch.DB.dbModel.BloodPressureData;
import com.szip.sportwatch.DB.dbModel.EcgData;
import com.szip.sportwatch.DB.dbModel.HeartData;
import com.szip.sportwatch.DB.dbModel.SleepData;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.DB.dbModel.StepData;

import java.util.ArrayList;

public class DownloadDataBean extends BaseApi {

    private Data data;

    public class Data{
        ArrayList<BloodOxygenData> bloodOxygenDataList;
        ArrayList<BloodPressureData> bloodPressureDataList;
        ArrayList<EcgData> ecgDataList;
        ArrayList<HeartData> heartDataList;
        ArrayList<SleepData>  sleepDataList;
        ArrayList<SportData>  sportDataList;
        ArrayList<StepData>  stepDataList;
        ArrayList<AnimalHeatData>  tempDataList;

        public ArrayList<BloodOxygenData> getBloodOxygenData() {
            return bloodOxygenDataList;
        }

        public ArrayList<BloodPressureData> getBloodPressureDataList() {
            return bloodPressureDataList;
        }

        public ArrayList<EcgData> getEcgDataList() {
            return ecgDataList;
        }

        public ArrayList<HeartData> getHeartDataList() {
            return heartDataList;
        }

        public ArrayList<SleepData> getSleepDataList() {
            return sleepDataList;
        }

        public ArrayList<SportData> getSportDataList() {
            return sportDataList;
        }

        public ArrayList<StepData> getStepDataList() {
            return stepDataList;
        }

        public ArrayList<AnimalHeatData> getAnimalHeatDataList() {
            return tempDataList;
        }
    }

    public Data getData() {
        return data;
    }
}
