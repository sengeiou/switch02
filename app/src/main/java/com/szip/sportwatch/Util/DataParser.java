package com.szip.sportwatch.Util;

import android.util.Log;


import com.szip.sportwatch.DB.dbModel.HeartData;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.Interface.IDataResponse;
import com.szip.sportwatch.Model.BleStepModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Hqs on 2018/1/4
 */
public class DataParser {


    private ArrayList<BleStepModel> stepDataArrayList;
    private ArrayList<HeartData> heartDataArrayList;
    private ArrayList<SportData> sportDataArrayList;
    private long timeOfdata = 0;//用来存储上一段数据的时间，以判断下一段数据是否为同一天的数据

    private IDataResponse mIDataResponse;

    private DataParser(){}
    private static DataParser mDataParser;
    public static DataParser newInstance(){                     // 单例模式，双重锁
        if( mDataParser == null ){
            synchronized (DataParser.class){
                if( mDataParser == null ){
                    mDataParser = new DataParser();
                }
            }
        }
        return mDataParser ;
    }

    public void setmIDataResponse(IDataResponse mIDataResponse) {
        this.mIDataResponse = mIDataResponse;
    }

    public void parseData(byte[] data){
       if (data[1]==0x32){
            int deviceNum = (data[9]&0xff)<<8|(data[8]&0xff)&0x0ffff;
            ArrayList<Integer> datas = new ArrayList<>();
            for (int i = 10;i<data.length-1;i++){
                if (data[i]!=0){
                    datas.add(i-9);
                    Log.d("SZIP******","datetype = "+(i-9));
                }
            }
            if (data[27]!=0)
                datas.add(0x14);
            if (mIDataResponse!=null)
                mIDataResponse.onGetDataIndex(deviceNum+"",datas);
        }
    }

    public void parseData(int type,byte[] data,long time,boolean isEnd){
        if(type==0x01){//计步数据
            if (stepDataArrayList==null)
                stepDataArrayList = new ArrayList<>();
            long timeOfDay = DateUtil.getTimeScopeForDay(time);//这段计步数据所属的日期
            Log.d("SZIP******","timeOfDay = "+timeOfDay);
            if (timeOfDay!= timeOfdata){//判断这段计步数据是否属于同一天，如果跟上次缓存的时间不一样，说明是跨天了
                timeOfdata = timeOfDay;
                int step = (data[0] & 0xff) + ((data[1] & 0xFF) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xFF) << 24);
                int distence = (data[4] & 0xff) + ((data[5] & 0xFF) << 8) + ((data[6] & 0xff) << 16) + ((data[7] & 0xFF) << 24);
                int calorie = (data[8] & 0xff) + ((data[9] & 0xFF) << 8) + ((data[10] & 0xff) << 16) + ((data[11] & 0xFF) << 24);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time*1000);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                Log.d("SZIP******","hour = "+hour);
                HashMap<Integer,Integer> hashMap = new HashMap<>();
                hashMap.put(hour,step);
                stepDataArrayList.add(new BleStepModel(step,distence,calorie,timeOfDay,hashMap));
            }else {
                BleStepModel model = stepDataArrayList.get(stepDataArrayList.size()-1);
                int step = (data[0] & 0xff) + ((data[1] & 0xFF) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xFF) << 24);
                int distence = (data[4] & 0xff) + ((data[5] & 0xFF) << 8) + ((data[6] & 0xff) << 16) + ((data[7] & 0xFF) << 24);
                int calorie = (data[8] & 0xff) + ((data[9] & 0xFF) << 8) + ((data[10] & 0xff) << 16) + ((data[11] & 0xFF) << 24);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time*1000);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                model.setStep(step);
                model.setCalorie(calorie);
                model.setDistance(distence);
                model.setStepInfo(hour,step);
            }

            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveStepDatas(stepDataArrayList);
                stepDataArrayList = null;
                timeOfdata = 0;
                Log.d("SZIP******","计步数据接受结束");
            }
        }else if (type==0x02){//心率数据
            if (heartDataArrayList==null)
                heartDataArrayList = new ArrayList<>();
            long timeOfDay = DateUtil.getTimeScopeForDay(time);//这段心率数据所属的日期
            if (timeOfDay!= timeOfdata){//判断这段计步数据是否属于同一天，如果跟上次缓存的时间不一样，说明是跨天了
                timeOfdata = timeOfDay;
                int heart = (data[0] & 0xff) + ((data[1] & 0xFF) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xFF) << 24);
                heartDataArrayList.add(new HeartData(timeOfDay,heart,heart+""));
            }else {
                HeartData model = heartDataArrayList.get(heartDataArrayList.size()-1);
                int heart = (data[0] & 0xff) + ((data[1] & 0xFF) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xFF) << 24);
                model.heartArray+=(","+heart);
                model.averageHeart+=heart;
            }
            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveHeartDatas(heartDataArrayList);
                heartDataArrayList = null;
                timeOfdata = 0;
                Log.d("SZIP******","心率数据接受结束");
            }


        }else if (type==0x03){//睡眠数据
            if (isEnd)
                Log.d("SZIP******","睡眠数据接受结束");

        }else if (type==0x04){//跑步数据
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            if (data[0]==0)
                sportData.type = 2;
            else
                sportData.type = 6;
            sportData.time = time;
            sportData.sportTime = (data[12] & 0xff) + ((data[13] & 0xFF) << 8) + ((data[14] & 0xff) << 16) + ((data[15] & 0xFF) << 24);
            sportData.calorie = (data[16] & 0xff) + ((data[17] & 0xFF) << 8) + ((data[18] & 0xff) << 16) + ((data[19] & 0xFF) << 24);
            sportData.distance = (data[8] & 0xff) + ((data[9] & 0xFF) << 8) + ((data[10] & 0xff) << 16) + ((data[11] & 0xFF) << 24);
            int longLenght = (data[32] & 0xff) + ((data[33] & 0xFF) << 8);
            int latLenght = (data[32+2+longLenght] & 0xff) + ((data[33+2+longLenght] & 0xFF) << 8);
            int shuduLenght = (data[32+4+longLenght+latLenght] & 0xff) + ((data[33+4+longLenght+latLenght] & 0xFF) << 8);
            int altitudeLenght = (data[32+6+longLenght+latLenght+shuduLenght] & 0xff) + ((data[33+6+longLenght+latLenght+shuduLenght] & 0xFF) << 8);
            byte[] altitudeDatas = new byte[altitudeLenght*2];
            if (altitudeLenght!=0)
                System.arraycopy(data,32+8+longLenght+latLenght+shuduLenght,altitudeDatas,0,altitudeLenght*2);
            dataHash = CommandUtil.getAvenrage(altitudeDatas,2);
            for (int key:dataHash.keySet()){
                sportData.altitude = key;
                sportData.altitudeArray = dataHash.get(key);
            }
            int speedLenght = (data[32+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xff) +
                    ((data[33+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xFF) << 8);
            byte[] speedDatas = new byte[speedLenght*2];
            if (speedLenght!=0)
                System.arraycopy(data,32+10+longLenght+latLenght+shuduLenght+altitudeLenght*2,speedDatas,0,speedLenght*2);
            dataHash = CommandUtil.getAvenrage(speedDatas,2);
            for (int key:dataHash.keySet()){
                sportData.speed = key;
                sportData.speedArray = dataHash.get(key);
            }

            int heartLenght = (data[32+10+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2] & 0xff) +
                    ((data[33+10+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,32+12+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2,
                    heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }

            int strideLenght = (data[32+12+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2+heartLenght] & 0xff) +
                    ((data[33+12+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2] & 0xFF+heartLenght) << 8);
            byte[] strideArray = new byte[strideLenght*2];
            if (strideLenght!=0)
                System.arraycopy(data,32+14+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2+heartLenght,
                        strideArray,0,strideLenght*2);
            dataHash = CommandUtil.getAvenrage(strideArray,2);
            for (int key:dataHash.keySet()){
                sportData.stride = key;
                sportData.strideArray = dataHash.get(key);
            }

            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的跑步数据 : "+time+" ;跑步时长 = "+sportData.sportTime+" ;卡路里 = "+sportData.calorie+
                    " ;距离 = "+sportData.distance+" ;平均配速 = "+sportData.speed+" ;配速数组 = "+sportData.speedArray+" ;平均心率 = "+sportData.heart
            +" ;心率数组 = "+sportData.heartArray+" ;平均海拔 = "+sportData.altitude+" ;海拔数组 = "+sportData.altitudeArray);
            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","跑步数据接受结束");
            }


        }else if (type==0x05){//徒步数据
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            sportData.type = 1;
            sportData.time = time;
            sportData.distance = (data[0] & 0xff) + ((data[1] & 0xFF) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xFF) << 24);
            sportData.sportTime = (data[4] & 0xff) + ((data[5] & 0xFF) << 8) + ((data[6] & 0xff) << 16) + ((data[7] & 0xFF) << 24);
            sportData.step = (data[8] & 0xff) + ((data[9] & 0xFF) << 8) + ((data[10] & 0xff) << 16) + ((data[11] & 0xFF) << 24);
            sportData.calorie = (data[12] & 0xff) + ((data[13] & 0xFF) << 8) + ((data[14] & 0xff) << 16) + ((data[15] & 0xFF) << 24);

            int longLenght = (data[28] & 0xff) + ((data[29] & 0xFF) << 8);
            int latLenght = (data[28+2+longLenght] & 0xff) + ((data[29+2+longLenght] & 0xFF) << 8);
            int shuduLenght = (data[28+4+longLenght+latLenght] & 0xff) + ((data[29+4+longLenght+latLenght] & 0xFF) << 8);
            int altitudeLenght = (data[28+6+longLenght+latLenght+shuduLenght] & 0xff) + ((data[29+6+longLenght+latLenght+shuduLenght] & 0xFF) << 8);
            byte[] altitudeDatas = new byte[altitudeLenght*2];
            if (altitudeLenght!=0)
                System.arraycopy(data,28+8+longLenght+latLenght+shuduLenght,altitudeDatas,0,altitudeLenght*2);
            dataHash = CommandUtil.getAvenrage(altitudeDatas,2);
            for (int key:dataHash.keySet()){
                sportData.altitude = key;
                sportData.altitudeArray = dataHash.get(key);
            }
            int speedLenght = (data[28+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xff) +
                    ((data[29+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xFF) << 8);
            byte[] speedDatas = new byte[speedLenght*2];
            if (speedLenght!=0)
                System.arraycopy(data,28+10+longLenght+latLenght+shuduLenght+altitudeLenght*2,speedDatas,0,speedLenght*2);
            dataHash = CommandUtil.getAvenrage(speedDatas,2);
            for (int key:dataHash.keySet()){
                sportData.speed = key;
                sportData.speedArray = dataHash.get(key);
            }
            int heartLenght = (data[28+10+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2] & 0xff) +
                    ((data[29+10+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,28+12+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2,
                    heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }
            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的徒步数据 : "+time+" ;时长 = "+sportData.sportTime+" ;卡路里 = "+sportData.calorie+
                    " ;步数 = "+sportData.step+" ;徒步里程"+sportData.distance+" ;平均配速 = "+sportData.speed+" ;配速数组"+sportData.speedArray+
                    " ;平均心率 = "+sportData.heart+" ;心率数组"+sportData.heartArray+" ;平均海拔 = "+sportData.altitude+" ;海拔数组 = "+sportData.altitudeArray);
            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","徒步数据接受结束");
            }
        }else if (type==0x06){//马拉松
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            sportData.type = 5;
            sportData.time = time;
            sportData.sportTime = (data[8] & 0xff) + ((data[9] & 0xFF) << 8) + ((data[10] & 0xff) << 16) + ((data[11] & 0xFF) << 24);
            sportData.distance = (data[12] & 0xff) + ((data[13] & 0xFF) << 8) + ((data[14] & 0xff) << 16) + ((data[15] & 0xFF) << 24);
            sportData.calorie = (data[16] & 0xff) + ((data[17] & 0xFF) << 8) + ((data[18] & 0xff) << 16) + ((data[19] & 0xFF) << 24);
            int longLenght = (data[32] & 0xff) + ((data[33] & 0xFF) << 8);
            int latLenght = (data[32+2+longLenght] & 0xff) + ((data[33+2+longLenght] & 0xFF) << 8);
            int shuduLenght = (data[32+4+longLenght+latLenght] & 0xff) + ((data[33+4+longLenght+latLenght] & 0xFF) << 8);
            int altitudeLenght = (data[32+6+longLenght+latLenght+shuduLenght] & 0xff) + ((data[33+6+longLenght+latLenght+shuduLenght] & 0xFF) << 8);
            byte[] altitudeDatas = new byte[altitudeLenght*2];
            if (altitudeLenght!=0)
                System.arraycopy(data,32+8+longLenght+latLenght+shuduLenght,altitudeDatas,0,altitudeLenght*2);
            dataHash = CommandUtil.getAvenrage(altitudeDatas,2);
            for (int key:dataHash.keySet()){
                sportData.altitude = key;
                sportData.altitudeArray = dataHash.get(key);
            }
            int speedLenght = (data[32+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xff) +
                    ((data[33+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xFF) << 8);
            byte[] speedDatas = new byte[speedLenght*2];
            if (speedLenght!=0)
                System.arraycopy(data,32+10+longLenght+latLenght+shuduLenght+altitudeLenght*2,speedDatas,0,speedLenght*2);
            dataHash = CommandUtil.getAvenrage(speedDatas,2);
            for (int key:dataHash.keySet()){
                sportData.speed = key;
                sportData.speedArray = dataHash.get(key);
            }
            int heartLenght = (data[32+10+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2] & 0xff) +
                    ((data[33+10+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,32+12+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2,
                    heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }

            int strideLenght = (data[32+12+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2+heartLenght] & 0xff) +
                    ((data[33+12+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2] & 0xFF+heartLenght) << 8);
            byte[] strideArray = new byte[strideLenght*2];
            if (strideLenght!=0)
                System.arraycopy(data,32+14+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2+heartLenght,
                        strideArray,0,strideLenght*2);
            dataHash = CommandUtil.getAvenrage(strideArray,2);
            for (int key:dataHash.keySet()){
                sportData.stride = key;
                sportData.strideArray = dataHash.get(key);
            }

            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的马拉松数据 : "+time+" ;跑步时长 = "+sportData.sportTime+" ;卡路里 = "+sportData.calorie+
                    " ;步数 = "+sportData.distance+" ;平均配速 = "+sportData.speed+" ;配速数组"+sportData.speedArray+
                    " ;平均心率 = "+sportData.heart+" ;心率数组"+sportData.heartArray+" ;平均海拔 = "+sportData.altitude+" ;海拔数组 = "+sportData.altitudeArray);
            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","马拉松数据接受结束");
            }

        }else if (type==0x07){//跳绳
            if (isEnd)
                Log.d("SZIP******","跳绳数据接受结束");

        }else if (type==0x08){//户外游泳
            if (isEnd)
                Log.d("SZIP******","户外游泳数据接受结束");

        }else if (type==0x09){//攀岩
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            sportData.type = 19;
            sportData.time = time;
            sportData.sportTime = (data[2] & 0xff) + ((data[3] & 0xFF) << 8) + ((data[4] & 0xff) << 16) + ((data[5] & 0xFF) << 24);
            sportData.height = (data[0] & 0xff) + ((data[1] & 0xFF) << 8);
            int heartLenght = (data[6] & 0xff) + ((data[7] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,8,
                    heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }
            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的攀岩数据 : "+time+" ;运动时长 = "+sportData.sportTime+" ;攀爬高度"+sportData.height+" ;平均心率 = "+
                    sportData.heart+" ;心率数组 ="+ sportData.heartArray);

            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","攀岩数据接受结束");
            }

        }else if (type==0x0a){//滑雪
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            sportData.type = 12;
            sportData.time = time;
            sportData.distance = (data[0] & 0xff) + ((data[1] & 0xFF) << 8);
            sportData.height = (data[2] & 0xff) + ((data[3] & 0xFF) << 8);
            sportData.sportTime = (data[4] & 0xff) + ((data[5] & 0xFF) << 8) + ((data[6] & 0xff) << 16) + ((data[7] & 0xFF) << 24);
            int longLenght = (data[16] & 0xff) + ((data[17] & 0xFF) << 8);
            int latLenght = (data[16+2+longLenght] & 0xff) + ((data[17+2+longLenght] & 0xFF) << 8);
            int shuduLenght = (data[16+4+longLenght+latLenght] & 0xff) + ((data[17+4+longLenght+latLenght] & 0xFF) << 8);
            int altitudeLenght = (data[16+6+longLenght+latLenght+shuduLenght] & 0xff) + ((data[17+6+longLenght+latLenght+shuduLenght] & 0xFF) << 8);
            byte[] altitudeDatas = new byte[altitudeLenght*2];
            if (altitudeLenght!=0)
                System.arraycopy(data,16+8+longLenght+latLenght+shuduLenght,altitudeDatas,0,altitudeLenght*2);
            dataHash = CommandUtil.getAvenrage(altitudeDatas,2);
            for (int key:dataHash.keySet()){
                sportData.altitude = key;
                sportData.altitudeArray = dataHash.get(key);
            }
            int heartLenght = (data[16+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xff) +
                    ((data[17+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,16+10+longLenght+latLenght+shuduLenght+altitudeLenght*2,
                        heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }
            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的滑雪数据 : "+time+" ;运动时长 = "+sportData.sportTime+" ;爬升高度 = "+sportData.altitude+
                    " ;里程 = "+sportData.distance+" ;平均心率 = "+sportData.heart+" ;心率数组 = "+sportData.heartArray+" ;海拔数组 = "+sportData.altitudeArray);
            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","滑雪数据接受结束");
            }
        }else if (type==0x0b){//骑行
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            sportData.type = 11;
            sportData.time = time;
            sportData.sportTime = (data[14] & 0xff) + ((data[15] & 0xFF) << 8) + ((data[16] & 0xff) << 16) + ((data[17] & 0xFF) << 24);
//            sportData.calorie = (data[16] & 0xff) + ((data[17] & 0xFF) << 8) + ((data[18] & 0xff) << 16) + ((data[19] & 0xFF) << 24);
            sportData.distance = (data[10] & 0xff) + ((data[11] & 0xFF) << 8) + ((data[12] & 0xff) << 16) + ((data[13] & 0xFF) << 24);
            int longLenght = (data[26] & 0xff) + ((data[27] & 0xFF) << 8);
            int latLenght = (data[26+2+longLenght] & 0xff) + ((data[27+2+longLenght] & 0xFF) << 8);
            int shuduLenght = (data[26+4+longLenght+latLenght] & 0xff) + ((data[27+4+longLenght+latLenght] & 0xFF) << 8);
            int altitudeLenght = (data[26+6+longLenght+latLenght+shuduLenght] & 0xff) + ((data[27+6+longLenght+latLenght+shuduLenght] & 0xFF) << 8);
            byte[] altitudeDatas = new byte[altitudeLenght*2];
            if (altitudeLenght!=0)
                System.arraycopy(data,26+8+longLenght+latLenght+shuduLenght,altitudeDatas,0,altitudeLenght*2);
            dataHash = CommandUtil.getAvenrage(altitudeDatas,2);
            for (int key:dataHash.keySet()){
                sportData.altitude = key;
                sportData.altitudeArray = dataHash.get(key);
            }
            int speedLenght = (data[26+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xff) +
                    ((data[27+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xFF) << 8);
            byte[] speedDatas = new byte[speedLenght*2];
            if (speedLenght!=0)
                System.arraycopy(data,26+10+longLenght+latLenght+shuduLenght+altitudeLenght*2,speedDatas,0,speedLenght*2);
            dataHash = CommandUtil.getAvenrage(speedDatas,2);
            for (int key:dataHash.keySet()){
                sportData.speed = key;
                sportData.speedArray = dataHash.get(key);
            }
            int heartLenght = (data[26+10+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2] & 0xff) +
                    ((data[27+10+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,26+12+longLenght+latLenght+shuduLenght+altitudeLenght*2+speedLenght*2,
                        heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }
            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的骑行数据 : "+time+" ;运动时长 = "+sportData.sportTime+
                    " ;距离 = "+sportData.distance+" ;平均配速 = "+sportData.speed+" ;配速数组"+sportData.speedArray+
                    " ;平均心率 = "+sportData.heart+" ;心率数组"+sportData.heartArray+" ;平均海拔 = "+sportData.altitude+" ;海拔数组 = "+sportData.altitudeArray);
            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","骑行数据接受结束");
            }

        }else if (type==0x0c){//划船
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            sportData.type = 20;
            sportData.time = time;
            sportData.distance = (data[0] & 0xff) + ((data[1] & 0xFF) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xFF) << 24);
            sportData.sportTime = (data[4] & 0xff) + ((data[5] & 0xFF) << 8) + ((data[6] & 0xff) << 16) + ((data[7] & 0xFF) << 24);
            sportData.speed = (data[8] & 0xff) + ((data[9] & 0xFF) << 8) + ((data[10] & 0xff) << 16) + ((data[11] & 0xFF) << 24);
            int longLenght = (data[20] & 0xff) + ((data[21] & 0xFF) << 8);
            int latLenght = (data[20+2+longLenght] & 0xff) + ((data[21+2+longLenght] & 0xFF) << 8);
            int shuduLenght = (data[20+4+longLenght+latLenght] & 0xff) + ((data[21+4+longLenght+latLenght] & 0xFF) << 8);
            int heartLenght = (data[20+6+longLenght+latLenght+shuduLenght] & 0xff) +
                    ((data[21+6+longLenght+latLenght+shuduLenght] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,20+8+longLenght+latLenght+shuduLenght,
                        heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }
            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的划船数据 : "+time+" ;运动时长 = "+sportData.sportTime+
                    " ;距离 = "+sportData.distance+" ;划桨频率 = "+sportData.speed+" ;平均心率 = "+sportData.heart+" ;心率数组 = "+sportData.heartArray);

            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","划船数据接受结束");
            }
        }else if (type==0x0d){//蹦极
            if (isEnd)
                Log.d("SZIP******","蹦极数据接受结束");


        }else if (type==0x0e){//登山
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            sportData.type = 4;
            sportData.time = time;
            sportData.distance = (data[0] & 0xff) + ((data[1] & 0xFF) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xFF) << 24);
            sportData.sportTime = (data[4] & 0xff) + ((data[5] & 0xFF) << 8) + ((data[6] & 0xff) << 16) + ((data[7] & 0xFF) << 24);
            sportData.step = (data[8] & 0xff) + ((data[9] & 0xFF) << 8) + ((data[10] & 0xff) << 16) + ((data[11] & 0xFF) << 24);
            sportData.height = (data[12] & 0xff) + ((data[13] & 0xFF) << 8) + ((data[14] & 0xff) << 16) + ((data[15] & 0xFF) << 24);
            int longLenght = (data[24] & 0xff) + ((data[25] & 0xFF) << 8);
            int latLenght = (data[24+2+longLenght] & 0xff) + ((data[25+2+longLenght] & 0xFF) << 8);
            int shuduLenght = (data[24+4+longLenght+latLenght] & 0xff) + ((data[25+4+longLenght+latLenght] & 0xFF) << 8);
            int altitudeLenght = (data[24+6+longLenght+latLenght+shuduLenght] & 0xff) + ((data[25+6+longLenght+latLenght+shuduLenght] & 0xFF) << 8);
            byte[] altitudeDatas = new byte[altitudeLenght*2];
            if (altitudeLenght!=0)
                System.arraycopy(data,24+8+longLenght+latLenght+shuduLenght,altitudeDatas,0,altitudeLenght*2);
            dataHash = CommandUtil.getAvenrage(altitudeDatas,2);
            for (int key:dataHash.keySet()){
                sportData.altitude = key;
                sportData.altitudeArray = dataHash.get(key);
            }
            int heartLenght = (data[24+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xff) +
                    ((data[25+8+longLenght+latLenght+shuduLenght+altitudeLenght*2] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,24+10+longLenght+latLenght+shuduLenght+altitudeLenght*2,
                        heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }
            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的登山数据 : "+time+" ;运动时长 = "+sportData.sportTime+" ;记步数 = "+sportData.step+
                    " ;攀爬高度"+sportData.altitude+" ;距离 = "+sportData.distance+" ;平均心率 = "+sportData.heart+" ;心率数组"+sportData.heartArray
            +" ;海拔数组 = "+sportData.altitudeArray);

            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","登山数据接受结束");
            }

        }else if (type==0x0f){//跳伞
            if (isEnd)
                Log.d("SZIP******","跳伞数据接受结束");


        }else if (type==0x10){//高尔夫
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            sportData.type = 21;
            sportData.time = time;
            sportData.pole = (data[0] & 0xff) + ((data[1] & 0xFF) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xFF) << 24);
            sportData.sportTime = (data[4] & 0xff) + ((data[5] & 0xFF) << 8) + ((data[6] & 0xff) << 16) + ((data[7] & 0xFF) << 24);
            sportData.step = (data[8] & 0xff) + ((data[9] & 0xFF) << 8) + ((data[10] & 0xff) << 16) + ((data[11] & 0xFF) << 24);
            int longLenght = (data[20] & 0xff) + ((data[21] & 0xFF) << 8);
            int latLenght = (data[20+2+longLenght] & 0xff) + ((data[21+2+longLenght] & 0xFF) << 8);
            int altitudeLenght = (data[20+4+longLenght+latLenght] & 0xff) + ((data[21+6+longLenght+latLenght] & 0xFF) << 8);
            byte[] altitudeDatas = new byte[altitudeLenght*2];
            if (altitudeLenght!=0)
                System.arraycopy(data,20+6+longLenght+latLenght,altitudeDatas,0,altitudeLenght*2);
            dataHash = CommandUtil.getAvenrage(altitudeDatas,2);
            for (int key:dataHash.keySet()){
                sportData.altitude = key;
                sportData.altitudeArray = dataHash.get(key);
            }
            int heartLenght = (data[20+6+longLenght+latLenght+altitudeLenght*2] & 0xff) +
                    ((data[21+6+longLenght+latLenght+altitudeLenght*2] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,20+8+longLenght+latLenght+altitudeLenght*2,
                        heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }
            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的高尔夫数据 : "+time+" ;运动时长 = "+sportData.sportTime+" ;挥杆次数 = "+sportData.pole+
                   " ;计步数据 = "+sportData.step+" ;平均心率 = "+sportData.heart+" ;心率数组 = "+sportData.heartArray+" ;海拔数组 = "+sportData.altitudeArray);

            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","高尔夫数据接受结束");
            }

        }else if (type==0x11){//冲浪
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            sportData.type = 22;
            sportData.time = time;
            sportData.distance = (data[0] & 0xff) + ((data[1] & 0xFF) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xFF) << 24);
            sportData.sportTime = (data[4] & 0xff) + ((data[5] & 0xFF) << 8) + ((data[6] & 0xff) << 16) + ((data[7] & 0xFF) << 24);
            int longLenght = (data[16] & 0xff) + ((data[17] & 0xFF) << 8);
            int latLenght = (data[16+2+longLenght] & 0xff) + ((data[17+2+longLenght] & 0xFF) << 8);
            int shuduLenght = (data[16+4+longLenght+latLenght] & 0xff) + ((data[17+4+longLenght+latLenght] & 0xFF) << 8);
            int heartLenght = (data[16+6+longLenght+latLenght+shuduLenght] & 0xff) +
                    ((data[17+6+longLenght+latLenght+shuduLenght] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,16+8+longLenght+latLenght+shuduLenght,
                        heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }
            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的登山数据 : "+time+" ;运动时长 = "+sportData.sportTime+
                    " ;距离 = "+sportData.distance+" ;平均心率 = "+sportData.heart);

            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","冲浪数据接受结束");
            }
        }else if (type==0x14){//跑步机
            if(sportDataArrayList==null)
                sportDataArrayList = new ArrayList<>();
            HashMap<Integer,String> dataHash;
            SportData sportData = new SportData();
            sportData.type = 3;
            sportData.time = time;
            sportData.sportTime = (data[0] & 0xff) + ((data[1] & 0xFF) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xFF) << 24);
            sportData.step = (data[4] & 0xff) + ((data[5] & 0xFF) << 8) + ((data[6] & 0xff) << 16) + ((data[7] & 0xFF) << 24);
            sportData.calorie = (data[8] & 0xff) + ((data[9] & 0xFF) << 8) + ((data[10] & 0xff) << 16) + ((data[11] & 0xFF) << 24);
            int strideLenght = (data[12] & 0xff) + ((data[13] & 0xFF) << 8);
            byte[] strideDatas = new byte[strideLenght*2];
            if (strideLenght!=0)
                System.arraycopy(data,12+2,
                        strideDatas,0,strideLenght*2);
            dataHash = CommandUtil.getAvenrage(strideDatas,2);
            for (int key:dataHash.keySet()){
                sportData.stride = key;
                sportData.strideArray = dataHash.get(key);
            }
            int heartLenght = (data[12+2+strideLenght*2] & 0xff) +
                    ((data[12+2+strideLenght*2] & 0xFF) << 8);
            byte[] heartDatas = new byte[heartLenght];
            if (heartLenght!=0)
                System.arraycopy(data,12+4+strideLenght*2,
                        heartDatas,0,heartLenght);
            dataHash = CommandUtil.getAvenrage(heartDatas,1);
            for (int key:dataHash.keySet()){
                sportData.heart = key;
                sportData.heartArray = dataHash.get(key);
            }
            sportDataArrayList.add(sportData);
            Log.d("SZIP******","解析到的跑步机数据 : "+time+" ;运动时长 = "+sportData.sportTime+" ;卡路里 = "+sportData.calorie+
                    " ;步频 = "+sportData.stride+" ;步频数组 = "+sportData.strideArray+" ;平均心率 = "+sportData.heart+" ;心率数组 = "+sportData.heartArray);

            if (isEnd){
                if (mIDataResponse!=null)
                    mIDataResponse.onSaveRunDatas(sportDataArrayList);
                sportDataArrayList = null;
                Log.d("SZIP******","跑步机数据接受结束");
            }


        }
    }

}
