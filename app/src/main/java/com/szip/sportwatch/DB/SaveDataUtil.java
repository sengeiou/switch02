package com.szip.sportwatch.DB;

import android.content.Context;
import android.util.Log;

import com.necer.utils.CalendarUtil;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.szip.sportwatch.DB.dbModel.BloodOxygenData;
import com.szip.sportwatch.DB.dbModel.BloodOxygenData_Table;
import com.szip.sportwatch.DB.dbModel.BloodPressureData;
import com.szip.sportwatch.DB.dbModel.BloodPressureData_Table;
import com.szip.sportwatch.DB.dbModel.EcgData;
import com.szip.sportwatch.DB.dbModel.EcgData_Table;
import com.szip.sportwatch.DB.dbModel.HeartData;
import com.szip.sportwatch.DB.dbModel.HeartData_Table;
import com.szip.sportwatch.DB.dbModel.SleepData;
import com.szip.sportwatch.DB.dbModel.SleepData_Table;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.DB.dbModel.SportData_Table;
import com.szip.sportwatch.DB.dbModel.StepData;
import com.szip.sportwatch.DB.dbModel.StepData_Table;
import com.szip.sportwatch.Model.EvenBusModel.ConnectState;
import com.szip.sportwatch.Util.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * Created by Administrator on 2019/12/22.
 */

public class SaveDataUtil {
    private static SaveDataUtil saveDataUtil;
    private Context mContext;
    private SaveDataUtil(Context context){
        mContext = context;
    }

    public static SaveDataUtil newInstance(Context context){                     // 单例模式，双重锁
        if( saveDataUtil == null ){
            synchronized (SaveDataUtil.class){
                if( saveDataUtil == null ){
                    saveDataUtil = new SaveDataUtil(context);
                }
            }
        }
        return saveDataUtil ;
    }

    /**
     * 批量保存计步
     * */
    public void saveStepDataListData(List<StepData> stepDataList){
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<StepData>() {
                            @Override
                            public void processModel(StepData stepData, DatabaseWrapper wrapper) {
                                StepData sqlData = SQLite.select()
                                        .from(StepData.class)
                                        .where(StepData_Table.time.is(stepData.time))
                                        .querySingle();
                                if (sqlData == null) {//为null则代表数据库没有保存
                                    CalendarUtil.addPoint(new LocalDate(DateUtil.getStringDateFromSecond(stepData.time,
                                            "yyyy-MM-dd")),0);
                                    stepData.save();
                                }
                                else {//不为null则代表数据库存在，进行更新
                                   sqlData.calorie = stepData.calorie;
                                   sqlData.distance = stepData.distance;
                                   sqlData.steps = stepData.steps;
                                   sqlData.update();
                                }
                            }
                        }).addAll(stepDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                }).success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        Log.d("SZIP******","计步数据保存成功");
                        EventBus.getDefault().post(new ConnectState());
                    }
                }).build().execute();
    }

    /**
     * 批量保存详情计步
     * */
    public void saveStepInfoDataListData(List<StepData> stepDataList){
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<StepData>() {
                            @Override
                            public void processModel(StepData stepData, DatabaseWrapper wrapper) {
                                StepData sqlData = SQLite.select()
                                        .from(StepData.class)
                                        .where(StepData_Table.time.is(stepData.time))
                                        .querySingle();
                                if (sqlData == null){//为null则代表数据库没有保存
                                    CalendarUtil.addPoint(new LocalDate(DateUtil.getStringDateFromSecond(stepData.time,
                                            "yyyy-MM-dd")),0);
                                    stepData.save();
                                }
                                else {//不为null则代表数据库存在，进行更新
                                    if (sqlData.dataForHour != null&&
                                            !sqlData.dataForHour.equals(stepData.dataForHour)){
                                        int sql[] = new int[24];
                                        String[] sqlStr = sqlData.dataForHour.split(",");
                                        Log.d("SZIP******","old stepStr = "+sqlData.dataForHour);
                                        int step[] = new int[24];
                                        Log.d("SZIP******","new stepStr = "+stepData.dataForHour);
                                        String[] stepStr = stepData.dataForHour.split(",");
                                        for (int i = 0;i<sqlStr.length;i++){
                                            sql[Integer.valueOf(sqlStr[i].substring(0,2))] = Integer.valueOf(sqlStr[i].substring(3));
                                        }
                                        for (int i = 0;i<stepStr.length;i++){
                                            step[Integer.valueOf(stepStr[i].substring(0,2))] = Integer.valueOf(stepStr[i].substring(3));
                                        }
                                        StringBuffer stepString = new StringBuffer();
                                        for (int i = 0;i<24;i++){
                                            if (sql[i]+step[i]!=0){
                                                stepString.append(String.format(",%02d:%d",i,sql[i]+step[i]));
                                            }
                                        }
                                        sqlData.dataForHour = stepString.toString().substring(1);
                                    }else
                                        sqlData.dataForHour = stepData.dataForHour;
                                    sqlData.update();
                                }
                            }
                        }).addAll(stepDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.d("SZIP******","计步详情数据保存成功");
                EventBus.getDefault().post(new ConnectState());
            }
        }).build().execute();
    }

    /**
     * 批量保存睡眠
     * */
    public void saveSleepDataListData(List<SleepData> sleepDataList){
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<SleepData>() {
                            @Override
                            public void processModel(SleepData sleepData, DatabaseWrapper wrapper) {
                                SleepData sqlData = SQLite.select()
                                        .from(SleepData.class)
                                        .where(SleepData_Table.time.is(sleepData.time))
                                        .querySingle();
                                if (sqlData == null){//为null则代表数据库没有保存
                                    CalendarUtil.addPoint(new LocalDate(DateUtil.getStringDateFromSecond(sleepData.time,
                                            "yyyy-MM-dd")),1);
                                    sleepData.save();
                                } else {//不为null则代表数据库存在，进行更新
                                    sqlData.deepTime = sleepData.deepTime;
                                    sqlData.lightTime = sleepData.lightTime;
                                    sqlData.update();
                                }
                            }
                        }).addAll(sleepDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.d("SZIP******","睡眠数据保存成功");
            }
        }).build().execute();
    }

    /**
     * 批量保存详情睡眠
     * */
    public void saveSleepInfoDataListData(List<SleepData> sleepDataList){
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<SleepData>() {
                            @Override
                            public void processModel(SleepData sleepData, DatabaseWrapper wrapper) {
                                SleepData sqlData = SQLite.select()
                                        .from(SleepData.class)
                                        .where(SleepData_Table.time.is(sleepData.time))
                                        .querySingle();
                                if (sqlData == null){//为null则代表数据库没有保存
                                    CalendarUtil.addPoint(new LocalDate(DateUtil.getStringDateFromSecond(sleepData.time,
                                            "yyyy-MM-dd")),1);
                                    sleepData.save();

                                } else {//不为null则代表数据库存在，进行更新
                                    sqlData.dataForHour = sleepData.dataForHour;
                                    sqlData.update();
                                }
                            }
                        }).addAll(sleepDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.d("SZIP******","睡眠详情保存成功");
                EventBus.getDefault().post(new ConnectState());
            }
        }).build().execute();
    }


    /**
     * 批量保存心率
     * @param isAdd   判断该条数据是当天需要往上累加的数据还是服务费返回的需要替代的数据
     * */
    public void saveHeartDataListData(List<HeartData> heartDataList, final boolean isAdd){
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<HeartData>() {
                            @Override
                            public void processModel(HeartData heartData, DatabaseWrapper wrapper) {
                                HeartData sqlData = SQLite.select()
                                        .from(HeartData.class)
                                        .where(HeartData_Table.time.is(heartData.time))
                                        .querySingle();
                                if (sqlData == null){//为null则代表数据库没有保存
                                    CalendarUtil.addPoint(new LocalDate(DateUtil.getStringDateFromSecond(heartData.time,
                                            "yyyy-MM-dd")),2);
                                    heartData.save();
                                } else {//不为null则代表数据库存在，进行更新
                                    if (isAdd){
                                        String heartStr = sqlData.heartArray+","+heartData.heartArray;
                                        String []heartArray = heartStr.split(",");
                                        int heartSum = 0;
                                        int sum = 0;
                                        StringBuffer heartBuffer = new StringBuffer();
                                        for (int i = 0;i<heartArray.length;i++){
                                            if (!heartArray[i].equals("0")){
                                                heartSum+=Integer.valueOf(heartArray[i]);
                                                sum++;
                                                heartBuffer.append(","+heartArray[i]);
                                            }
                                        }
                                        sqlData.averageHeart = heartSum/sum;
                                        sqlData.heartArray = heartBuffer.toString().substring(1);
                                        sqlData.update();
                                    }else {
                                        sqlData.averageHeart = heartData.averageHeart;
                                        sqlData.heartArray = heartData.heartArray;
                                        sqlData.update();
                                    }

                                }
                            }
                        }).addAll(heartDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.d("SZIP******","心率数据保存成功");
                EventBus.getDefault().post(new ConnectState());
            }
        }).build().execute();
    }


    /**
     * 批量保存血压
     * */
    public void saveBloodPressureDataListData(List<BloodPressureData> bloodPressureDataList){
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<BloodPressureData>() {
                            @Override
                            public void processModel(BloodPressureData bloodPressureData, DatabaseWrapper wrapper) {
                                BloodPressureData sqlData = SQLite.select()
                                        .from(BloodPressureData.class)
                                        .where(BloodPressureData_Table.time.is(bloodPressureData.time))
                                        .querySingle();
                                if (sqlData == null){//为null则代表数据库没有保存
                                    CalendarUtil.addPoint(new LocalDate(DateUtil.getStringDateFromSecond(bloodPressureData.time,
                                            "yyyy-MM-dd")),3);
                                    bloodPressureData.save();
                                }
                            }
                        }).addAll(bloodPressureDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.d("SZIP******","血压数据保存成功");
                EventBus.getDefault().post(new ConnectState());
            }
        }).build().execute();
    }

    /**
     * 批量保存血氧
     * */
    public void saveBloodOxygenDataListData(List<BloodOxygenData> bloodOxygenDataList){
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<BloodOxygenData>() {
                            @Override
                            public void processModel(BloodOxygenData bloodOxygenData, DatabaseWrapper wrapper) {
                                BloodOxygenData sqlData = SQLite.select()
                                        .from(BloodOxygenData.class)
                                        .where(BloodOxygenData_Table.time.is(bloodOxygenData.time))
                                        .querySingle();
                                if (sqlData == null){//为null则代表数据库没有保存
                                    CalendarUtil.addPoint(new LocalDate(DateUtil.getStringDateFromSecond(bloodOxygenData.time,
                                            "yyyy-MM-dd")),4);
                                    bloodOxygenData.save();
                                }
                            }
                        }).addAll(bloodOxygenDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.d("SZIP******","血氧数据保存成功");
                EventBus.getDefault().post(new ConnectState());
            }
        }).build().execute();
    }

    /**
     * 批量保存ecg
     * */
    public void saveEcgDataListData(List<EcgData> ecgDataList){
        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<EcgData>() {
                            @Override
                            public void processModel(EcgData ecgData, DatabaseWrapper wrapper) {
                                EcgData sqlData = SQLite.select()
                                        .from(EcgData.class)
                                        .where(EcgData_Table.time.is(ecgData.time))
                                        .querySingle();
                                if (sqlData == null){//为null则代表数据库没有保存
                                    CalendarUtil.addPoint(new LocalDate(DateUtil.getStringDateFromSecond(ecgData.time,
                                            "yyyy-MM-dd")),5);
                                    ecgData.save();
                                }
                            }
                        }).addAll(ecgDataList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.d("SZIP******","ECG数据保存成功");
                EventBus.getDefault().post(new ConnectState());
            }
        }).build().execute();
    }

    /**
     * 保存sport
     * */
    public void saveSportData(SportData sportData){
        SportData sqlData = SQLite.select()
                .from(SportData.class)
                .where(SportData_Table.time.is(sportData.time))
                .querySingle();
        if (sqlData == null){//为null则代表数据库没有保存
            CalendarUtil.addPoint(new LocalDate(DateUtil.getStringDateFromSecond(sportData.time,
                    "yyyy-MM-dd")),6);
            sportData.save();
            Log.d("SZIP******","sport数据保存成功 time = "+sportData.time+" ;distance = "+sportData.distance+" ;caloria = "+sportData.calorie+
                    " ;speed = "+sportData.speed+" ;sportTime = "+sportData.sportTime+" type = "+sportData.type);
        }
    }
    /**
     * 清除数据库
     * */
    public void clearDB(){
        SQLite.delete()
                .from(BloodOxygenData.class)
                .execute();
        SQLite.delete()
                .from(BloodPressureData.class)
                .execute();
        SQLite.delete()
                .from(EcgData.class)
                .execute();
        SQLite.delete()
                .from(HeartData.class)
                .execute();
        SQLite.delete()
                .from(SleepData.class)
                .execute();
        SQLite.delete()
                .from(StepData.class)
                .execute();
        SQLite.delete()
                .from(SportData.class)
                .execute();
    }
}
