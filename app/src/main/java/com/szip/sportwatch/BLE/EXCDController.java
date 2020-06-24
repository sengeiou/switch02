
package com.szip.sportwatch.BLE;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.util.Log;

import com.mediatek.ctrl.music.RemoteMusicController;
import com.mediatek.wearable.Controller;
import com.szip.sportwatch.Contorller.CameraActivity;
import com.szip.sportwatch.Interface.OnCameraListener;
import com.szip.sportwatch.Interface.ReviceDataCallback;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.Model.HttpBean.WeatherBean;
import com.szip.sportwatch.Model.UserInfo;
import com.szip.sportwatch.Model.WeatherModel;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.Util.DateUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EXCDController extends Controller {

//    private String cmdHead = "KCT_PEDOMETER kct_pedometer ";
    private String cmdHead = "ZNSD_WATCH znsd_watch ";

    private static final String sControllerTag = "EXCDController";

    private static final String TAG = "EXCDControllerSZIP******";

    private static EXCDController mInstance;

    private Context mContext = MyApplication.getInstance().getApplicationContext();

    private ReviceDataCallback reviceDataCallback;

    public static final String EXTRA_DATA = "EXTRA_DATA";

    private ArrayList<String> sportList = new ArrayList<>();//运动数据索引链表，长度为0则代表无索引数据
    private ArrayList<String> steps;
    private ArrayList<String> sleeps;
    private ArrayList<String> ecgs;
    private ArrayList<String> sports;

    private OnCameraListener onCameraListener;



    private EXCDController() {
        super(sControllerTag, CMD_9);
    }

    public static EXCDController getInstance() {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new EXCDController();
        }
        return mInstance;
    }

    public void setReviceDataCallback(ReviceDataCallback reviceDataCallback) {
        this.reviceDataCallback = reviceDataCallback;
    }

    public void setOnCameraListener(OnCameraListener onCameraListener) {
        this.onCameraListener = onCameraListener;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Override
    public void onConnectionStateChange(int state) {
        super.onConnectionStateChange(state);
    }

    @Override
    public void send(String cmd, byte[] dataBuffer, boolean response, boolean progress, int priority) {
        try {
            super.send(cmd, dataBuffer, response, progress, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(byte[] dataBuffer) {
        super.onReceive(dataBuffer);
        String command = new String(dataBuffer);
        String[] commands = command.split(",");

        Log.i(TAG, "onReceive(), command :" + command);

        if (commands[0].contains("GET")){//GET指令
            if (commands[1].equals("0")){//心跳包
                String step[] = commands[5].split("\\|");
                String sleep[] = commands[6].split("\\|");
                String heart = commands[7];
                String bloodPressure = commands[15];
                String bloodOxygen = commands[16];
                String ecg = commands[22];
                String animalHeat = null;
                if(commands.length>23)
                    animalHeat = commands[23];
                if (reviceDataCallback!=null)
                    reviceDataCallback.checkVersion(!step[0].equals("0"),!step[1].equals("0"),
                            !sleep[0].equals("0"),!sleep[1].equals("0"),!heart.equals("0"),
                            !bloodPressure.equals("0"),!bloodOxygen.equals("0"),!ecg.equals("0"),(animalHeat==null)?false:!animalHeat.endsWith("0"),commands[17]);
            }else if (commands[1].equals("10")){//同步计步数据
                if (commands.length>2){//有数据
                    String datas[] = new String[commands.length-2];
                    System.arraycopy(commands,2,datas,0,datas.length);
                    if (reviceDataCallback!=null)
                        reviceDataCallback.getStepsForDay(datas);
                    writeForRET("GET,"+commands[1]);
                }
            }else if (commands[1].equals("11")){//同步计步详情数据
                if (commands.length>4){//有数据
                    if (steps == null){
                        steps = new ArrayList<>();
                        steps.add(command);
                    }else {
                        steps.add(command);
                    }
                    if (commands[2].equals(commands[3])){//接收结束
                        StringBuffer str = new StringBuffer();
                        for (int i = 0;i<steps.size();i++){
                            String strs[] = steps.get(i).split(",");
                            str.append(steps.get(i).substring(strs[0].length()+strs[1].length()+strs[2].length()+
                                    strs[3].length()+4));
                        }
                        if (reviceDataCallback!=null)
                            reviceDataCallback.getSteps(str.toString().split(","));
                        steps = null;
                    }
                    writeForRET("GET,"+commands[1]+","+commands[2]+","+commands[3]);
                }
            }else if (commands[1].equals("12")){//接收睡眠数据
                if (commands.length>2){//有数据
                    String datas[] = new String[commands.length-2];
                    System.arraycopy(commands,2,datas,0,datas.length);
                    if (reviceDataCallback!=null)
                        reviceDataCallback.getSleepForDay(datas);
                    writeForRET("GET,"+commands[1]);
                }
            }else if (commands[1].equals("13")){//接收睡眠详情数据
                if (commands.length>4){//有数据
                    if (sleeps == null){
                        sleeps = new ArrayList<>();
                        sleeps.add(command);
                    }else {
                        sleeps.add(command);
                    }
                    if (commands[2].equals(commands[3])){//接收结束
                        StringBuffer str = new StringBuffer();
                        for (int i = 0;i<sleeps.size();i++){
                            String strs[] = sleeps.get(i).split(",");
                            str.append(sleeps.get(i).substring(strs[0].length()+strs[1].length()+strs[2].length()+
                                    strs[3].length()+4));
                        }
                        if (reviceDataCallback!=null)
                            reviceDataCallback.getSleep(str.toString().split(","));
                        sleeps = null;
                    }
                    writeForRET("GET,"+commands[1]+","+commands[2]+","+commands[3]);
                }
            }else if (commands[1].equals("14")){//接受心率数据
                if (commands.length>2){//有数据
                    String datas[] = new String[commands.length-2];
                    System.arraycopy(commands,2,datas,0,datas.length);
                    if (reviceDataCallback!=null)
                        reviceDataCallback.getHeart(datas);
                    writeForRET("GET,"+commands[1]);
                }
            }else if (commands[1].equals("17")){
                for (int i = 2;i<commands.length;i++){//把收到的索引统计起来
                    sportList.add(commands[i]);
                }
                if (sportList.size()!=0){//如果索引不等于0，则拿第一个数据，拿完即刻删掉
                    writeForSport(sportList.get(0));
                    sportList.remove(0);
                }else {
                    EventBus.getDefault().post(new UpdateReport());
                }
            }else if (commands[1].equals("18")){
                if (commands.length>6){//有数据
                    if (sports == null){
                        sports = new ArrayList<>();
                        sports.add(command);
                    }else {
                        sports.add(command);
                    }
                    if (commands[3].equals(commands[4])){//接收结束
                        StringBuffer str = new StringBuffer();
                        for (int i = 0;i<sports.size();i++){
                            String strs[] = sports.get(i).split(",");
                            str.append(sports.get(i).substring(strs[0].length()+strs[1].length()+strs[2].length()+
                                    strs[3].length()+strs[4].length()+5));
                        }
                        if (reviceDataCallback!=null)
                            reviceDataCallback.getSport(str.toString().split(","));
                        if (sportList.size()!=0){
                            writeForSport(sportList.get(0));
                            sportList.remove(0);
                        }else {
                            EventBus.getDefault().post(new UpdateReport());
                        }
                        sports = null;
                    }
                    writeForRET("GET,"+commands[1]+","+commands[2]+","+commands[3]+","+commands[4]);
                }else {
                    if (sportList.size()!=0){
                        writeForSport(sportList.get(0));
                        sportList.remove(0);
                    }else {
                        EventBus.getDefault().post(new UpdateReport());
                    }
                    writeForRET("GET,"+commands[1]+","+commands[2]+","+commands[3]+","+commands[4]);
                }
            }else if (commands[1].equals("20")){//接受心电数据
                if (commands.length>4){//有数据
                    if (ecgs == null){
                        ecgs = new ArrayList<>();
                        ecgs.add(command);
                    }else {
                        ecgs.add(command);
                    }
                    if (commands[2].equals(commands[3])){//接收结束
                        StringBuffer str = new StringBuffer();
                        for (int i = 0;i<ecgs.size();i++){
                            String strs[] = ecgs.get(i).split(",");
                            str.append(ecgs.get(i).substring(strs[0].length()+strs[1].length()+strs[2].length()+
                                    strs[3].length()+5));
                        }
                        if (reviceDataCallback!=null)
                            reviceDataCallback.getEcg(str.toString().split("#"));
                        ecgs = null;
                    }
                    writeForRET("GET,"+commands[1]+","+commands[2]+","+commands[3]);
                }
            }else if (commands[1].equals("51")){//接受血压数据
                if (commands.length>2){//有数据
                    String datas[] = new String[commands.length-2];
                    System.arraycopy(commands,2,datas,0,datas.length);
                    if (reviceDataCallback!=null)
                        reviceDataCallback.getBloodPressure(datas);
                    writeForRET("GET,"+commands[1]);
                }
            }else if (commands[1].equals("52")){//接受血氧数据
                if (commands.length>2){//有数据
                    String datas[] = new String[commands.length-2];
                    System.arraycopy(commands,2,datas,0,datas.length);
                    if (reviceDataCallback!=null)
                        reviceDataCallback.getBloodOxygen(datas);
                    writeForRET("GET,"+commands[1]);
                }
            }else if (commands[1].equals("80")){//接受血氧数据
                if (commands.length>2){//有数据
                    String datas[] = new String[commands.length-2];
                    System.arraycopy(commands,2,datas,0,datas.length);
                    if (reviceDataCallback!=null)
                        reviceDataCallback.getAnimalHeat(datas);
                    writeForRET("GET,"+commands[1]);
                }
            }
        }else if (commands[0].contains("SET")){//SET指令
            if (commands[1].equals("14")){//操作相机指令
                if (((MyApplication)mContext.getApplicationContext()).isCamerable())
                if (commands[2].equals("1")){//打开相机
                    Intent intent1=new Intent(mContext, CameraActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
                }else if (commands[2].equals("0")){//关闭相机
                    if (onCameraListener!=null)
                        onCameraListener.onCamera(0);
                }else {//拍照
                    if (onCameraListener!=null)
                        onCameraListener.onCamera(1);
                }
                writeForRET("SET,"+commands[1]+","+commands[2]);
            }else if (commands[1].equals("40")){//找手机
                if (commands[2].equals("1")){//开始找手机
                   if (reviceDataCallback!=null)
                       reviceDataCallback.findPhone(1);
                }else {
                    if (reviceDataCallback!=null)
                        reviceDataCallback.findPhone(0);
                }
            }else if (commands[1].equals("43")){//收到音乐播放指令，转换成music可识别的指令
                String str = "mtk_msctrl msctrl_apk 0 1 ";
                String cmd = command.split(",")[2];
                str = str+cmd+" 1 FF";
                byte[] datas = new byte[0];
                try {
                    datas = str.getBytes("ASCII");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                RemoteMusicController.getInstance(mContext).onReceive(datas);
                if (cmd.equals("2")){
                    writeForRET("SET,"+commands[1]+",0");
                }else
                    writeForRET("SET,"+commands[1]+",1");
            }
        }else if (commands[0].contains("SEND")){//SEND指令

        }
    }




    /**
     * 应用指令集
     * */
    //心跳包
    public void writeForCheckVersion(){
        String str = "GET,0";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 5 ",datas,true,false,0);
    }
    //日均计步数据
    public void writeForGetDaySteps(){
        String str = "GET,10";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 6 ",datas,true,false,0);
    }
    //日详情计步数据
    public void writeForGetSteps(){
        String str = "GET,11";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 6 ",datas,true,false,0);
    }
    //日均睡眠数据
    public void writeForGetDaySleep(){
        String str = "GET,12";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 6 ",datas,true,false,0);
    }
    //日详情睡眠
    public void writeForGetSleep(){
        String str = "GET,13";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 6 ",datas,true,false,0);
    }
    //获取心率
    public void writeForGetHeart(){
        String str = "GET,14";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 6 ",datas,true,false,0);
    }
    //获取运动数据索引
    public void writeForGetSportPosition(){
        String str = "GET,17";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 8 ",datas,true,false,0);
    }
    //获取运动数据
    public void writeForGetSportData(int index){
        String str = String.format("GET,18,%d",index);
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 8 ",datas,true,false,0);
    }
    //获取血压数据
    public void writeForGetBloodPressure(){
        String str = "GET,51";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 6 ",datas,true,false,0);
    }
    //获取血氧数据
    public void writeForGetBloodOxygen(){
        String str = "GET,52";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 6 ",datas,true,false,0);
    }
    //获取ecg数据
    public void writeForGetEcg(){
        String str = "GET,20";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 6 ",datas,true,false,0);
    }

    //获取体温数据
    public void writeForGetAnimalHeat(){
        String str = "GET,80";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 6 ",datas,true,false,0);
    }

    //设置时间
    public void writeForSetDate(){
        int gmt = DateUtil.getGMT();
        String str = "SET,45,1|"+(gmt>0?"+":"-")+String.format("%04.1f|%d",Math.abs(gmt)/60f, Calendar.getInstance().getTimeInMillis()/1000);
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+String.format("0 0 %d ",str.length()),datas,true,false,0);
    }

    public void writeForSetUnit(UserInfo info){
        String str = "SET,35,"+(info.getUnit().equals("metric")?"0":"1");
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 8 ",datas,true,false,0);
    }

    //设置个人信息
    public void writeForSetInfo(UserInfo info){
        String height = info.getHeight();
        if (height!=null)
            height = height.substring(0,height.length()-2);
        String weight = info.getWeight();
        if (weight!=null)
            weight = weight.substring(0,weight.length()-2);
        String str = "SET,10,"+String.format("%d|%d|",info.getStepsPlan(),info.getSex())+
                (height==null?"100":height)+"|"+(weight==null?"100":weight);
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+String.format("0 0 %d ",str.length()),datas,true,false,0);
    }

    //找手表
    public void writeForFindDevice(){
        String str = "SET,40,1";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 8 ",datas,true,false,0);
    }

    //获取运动指引
    public void writeForSportIndex(){
        String str = "GET,17";
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 6 ",datas,true,false,0);
    }

    //获取运动数据
    public void writeForSport(String index){
        String str = "GET,18,"+index;
        Log.d("SZIP******","sport index = "+index);
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+"0 0 8 ",datas,true,false,0);
    }

    //同步天气
    public void writeForUpdateWeather(ArrayList<WeatherBean.Condition> weatherModel,String city){

        String str;
        if (weatherModel!=null){
            str = "WEATHER;"+city+";0,"+DateUtil.getStringDateFromSecond
                    (Calendar.getInstance().getTimeInMillis()/1000,"yyyy-MM-dd")+","+((int)weatherModel.get(0).getLow())+
                    ","+((int)weatherModel.get(0).getHigh())+","+weatherModel.get(0).getCode()+"|1,"+DateUtil.getStringDateFromSecond
                    (Calendar.getInstance().getTimeInMillis()/1000+24*60*60,"yyyy-MM-dd")+","+((int)weatherModel.get(1).getLow())+
                    ","+((int)weatherModel.get(1).getHigh())+","+weatherModel.get(1).getCode()+"|2,"+DateUtil.getStringDateFromSecond
                    (Calendar.getInstance().getTimeInMillis()/1000+24*60*60*2,"yyyy-MM-dd")+","+((int)weatherModel.get(2).getLow())+
                    ","+((int)weatherModel.get(2).getHigh())+","+weatherModel.get(2).getCode();

            byte[] datas = new byte[0];
//        try {
            datas = str.getBytes();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        for (int i = 0; i < datas.length; i++) {
//            // 将每个字符转换成ASCII码
//            datas.append(Integer.toHexString(bGBK[i] & 0xff) + " ");
//        }
            Log.d("SZIP******","DATA = "+new String(datas));
            this.send(cmdHead+String.format("0 0 %d ",datas.length),datas,true,false,0);
        }
    }

    private   String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        // 将字符串转换成字节序列
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            // 将每个字符转换成ASCII码
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff) + " ");
        }
        return strBuf.toString();
    }

    //回复指令
    public void writeForRET(String tag){
        String str = "RET,"+tag;
        byte[] datas = new byte[0];
        try {
            datas = str.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.send(cmdHead+String.format("0 0 %d ",str.length()),datas,true,false,0);
    }
}
