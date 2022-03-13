package com.szip.jswitch.Model;

/**
 * 绘图Model
 * Created by Administrator on 2019/12/20.
 */

public class DrawDataBean implements Comparable<DrawDataBean>{
    private int value,value1,value2;//总数据和分段数据，如果不需要分段则value2 = 0
    private long time;//时间戳

    public DrawDataBean(int value, int value1, long time) {
        this.value = value;
        this.value1 = value1;
        this.time = time;
    }

    public DrawDataBean(int value, int value1, int value2,long time) {
        this.value = value;
        this.value1 = value1;
        this.value2 = value2;
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

    @Override
    public int compareTo(DrawDataBean o) {
        return (int)(o.time-this.time);
    }
}
