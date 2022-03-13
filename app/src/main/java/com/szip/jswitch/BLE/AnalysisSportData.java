package com.szip.jswitch.BLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;


import com.szip.jswitch.Util.CommandUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kb on 2018/1/4.
 * 这个类用于解析蓝牙上传的运动数据
 */

public class AnalysisSportData {
    //通知APP更新UI延长时间（毫秒）
    private static final int MESSAGE_DELAYED_TIME = 1500;
    //最大的运动时间，手表上是16个小时
    private static final long MAX_SPORT_TIME = 17 * 60 * 60;

    /**
     * @param mContext
     * @param mHandler
     * @param mHandlerFlag
     * @param pkg_type      要解析的运动数据的类型
     * @param pkg_timeStamp 运动数据的开始时间
     * @param pkg_data      运动数据byte[]
     * @param mEditor
     */
    public static void analysisSportData(Context mContext, Handler mHandler, int mHandlerFlag, int pkg_type, int pkg_timeStamp, byte[] pkg_data, SharedPreferences.Editor mEditor) {
//        if (pkg_type == CommandUtil.SYNC_TYPE_STEP) {
//            if (pkg_data.length < 12) {
//                return;
//            }
//            int steps = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xFF) << 8) + ((pkg_data[2] & 0xff) << 16) + ((pkg_data[3] & 0xFF) << 24);
//            int dist = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xFF) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xFF) << 24);
//            int cal = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xFF) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xFF) << 24);
//            ClientSubmit.PedometerInfo info = new ClientSubmit.PedometerInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setBs(steps);
//            info.setLc(dist);
//            info.setKcal(cal);
//            info.setLcs(0);
//            Log.e("TAG", "time=" + (long2String(pkg_timeStamp * 1000L)) + " steps=" + steps);
//            PedometerBLL.getInstance().updatePedometer(info);
//
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_HEART) {    // heart-rate
//            if (pkg_data.length < 4) {
//                return;
//            }
//            int rate = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xFF) << 8) + ((pkg_data[2] & 0xff) << 16) + ((pkg_data[3] & 0xFF) << 24);
//            ClientSubmit.HeartInfo info = new ClientSubmit.HeartInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            rate = ((rate >= 200) ? 140 : rate);
//            info.setXl(rate);
//            Log.e("TAG", "GET heart rate=" + rate);
//            HeartBLL.getInstance().insert(info);
//
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_SLEEP) {
//            if (pkg_data.length < 12) {
//                return;
//            }
//            int endTime = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xFF) << 8) + ((pkg_data[2] & 0xff) << 16) + ((pkg_data[3] & 0xFF) << 24);
//            int sleepTime = (pkg_data[4] & 0xFF) + ((pkg_data[5] & 0xFF) << 8);
//            int deepTime = (pkg_data[6] & 0xFF) + ((pkg_data[7] & 0xFF) << 8);
//            int quality = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xFF) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xFF) << 24);
//
//            ClientSubmit.SleepInfo info = new ClientSubmit.SleepInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setSleep_quality(String.valueOf(quality));
//            info.setDeep_sleep_time(deepTime);
//            info.setSleep_time(sleepTime);
//            info.setEnd_time(endTime);
//            Log.e("TAG", "GET sleepTime=" + sleepTime);
//            SleepBLL.getInstance().insert(info);
//
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_ONFOOT) {
//            if (pkg_data.length < 30) {
//                return;
//            }
//            int mileage = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8) + ((pkg_data[2] & 0xff) << 16) + ((pkg_data[3] & 0xff) << 24);//里程（米）
//            int duration = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//用时（秒）
//            int step = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);
//            int kcal = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//卡路里（卡）
//            int height = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8) + ((pkg_data[18] & 0xff) << 16) + ((pkg_data[19] & 0xff) << 24);//爬升高度（米）
//            int startLo = (pkg_data[20] & 0xff) + ((pkg_data[21] & 0xff) << 8) + ((pkg_data[22] & 0xff) << 16) + ((pkg_data[23] & 0xff) << 24);//经度(精度 0.00001度)
//            int startLa = (pkg_data[24] & 0xff) + ((pkg_data[25] & 0xff) << 8) + ((pkg_data[26] & 0xff) << 16) + ((pkg_data[27] & 0xff) << 24);//纬度(精度 0.00001度)
//            int laLength = (pkg_data[28] & 0xff) + ((pkg_data[29] & 0xff) << 8);
//
//            byte[] loArray = new byte[laLength];
//            byte[] laArray = new byte[laLength];
//
//            if (!checkArraycopy(pkg_data, 30, loArray, 0, laLength)) {
//                return;
//            }
//            int loIndex = 30 + laLength + 2;
//            if (!checkArraycopy(pkg_data, loIndex, laArray, 0, laLength)) {
//                return;
//            }
//
//            StringBuilder lolaSb = CommandUtil.getLaLoSB(laArray, loArray, startLa, startLo, laLength);
//
//            //速度（蓝牙上传的是单位是0.1km/h）
//            int speedIndex = loIndex + laLength;
//            if ((speedIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int speedLength = (pkg_data[speedIndex] & 0xff) + ((pkg_data[speedIndex + 1] & 0xff) << 8);
//            byte[] speedByte = new byte[speedLength];
//            speedIndex += 2;
//
//            if (!checkArraycopy(pkg_data, speedIndex, speedByte, 0, speedLength)) {
//                return;
//            }
//
//            StringBuilder speedSb = CommandUtil.getSpeedStringBuilder(speedByte, speedLength);
//
//            //海拔
//            int altitudeIndex = speedIndex + speedLength;
//            if ((altitudeIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int altitudeLength = (pkg_data[altitudeIndex] & 0xff) + ((pkg_data[altitudeIndex + 1] & 0xff) << 8);
//            byte[] altitudeByte = new byte[2 * altitudeLength];
//            altitudeIndex += 2;
//
//            if (!checkArraycopy(pkg_data, altitudeIndex, altitudeByte, 0, altitudeLength * 2)) {
//                return;
//            }
//
//            StringBuilder altitudeSB = CommandUtil.getAltitudeStringBuilder(altitudeByte, altitudeLength);
//
//            //配速
//            int paceIndex = altitudeIndex + 2 * altitudeLength;
//            if ((paceIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int paceLength = (pkg_data[paceIndex] & 0xff) + ((pkg_data[paceIndex + 1] & 0xff) << 8);
//            byte[] paceByte = new byte[2 * paceLength];
//            paceIndex += 2;
//
//            if (!checkArraycopy(pkg_data, paceIndex, paceByte, 0, paceLength * 2)) {
//                return;
//            }
//
//            StringBuilder paceSB = CommandUtil.getPaceStringBuilder(paceByte, paceLength);
//
//            //心率
//            int heartIndex = paceIndex + 2 * paceLength;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            byte[] heartByte = new byte[heartLength];
//            heartIndex += 2;
//
//            if (!checkArraycopy(pkg_data, heartIndex, heartByte, 0, heartLength)) {
//                return;
//            }
//
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartByte, heartLength);
//
//            float[] speedValues = Utils.getAverageMaxMinValuesFloat(speedSb.toString(), 3);
//            int[] altitudeValues = Utils.getAverageAndMaxValue(altitudeSB.toString());
//            float mSd_avg = 0.0f;
//            if (duration > 0) {
//                mSd_avg = Utils.getFloatScale(3, ((mileage * 0.001f) / (duration * 1.0f / 3600.0f)));
//            }
//
//            //如果数据解析错了，比如耗时，里程小于0，那肯定是数据解析错了
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (mileage < 0 || duration <= 0 || duration > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.OnFootDetailsInfo info = new ClientSubmit.OnFootDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setLc(mileage);
//            info.setHs(duration);
//            info.setBs(step);
//            info.setKcal(kcal);
//            info.setGd(height);
//            info.setSd_avg(mSd_avg);
//            info.setSd_max(speedValues[1]);
//            info.setHb_avg(altitudeValues[0]);
//            info.setHb_max(altitudeValues[1]);
//            info.setGps_jh(lolaSb.toString());
//            info.setSpeed_jh(speedSb.toString());
//            info.setHbz_jh(altitudeSB.toString());
//            info.setPsz_jh(paceSB.toString());
//            info.setXlz_jh(heartSB.toString());
//
//            Log.e("TAG", "GET onfoot mileage=" + mileage);
//            OnFootBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_RUN) {
//            if (pkg_data.length < 34) {
//                return;
//            }
//            int runMode = pkg_data[0] & 0xff;//(0: 普通模式； 1：训练模式)
//            int complete = pkg_data[1] & 0xff;//完成度（0~100）
//            int goalMileage = (pkg_data[2] & 0xff) + ((pkg_data[3] & 0xff) << 8);//目标训练里程(公里数)
//            int goalDuration = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//目标训练时间（秒）
//            int realMileage = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//实际里程(米)
//            int realDuration = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//实际耗时（秒）
//            int kcal = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8) + ((pkg_data[18] & 0xff) << 16) + ((pkg_data[19] & 0xff) << 24);//卡路里（卡）
//            int climbHeight = (pkg_data[20] & 0xff) + ((pkg_data[21] & 0xff) << 8) + ((pkg_data[22] & 0xff) << 16) + ((pkg_data[23] & 0xff) << 24);//爬升高度（米）
//
//            int startLo = (pkg_data[24] & 0xff) + ((pkg_data[25] & 0xff) << 8) + ((pkg_data[26] & 0xff) << 16) + ((pkg_data[27] & 0xff) << 24);//起始经度	(精度 0.00001度)
//            int startLa = (pkg_data[28] & 0xff) + ((pkg_data[29] & 0xff) << 8) + ((pkg_data[30] & 0xff) << 16) + ((pkg_data[31] & 0xff) << 24);//起始纬度	(精度 0.00001度)
//
//            //获得精度数组
//            int loLength = (pkg_data[32] & 0xff) + ((pkg_data[33] & 0xff) << 8);
//            byte[] loArray = new byte[loLength];
//            byte[] laArray = new byte[loLength];
//            if (!checkArraycopy(pkg_data, 34, loArray, 0, loLength)) {
//                return;
//            }
//
//            int laIndex = 34 + loLength + 2;//加上2个比特纬度长度
//            if (!checkArraycopy(pkg_data, laIndex, laArray, 0, loLength)) {
//                return;
//            }
//
//            StringBuilder lolaSb = CommandUtil.getLaLoSB(laArray, loArray, startLa, startLo, loLength);
//
//            //速度
//            int speedIndex = laIndex + loLength;
//            if ((speedIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int speedLength = (pkg_data[speedIndex] & 0xff) + ((pkg_data[speedIndex + 1] & 0xff) << 8);
//            byte[] speedArr = new byte[speedLength];
//            speedIndex += 2;
//            if (!checkArraycopy(pkg_data, speedIndex, speedArr, 0, speedLength)) {
//                return;
//            }
//
//            StringBuilder speedSb = CommandUtil.getSpeedStringBuilder(speedArr, speedLength);
//
//            //海拔
//            int altitudeIndex = speedIndex + speedLength;
//            if ((altitudeIndex + 1) > pkg_data.length) {
//                return;
//            }
//            int altitudeLength = (pkg_data[altitudeIndex] & 0xff) + ((pkg_data[altitudeIndex + 1] & 0xff) << 8);
//            altitudeIndex += 2;
//            byte[] altitudeArr = new byte[altitudeLength * 2];
//            if (!checkArraycopy(pkg_data, altitudeIndex, altitudeArr, 0, altitudeLength * 2)) {
//                return;
//            }
//            StringBuilder altitudeSB = CommandUtil.getAltitudeStringBuilder(altitudeArr, altitudeLength);
//
//            //配速
//            int paceIndex = altitudeIndex + altitudeLength * 2;
//            if ((paceIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int paceLength = (pkg_data[paceIndex] & 0xff) + ((pkg_data[paceIndex + 1] & 0xff) << 8);
//            byte[] paceArr = new byte[paceLength * 2];
//            paceIndex += 2;
//            if (!checkArraycopy(pkg_data, paceIndex, paceArr, 0, paceLength * 2)) {
//                return;
//            }
//            StringBuilder paceSB = CommandUtil.getPaceStringBuilder(paceArr, paceLength);
//
//            //心率
//            int heartIndex = paceIndex + paceLength * 2;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            heartIndex += 2;
//            byte[] heartArr = new byte[heartLength];
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            //步频，UW90(原UW60) ,UW80C,UW80的1.28(包括)之后的版本的设备才有
//            //除了UW80的1.27之前的版本（不包括1.27版本）之外的设备没有步频数据，其他设备，UW80的1.27及以后的版本，UW90，UW60，UW200都有
//            boolean isHadStep = SmartDeviceBLL.getInstance().getDataStateInfo().isHasStepFrequencyData();
//            StringBuilder freqSB = new StringBuilder();
//            String freq_avg = "";
//            String freq_max = "";
//            int stepCount = 0;
//            if (isHadStep) {
//                int freqIndex = heartIndex + heartLength;
//                if ((freqIndex + 1) >= pkg_data.length) {
//                    return;
//                }
//                int freqLength = (pkg_data[freqIndex] & 0xff) + ((pkg_data[freqIndex + 1] & 0xff) << 8);
//                freqIndex += 2;
//                byte[] freqArr = new byte[freqLength * 2];
//                if (!checkArraycopy(pkg_data, freqIndex, freqArr, 0, freqLength * 2)) {
//                    return;
//                }
//                freqSB = CommandUtil.getStepFreqStringBuidler(freqArr, freqLength);
//
//                int[] freqArrInt = Utils.getAverageAndMaxExceptZone(freqSB.toString());
//                freq_avg = freqArrInt[0] + "";
//                freq_max = freqArrInt[1] + "";
//                stepCount = freqArrInt[3];
//            }
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (realMileage < 0 || realDuration <= 0 || realDuration > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.RunDetailsInfo info = new ClientSubmit.RunDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setLc(realMileage);
//            info.setLc_target(goalMileage * 1000);
//            info.setHs(realDuration);
//            info.setKcal(kcal);
//            info.setGd(climbHeight);
//            info.setWd(0);
//            info.setSdz_jh(speedSb.toString());//单位: 0.1km/h
//            info.setHbz_jh(altitudeSB.toString());//单位：米
//            info.setModel(runMode);
//            info.setWcqk(complete);
//            info.setTime_target(goalDuration);
//            info.setPath_thumb("");
//            info.setGps_jh(lolaSb.toString());
//            info.setXlz_jh(heartSB.toString());
//            info.setPsz_jh(paceSB.toString());
//            info.setBp_jh(freqSB.toString());
//            info.setBp_avg(freq_avg);
//            info.setBp_max(freq_max);
//            info.setBs(stepCount);
//            RunBLL.getInstance().insertOrUpdate(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//            Log.e("TAG", "GET run realMileage=" + realMileage);
//
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_MARATHON) {
//            if (pkg_data.length < 34) {
//                return;
//            }
//            int mode = pkg_data[0] & 0xff;//0=半程马拉松，1=全程马拉松
//            int complete = pkg_data[1] & 0xff;//完成度0~100
//            int mileage_goal = (pkg_data[2] & 0xff) + ((pkg_data[3] & 0xff) << 8);//目标训练里程（米）
//            int time_goal = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//目标训练时间（秒）
//            int mileage_real = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//实际里程（米）
//            int time_real = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//实际用时（秒）
//            int cal = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8) + ((pkg_data[18] & 0xff) << 16) + ((pkg_data[19] & 0xff) << 24);//卡路里（卡）
//            int height = (pkg_data[20] & 0xff) + ((pkg_data[21] & 0xff) << 8) + ((pkg_data[22] & 0xff) << 16) + ((pkg_data[23] & 0xff) << 24);//爬升高度
//            int startLo = (pkg_data[24] & 0xff) + ((pkg_data[25] & 0xff) << 8) + ((pkg_data[26] & 0xff) << 16) + ((pkg_data[27] & 0xff) << 24);//起始经度（精度0.00001）
//            int startLa = (pkg_data[28] & 0xff) + ((pkg_data[29] & 0xff) << 8) + ((pkg_data[30] & 0xff) << 16) + ((pkg_data[31] & 0xff) << 24);//起始纬度（精度0.00001）
//
//            //获得经度数组/纬度数组
//            int loLength = (pkg_data[32] & 0xff) + ((pkg_data[33] & 0xff) << 8);//经度增量数组长度
//            byte[] loArray = new byte[loLength];
//            byte[] laArray = new byte[loLength];
//            if (!checkArraycopy(pkg_data, 34, loArray, 0, loLength)) {
//                return;
//            }
//
//            int laIndex = 34 + loLength + 2;
//            if (!checkArraycopy(pkg_data, laIndex, laArray, 0, loLength)) {
//                return;
//            }
//            StringBuilder lolaSb = CommandUtil.getLaLoSB(laArray, loArray, startLa, startLo, loLength);
//
//            //速度
//            int speedIndex = laIndex + loLength;
//            if ((speedIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int speedLength = (pkg_data[speedIndex] & 0xff) + ((pkg_data[speedIndex + 1] & 0xff) << 8);
//            byte[] speedArr = new byte[speedLength];
//            speedIndex += 2;
//            if (!checkArraycopy(pkg_data, speedIndex, speedArr, 0, speedLength)) {
//                return;
//            }
//            StringBuilder speedSb = CommandUtil.getSpeedStringBuilder(speedArr, speedLength);
//
//            //海拔
//            int altitudeIndex = speedIndex + speedLength;
//            if ((altitudeIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int altitudeLength = (pkg_data[altitudeIndex] & 0xff) + ((pkg_data[altitudeIndex + 1] & 0xff) << 8);
//            byte[] altitudeArr = new byte[altitudeLength * 2];
//            altitudeIndex += 2;
//            if (!checkArraycopy(pkg_data, altitudeIndex, altitudeArr, 0, altitudeLength * 2)) {
//                return;
//            }
//            StringBuilder altitudeSB = CommandUtil.getAltitudeStringBuilder(altitudeArr, altitudeLength);
//
//            //配速
//            int paceIndex = altitudeIndex + altitudeLength * 2;
//            if ((paceIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int paceLength = (pkg_data[paceIndex] & 0xff) + ((pkg_data[paceIndex + 1] & 0xff) << 8);
//            byte[] paceArr = new byte[paceLength * 2];
//            paceIndex += 2;
//            if (!checkArraycopy(pkg_data, paceIndex, paceArr, 0, paceLength * 2)) {
//                return;
//            }
//            StringBuilder paceSB = CommandUtil.getPaceStringBuilder(paceArr, paceLength);
//
//            //心率
//            int heartIndex = paceIndex + paceLength * 2;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            heartIndex += 2;
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            //步频
//            boolean isHadStep = SmartDeviceBLL.getInstance().getDataStateInfo().isHasStepFrequencyData();
//            StringBuilder freqSB = new StringBuilder();
//            String freq_avg = "";
//            String freq_max = "";
//            int stepCount = 0;
//            if (isHadStep) {
//                int freqIndex = heartIndex + heartLength;
//                if ((freqIndex + 1) >= pkg_data.length) {
//                    return;
//                }
//                int freqLength = (pkg_data[freqIndex] & 0xff) + ((pkg_data[freqIndex + 1] & 0xff) << 8);
//                freqIndex += 2;
//                byte[] freqArr = new byte[freqLength * 2];
//                if (!checkArraycopy(pkg_data, freqIndex, freqArr, 0, freqLength * 2)) {
//                    return;
//                }
//                freqSB = CommandUtil.getStepFreqStringBuidler(freqArr, freqLength);
//
//                int[] freqArrInt = Utils.getAverageAndMaxExceptZone(freqSB.toString());
//                freq_avg = freqArrInt[0] + "";
//                freq_max = freqArrInt[1] + "";
//                stepCount = freqArrInt[3];
//            }
//
//            int[] paceValue = Utils.getAverageAndMaxValue(paceSB.toString());
//            float[] speedValue = Utils.getAverageMaxMinValuesFloat(speedSb.toString(), 3);
//            int[] altitudeValue = Utils.getAverageAndMaxValue(altitudeSB.toString());
//            float mSd_avg = 0.0f;
//            if (time_real > 0) {
//                mSd_avg = Utils.getFloatScale(3, ((mileage_real * 0.001f) / (time_real * 1.0f / 3600.0f)));
//            }
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (mileage_real < 0 || time_real <= 0 || time_real > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.MarathonDetailsInfo info = new ClientSubmit.MarathonDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setLc(mileage_real);
//            info.setHs(time_real);
//            info.setPs(paceValue[0]);
//            info.setKcal(cal);
//            info.setGd(height);
//            info.setSdz_jh(speedSb.toString());
//            info.setWd(0);
//            info.setSd_avg(mSd_avg);
//            info.setSd_max(speedValue[1]);
//            info.setHbz_jh(altitudeSB.toString());
//            info.setHb_avg(altitudeValue[0]);
//            info.setHb_max(altitudeValue[1]);
//            info.setModel(mode);
//            info.setWcqk(complete);
//            info.setPath_thumb("");
//            info.setGps_jh(lolaSb.toString());
//            info.setXlz_jh(heartSB.toString());
//            info.setPsz_jh(paceSB.toString());
//            info.setBp_jh(freqSB.toString());
//            info.setBp_avg(freq_avg);
//            info.setBp_max(freq_max);
//            info.setBs(stepCount);
//            MarathonBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//            Log.e("TAG", "GET marathon realMileage=" + mileage_real);
//
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_ROPE_SHIPPING) {
//            if (pkg_data.length < 18) {
//                return;
//            }
//            int mode = pkg_data[0] & 0xff;//模式 0=普通，1=训练
//            int complete = pkg_data[1] & 0xff;//完成度0~100
//            int count_goal = (pkg_data[2] & 0xff) + ((pkg_data[3] & 0xff) << 8);//训练目标（次）
//            int time_goal = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//训练时间（秒）
//            int time_real = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//实际用时（秒）
//            int count_real = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//实际次数
//            int heartLength = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            if (!checkArraycopy(pkg_data, 18, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSb = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSb.toString());
//            if (time_real <= 0 || time_real > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.RopeShippingDetailsInfo info = new ClientSubmit.RopeShippingDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setHs(time_real);
//            info.setCs(count_real);
//            info.setModel(mode);
//            info.setWcqk(complete);
//            info.setXlz_jh(heartSb.toString());
//            RopeShippingBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_SWIMMING) {
//            if (pkg_data.length < 18) {
//                return;
//            }
//            int mileage = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8);//里程（米）
//            int swing_arm_count = (pkg_data[2] & 0xff) + ((pkg_data[3] & 0xff) << 8);//摆臂次数
//            int time = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//耗时（秒）
//            int startLo = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//起始经度
//            int startLA = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//起始纬度
//            int loLength = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8);//经度数组长度
//            byte[] loArr = new byte[loLength];
//            byte[] laArr = new byte[loLength];
//            if (!checkArraycopy(pkg_data, 18, loArr, 0, loLength)) {
//                return;
//            }
//            int laIndex = 18 + loLength + 2;
//            if (!checkArraycopy(pkg_data, laIndex, laArr, 0, loLength)) {
//                return;
//            }
//            StringBuilder loLaSB = CommandUtil.getLaLoSB(laArr, loArr, startLA, startLo, loLength);
//
//            //速度
//            int speedIndex = laIndex + loLength;
//            if ((speedIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int speedLength = (pkg_data[speedIndex] & 0xff) + ((pkg_data[speedIndex + 1] & 0xff) << 8);
//            speedIndex += 2;
//            byte[] speedArr = new byte[speedLength];
//            if (!checkArraycopy(pkg_data, speedIndex, speedArr, 0, speedLength)) {
//                return;
//            }
//            StringBuilder speedSB = CommandUtil.getSpeedStringBuilder(speedArr, speedLength);
//            //心率
//            int heartIndex = speedIndex + speedLength;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            heartIndex += 2;
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            //温度
//            boolean isHadStep = SmartDeviceBLL.getInstance().getDataStateInfo().isHasTemperatureData();
//            StringBuilder tempSB = new StringBuilder();
//            if (isHadStep) {
//                int tempIndex = heartIndex + heartLength;
//                if ((tempIndex + 1) >= pkg_data.length) {
//                    return;
//                }
//                int tempLength = (pkg_data[tempIndex] & 0xff) + ((pkg_data[tempIndex + 1] & 0xff) << 8);
//                tempIndex += 2;
//                byte[] freqArr = new byte[tempLength];
//                if (!checkArraycopy(pkg_data, tempIndex, freqArr, 0, tempLength)) {
//                    return;
//                }
//                tempSB = CommandUtil.getTempStringBuilder(freqArr, tempLength);
//            }
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (mileage < 0 || time <= 0 || time > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.SwimDetailsInfo info = new ClientSubmit.SwimDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setLc(mileage);
//            info.setHs(time);
//            info.setCs(swing_arm_count);
//            info.setWd(0);
//            info.setSdz_jh(speedSB.toString());
//            info.setXlz_jh(heartSB.toString());
//            info.setPath_thumb("");
//            info.setGps_jh(loLaSB.toString());
//            info.setWd_jh(tempSB.toString());
//            Log.e("TAG", "GET swim realMileage=" + mileage);
//            SwimBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_ROCK_CLIMBING) {
//            if (pkg_data.length < 8) {
//                return;
//            }
//            int height = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8);//高度
//            int time = (pkg_data[2] & 0xff) + ((pkg_data[3] & 0xff) << 8) + ((pkg_data[4] & 0xff) << 16) + ((pkg_data[5] & 0xff) << 24);//用时（秒）
//            //心率
//            int heartLength = (pkg_data[6] & 0xff) + ((pkg_data[7] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            if (!checkArraycopy(pkg_data, 8, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (time <= 0 || time > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.RockClimbDetailsInfo rockClimbDetailsInfo = new ClientSubmit.RockClimbDetailsInfo();
//            rockClimbDetailsInfo.setDate(long2String(pkg_timeStamp * 1000L));
//            rockClimbDetailsInfo.setGd(height);
//            rockClimbDetailsInfo.setHs(time);
//            rockClimbDetailsInfo.setXlz_jh(heartSB.toString());
//
//            RockClimbBLL.getInstance().insert(rockClimbDetailsInfo);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//            Log.e("TAG", "GET climb height=" + height);
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_SKIING) {
//            if (pkg_data.length < 18) {
//                return;
//            }
//            int mileage = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8);//里程
//            int height = (pkg_data[2] & 0xff) + ((pkg_data[3] & 0xff) << 8);//下降高度
//            int time = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//耗时（秒）
//            int startLa = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//起始经度（经度0.00001）
//            int startLo = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//起始经度（经度0.00001）
//            int loLength = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8);//经纬度增量的数组长度
//            //经纬度数组
//            byte[] laArr = new byte[loLength];
//            byte[] loArr = new byte[loLength];
//            if (!checkArraycopy(pkg_data, 18, laArr, 0, loLength)) {
//                return;
//            }
//            int loIndex = 18 + loLength + 2;
//            if (!checkArraycopy(pkg_data, loIndex, loArr, 0, loLength)) {
//                return;
//            }
//            StringBuilder loLaSB = CommandUtil.getLaLoSB(loArr, laArr, startLo, startLa, loLength);
//
//            //速度
//            int speedIndex = loIndex + loLength;
//            if ((speedIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int speedLength = (pkg_data[speedIndex] & 0xff) + ((pkg_data[speedIndex + 1] & 0xff) << 8);
//            speedIndex += 2;
//            byte[] speedArr = new byte[speedLength];
//            if (!checkArraycopy(pkg_data, speedIndex, speedArr, 0, speedLength)) {
//                return;
//            }
//            StringBuilder speedSB = CommandUtil.getSpeedStringBuilder(speedArr, speedLength);
//
//            //海拔
//            int altitudeIndex = speedIndex + speedLength;
//            if ((altitudeIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int altitudeLength = (pkg_data[altitudeIndex] & 0xff) + ((pkg_data[altitudeIndex + 1] & 0xff) << 8);
//            altitudeIndex += 2;
//            byte[] altitudeArr = new byte[altitudeLength * 2];
//            if (!checkArraycopy(pkg_data, altitudeIndex, altitudeArr, 0, altitudeLength * 2)) {
//                return;
//            }
//            StringBuilder altitudeSB = CommandUtil.getAltitudeStringBuilder(altitudeArr, altitudeLength);
//
//            //心率
//            int heartIndex = altitudeIndex + altitudeLength * 2;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            heartIndex += 2;
//            byte[] heartArr = new byte[heartLength];
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (mileage < 0 || time <= 0 || time > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.SkiingDetailsInfo skiingDetailsInfo = new ClientSubmit.SkiingDetailsInfo();
//            skiingDetailsInfo.setDate(long2String(pkg_timeStamp * 1000L));
//            skiingDetailsInfo.setLc(mileage);
//            skiingDetailsInfo.setHs(time);
//            skiingDetailsInfo.setHbz_jh(altitudeSB.toString());
//            skiingDetailsInfo.setWd(0);
//            skiingDetailsInfo.setSdz_jh(speedSB.toString());
//            skiingDetailsInfo.setXlz_jh(heartSB.toString());
//            skiingDetailsInfo.setGd(height);
//            skiingDetailsInfo.setPath_thumb("");
//            skiingDetailsInfo.setGps_jh(loLaSB.toString());
//            skiingDetailsInfo.setPsz_jh("");//????????????????配速，蓝牙没有提供该数据
//            SkiingBLL.getInstance().insert(skiingDetailsInfo);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//            Log.e("TAG", "GET skiing mileage=" + mileage);
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_RIDING) {
//            if (pkg_data.length < 28) {
//                return;
//            }
//            int mode = pkg_data[0] & 0xff;//模式0=普通，1=里程训练模式，2=时间训练模式
//            int complete = pkg_data[1] & 0xff;//完成度0~100
//            int mileage_goal = (pkg_data[2] & 0xff) + ((pkg_data[3] & 0xff) << 8) + ((pkg_data[4] & 0xff) << 16) + ((pkg_data[5] & 0xff) << 24);//计划训练里程（米）
//            int time_goal = (pkg_data[6] & 0xff) + ((pkg_data[7] & 0xff) << 8) + ((pkg_data[8] & 0xff) << 16) + ((pkg_data[9] & 0xff) << 24);//计划训练时间（秒）
//            int mileage_real = (pkg_data[10] & 0xff) + ((pkg_data[11] & 0xff) << 8) + ((pkg_data[12] & 0xff) << 16) + ((pkg_data[13] & 0xff) << 24);//实际里程（米）
//            int time_real = (pkg_data[14] & 0xff) + ((pkg_data[15] & 0xff) << 8) + ((pkg_data[16] & 0xff) << 16) + ((pkg_data[17] & 0xff) << 24);//实际时间（秒）
//            int startLo = (pkg_data[18] & 0xff) + ((pkg_data[19] & 0xff) << 8) + ((pkg_data[20] & 0xff) << 16) + ((pkg_data[21] & 0xff) << 24);//起始经度
//            int startLa = (pkg_data[22] & 0xff) + ((pkg_data[23] & 0xff) << 8) + ((pkg_data[24] & 0xff) << 16) + ((pkg_data[25] & 0xff) << 24);//起始纬度
//            //经纬度数组
//            int loLength = (pkg_data[26] & 0xff) + ((pkg_data[27] & 0xff) << 8);
//            byte[] loArr = new byte[loLength];
//            byte[] laArr = new byte[loLength];
//            if (!checkArraycopy(pkg_data, 28, loArr, 0, loLength)) {
//                return;
//            }
//            int loIndex = 28 + loLength + 2;
//            if (!checkArraycopy(pkg_data, loIndex, laArr, 0, loLength)) {
//                return;
//            }
//            StringBuilder lolaSB = CommandUtil.getLaLoSB(laArr, loArr, startLa, startLo, loLength);
//            //速度
//            int speedIndex = loIndex + loLength;
//            if ((speedIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int speedLength = (pkg_data[speedIndex] & 0xff) + ((pkg_data[speedIndex + 1] & 0xff) << 8);
//            byte[] speedArr = new byte[speedLength];
//            speedIndex += 2;
//            if (!checkArraycopy(pkg_data, speedIndex, speedArr, 0, speedLength)) {
//                return;
//            }
//            StringBuilder speedSB = CommandUtil.getSpeedStringBuilder(speedArr, speedLength);
//            //海拔
//            int altitudeIndex = speedIndex + speedLength;
//            if ((altitudeIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int altitudeLength = (pkg_data[altitudeIndex] & 0xff) + ((pkg_data[altitudeIndex + 1] & 0xff) << 8);
//            altitudeIndex += 2;
//            byte[] altitudeArr = new byte[altitudeLength * 2];
//            if (!checkArraycopy(pkg_data, altitudeIndex, altitudeArr, 0, altitudeLength * 2)) {
//                return;
//            }
//            StringBuilder altitudeSB = CommandUtil.getAltitudeStringBuilder(altitudeArr, altitudeLength);
//            //配速
//            int paceIndex = altitudeIndex + altitudeLength * 2;
//            if ((paceIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int paceLength = (pkg_data[paceIndex] & 0xff) + ((pkg_data[paceIndex + 1] & 0xff) << 8);
//            byte[] paceArr = new byte[paceLength * 2];
//            paceIndex += 2;
//            if (!checkArraycopy(pkg_data, paceIndex, paceArr, 0, paceLength * 2)) {
//                return;
//            }
//            StringBuilder paceSB = CommandUtil.getPaceStringBuilder(paceArr, paceLength);
//            //心率
//            int heartIndex = paceIndex + paceLength * 2;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            heartIndex += 2;
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (mileage_real < 0 || time_real <= 0 || time_real > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.RideDetailsInfo info = new ClientSubmit.RideDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setLc(mileage_real);
//            info.setHs(time_real);
//            info.setHbz_jh(altitudeSB.toString());
//            info.setWd(0);
//            info.setSdz_jh(speedSB.toString());
//            info.setGd(0);
//            info.setModel(mode);
//            info.setWcqk(complete);
//            info.setLc_target(mileage_goal);
//            info.setTime_target(time_goal);
//            info.setPath_thumb("");
//            info.setXlz_jh(heartSB.toString());
//            info.setGps_jh(lolaSB.toString());
//            info.setPsz_jh(paceSB.toString());
//
//            RideBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//            Log.e("TAG", "GET riding mileage=" + mileage_real);
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_ROWING) {
//            if (pkg_data.length < 22) {
//                return;
//            }
//            int mileage = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8) + ((pkg_data[2] & 0xff) << 16) + ((pkg_data[3] & 0xff) << 24);//里程（米）
//            int time = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//耗时（秒）
//            int paddle_frequency = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//桨频
//            int startLo = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//起始经度
//            int startLa = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8) + ((pkg_data[18] & 0xff) << 16) + ((pkg_data[19] & 0xff) << 24);//起始纬度
//            //经纬度数组
//            int loLength = (pkg_data[20] & 0xff) + ((pkg_data[21] & 0xff) << 8);
//            byte[] loArr = new byte[loLength];
//            byte[] laArr = new byte[loLength];
//            if (!checkArraycopy(pkg_data, 22, loArr, 0, loLength)) {
//                return;
//            }
//            int laIndex = 22 + loLength + 2;
//            if (!checkArraycopy(pkg_data, laIndex, laArr, 0, loLength)) {
//                return;
//            }
//            StringBuilder laLoSB = CommandUtil.getLaLoSB(laArr, loArr, startLa, startLo, loLength);
//            //速度
//            int speedIndex = laIndex + loLength;
//            if ((speedIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int speedLength = (pkg_data[speedIndex] & 0xff) + ((pkg_data[speedIndex + 1] & 0xff) << 8);
//            byte[] speedArr = new byte[speedLength];
//            speedIndex += 2;
//            if (!checkArraycopy(pkg_data, speedIndex, speedArr, 0, speedLength)) {
//                return;
//            }
//            StringBuilder speedSB = CommandUtil.getSpeedStringBuilder(speedArr, speedLength);
//            //心率
//            int heartIndex = speedIndex + speedLength;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            heartIndex += 2;
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (mileage < 0 || time <= 0 || time > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.RowingDetailsInfo info = new ClientSubmit.RowingDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setLc(mileage);
//            info.setHs(time);
//            info.setOar_frequency(paddle_frequency);
//            info.setWd(0);
//            info.setSdz_jh(speedSB.toString());
//            info.setXlz_jh(heartSB.toString());
//            info.setPath_thumb("");
//            info.setGps_jh(laLoSB.toString());
//            RowingBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//            Log.e("TAG", "GET rowe mileage=" + mileage);
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_BUNGEE_JUMPING) {//蹦极
//            if (pkg_data.length < 6) {
//                return;
//            }
//            int height = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8);//起始高度
//            int count = (pkg_data[2] & 0xff) + ((pkg_data[3] & 0xff) << 8);//回弹次数
//            //心率
//            int heartLength = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            if (!checkArraycopy(pkg_data, 6, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            ClientSubmit.BungeeDetailsInfo info = new ClientSubmit.BungeeDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setXlz_jh(heartSB.toString());
//            info.setGd(height);
//            info.setCs(count);
//            BungeeBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_MOUNTAINEERING) {//登山
//            if (pkg_data.length < 26) {
//                return;
//            }
//            int mileage = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8) + ((pkg_data[2] & 0xff) << 16) + ((pkg_data[3] & 0xff) << 24);//里程（米）
//            int time = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//耗时（秒）
//            int steps = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//步数
//            int heightC = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//高度差
//            int startLo = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8) + ((pkg_data[18] & 0xff) << 16) + ((pkg_data[19] & 0xff) << 24);//起始经度
//            int startLa = (pkg_data[20] & 0xff) + ((pkg_data[21] & 0xff) << 8) + ((pkg_data[22] & 0xff) << 16) + ((pkg_data[23] & 0xff) << 24);//起始纬度
//            //经纬度数组
//            int loLength = (pkg_data[24] & 0xff) + ((pkg_data[25] & 0xff) << 8);
//            byte[] loArr = new byte[loLength];
//            byte[] laArr = new byte[loLength];
//            if (!checkArraycopy(pkg_data, 26, loArr, 0, loLength)) {
//                return;
//            }
//            int laIndex = 26 + loLength + 2;
//            if (!checkArraycopy(pkg_data, laIndex, laArr, 0, loLength)) {
//                return;
//            }
//            StringBuilder laLoSB = CommandUtil.getLaLoSB(laArr, loArr, startLa, startLo, loLength);
//            //速度
//            int speedIndex = laIndex + loLength;
//            if ((speedIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int speedLength = (pkg_data[speedIndex] & 0xff) + ((pkg_data[speedIndex + 1] & 0xff) << 8);
//            byte[] speedArr = new byte[speedLength];
//            speedIndex += 2;
//            if (!checkArraycopy(pkg_data, speedIndex, speedArr, 0, speedLength)) {
//                return;
//            }
//            StringBuilder speedSB = CommandUtil.getSpeedStringBuilder(speedArr, speedLength);
//            //海拔
//            int altitudeIndex = speedIndex + speedLength;
//            if ((altitudeIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int altitudeLength = (pkg_data[altitudeIndex] & 0xff) + ((pkg_data[altitudeIndex + 1] & 0xff) << 8);
//            byte[] altitudeArr = new byte[altitudeLength * 2];
//            altitudeIndex += 2;
//            if (!checkArraycopy(pkg_data, altitudeIndex, altitudeArr, 0, altitudeLength * 2)) {
//                return;
//            }
//            StringBuilder altitudeSB = CommandUtil.getAltitudeStringBuilder(altitudeArr, altitudeLength);
//            //心率
//            int heartIndex = altitudeIndex + altitudeLength * 2;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            heartIndex += 2;
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (mileage < 0 || time <= 0 || time > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.MountainDetailsInfo info = new ClientSubmit.MountainDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setLc(mileage);
//            info.setHs(time);
//            info.setGdc(heightC);
//            info.setBs(steps);
//            info.setWd(0);
//            info.setSdz_jh(speedSB.toString());
//            info.setHbz_jh(altitudeSB.toString());
//            info.setPath_thumb("");
//            info.setGps_jh(laLoSB.toString());
//            info.setXlz_jh(heartSB.toString());
//            MountainBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//            Log.e("TAG", "GET mountaion mileage=" + mileage);
//
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_PARACHUTE) {//跳伞
//            if (pkg_data.length < 18) {
//                return;
//            }
//            int height = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8) + ((pkg_data[2] & 0xff) << 16) + ((pkg_data[3] & 0xff) << 24);//起始高度（米）
//            int time = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//用时（米）
//            int startLo = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//起始经度
//            int startLa = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//其实纬度
//            //经纬度数组
//            int loLength = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8);
//            byte[] loArr = new byte[loLength];
//            byte[] laArr = new byte[loLength];
//            //System.arraycopy(pkg_data, 18, loArr, 0, loLength);
//            if (!checkArraycopy(pkg_data, 18, loArr, 0, loLength)) {
//                return;
//            }
//            int laIndex = 16 + loLength + 2;
//            //System.arraycopy(pkg_data, laIndex, laArr, 0, loLength);
//            if (!checkArraycopy(pkg_data, laIndex, laArr, 0, loLength)) {
//                return;
//            }
//            StringBuilder laloSB = CommandUtil.getLaLoSB(laArr, loArr, startLa, startLo, loLength);
//            //海拔
//            int altitudeIndex = laIndex + loLength;
//            if ((altitudeIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int altitudeLength = (pkg_data[altitudeIndex] & 0xff) + ((pkg_data[altitudeIndex + 1] & 0xff) << 8);
//            altitudeIndex += 2;
//            byte[] altitudeArr = new byte[altitudeLength * 2];
//            if (!checkArraycopy(pkg_data, altitudeIndex, altitudeArr, 0, altitudeLength * 2)) {
//                return;
//            }
//            StringBuilder altitudeSB = CommandUtil.getAltitudeStringBuilder(altitudeArr, altitudeLength);
//            //心率
//            int heartIndex = altitudeIndex + altitudeLength * 2;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            heartIndex += 2;
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (time <= 0 || time > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.ParachuteDetailsInfo info = new ClientSubmit.ParachuteDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setHs(time);
//            info.setGd(height);
//            info.setXlz_jh(heartSB.toString());
//            ParachuteBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_GOLF) {//高尔夫
//            if (pkg_data.length < 22) {
//                return;
//            }
//            int pole = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8) + ((pkg_data[2] & 0xff) << 16) + ((pkg_data[3] & 0xff) << 24);//杆数
//            int time = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//时间（秒）
//            int steps = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//步数
//            int startLo = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//起始经度
//            int startLa = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8) + ((pkg_data[18] & 0xff) << 16) + ((pkg_data[19] & 0xff) << 24);//其实纬度
//            //经纬度数组
//            int loLength = (pkg_data[20] & 0xff) + ((pkg_data[21] & 0xff) << 8);
//            byte[] loArr = new byte[loLength];
//            byte[] laArr = new byte[loLength];
//            if (!checkArraycopy(pkg_data, 22, loArr, 0, loLength)) {
//                return;
//            }
//            int laIndex = 22 + loLength + 2;
//            if (!checkArraycopy(pkg_data, laIndex, laArr, 0, loLength)) {
//                return;
//            }
//            StringBuilder laloSB = CommandUtil.getLaLoSB(laArr, loArr, startLa, startLo, loLength);
//            //海拔
//            int altitudeIndex = laIndex + loLength;
//            if ((altitudeIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int altitudeLength = (pkg_data[altitudeIndex] & 0xff) + ((pkg_data[altitudeIndex + 1] & 0xff) << 8);
//            altitudeIndex += 2;
//            byte[] altitudeArr = new byte[altitudeLength * 2];
//            if (!checkArraycopy(pkg_data, altitudeIndex, altitudeArr, 0, altitudeLength * 2)) {
//                return;
//            }
//            StringBuilder altitudeSB = CommandUtil.getAltitudeStringBuilder(altitudeArr, altitudeLength);
//            //心率
//            int heartIndex = altitudeIndex + altitudeLength * 2;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            heartIndex += 2;
//            //System.arraycopy(pkg_data, heartIndex, heartArr, 0, heartLength);
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//            //击球距离
//            int distanceIndex = heartIndex + heartLength;
//            if ((distanceIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int distanceLength = (pkg_data[distanceIndex] & 0xff) + ((pkg_data[distanceIndex + 1] & 0xff) << 8);
//            byte[] hitBall = new byte[distanceLength * 2];
//            distanceIndex += 2;
//
//            if (!checkArraycopy(pkg_data, distanceIndex, hitBall, 0, distanceLength * 2)) {
//                return;
//            }
//
//            StringBuilder hitBallSB = CommandUtil.getHitBallStringBuilder(hitBall, distanceLength);
//            //每洞杆数
//            int poleIndex = distanceIndex + distanceLength * 2;
//            if ((poleIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int poleLength = (pkg_data[poleIndex] & 0xff) + ((pkg_data[poleIndex + 1] & 0xff) << 8);
//            byte[] polebyte = new byte[poleLength];
//            poleIndex += 2;
//            if (!checkArraycopy(pkg_data, poleIndex, polebyte, 0, poleLength)) {
//                return;
//            }
//            StringBuilder poleSb = CommandUtil.getPoleNumberStringBuilder(polebyte, poleLength);
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (time <= 0 || time > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//
//            //int mileage = (int)Math.round(0.5 * steps);
//            int mileage = Utils.getGPSMileage(Utils.getPreViewGPSList(mContext, laloSB.toString()));
//            ClientSubmit.GolfDetailsInfo info = new ClientSubmit.GolfDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setGs(pole);
//            info.setLc(mileage);
//            info.setBs(steps);
//            info.setHs(time);
//            info.setHbz_jh(altitudeSB.toString());
//            info.setWd(0);
//            info.setXlz_jh(heartSB.toString());
//            info.setGps_jh(laloSB.toString());
//            info.setJqjl(hitBallSB.toString());
//            info.setMdgs(poleSb.toString());
//            GolfBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//            Log.e("TAG", "GET golf pole=" + pole);
//
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_SURFING) {//冲浪
//            if (pkg_data.length < 18) {
//                return;
//            }
//            int mileage = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8) + ((pkg_data[2] & 0xff) << 16) + ((pkg_data[3] & 0xff) << 24);//里程（米）
//            int time = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//耗时（秒）
//            int startLo = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//起始经度
//            int startLa = (pkg_data[12] & 0xff) + ((pkg_data[13] & 0xff) << 8) + ((pkg_data[14] & 0xff) << 16) + ((pkg_data[15] & 0xff) << 24);//其实纬度
//            //经纬度数组
//            int loLength = (pkg_data[16] & 0xff) + ((pkg_data[17] & 0xff) << 8);
//            byte[] loArr = new byte[loLength];
//            byte[] laArr = new byte[loLength];
//
//            //System.arraycopy(pkg_data, 18, loArr, 0, loLength);
//            if (!checkArraycopy(pkg_data, 18, loArr, 0, loLength)) {
//                return;
//            }
//            int laIndex = 18 + loLength + 2;
//            //System.arraycopy(pkg_data, laIndex, laArr, 0, loLength);
//            if (!checkArraycopy(pkg_data, laIndex, laArr, 0, loLength)) {
//                return;
//            }
//            StringBuilder laloSB = CommandUtil.getLaLoSB(laArr, loArr, startLa, startLo, loLength);
//
//            //速度
//            int speedIndex = laIndex + loLength;
//            if ((speedIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int speedLength = (pkg_data[speedIndex] & 0xff) + ((pkg_data[speedIndex + 1] & 0xff) << 8);
//            byte[] speedArr = new byte[speedLength];
//            speedIndex += 2;
//            if (!checkArraycopy(pkg_data, speedIndex, speedArr, 0, speedLength)) {
//                return;
//            }
//            StringBuilder speedSB = CommandUtil.getSpeedStringBuilder(speedArr, speedLength);
//
//            //心率
//            int heartIndex = speedIndex + speedLength;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            heartIndex += 2;
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (mileage < 0 || time <= 0 || time > MAX_SPORT_TIME || mMinHeartRate <= 0) {
//                return;
//            }
//            ClientSubmit.SurfDetailsInfo info = new ClientSubmit.SurfDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setLc(mileage);
//            info.setHs(time);
//            info.setWd(0);
//            info.setSdz_jh(speedSB.toString());
//            info.setXlz_jh(heartSB.toString());
//            info.setGps_jh(laloSB.toString());
//            SurfBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//            Log.e("TAG", "GET suft mileage=" + mileage);
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_TREADMILL) {
//            //跑步机
//            if (pkg_data.length < 13) {
//                return;
//            }
//            //耗时
//            int time = (pkg_data[0] & 0xff) + ((pkg_data[1] & 0xff) << 8) + ((pkg_data[2] & 0xff) << 16) + ((pkg_data[3] & 0xff) << 24);//耗时（秒）
//            int steps = (pkg_data[4] & 0xff) + ((pkg_data[5] & 0xff) << 8) + ((pkg_data[6] & 0xff) << 16) + ((pkg_data[7] & 0xff) << 24);//步数
//            int cal = (pkg_data[8] & 0xff) + ((pkg_data[9] & 0xff) << 8) + ((pkg_data[10] & 0xff) << 16) + ((pkg_data[11] & 0xff) << 24);//步数
//
//            int freqIndex = 12;
//            StringBuilder freqSB = new StringBuilder();
//            String freq_avg = "";
//            String freq_max = "";
//            int stepCount = 0;
//            if ((freqIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int freqLength = (pkg_data[freqIndex] & 0xff) + ((pkg_data[freqIndex + 1] & 0xff) << 8);
//            freqIndex += 2;
//            byte[] freqArr = new byte[freqLength * 2];
//            if (!checkArraycopy(pkg_data, freqIndex, freqArr, 0, freqLength * 2)) {
//                return;
//            }
//            freqSB = CommandUtil.getStepFreqStringBuidler(freqArr, freqLength);
//            int[] freqArrInt = Utils.getAverageAndMaxExceptZone(freqSB.toString());
//            freq_avg = freqArrInt[0] + "";
//            freq_max = freqArrInt[1] + "";
//
//            int heartIndex = freqIndex + freqLength * 2;
//            if ((heartIndex + 1) >= pkg_data.length) {
//                return;
//            }
//            int heartLength = (pkg_data[heartIndex] & 0xff) + ((pkg_data[heartIndex + 1] & 0xff) << 8);
//            byte[] heartArr = new byte[heartLength];
//            heartIndex += 2;
//            if (!checkArraycopy(pkg_data, heartIndex, heartArr, 0, heartLength)) {
//                return;
//            }
//            StringBuilder heartSB = CommandUtil.getHeartStringBuilder(heartArr, heartLength);
//
//            int mMinHeartRate = Utils.getHeartMinValue(heartSB.toString());
//            if (time <= 0 || (time > MAX_SPORT_TIME) || (mMinHeartRate <= 0)) {
//                return;
//            }
//            ClientSubmit.TreadmillDetailsInfo info = new ClientSubmit.TreadmillDetailsInfo();
//            info.setDate(long2String(pkg_timeStamp * 1000L));
//            info.setBp_max(freq_max);
//            info.setBp_avg(freq_avg);
//            info.setBp_jh(freqSB.toString());
//            info.setBs(steps);
//            info.setKcal(cal);
//            info.setHs(time);
//            info.setXlz_jh(heartSB.toString());
//
//            TreadmillBLL.getInstance().insert(info);
//            mHandler.sendEmptyMessageDelayed(mHandlerFlag, MESSAGE_DELAYED_TIME);
//            Log.e("TAG", "treadmill steps = " + steps);
//        } else if (pkg_type == CommandUtil.SYNC_TYPE_SMARTDEVICE_INFO) {
//            //手表信息
//            Log.e("TAG", "smartDevice");
//            if (pkg_data.length == 0) {
//                return;
//            }
//            int gms_gprs = pkg_data[0] & 0xff;
//            int gps = pkg_data[1] & 0xff;
//            int e_Compass = pkg_data[2] & 0xff;
//            int accelerometer = pkg_data[3] & 0xff;
//            int gyroscope = pkg_data[4] & 0xff;
//            int temperature = pkg_data[5] & 0xff;
//            int humidity = pkg_data[6] & 0xff;
//            int pressure = pkg_data[7] & 0xff;
//            int proximity = pkg_data[8] & 0xff;
//            int pedomoter = pkg_data[9] & 0xff;
//            int heart_rate = pkg_data[10] & 0xff;
//            int fota = pkg_data[11] & 0xff;
//
//            byte[] moduleByte = new byte[16];
//            System.arraycopy(pkg_data, 12, moduleByte, 0, 16);
//            String module = CommandUtil.byte2String(moduleByte);
//            byte[] versionByte = new byte[16];
//            System.arraycopy(pkg_data, 28, versionByte, 0, 16);
//            String version = CommandUtil.byte2String(versionByte);
//
//            SmartDeviceInfo info = new SmartDeviceInfo();
//            info.setGsm_gprs(gms_gprs);
//            info.setGps(gps);
//            info.setE_compass(e_Compass);
//            info.setAccelerometer(accelerometer);
//            info.setGyroscope(gyroscope);
//            info.setTemperature(temperature);
//            info.setHumidity(humidity);
//            info.setPressure(pressure);
//            info.setProximity(proximity);
//            info.setPedometer(pedomoter);
//            info.setHeart_rate(heart_rate);
//            info.setFota(fota);
//            info.setModule(module);
//            info.setVersion(version);
//            SmartDeviceBLL.getInstance().insert(info);
//
//            //去网络取FOTA文件
//            Intent intent = new Intent(BTNotificationApplication.GET_FOTA_INFO_ACTION);
//            mContext.sendBroadcast(intent);
//            //去网络取天气信息
//            SharedPreferences sharedPreferences = mContext.getSharedPreferences(BTNotificationApplication.SHARED_NAME, MODE_PRIVATE);
//            String cityId = sharedPreferences.getString(BTNotificationApplication.WEATHER_ID, "");
//            boolean isTurnOn = sharedPreferences.getBoolean(BTNotificationApplication.WEATHER_STATE, false);
//            if (/*!TextUtils.isEmpty(cityId) &&*/ isTurnOn) {
//                        /*String json = "https://api.heweather.com/v5/weather?city=" + cityId + "&key=" + BTNotificationApplication.WEATHER_KEY;
//                        Intent weatherIntent = new Intent(mContext, AccountOperateService.class);
//                        weatherIntent.putExtra(BTNotificationApplication.ACCOUNT_OPERATE_TYPE, BTNotificationApplication.ACCOUNT_OPEATE_GET_WEATHER_INFO);
//                        weatherIntent.putExtra(BTNotificationApplication.ACCOUNT_OPERATE_CONTENT, json);
//                        mContext.startService(weatherIntent);*/
//                String mUser_key = UserInfoBLL.getInstance().getUserKey();
//                if (!TextUtils.isEmpty(mUser_key)) {
//                    try {
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("user_key", mUser_key);
//                        String json = JsonUtil.object2String(jsonObject.toString());
//                        Intent weatherIntent = new Intent(mContext, AccountOperateService.class);
//                        weatherIntent.putExtra(BTNotificationApplication.ACCOUNT_OPERATE_TYPE, BTNotificationApplication.ACCOUNT_OPEATE_GET_WEATHER_INFO);
//                        weatherIntent.putExtra(BTNotificationApplication.ACCOUNT_OPERATE_CONTENT, json);
//                        //ContextCompat.startForegroundService(mContext, weatherIntent);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            AccountOperateJobService.enqueueWork(mContext, weatherIntent);
//                        } else {
//                            mContext.startService(weatherIntent);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            //同步我的一天数据
//            mEditor.putInt(BTNotificationApplication.SYNC_SPORT_DATA_TYPE, 0);
//            mEditor.commit();
//            MessageClient.getInstance().synSmartDeviceSportData(12);
//
//        } else if (pkg_type == CommandUtil.SYNC_SOS) {
//            //手表发出SOS信号
//            Log.e("TAg", "SOS");
//            int status = (pkg_data[0] & 0xff);//0代表经纬度无效，1代表经纬度有效
//            int startLo = (pkg_data[1] & 0xff) + ((pkg_data[2] & 0xff) << 8) + ((pkg_data[3] & 0xff) << 16) + ((pkg_data[4] & 0xff) << 24);//经度
//            int startLa = (pkg_data[5] & 0xff) + ((pkg_data[6] & 0xff) << 8) + ((pkg_data[7] & 0xff) << 16) + ((pkg_data[8] & 0xff) << 24);//纬度
//            if (status == 1) {
//                ClientSubmit.SharedInfo info = new ClientSubmit.SharedInfo();
//                info.setUser_key(UserInfoBLL.getInstance().getUserKey());
//                info.setShare_gps(Utils.getFloatScale(5, startLa * 0.00001f) + "," + Utils.getFloatScale(5, startLo * 0.00001f));
//                info.setShare_sos("1");
//                info.setShare_time(Utils.date2String1(new Date()));
//
//                String jsonString = JsonUtil.object2String(info);
//                Intent intent1 = new Intent(mContext, AccountOperateService.class);
//                intent1.putExtra(BTNotificationApplication.ACCOUNT_OPERATE_TYPE, BTNotificationApplication.ACCOUNT_OPERATE_SHARED_LOCATION_INFO);
//                intent1.putExtra(BTNotificationApplication.ACCOUNT_OPERATE_CONTENT, TranscodeUtil.getEncryptCode(jsonString));
//                //ContextCompat.startForegroundService(mContext,intent1);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    AccountOperateJobService.enqueueWork(mContext, intent1);
//                } else {
//                    mContext.startService(intent1);
//                }
//            } else {
//                //SOS坐标无效？
//            }
//        }
    }

    /**
     * 提取出需要解析的数据数组
     *
     * @param sourceByte     源数据数组
     * @param startIndex     开始截取的源数据下标
     * @param destByte       目标数据数组
     * @param startDestIndex 开始截取的目标数据下标
     * @param destLength     目标数据长度
     * @return
     * @throws ArrayIndexOutOfBoundsException
     */
    private static boolean checkArraycopy(byte[] sourceByte, int startIndex, byte[] destByte, int startDestIndex, int destLength) {
        try {
            System.arraycopy(sourceByte, startIndex, destByte, startDestIndex, destLength);
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }
}
