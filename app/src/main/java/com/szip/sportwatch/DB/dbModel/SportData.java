package com.szip.sportwatch.DB.dbModel;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.szip.sportwatch.DB.AppDatabase;

import java.io.Serializable;

import androidx.annotation.NonNull;

@Table(database = AppDatabase.class)
public class SportData extends BaseModel implements Comparable<SportData>,Serializable{

    @PrimaryKey(autoincrement = true)
    public long id;

    /**
     * 1:健走  2：跑步  3：室内跑步 4：登山 5：马拉松 6：训练跑 7：8：跳绳 9：羽毛球 10：篮球 11：骑行 12：滑冰 13：健身房 14：瑜伽 15：网球 16：乒乓球 17：足球
     * 18：游泳 19：攀岩 20：划船 21:高尔夫 22:冲浪
     * */
    @Column
    public int type;

    /**
     * 运动开始的时间
     * */
    @Column
    public long time;

    /**
     * 运动时长
     * */
    @Column
    public int sportTime;

    /**
     * 里程
     * */
    @Column
    public int distance;

    /**
     * 卡路里
     * */
    @Column
    public int calorie;

    /**
     * 平均配速
     * */
    @Column
    public int speed;

    /**
     * 配速数组
     * */
    @Column
    public String speedArray;

    /**
     * 平均心率
     * */
    @Column
    public int heart;
    /**
     * 心率数组
     * */
    @Column
    public String heartArray;

    /**
     * 平均步频
     * */
    @Column
    public int stride;

    /**
     * 步频数组
     * */
    @Column
    public String strideArray;

    /**
     * 计步
     * */
    @Column
    public int step;

    /**
     * 平均海拔
     * */
    @Column
    public int altitude;

    /**
     * 海拔数组
     * */
    @Column
    public String altitudeArray;

    /**
     * 高尔夫杆数
     * */
    @Column
    public int pole;

    /**
     * 攀爬高度
     * */
    @Column
    public int height;

    /**
     * 设备号
     * */
    @Column
    public String deviceCode;

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
