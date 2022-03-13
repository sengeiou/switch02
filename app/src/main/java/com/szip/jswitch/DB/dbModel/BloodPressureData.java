package com.szip.jswitch.DB.dbModel;

import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.szip.jswitch.DB.AppDatabase;

/**
 * Created by Administrator on 2019/12/28.
 */

@Table(database = AppDatabase.class)
public class BloodPressureData extends BaseModel implements Comparable<BloodPressureData>{

    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public long time;

    @Column
    public int sbpDate;

    @Column
    public int dbpDate;

    @Column
    public String deviceCode;

    public long getTime() {
        return time;
    }

    public BloodPressureData(long time, int sbpDate, int dbpDate) {
        this.time = time;
        this.sbpDate = sbpDate;
        this.dbpDate = dbpDate;
    }

    public BloodPressureData() {
    }

    @Override
    public int compareTo(@NonNull BloodPressureData o) {
        return (int)(this.time-o.time);
    }

}
