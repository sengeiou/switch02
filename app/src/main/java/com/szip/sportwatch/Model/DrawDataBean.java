package com.szip.sportwatch.Model;

/**
 * 绘图Model
 * Created by Administrator on 2019/12/20.
 */

public class DrawDataBean {
    private int value,value1,value2;//总数据和分段数据，如果不需要分段则value2 = 0
    private long time;//时间戳

    public DrawDataBean(int value, int value2, long time) {
        this.value = value;
        this.value1 = value2;
        this.time = time;
    }

    public DrawDataBean(int value, int value2, int value3,long time) {
        this.value = value;
        this.value1 = value2;
        this.value2 = value3;
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public int getValue1() {
        return value1;
    }

    public int getValue2() {
        return value2;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
