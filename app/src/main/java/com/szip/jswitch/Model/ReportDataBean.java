package com.szip.jswitch.Model;

import com.szip.jswitch.DB.LoadDataUtil;
import com.szip.jswitch.Util.MathUitl;

import java.util.ArrayList;

/**
 * 报告model：绘图数据，平均值，总值，最大值，最小值，距离，卡路里
 * Created by Administrator on 2019/12/22.
 */

public class ReportDataBean {
    private ArrayList<DrawDataBean> drawDataBeans;
    private int value;//总值，平均心率，收缩压，平均血氧
    private int value1;//距离，平均数，浅睡，最大值，舒张压，达标率
    private int value2;//卡路里，深睡，最小值

    public ArrayList<DrawDataBean> getDrawDataBeans() {
        return drawDataBeans;
    }

    public void setDrawDataBeans(ArrayList<DrawDataBean> drawDataBeans) {
        this.drawDataBeans = drawDataBeans;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

}
