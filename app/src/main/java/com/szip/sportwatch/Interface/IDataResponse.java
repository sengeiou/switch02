package com.szip.sportwatch.Interface;

import com.szip.sportwatch.DB.dbModel.HeartData;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.Model.BleStepModel;

import java.util.ArrayList;

/**
 * Created by Hqs on 2018/1/12
 * 设备回传上来的信息
 */
public interface IDataResponse {

    /**
     * 接收完成计步数据
     */
    void onSaveStepDatas(ArrayList<BleStepModel> datas);

    /**
     * 接收完成心率数据
     */
    void onSaveHeartDatas(ArrayList<HeartData> datas);

    /**
     * 接收完成睡眠数据
     */
    void onSaveSleepDatas(byte[] datas);

    /**
     * 接收完成跑步数据
     */
    void onSaveRunDatas(ArrayList<SportData> datas);


    /**
     * 解析完业务数据索引
     */
    void onGetDataIndex(String deviceNum, ArrayList<Integer> dataIndex);

}
