package com.szip.jswitch.DB.dbModel;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.szip.jswitch.DB.AppDatabase;

@Table(database = AppDatabase.class)
public class HealthyConfig extends BaseModel {

    @PrimaryKey(autoincrement = true)
    public long id;
    /**
     * 标识符
     */
    @Column
    public int identifier;

    /**
     * 是否支持心率 1支持 0不支持
     */
    @Column
    public byte heartRate;

    /**
     * 是否支持心电 1支持 0不支持
     */
    @Column
    public byte ecg;

    /**
     * 是否支持血氧 1支持 0不支持
     */
    @Column
    public byte bloodOxygen;

    /**
     * 是否支持血压 1支持 0不支持
     */
    @Column
    public byte bloodPressure;

    /**
     * 是否支持计步 1支持 0不支持
     */
    @Column
    public byte stepCounter;

    /**
     * 是否支持体温 1支持 0不支持
     */
    @Column
    public byte temperature;

    /**
     * 是否支持睡眠 1支持 0不支持
     */
    @Column
    public byte sleep;

}
