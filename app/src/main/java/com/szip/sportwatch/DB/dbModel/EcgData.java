package com.szip.sportwatch.DB.dbModel;

import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.szip.sportwatch.DB.AppDatabase;

/**
 * Created by Administrator on 2019/12/30.
 */
@Table(database = AppDatabase.class)
public class EcgData extends BaseModel implements Comparable<EcgData>{
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public long time;

    @Column
    public String heart;


    public long getTime() {
        return time;
    }

    public EcgData(long time, String heart) {
        this.time = time;
        this.heart = heart;
    }

    public EcgData() {
    }

    @Override
    public int compareTo(@NonNull EcgData o) {
        return (int)(this.time-o.time);
    }

}
