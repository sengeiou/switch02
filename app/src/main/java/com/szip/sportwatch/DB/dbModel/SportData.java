package com.szip.sportwatch.DB.dbModel;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.szip.sportwatch.DB.AppDatabase;

import java.io.Serializable;

import androidx.annotation.NonNull;

@Table(database = AppDatabase.class)
public class SportData extends BaseModel implements Comparable<SportData> {

    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public long time;

    @Column
    public int sportTime;

    @Column
    public int distance;

    @Column
    public int calorie;

    @Column
    public int speed;

    @Column
    public int type;

    @Column
    public int heart;

    @Column
    public int stride;


    public SportData(long time, int sportTime, int distance, int calorie, int speed,int type,int heart,int stride) {
        this.time = time;
        this.sportTime = sportTime;
        this.distance = distance;
        this.calorie = calorie;
        this.speed = speed;
        this.type = type;
        this.heart = heart;
        this.stride = stride;
    }

    public SportData() {}

    @Override
    public int compareTo(@NonNull SportData o) {
        return (int)(o.time-this.time);
    }
}
