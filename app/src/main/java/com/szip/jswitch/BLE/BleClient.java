package com.szip.jswitch.BLE;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;

import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleMtuResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;

import com.szip.jswitch.Activity.camera.CameraActivity;
import com.szip.jswitch.DB.SaveDataUtil;
import com.szip.jswitch.DB.dbModel.AnimalHeatData;
import com.szip.jswitch.DB.dbModel.BloodOxygenData;
import com.szip.jswitch.DB.dbModel.HeartData;
import com.szip.jswitch.DB.dbModel.SleepData;
import com.szip.jswitch.DB.dbModel.SportData;
import com.szip.jswitch.DB.dbModel.StepData;
import com.szip.jswitch.Interface.IDataResponse;
import com.szip.jswitch.Interface.IOtaResponse;
import com.szip.jswitch.Interface.MyPhoneCallListener;
import com.szip.jswitch.Interface.OnCameraListener;
import com.szip.jswitch.Model.BleStepModel;
import com.szip.jswitch.Model.EvenBusModel.ConnectState;

import com.szip.jswitch.Model.EvenBusModel.UpdateReport;
import com.szip.jswitch.Model.HttpBean.BaseApi;
import com.szip.jswitch.Model.HttpBean.WeatherBean;
import com.szip.jswitch.Model.UpdateSportView;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.Notification.NotificationView;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.CommandUtil;
import com.szip.jswitch.Util.DataParser;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.JsonGenericsSerializator;
import com.szip.jswitch.Util.LogUtil;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.Util.MusicUtil;
import com.zhy.http.okhttp.callback.GenericsCallback;


import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import okhttp3.Call;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.STREAM_MUSIC;

public class BleClient {
    private UUID serviceUUID;
    private String mMac = null;
    private static BleClient mInstance;
    /**
     * 蓝牙连接状态 0:未连接 2：正在连接 3：已经连接 5：连接失败
     * */
    private int connectState = 5;
    /**
     * 业务数据索引
     * */
    private ArrayList<Integer> indexData;
    private boolean isSync = false;//是否正在同步数据

    private int mSportIndex = 0;
    private byte[] recvBuffer = new byte[1024 * 50 * 4];
    private int recvLength = 0;   //buffer中有效数据的长度
    private int recvState = 0;   // 0: seek sync byte; 1: seek header; 2: get data
    private int pkg_type;
    private int pkg_dataLen;
    private int pkg_timeStamp;
    private HandlerThread mHandlerThread;
    private Handler mAnalysisHandler;
    private static final int ANALYSIS_HANDLER_FLAG = 0x100;

    private OnCameraListener onCameraListener;

    private MediaPlayer mediaPlayer;
    private int volume = 0;

    private IOtaResponse iOtaResponse;

    private TelephonyManager tm;
    private MyPhoneCallListener myPhoneCallListener = new MyPhoneCallListener();

    private BleClient() {
        DataParser.newInstance().setmIDataResponse(iDataResponse);
//        ClientManager.getClient().registerBluetoothStateListener(bluetoothStateListener);
        mHandlerThread = new HandlerThread("analysis-thread");
        mHandlerThread.start();
        mAnalysisHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == ANALYSIS_HANDLER_FLAG) {
                    byte[] value = (byte[]) msg.obj;
                    if (value != null && (value.length != 0)) {
                        LogUtil.getInstance().logd("DATA******", "value.length=" + value.length + " recvBuffer.length=" + recvBuffer.length + " recvLength=" + recvLength + " lastIndex =" + (value.length + recvLength));
                        try {
                            System.arraycopy(value, 0, recvBuffer, recvLength, value.length);
                            recvLength += value.length;
                        } catch (Exception e) {
                            e.printStackTrace();
                            recvLength = 0;
                            recvState = 0;
                            pkg_dataLen = 0;
                        }
                    }
                    phaserRecvBuffer();

                }
            }
        };

    }

    public void setiOtaResponse(IOtaResponse iOtaResponse) {
        this.iOtaResponse = iOtaResponse;
    }

    public void setOnCameraListener(OnCameraListener onCameraListener) {
        this.onCameraListener = onCameraListener;
    }

    /**
     * 蓝牙连接
     *
     */
    public void connect(String mac){
        this.mMac = mac;
        if (connectState == 5){
            LogUtil.getInstance().logd("SZIP******","开始连接蓝牙设备mac = "+mMac);
            connectState = 2;
            ClientManager.getClient().connect(mMac,bleConnectResponse);
            ClientManager.getClient().registerConnectStatusListener(mMac,connectStatusListener);
            EventBus.getDefault().post(new ConnectState(connectState));
        }
    }

    public void disConnect(){
        if (mMac!=null){
            LogUtil.getInstance().logd("SZIP******","断开蓝牙设备mac = "+mMac);
            connectState = 5;
            ClientManager.getClient().disconnect(mMac);
            EventBus.getDefault().post(new ConnectState(connectState));
        }
    }

    private BleConnectResponse bleConnectResponse = new BleConnectResponse() {
        @Override
        public void onResponse(int code, BleGattProfile data) {
            LogUtil.getInstance().logd("SZIP","code = "+code);
            if( code == 0 ){        // 0 成功
                ClientManager.getClient().requestMtu(mMac, 200, new BleMtuResponse() {
                    @Override
                    public void onResponse(int code, Integer data) {
                    }
                });
                setGattProfile(data);
            }else{
                LogUtil.getInstance().logd("SZIP******","连接蓝牙失败");
                connectState = 5;
                isSync = false;
                recvLength = 0;
                recvState = 0;
                pkg_dataLen = 0;
            }
        }
    };

    /**
     * 蓝牙状态的回调
     * */
    private BleConnectStatusListener connectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            LogUtil.getInstance().logd("connectStatus",status+"");
            if( status == 0x10){
                connectState = 3;
                LogUtil.getInstance().logd("SZIP******","连接");
                MainService.getInstance().setReconnectTimes(0);
                //连接成功，获取设备信息
                TimerTask timerTask= new TimerTask() {
                    @Override
                    public void run() {
                        MainService.getInstance().startForeground(0103,NotificationView.getInstance().getNotify(true));
                        writeForSyncTime();
                        writeForSyncTimeStyle();
                        writeForSetLanuage();
                        writeForSetWeather(MyApplication.getInstance().getWeatherModel(),MyApplication.getInstance().getCity());
                        writeForSetElevation();
                        writeForUpdateUserInfo();
                        writeForSetUnit();
                        initPhoneStateListener(true);
                        MusicUtil.getSingle().registerNotify();
                        MainService.getInstance().startThread();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask,300);
            }else{
                MainService.getInstance().startForeground(0103,NotificationView.getInstance().getNotify(false));
                initPhoneStateListener(false);
                LogUtil.getInstance().logd("SZIP******","断开");
                MusicUtil.getSingle().unRegisterNotify();
                connectState = 5;
                isSync = false;
                recvLength = 0;
                recvState = 0;
                pkg_dataLen = 0;
            }
            EventBus.getDefault().post(new ConnectState(connectState));
        }
    };

    private void initPhoneStateListener(boolean flag) {

        if (flag){
            tm = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            if(tm != null) {
                try {
                    myPhoneCallListener.setCallListener(new MyPhoneCallListener.CallListener() {
                        @Override
                        public void onCallRinging(String num,String name) {
                            if (num == null&&name==null){
                                writeForSendPhoneCall(num,name);
                            } else if (!num.equals("")){
                                writeForSendPhoneCall(num,name);
                            }
                        }
                    });
                    // 注册来电监听
                    tm.listen(myPhoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);
                } catch(Exception e) {
                    // 异常捕捉
                    LogUtil.getInstance().logd("DATA******",e.getMessage());
                }
            }
        }else {
            tm = null;
            myPhoneCallListener.setCallListener(null);
        }
    }

    /**
     * 配置特征值以及接受特征值的通知
     * */
    public void setGattProfile(BleGattProfile profile) {
        List<com.inuker.bluetooth.library.model.BleGattService> services = profile.getServices();
        for (com.inuker.bluetooth.library.model.BleGattService service : services) {
            if(Config.char0.equalsIgnoreCase(service.getUUID().toString())){
                serviceUUID = service.getUUID();
                List<BleGattCharacter> characters = service.getCharacters();
                for(BleGattCharacter character : characters){
                    String uuidCharacteristic = character.getUuid().toString();
                    if( character.getUuid().toString().equalsIgnoreCase(Config.char2)){     // 主要用于回复等操作
                        openid(serviceUUID,character.getUuid());
                    }
                }
            }
        }
    }

    public void openid(UUID serviceUUID, UUID characterUUID) {
        ClientManager.getClient().notify(mMac,serviceUUID,characterUUID,bleNotifyResponse);
    }

    private BleWriteResponse bleWriteResponse = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {

        }
    };


    /**
     * 收到通知的消息
     * */
    private BleNotifyResponse bleNotifyResponse = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            String data = DateUtil.byteToHexString(value);
            LogUtil.getInstance().logd("DATA******","收到蓝牙信息:"+data);
            if (value.length == 8) {
                if ((value[4] == -16) && (value[5] == -16) && (value[6] == -16) && (value[7] == -16)) {
                    ClientManager.getClient().read(mMac,serviceUUID,UUID.fromString(Config.char3),bleReadResponse);
                }
            }else {
                DataParser.newInstance().parseData(value);
            }
        }

        @Override
        public void onResponse(int code) {

        }
    };

    /**
     * 读取的消息
     * */
    private BleReadResponse bleReadResponse = new BleReadResponse() {
        @Override
        public void onResponse(int code, byte[] data) {
            String value = DateUtil.byteToHexString(data);
            LogUtil.getInstance().logd("DATA******", "读取到蓝牙信息:" + value);
            if (data.length != 0){
                if (data.length > 0) {
                    Message message = mAnalysisHandler.obtainMessage();
                    message.what = ANALYSIS_HANDLER_FLAG;
                    message.obj = data;
                    mAnalysisHandler.sendMessage(message);
                }
            }
        }
    };



    private IDataResponse iDataResponse = new IDataResponse() {

        @Override
        public void onSaveStepDatas(ArrayList<BleStepModel> datas) {
            ArrayList<StepData> stepData = new ArrayList<>();
            for (int i = 0;i<datas.size();i++){
                HashMap<Integer,Integer> hashMap = datas.get(i).getStepInfo();
                StringBuffer stepString = new StringBuffer();
                for (int key:hashMap.keySet()){
                    stepString.append(String.format(Locale.ENGLISH,",%02d:%d",key,hashMap.get(key)));
                }
                stepData.add(new StepData(datas.get(i).getTime(),datas.get(i).getStep(),datas.get(i).getDistance()*10,
                        datas.get(i).getCalorie(),stepString.substring(1)));
                LogUtil.getInstance().logd("DATA******","统计出来的计步数据  "+"time = "+datas.get(i).getTime()+" ;step = "+datas.get(i).getStep()+
                        " ;distance = "+datas.get(i).getDistance()+" ;calorie = "+datas.get(i).getCalorie()+
                        " ;stepInfo = "+stepString.substring(1));
            }
            SaveDataUtil.newInstance().saveStepInfoDataListData1(stepData);
        }

        @Override
        public void onSaveDayStepDatas(ArrayList<StepData> datas) {
            SaveDataUtil.newInstance().saveStepDataListData(datas);
        }

        @Override
        public void onSaveHeartDatas(ArrayList<HeartData> datas) {
            for (int i = 0;i<datas.size();i++){
                datas.get(i).averageHeart = datas.get(i).averageHeart/datas.get(i).heartArray.split(",").length;
                LogUtil.getInstance().logd("DATA******","统计出来的心率数据  "+"time = "+datas.get(i).getTime()+" ;heart = "+datas.get(i).getAverageHeart()+
                        " ;heartArray = "+datas.get(i).getHeartArray());
            }
            SaveDataUtil.newInstance().saveHeartDataListData(datas,true);
        }

        @Override
        public void onSaveTempDatas(ArrayList<AnimalHeatData> datas) {
            SaveDataUtil.newInstance().saveAnimalHeatDataListData(datas);
        }

        @Override
        public void onSaveSleepDatas(ArrayList<SleepData> datas) {
            SaveDataUtil.newInstance().saveSleepDataListData(datas);
        }

        @Override
        public void onSaveRunDatas(ArrayList<SportData> datas) {
            SaveDataUtil.newInstance().saveSportDataListData(datas);
        }

        @Override
        public void onSaveBloodOxygenDatas(ArrayList<BloodOxygenData> datas) {
            SaveDataUtil.newInstance().saveBloodOxygenDataListData(datas);
        }

        @Override
        public void onGetDataIndex(String deviceNum, ArrayList<Integer> dataIndex) {
            if(MyApplication.getInstance().getDeviceNum()!=deviceNum){
                MyApplication.getInstance().setDeviceNum(deviceNum);
                EventBus.getDefault().post(new UpdateSportView());
            }
            if (indexData==null){
                Log.d("DATA******","index!=null");
                indexData = dataIndex;
                if (indexData.size()>0){
                    isSync = false;
                    Log.d("DATA******","index>0");
                    synSmartDeviceData(0);
                }else {
                    indexData = null;
                }

            }
        }

        @Override
        public void onCamera(int flag) {
            if (MyApplication.getInstance().isCamerable())
                if (flag == 1){//打开相机
                    Intent intent1=new Intent(MyApplication.getInstance().getApplicationContext(), CameraActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getInstance().getApplicationContext().startActivity(intent1);
                }else if (flag == 0){//关闭相机
                    if (onCameraListener!=null)
                        onCameraListener.onCamera(0);
                }else {//拍照
                    if (onCameraListener!=null)
                        onCameraListener.onCamera(1);
                }
        }

        @Override
        public void findPhone(int flag) {
            final AudioManager am = (AudioManager)MyApplication.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            if (flag == 1){
                MathUitl.speaker(am);
                starVibrate(new long[]{500,500,500});
                volume  = am.getStreamVolume(STREAM_MUSIC);//保存手机原来的音量
                am.setStreamVolume (STREAM_MUSIC, am.getStreamMaxVolume(STREAM_MUSIC), FLAG_PLAY_SOUND);//设置系统音乐最大
                if (mediaPlayer==null){
                    mediaPlayer = MediaPlayer.create(MyApplication.getInstance().getApplicationContext(), R.raw.dang_ring);
                    mediaPlayer.start();
                    mediaPlayer.setVolume(1f,1f);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopVibrate();
                            am.setStreamVolume (STREAM_MUSIC, volume, FLAG_PLAY_SOUND);//播放完毕，设置回之前的音量
                            mediaPlayer = null;
                            MathUitl.offSpeaker(am);
                        }
                    });
                }
            }else{
                if (mediaPlayer!=null){
                    mediaPlayer.stop();
                    stopVibrate();
                    am.setStreamVolume (STREAM_MUSIC, volume, FLAG_PLAY_SOUND);//播放完毕，设置回之前的音量
                    mediaPlayer = null;
                    MathUitl.offSpeaker(am);
                }
            }
        }

        @Override
        public void updateUserInfo() {
            try {
                HttpMessgeUtil.getInstance().postForSetUserInfo1(MyApplication.getInstance().getUserInfo(),
                        new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(BaseApi response, int id) {

                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void updateOtaProgress(int state, int address) {
            if (iOtaResponse!=null){
                if (state==0){
                    iOtaResponse.onSendFail();
                }else if (state == 1){
                    iOtaResponse.onSendProgress();
                }else if (state == 2){
                    iOtaResponse.onSendSccuess();
                }else {
                    iOtaResponse.onStartToSendFile(state,address);
                }
            }

        }

        @Override
        public void onMusicControl(int cmd, int voiceValue) {
            if (cmd == 0){//暂停
                MusicUtil.getSingle().controlMusic(127);
            }else if (cmd == 1){//开始
                MusicUtil.getSingle().controlMusic(126);
            }else if (cmd == 2){//上一曲
                MusicUtil.getSingle().controlMusic(88);
            }else if (cmd == 3){//下一曲
                MusicUtil.getSingle().controlMusic(87);
            }else if (cmd == 4){//音量
                MusicUtil.getSingle().setVoiceValue(voiceValue);
            }
        }
    };

    private void starVibrate(long[] pattern) {
        Vibrator vib = (Vibrator) MyApplication.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, 1);
    }


    private void stopVibrate() {
        Vibrator vib = (Vibrator) MyApplication.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
        vib.cancel();
    }

    private BluetoothStateListener bluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {

        }
    };


    /**
     * 解析运动数据
     */
    private synchronized void phaserRecvBuffer() {
        if (recvState == 0) {//把一条完整的AA开头的协议拉到数组的最前面
            for (int offset = 0; offset < recvLength; offset++) {
                if (recvBuffer[offset] == (byte) 0xAA) {
                    System.arraycopy(recvBuffer, offset, recvBuffer, 0, recvLength - offset);
                    recvLength -= offset;
                    recvState = 1;
                    break;
                }
            }
            if (recvState != 1) {
                recvLength = 0;
            }
        }

        if (recvState == 1) {//把一条协议除了协议头之外的业务数据提取出来放到数据最前面
            if (recvLength >= 8) {
                pkg_type = (recvBuffer[1] & 0xFF);
                pkg_dataLen = (recvBuffer[2] & 0xff) + ((recvBuffer[3] & 0xFF) << 8);
                pkg_timeStamp = (recvBuffer[4] & 0xff) + ((recvBuffer[5] & 0xFF) << 8) + ((recvBuffer[6] & 0xff) << 16) + ((recvBuffer[7] & 0xFF) << 24);
                if (pkg_dataLen == 0) {
                    recvState = 0;
                } else {
                    recvState = 2;
                }
                if (!checkArrayCopy(recvBuffer, 8, recvBuffer, 0, recvLength - 8)) {
                    return;
                }
                recvLength -= 8;
            } else {
                LogUtil.getInstance().logd("DATA******", "......");
                return;
            }
        }

        if (recvState == 2) {
            LogUtil.getInstance().logd("DATA******", "recvLenght=" + recvLength + " dataLength=" + pkg_dataLen+" time = "+pkg_timeStamp);
            if (recvLength >= pkg_dataLen) {
                byte[] pkg_data = new byte[pkg_dataLen];
                System.arraycopy(recvBuffer, 0, pkg_data, 0, pkg_dataLen);
                System.arraycopy(recvBuffer, pkg_dataLen, recvBuffer, 0, recvLength - pkg_dataLen);
                recvLength -= pkg_dataLen;
                recvState = 0;
                DataParser.newInstance().parseData(pkg_type,pkg_data,pkg_timeStamp,recvLength > 0?false:true);
            } else {
                LogUtil.getInstance().logd("DATA******", "------");
                return;
            }
        }

        if (recvLength > 0) {
            Message message = mAnalysisHandler.obtainMessage();
            message.what = ANALYSIS_HANDLER_FLAG;
            message.obj = null;
            mAnalysisHandler.sendMessage(message);
        } else {
            //用于判断数据是否取完
            LogUtil.getInstance().logd("DATA******", "//////");
            recvLength = 0;
            recvState = 0;
            pkg_dataLen = 0;
            syncSportDataByOrder();
            return;
        }

    }

    /**
     * 依次去同步运动数据
     */
    private void syncSportDataByOrder() {
            //0表示只同步心率，记步，睡眠
        if (indexData==null||mSportIndex == indexData.size()-1) {
            //说明所有的运动都同步完了
            isSync = false;
            indexData = null;
            mSportIndex = 0;
            EventBus.getDefault().post(new UpdateReport());
        } else {
            mSportIndex++;
            synSmartDeviceData(mSportIndex);
        }


    }

    /**
     * 提取出需要解析的数据数组
     *
     * @param sourceByte     源数据数组
     * @param startIndex     开始截取的源数据下标
     * @param destByte       目标数据数组
     * @param startDestIndex 开始截取的目标数据下标
     * @param destLength     目标数据长度
     * @return boolean
     * @throws ArrayIndexOutOfBoundsException ArrayIndexOutOfBoundsException
     */
    private static boolean checkArrayCopy(byte[] sourceByte, int startIndex, byte[] destByte, int startDestIndex, int destLength) {
        try {
            System.arraycopy(sourceByte, startIndex, destByte, startDestIndex, destLength);
            return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void synSmartDeviceData(final int dataType) {
        byte[] datas = new byte[0];
        int type = indexData.get(dataType);
        Log.d("DATA******","type = "+type);
        if (connectState==3) {
            switch (type) {
                case 0x01:
                    //记步数据
                    LogUtil.getInstance().logd("DATA******", "sync step");
                    datas = CommandUtil.getCommandbyteArray(0x01, 8,
                            0, true);
                    break;

                case 0x02:
                    //心率数据
                    LogUtil.getInstance().logd("DATA******", "sync heart_rate");
                    datas = CommandUtil.getCommandbyteArray(0x02, 8,
                            0, true);
                    break;

                case 0x03:
                    //睡眠数据
                    LogUtil.getInstance().logd("DATA******", "sync sleep");
                    datas = CommandUtil.getCommandbyteArray(0x03, 8,
                            0, true);
                    break;

                case 0x04:
                    //跑步数据
                    LogUtil.getInstance().logd("DATA******", "sync run");
                    datas = CommandUtil.getCommandbyteArray(0x04, 8,
                            0, true);
                    break;

                case 0x05:
                    //徒步数据
                    LogUtil.getInstance().logd("DATA******", "sync onfoot");
                    datas = CommandUtil.getCommandbyteArray(0x05, 8,
                            0, true);
                    break;

                case 0x06:
                    //马拉松
                    LogUtil.getInstance().logd("DATA******", "sync marathon");
                    datas = CommandUtil.getCommandbyteArray(0x06, 8,
                            0, true);
                    break;

                case 0x07:
                    //跳绳
                    LogUtil.getInstance().logd("DATA******", "sync rope_shipping");
                    datas = CommandUtil.getCommandbyteArray(0x07, 8,
                            0, true);
                    break;

                case 0x08:
                    //户外游泳
                    LogUtil.getInstance().logd("DATA******", "sync swim");
                    datas = CommandUtil.getCommandbyteArray(0x08, 8,
                            0, true);
                    break;

                case 0x09:
                    //攀岩
                    LogUtil.getInstance().logd("DATA******", "sync rock_climbing");
                    datas = CommandUtil.getCommandbyteArray(0x09, 8,
                            0, true);
                    break;

                case 0x0A:
                    //滑雪
                    LogUtil.getInstance().logd("DATA******", "sync skking");
                    datas = CommandUtil.getCommandbyteArray(0x0A, 8,
                            0, true);
                    break;

                case 0x0B:
                    //骑行
                    LogUtil.getInstance().logd("DATA******", "sync riding");
                    datas = CommandUtil.getCommandbyteArray(0x0B, 8,
                            0, true);
                    break;

                case 0x0C:
                    //划船
                    LogUtil.getInstance().logd("DATA******", "sync rowing");
                    datas = CommandUtil.getCommandbyteArray(0x0C, 8,
                            0, true);
                    break;

                case 0x0D:
                    //蹦极
                    LogUtil.getInstance().logd("DATA******", "sync bungee");
                    datas = CommandUtil.getCommandbyteArray(0x0D, 8,
                            0, true);
                    break;

                case 0x0E:
                    //登山
                    LogUtil.getInstance().logd("DATA******", "sync mountaineer");
                    datas = CommandUtil.getCommandbyteArray(0x0E, 8,
                            0, true);
                    break;

                case 0x0F:
                    //跳伞
                    LogUtil.getInstance().logd("DATA******", "sync parachute");
                    datas = CommandUtil.getCommandbyteArray(0x0F, 8,
                            0, true);
                    break;

                case 0x10:
                    //高尔夫
                    LogUtil.getInstance().logd("DATA******", "sync golf");
                    datas = CommandUtil.getCommandbyteArray(0x10, 8,
                            0, true);
                    break;

                case 0x11:
                    //冲浪
                    LogUtil.getInstance().logd("DATA******", "sync surf");
                    datas = CommandUtil.getCommandbyteArray(0x11, 8,
                            0, true);
                    break;

                case 0x14:
                    //跑步机
                    LogUtil.getInstance().logd("DATA******", "sync treadmill");
                    datas = CommandUtil.getCommandbyteArray(0x14, 8,
                            0, true);
                    break;
                case 0x19:
                    //跑步机
                    LogUtil.getInstance().logd("DATA******", "sync step on day");
                    datas = CommandUtil.getCommandbyteArray(0x19, 8,
                            0, true);
                    break;
                case 0x21:
                    //跑步机
                    LogUtil.getInstance().logd("DATA******", "sync badnition on day");
                    datas = CommandUtil.getCommandbyteArray(0x21, 8,
                            0, true);
                    break;
                case 0x22:
                    //跑步机
                    LogUtil.getInstance().logd("DATA******", "sync basket on day");
                    datas = CommandUtil.getCommandbyteArray(0x22, 8,
                            0, true);
                    break;
                case 0x23:
                    //跑步机
                    LogUtil.getInstance().logd("DATA******", "sync foot on day");
                    datas = CommandUtil.getCommandbyteArray(0x23, 8,
                            0, true);
                    break;
                case 0x24:
                    //血氧
                    LogUtil.getInstance().logd("DATA******", "sync bloodoxygen on day");
                    datas = CommandUtil.getCommandbyteArray(0x24, 8,
                            0, true);
                    break;
                default:
                    break;
            }

            ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                    datas,bleWriteResponse);

        }
    }

    public void writeForSyncTime(){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(0x30, 21, 13, true),bleWriteResponse);
    }
    public void writeForSyncTimeStyle(){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(0x44, 9, 1, true),bleWriteResponse);
    }
    public void writeForUpdateUserInfo(){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(0x31, 20, 12, true),bleWriteResponse);
    }

    public void writeForFindWatch(){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(0x38, 9, 1, true),bleWriteResponse);
    }

    public void writeForSetLanuage(){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(0x39, 13, 5, true),bleWriteResponse);
    }

    public void writeForGetDeviceState(){
        if (indexData==null){
            ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                    CommandUtil.getCommandbyteArray(0x32, 8,
                            0, true),bleWriteResponse);
        }
    }

    public void writeForSetWeather(ArrayList<WeatherBean.Condition> weatherModel,String city){
        if (weatherModel!=null  )
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(weatherModel,city),bleWriteResponse);
    }

    public void writeForSetElevation(){
            ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                    CommandUtil.getCommandbyteArray(0x43, 12, 4, true),bleWriteResponse);
    }

    public void writeForSetUnit(){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(0x41, 10, 2, true),bleWriteResponse);
    }

    public void writeForSendPhoneCall(String num,String name){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(0x49,num,name,false),bleWriteResponse);
    }

    public void writeForSendMusicInfo(String name,String singer,boolean playState){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(0x50,singer,name,playState),bleWriteResponse);

    }

    public void writeForSendVolume(){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(0x51,9,1,true),bleWriteResponse);

    }

    public void writeForSetHeartSwitch(){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(0x45, 9, 1, true),bleWriteResponse);
    }

    public void writeForSendNotify(String content,String name,int type){
        ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                CommandUtil.getCommandbyteArray(content,name,type),bleWriteResponse);
    }

    public void writeForSendPicture(int type,int clockType,int clockIndex,int num,byte[] datas){
        if (type == 0){
            ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                    CommandUtil.getCommandbytePicture(13,5,type,clockType,clockType,num,datas),bleWriteResponse);
        }else if (type == 1){
            ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                    CommandUtil.getCommandbytePicture(datas.length+11,datas.length+3,type,clockType,clockType,num,datas),bleWriteResponse);
        }else {
            ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                    CommandUtil.getCommandbytePicture(10,2,type,clockType,clockType,num,datas),bleWriteResponse);
        }
    }

    public void writeForSendOtaFile(int type,byte[] version,int addresss,int num,byte[] datas){
        if (type == 0){
            ClientManager.getClient().write(mMac,UUID.fromString(Config.char5),UUID.fromString(Config.char4),
                    CommandUtil.getCommandbyteOtaFileTest(14,6,type,version,addresss,num,datas),bleWriteResponse);
        }else if (type == 1){
            ClientManager.getClient().write(mMac,UUID.fromString(Config.char5),UUID.fromString(Config.char4),
                    CommandUtil.getCommandbyteOtaFileTest(datas.length+15,datas.length+7,type,version,addresss,num,datas),bleWriteResponse);
        }else {
            ClientManager.getClient().write(mMac,UUID.fromString(Config.char5),UUID.fromString(Config.char4),
                    CommandUtil.getCommandbyteOtaFileTest(10,2,type,version,addresss,num,datas),bleWriteResponse);
        }
    }

    public void writeForSendDialFile(int type,byte clockId,int addresss,int num,byte[] datas){
        if (type == 3){
            ClientManager.getClient().write(mMac,UUID.fromString(Config.char5),UUID.fromString(Config.char4),
                    CommandUtil.getCommandbyteDialFile(2,type,clockId,addresss,num,datas),bleWriteResponse);
        }else if (type == 4){
            ClientManager.getClient().write(mMac,UUID.fromString(Config.char5),UUID.fromString(Config.char4),
                    CommandUtil.getCommandbyteDialFile(datas.length+7,type,clockId,addresss,num,datas),bleWriteResponse);
        }else if (type == 5){
            ClientManager.getClient().write(mMac,UUID.fromString(Config.char5),UUID.fromString(Config.char4),
                    CommandUtil.getCommandbyteDialFile(2,type,clockId,addresss,num,datas),bleWriteResponse);
        }else if (type == 6){
            ClientManager.getClient().write(mMac,UUID.fromString(Config.char5),UUID.fromString(Config.char4),
                    CommandUtil.getCommandbyteDialFile(2,type,clockId,addresss,num,datas),bleWriteResponse);
        }else if (type == 7){
            ClientManager.getClient().write(mMac,UUID.fromString(Config.char5),UUID.fromString(Config.char4),
                    CommandUtil.getCommandbyteDialFile(datas.length+7,type,clockId,addresss,num,datas),bleWriteResponse);
        }else if (type == 8){
            ClientManager.getClient().write(mMac,UUID.fromString(Config.char5),UUID.fromString(Config.char4),
                    CommandUtil.getCommandbyteDialFile(2,type,clockId,addresss,num,datas),bleWriteResponse);
        }
    }

    public int getConnectState() {
        return connectState;
    }

    public static BleClient getInstance() {
        if (mInstance == null) {
            synchronized(BleClient.class){
                if (mInstance==null)
                    mInstance = new BleClient();
            }
        }
        return mInstance;
    }

    public boolean isSync() {
        return isSync;
    }

}
