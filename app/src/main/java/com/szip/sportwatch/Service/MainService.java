package com.szip.sportwatch.Service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.mediatek.ctrl.map.MapController;
import com.mediatek.ctrl.music.RemoteMusicController;
import com.mediatek.ctrl.notification.NotificationController;
import com.mediatek.wearable.WearableListener;
import com.mediatek.wearable.WearableManager;
import com.szip.sportwatch.BLE.BleClient;
import com.szip.sportwatch.DB.SaveDataUtil;
import com.szip.sportwatch.DB.dbModel.AnimalHeatData;
import com.szip.sportwatch.DB.dbModel.BloodOxygenData;
import com.szip.sportwatch.DB.dbModel.BloodPressureData;
import com.szip.sportwatch.DB.dbModel.EcgData;
import com.szip.sportwatch.DB.dbModel.HeartData;
import com.szip.sportwatch.DB.dbModel.SleepData;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.DB.dbModel.StepData;
import com.szip.sportwatch.Interface.ReviceDataCallback;
import com.szip.sportwatch.Model.EvenBusModel.ConnectState;
import com.szip.sportwatch.Model.UpdateSportView;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.Notification.AppList;
import com.szip.sportwatch.Notification.NotificationReceiver;
import com.szip.sportwatch.Notification.NotificationService;
import com.szip.sportwatch.Notification.SmsService;
import com.szip.sportwatch.Notification.SystemNotificationService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Map;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by Administrator on 2019/12/27.
 */

public class MainService extends Service {

    // Debugging
    private static final String TAG = "AppManager/MainService";

    // Global instance
    private static MainService mSevice = null;

    // Application context
    private static final Context sContext = MyApplication.getInstance()
            .getApplicationContext();

    // Flag to indicate whether main service has been start
    private static boolean mIsMainServiceActive = false;

    private boolean mIsSmsServiceActive = false;


    // Register and unregister SMS service dynamically
    private SmsService mSmsService = null;

    private SystemNotificationService mSystemNotificationService = null;


    private NotificationService mNotificationService = null;

    private Thread heartThread;//心跳包线程
    private Thread connectThread;//回连线程
    private boolean isThreadRun = true;

    private MediaPlayer mediaPlayer;

    private int errorTimes = 0;//用于统计连接失败次数，如果连接失败次数太多，则提示用户重启手表

    private boolean connectAble = false;//判断是否需要连接


    private MyApplication app;

    private int volume = 0;



    public int getState() {
        return app.isMtk()?WearableManager.getInstance().getConnectState(): BleClient.getInstance().getConnectState();
    }

    public void setConnectAble(boolean connectAble) {
        this.connectAble = connectAble;
    }

    private WearableListener mWearableListener = new WearableListener() {

        @Override
        public void onConnectChange(int oldState, int newState) {
            if (app.isMtk()){
                Log.d("SZIP******","STATE = "+newState);
                EventBus.getDefault().post(new ConnectState(newState));
                if (newState == WearableManager.STATE_CONNECTED){//连接成功，发送同步数据指令
                    startThread();//使能线程
                    errorTimes = 0;
                    String str = getResources().getConfiguration().locale.getLanguage();
                    if (str.equals("en"))
                        EXCDController.getInstance().writeForSetLanuage("en_US");
                    else if (str.equals("de"))
                        EXCDController.getInstance().writeForSetLanuage("de_DE");
                    else if (str.equals("fr"))
                        EXCDController.getInstance().writeForSetLanuage("fr_FR");
                    else if (str.equals("it"))
                        EXCDController.getInstance().writeForSetLanuage("it_IT");
                    else if (str.equals("es"))
                        EXCDController.getInstance().writeForSetLanuage("es_ES");
                    else if (str.equals("pt"))
                        EXCDController.getInstance().writeForSetLanuage("pt_PT");
                    else if (str.equals("tr"))
                        EXCDController.getInstance().writeForSetLanuage("tr_TR");
                    else if (str.equals("ru"))
                        EXCDController.getInstance().writeForSetLanuage("ru_RU");
                    else if (str.equals("ar"))
                        EXCDController.getInstance().writeForSetLanuage("ar_SA");
                    else if (str.equals("th"))
                        EXCDController.getInstance().writeForSetLanuage("th_TH");
                    else if (str.equals("zh"))
                        EXCDController.getInstance().writeForSetLanuage("zh_CN");
                    EXCDController.getInstance().writeForEnableSend(1);
                    EXCDController.getInstance().writeForSetDate();
                    EXCDController.getInstance().writeForSetInfo(app.getUserInfo());
                    EXCDController.getInstance().writeForSetUnit(app.getUserInfo());
                    EXCDController.getInstance().writeForCheckVersion();
                    EXCDController.getInstance().writeForUpdateWeather(app.getWeatherModel(),
                            app.getCity());
                }else if (newState == WearableManager.STATE_CONNECT_LOST){
                    if (errorTimes<3){
                        errorTimes++;
                    } else{
                        errorTimes = 0;
//                        Looper.prepare();
//                        Toast.makeText(mSevice,getString(R.string.lineError),Toast.LENGTH_SHORT).show();
//                        Looper.loop();
                    }
                }
            }
        }

        @Override
        public void onDeviceChange(BluetoothDevice device) {
            return;
        }

        @Override
        public void onDeviceScan(BluetoothDevice device) {
            if (connectAble){
                if (device.getAddress().equals(app.getUserInfo().getDeviceCode())){
                    connectAble = false;
                    Log.d("SZIP******","正在搜索="+device.getAddress());
                    WearableManager.getInstance().scanDevice(false);
                    WearableManager.getInstance().setRemoteDevice(device);
                    startConnect();
                }
            }
        }

        @Override
        public void onModeSwitch(int newMode) {
            Log.d(TAG, "onModeSwitch newMode = " + newMode);
        }
    };


    /**
     * 处理手表返回来的数据
     * */
    private ReviceDataCallback reviceDataCallback = new ReviceDataCallback() {
        @Override
        public void checkVersion(boolean stepNum, boolean deltaStepNum, boolean sleepNum, boolean deltaSleepNum,
                                 boolean heart, boolean bloodPressure, boolean bloodOxygen,boolean ecg,boolean animalHeat,String deviceNum) {
            Log.d("SZIP******","收到心跳包step = "+stepNum+" ;stepD = "+deltaStepNum+" ;sleep = "+sleepNum+
                    " ;sleepD = "+deltaSleepNum+" ;heart = "+heart+ " ;bloodPressure = "+bloodPressure+
                    " ;bloodOxygen = "+heart+" ;ecg = "+ecg+" ;animalHeat = "+animalHeat);
            if (stepNum)
                EXCDController.getInstance().writeForGetDaySteps();
            if (deltaStepNum)
                EXCDController.getInstance().writeForGetSteps();
            if (sleepNum)
                EXCDController.getInstance().writeForGetDaySleep();
            if (deltaSleepNum)
                EXCDController.getInstance().writeForGetSleep();
            if (heart)
                EXCDController.getInstance().writeForGetHeart();
            if (bloodPressure)
                EXCDController.getInstance().writeForGetBloodPressure();
            if (bloodOxygen)
                EXCDController.getInstance().writeForGetBloodOxygen();
            if (ecg)
                EXCDController.getInstance().writeForGetEcg();
            if (animalHeat)
                EXCDController.getInstance().writeForGetAnimalHeat();

            if(app.getDeviceNum()!=deviceNum){
                app.setDeviceNum(deviceNum);
                EventBus.getDefault().post(new UpdateSportView());
            }

        }

        @Override
        public void getStepsForDay(String[] stepsForday) {
            Log.d("SZIP******","收到计步数据条数 = "+stepsForday.length);
            ArrayList<StepData> dataArrayList = new ArrayList<>();
            for (int i =0;i<stepsForday.length;i++){
                String datas[] = stepsForday[i].split("\\|");
                long time = DateUtil.getTimeScopeForDay(datas[0],"yyyy-MM-dd");
                int steps = Integer.valueOf(datas[1]);
                int distance = Integer.valueOf(datas[2]);
                int calorie = Integer.valueOf(datas[3]);
                Log.d("SZIP******","计步数据 = "+"time = "+time+" ;steps = "+steps+" ;distance = "+distance+" ;calorie = "+calorie);
                dataArrayList.add(new StepData(time,steps,distance,calorie,null));
            }
            SaveDataUtil.newInstance().saveStepDataListData(dataArrayList);
        }

        @Override
        public void getSteps(String[] steps) {
            Log.d("SZIP******","详情计步数据条数 = "+steps.length);
            ArrayList<StepData> dataArrayList = new ArrayList<>();
            ArrayList<String> list = null;//用来保存同一天的数据的数组
            String date = null;//用来判断现在保存数据的日期
            for (int i =0;i<steps.length;i++){//遍历数组，把不同日期的计步数据分开
                if (list == null){//如果当天的数据为空，则开一个新的数组用来保存这一天的数据
                    list = new ArrayList<>();
                    list.add(steps[i]);
                    date = steps[i].split("\\|")[0];//缓存需要保存数据的时间
                }else {
                    if (steps[i].split("\\|")[0].equals(date)){//如果是同一天的数据，则加入数组
                        list.add(steps[i]);
                    }else {//如果不是同一天的数据，则统计当天的数据，并把list置为null,date保存新一天的日期
                        dataArrayList.add(MathUitl.mathStepDataForDay(list));
                        list = new ArrayList<>();
                        list.add(steps[i]);
                        date = steps[i].split("\\|")[0];
                    }
                }
            }
            if (list!=null)
                dataArrayList.add(MathUitl.mathStepDataForDay(list));
            SaveDataUtil.newInstance().saveStepInfoDataListData(dataArrayList);
        }

        @Override
        public void getSleepForDay(String[] sleepForday) {
            Log.d("SZIP******","收到睡眠数据条数 = "+sleepForday.length);
            ArrayList<SleepData> dataArrayList = new ArrayList<>();
            for (int i =0;i<sleepForday.length;i++){
                String datas[] = sleepForday[i].split("\\|");
                long time = DateUtil.getTimeScopeForDay(datas[0],"yyyy-MM-dd")+24*60*60;
                int deepTime = DateUtil.getMinue(datas[1]);
                int lightTime = DateUtil.getMinue(datas[2]);
                Log.d("SZIP******","睡眠数据 = "+"time = "+time+" ;deep = "+deepTime+" ;light = "+lightTime);
                dataArrayList.add(new SleepData(time,deepTime,lightTime,null));
            }
            SaveDataUtil.newInstance().saveSleepDataListData(dataArrayList);
        }

        @Override
        public void getSleep(String[] sleep) {
            Log.d("SZIP******","收到详情睡眠数据条数 = "+sleep.length);
            ArrayList<SleepData> dataArrayList = new ArrayList<>();
            ArrayList<String> list = null;//用来保存同一天的数据的数组
            String date = null;//用来判断现在保存数据的日期
            String sleepDate = null;
            for (int i =0;i<sleep.length;i++){//遍历数组，把不同日期的睡眠数据分开
                sleepDate = DateUtil.getSleepDate(sleep[i]);//判断这个时间的数据是属于哪天的睡眠
                if (list == null){//如果当天的数据为空，则开一个新的数组用来保存这一天的数据
                    list = new ArrayList<>();
                    list.add(sleep[i]);
                    date = sleepDate;//缓存需要保存数据的时间
                }else {
                    if (sleepDate.equals(date)){//如果是同一天的数据，则加入数组
                        list.add(sleep[i]);
                    }else {//如果不是同一天的数据，则统计当天的数据，并把list置为null,date保存新一天的日期
                        dataArrayList.add(MathUitl.mathSleepDataForDay(list,date));
                        list = new ArrayList<>();
                        list.add(sleep[i]);
                        date = sleepDate;
                    }
                }
            }
            if (list!=null)
                dataArrayList.add(MathUitl.mathSleepDataForDay(list,date));
            SaveDataUtil.newInstance().saveSleepInfoDataListData(dataArrayList);
        }

        @Override
        public void getHeart(String[] heart) {
            Log.d("SZIP******","收到心率数据条数 = "+heart.length);
            ArrayList<HeartData> dataArrayList = new ArrayList<>();
            ArrayList<String> list = null;//用来保存同一天的数据的数组
            String date = null;//用来判断现在保存数据的日期
            for (int i =0;i<heart.length;i++){//遍历数组，把不同日期的计步数据分开
                if (list == null){//如果当天的数据为空，则开一个新的数组用来保存这一天的数据
                    list = new ArrayList<>();
                    list.add(heart[i]);
                    date = heart[i].split(" ")[0];//缓存需要保存数据的时间
                }else {
                    if (heart[i].split(" ")[0].equals(date)){//如果是同一天的数据，则加入数组
                        list.add(heart[i]);
                    }else {//如果不是同一天的数据，则统计当天的数据，并把list置为null,date保存新一天的日期
                        dataArrayList.add(MathUitl.mathHeartDataForDay(list));
                        list = new ArrayList<>();
                        list.add(heart[i]);
                        date = heart[i].split(" ")[0];
                    }
                }
            }
            if (list!=null)
                dataArrayList.add(MathUitl.mathHeartDataForDay(list));
            SaveDataUtil.newInstance().saveHeartDataListData(dataArrayList,true);
        }

        @Override
        public void getBloodPressure(String[] bloodPressure) {
            Log.d("SZIP******","收到血压数据条数 = "+bloodPressure.length);
            ArrayList<BloodPressureData> dataArrayList = new ArrayList<>();
            for (int i =0;i<bloodPressure.length;i++){
                String datas[] = bloodPressure[i].split("\\|");
                long time = DateUtil.getTimeScope(datas[0],"yyyy-MM-dd HH:mm:ss");
                int sbp = Integer.valueOf(datas[1]);
                int dbp = Integer.valueOf(datas[2]);
                Log.d("SZIP******","血压数据 = "+"time = "+time+" ;sbp = "+sbp+" ;dbp = "+dbp);
                dataArrayList.add(new BloodPressureData(time,sbp,dbp));
            }
            SaveDataUtil.newInstance().saveBloodPressureDataListData(dataArrayList);
        }

        @Override
        public void getBloodOxygen(String[] bloodOxygen) {
            Log.d("SZIP******","收到血氧数据条数 = "+bloodOxygen.length);
            ArrayList<BloodOxygenData> dataArrayList = new ArrayList<>();
            for (int i =0;i<bloodOxygen.length;i++){
                String datas[] = bloodOxygen[i].split("\\|");
                long time = DateUtil.getTimeScope(datas[0],"yyyy-MM-dd HH:mm:ss");
                int data = Integer.valueOf(datas[1]);
                Log.d("SZIP******","血氧数据 = "+"time = "+time+" ;oxygen = "+data);
                dataArrayList.add(new BloodOxygenData(time,data));
            }
            SaveDataUtil.newInstance().saveBloodOxygenDataListData(dataArrayList);
        }

        @Override
        public void getAnimalHeat(String[] animalHeat) {
            Log.d("SZIP******","收到体温数据条数 = "+animalHeat.length);
            ArrayList<AnimalHeatData> dataArrayList = new ArrayList<>();
            for (int i =0;i<animalHeat.length;i++){
                String datas[] = animalHeat[i].split("\\|");
                long time = DateUtil.getTimeScope(datas[0],"yyyy-MM-dd HH:mm:ss");
                int data = Integer.valueOf(datas[1])*10+Integer.valueOf(datas[2]);
                Log.d("SZIP******","体温数据 = "+"time = "+time+" ;animalHeat = "+data);
                dataArrayList.add(new AnimalHeatData(time,data));
            }
            SaveDataUtil.newInstance().saveAnimalHeatDataListData(dataArrayList);
        }

        @Override
        public void getEcg(String[] ecg) {
            Log.d("SZIP******","收到ecg数据条数 = "+ecg.length);
            ArrayList<EcgData> dataArrayList = new ArrayList<>();
            for (int i =0;i<ecg.length;i++){
                String datas[] = ecg[i].split("\\|");
                long time = DateUtil.getTimeScope(datas[0]+" "+datas[1],"yyyy-MM-dd HH:mm:ss");
                Log.d("SZIP******","ecg数据 = "+"time = "+time+" ;heart = "+datas[2]);
                dataArrayList.add(new EcgData(time,datas[2]));
            }
            SaveDataUtil.newInstance().saveEcgDataListData(dataArrayList);
        }

        @Override
        public void getSport(String[] sport) {
            int type = Integer.valueOf(sport[0]);
            long time = DateUtil.getTimeScope(sport[1],"yyyy|MM|dd|HH|mm|ss");
            int sportTime = Integer.valueOf(sport[2]);
            int distance = Integer.valueOf(sport[3]);
            int calorie = Integer.valueOf(sport[4]);
            int speed = Integer.valueOf(sport[5]);
            int heart = Integer.valueOf(sport[10]);
            int stride = Integer.valueOf(sport[7]);
            SportData sportData = new SportData(time,sportTime,distance,calorie,speed,type,heart,stride);
            SaveDataUtil.newInstance().saveSportData(sportData);
        }


        @Override
        public void findPhone(int flag) {
            final AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            if (flag == 1){
                starVibrate(new long[]{500,500,500});
                volume  = am.getStreamVolume(STREAM_MUSIC);//保存手机原来的音量
                am.setStreamVolume (STREAM_MUSIC, am.getStreamMaxVolume(STREAM_MUSIC), FLAG_PLAY_SOUND);//设置系统音乐最大
                if (mediaPlayer==null){
                    mediaPlayer = MediaPlayer.create(MainService.this, R.raw.dang_ring);
                    mediaPlayer.start();
                    mediaPlayer.setVolume(1f,1f);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopVibrate();
                            am.setStreamVolume (STREAM_MUSIC, volume, FLAG_PLAY_SOUND);//播放完毕，设置回之前的音量
                            mediaPlayer = null;
                        }
                    });
                }
            }else{
                if (mediaPlayer!=null){
                    mediaPlayer.stop();
                    stopVibrate();
                    am.setStreamVolume (STREAM_MUSIC, volume, FLAG_PLAY_SOUND);//播放完毕，设置回之前的音量
                    mediaPlayer = null;
                }
            }
        }
    };


    public MainService() {
        Log.i(TAG, "MainService(), MainService in construction!");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        // updateConnectionStatus(false);

        super.onCreate();
        Log.d("SZIP******","service start");
        mSevice = this;
        app = MyApplication.getInstance();
        mIsMainServiceActive = true;
        Map<Object, Object> applist = AppList.getInstance().getAppList();
        if (applist.size() == 0) {
            applist.put(AppList.MAX_APP, (int) AppList.CREATE_LENTH);
            applist.put(AppList.CREATE_LENTH, AppList.BATTERYLOW_APPID);
            applist.put(AppList.CREATE_LENTH, AppList.SMSRESULT_APPID);
            AppList.getInstance().saveAppList(applist);
        }
        if (!applist.containsValue(AppList.BATTERYLOW_APPID)) {
            int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
            applist.remove(AppList.MAX_APP);
            max = max + 1;
            applist.put(AppList.MAX_APP, max);
            applist.put(max, AppList.BATTERYLOW_APPID);
            AppList.getInstance().saveAppList(applist);
        }
        if (!applist.containsValue(AppList.SMSRESULT_APPID)) {
            int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
            applist.remove(AppList.MAX_APP);
            max = max + 1;
            applist.put(AppList.MAX_APP, max);
            applist.put(max, AppList.SMSRESULT_APPID);
            AppList.getInstance().saveAppList(applist);
        }

        registerService();
    }

    public void startConnect(){
        if (!app.isMtk()) {//判断是否使用MTK的库进行蓝牙连接
            if ((getState()==WearableManager.STATE_CONNECT_FAIL||
                    getState()==WearableManager.STATE_CONNECT_LOST ||
                    getState()==WearableManager.STATE_NONE) ){//如果设备未连接，这连接设备
                //如果没有连接，则连接
                Log.d("SZIP******","连接设备BLE");
                BleClient.getInstance().connect(app.getUserInfo().getDeviceCode());
            }
        }else {
            if ((getState()==WearableManager.STATE_CONNECT_FAIL||
                    getState()==WearableManager.STATE_CONNECT_LOST ||
                    getState()==WearableManager.STATE_NONE||
                    getState()==WearableManager.STATE_LISTEN) ){//如果设备未连接，这连接设备
                //如果没有连接，则连接
                Log.d("SZIP******","连接设备MTK");
                WearableManager.getInstance().connect();
            }
        }
    }

    public void stopConnect(){
        if (app.isMtk()){
            WearableManager.getInstance().disconnect();
            WearableManager.getInstance().setRemoteDevice(null);
        }else {
            BleClient.getInstance().disConnect();
        }
        isThreadRun = false;
    }


    private void startThread(){
        isThreadRun = true;
//        if (heartThread ==null||!heartThread.isAlive()){//发送获取数据的心跳包
//            heartThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (isThreadRun){
//                        if (WearableManager.getInstance().getRemoteDevice()!=null&&
//                                getState()==WearableManager.STATE_CONNECTED){//如果设备连接上了，则发送心跳包
//                            Log.d("SZIP******","发送心跳包");
//                            EXCDController.getInstance().writeForCheckVersion();
//                        }
//                        try {
//                            Thread.sleep(10*60*1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            heartThread.start();
//        }

        if(connectThread == null||!connectThread.isAlive()){//断线重连机制
            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isThreadRun){
                        if ((getState()==WearableManager.STATE_CONNECT_FAIL||
                                getState()==WearableManager.STATE_CONNECT_LOST ||
                                getState()==WearableManager.STATE_NONE)){
                            Log.d("SZIP******","断线重连");
                            connectAble = true;
//                            WearableManager.getInstance().connect();
                            WearableManager.getInstance().scanDevice(true);
                        }
                        try {
                            Thread.sleep(10*1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            connectThread.start();
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");

//        this.unregisterReceiver(mReceiver);

        WearableManager manager = WearableManager.getInstance();
        manager.removeController(MapController.getInstance(sContext));
        manager.removeController(NotificationController.getInstance(sContext));
        manager.removeController(RemoteMusicController.getInstance(sContext));
        manager.removeController(EXCDController.getInstance());
        EXCDController.getInstance().setReviceDataCallback(null);
        manager.unregisterWearableListener(mWearableListener);
        mIsMainServiceActive = false;
        unregisterReceiver(mSystemNotificationService);
        mSystemNotificationService = null;
        stopNotificationService();
    }

    private void starVibrate(long[] pattern) {
        Vibrator vib = (Vibrator) mSevice.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, 1);
    }


    private void stopVibrate() {
        Vibrator vib = (Vibrator) mSevice.getSystemService(Service.VIBRATOR_SERVICE);
        vib.cancel();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    /**
     * Return the instance of main service.
     *
     * @return main service instance
     */
    public static MainService getInstance() {
        return mSevice;
    }

    /**
     * Return whether main service is started.
     *
     * @return Return true, if main service start, otherwise, return false.
     */
    public static boolean isMainServiceActive() {
        return mIsMainServiceActive;
    }


    private void registerService() {
        // regist battery low

        Log.i(TAG, "registerService()");
        WearableManager manager = WearableManager.getInstance();
        manager.addController(MapController.getInstance(sContext));
        manager.addController(NotificationController.getInstance(sContext));
        manager.addController(RemoteMusicController.getInstance(sContext));
        manager.addController(EXCDController.getInstance());
        EXCDController.getInstance().setReviceDataCallback(reviceDataCallback);
        manager.registerWearableListener(mWearableListener);
        // start SMS service
        startSmsService();
        // showChoiceNotification();
        startNotificationService();
    }

    public void startNotificationService() {
        Log.i(TAG, "startNotificationService()");
        mNotificationService = new NotificationService();
        NotificationController.setListener(mNotificationService);

    }

    public void stopNotificationService() {
        Log.i(TAG, "stopNotificationService()");
        NotificationController.setListener(null);
        mNotificationService = null;
    }

    public boolean getSmsServiceStatus() {
        return mIsSmsServiceActive;
    }

    /**
     * Start SMS service to push new SMS.
     */
    public void startSmsService() {
        Log.i(TAG, "startSmsService()");
        // Start SMS service
        if (mSmsService == null) {
            mSmsService = new SmsService();
        }
        IntentFilter filter = new IntentFilter("com.mtk.btnotification.SMS_RECEIVED");
        registerReceiver(mSmsService, filter);

        mIsSmsServiceActive = true;
    }

    /**
     * Stop SMS service.
     */
    public void stopSmsService() {
        Log.i(TAG, "stopSmsService()");

        // Stop SMS service
        if (mSmsService != null) {
            unregisterReceiver(mSmsService);
            mSmsService = null;
        }

        mIsSmsServiceActive = false;
    }
    /**
     * Save notification service instance.
     */
    public static void setNotificationReceiver(NotificationReceiver notificationReceiver) {
    }
//
    /**
     * Clear notification service instance.
     */
    public static void clearNotificationReceiver() {
    }


}
