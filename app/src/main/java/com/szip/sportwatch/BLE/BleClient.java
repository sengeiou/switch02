package com.szip.sportwatch.BLE;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;

import android.os.Message;
import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleMtuResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;

import com.mediatek.wearable.WearableManager;
import com.mediatek.wearableProfiles.WearableClientProfile;
import com.szip.sportwatch.DB.SaveDataUtil;
import com.szip.sportwatch.DB.dbModel.HeartData;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.DB.dbModel.StepData;
import com.szip.sportwatch.Interface.IDataResponse;
import com.szip.sportwatch.Model.BleStepModel;
import com.szip.sportwatch.Model.EvenBusModel.ConnectState;

import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.Model.UpdateSportView;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.Util.CommandUtil;
import com.szip.sportwatch.Util.DataParser;
import com.szip.sportwatch.Util.DateUtil;
import com.zhy.http.okhttp.utils.L;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static android.content.Context.MODE_PRIVATE;

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
    private int recvLength = 0;
    private int recvState = 0;   // 0: seek sync byte; 1: seek header; 2: get data
    private int pkg_type;
    private int pkg_dataLen;
    private int pkg_timeStamp;
    private HandlerThread mHandlerThread;
    private Handler mAnalysisHandler;
    private static final int ANALYSIS_HANDLER_FLAG = 0x100;

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
                        Log.e("SZIP******", "value.length=" + value.length + " recvBuffer.length=" + recvBuffer.length + " recvLength=" + recvLength + " lastIndex =" + (value.length + recvLength));
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

    /**
     * 蓝牙连接
     *
     */
    public void connect(String mac){
        this.mMac = mac;
        if (connectState == 5){
            Log.d("SZIP******","开始连接蓝牙设备mac = "+mMac);
            connectState = 2;
            ClientManager.getClient().connect(mMac,bleConnectResponse);
            ClientManager.getClient().registerConnectStatusListener(mMac,connectStatusListener);
            EventBus.getDefault().post(new ConnectState(connectState));
        }
    }

    public void disConnect(){
        if (mMac!=null){
            Log.d("SZIP******","断开蓝牙设备mac = "+mMac);
            connectState = 5;
            ClientManager.getClient().disconnect(mMac);
            EventBus.getDefault().post(new ConnectState(connectState));
        }
    }

    private BleConnectResponse bleConnectResponse = new BleConnectResponse() {
        @Override
        public void onResponse(int code, BleGattProfile data) {
            Log.e("SZIP","code = "+code);
            if( code == 0 ){        // 0 成功
                ClientManager.getClient().requestMtu(mMac, 150, new BleMtuResponse() {
                    @Override
                    public void onResponse(int code, Integer data) {
                        Log.d("SZIP******","设置MTU成功 :"+"code = "+code+" ;data = "+data);
                    }
                });
                setGattProfile(data);
            }else{
                Log.d("SZIP******","连接蓝牙失败");
                connectState = 5;
            }
        }
    };

    /**
     * 蓝牙状态的回调
     * */
    private BleConnectStatusListener connectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            Log.d("connectStatus",status+"");
            if( status == 0x10){
                connectState = 3;
                Log.d("SZIP******","连接");
                //连接成功，获取设备信息
                TimerTask timerTask= new TimerTask() {
                    @Override
                    public void run() {
                        writeForSyncTime();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask,300);
            }else{
                Log.d("SZIP******","断开");
                connectState = 5;
            }
            EventBus.getDefault().post(new ConnectState(connectState));
        }
    };

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
                    Log.d("SZIP******","characteristic : "+uuidCharacteristic);
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
            Log.d("SZIP******","收到蓝牙信息:"+data);
            if (value.length == 8) {
                if ((value[4] == -16) && (value[5] == -16) && (value[6] == -16) && (value[7] == -16)) {
                    ClientManager.getClient().read(mMac,serviceUUID,UUID.fromString(Config.char3),bleReadResponse);
                }
            }

//            /**
//             * 用线程池接收通知
//             * */
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    ClientManager.getClient().read(mMac,serviceUUID,UUID.fromString(Config.char3),bleReadResponse);
//                }
//            };
//            readExecutor.execute(runnable);
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
            Log.d("SZIP******", "读取到蓝牙信息:" + value);

                if (data.length > 0) {
                    if ((data.length>=2) && (data[0]==-86) && ((!(data[1]>=0x01 && data[1]<0x12)) && data[1]!=0x14)) {
                        DataParser.newInstance().parseData(data);
                    }else {
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
                stepData.add(new StepData(datas.get(i).getTime(),datas.get(i).getStep(),datas.get(i).getDistance(),
                        datas.get(i).getCalorie(),stepString.substring(1)));
                Log.d("SZIP******","统计出来的计步数据  "+"time = "+datas.get(i).getTime()+" ;step = "+datas.get(i).getStep()+
                        " ;distance = "+datas.get(i).getDistance()+" ;calorie = "+datas.get(i).getCalorie()+
                        " ;stepInfo = "+stepString.substring(1));
            }
            SaveDataUtil.newInstance().saveStepInfoDataListData1(stepData);
        }

        @Override
        public void onSaveHeartDatas(ArrayList<HeartData> datas) {
            for (int i = 0;i<datas.size();i++){
                datas.get(i).averageHeart = datas.get(i).averageHeart/datas.get(i).heartArray.split(",").length;
                Log.d("SZIP******","统计出来的心率数据  "+"time = "+datas.get(i).getTime()+" ;heart = "+datas.get(i).getAverageHeart()+
                        " ;heartArray = "+datas.get(i).getHeartArray());
            }
            SaveDataUtil.newInstance().saveHeartDataListData(datas,true);
        }

        @Override
        public void onSaveSleepDatas(byte[] datas) {

        }

        @Override
        public void onSaveRunDatas(ArrayList<SportData> datas) {
            SaveDataUtil.newInstance().saveSportDataListData(datas);
        }

        @Override
        public void onGetDataIndex(String deviceNum, ArrayList<Integer> dataIndex) {
            if(MyApplication.getInstance().getDeviceNum()!=deviceNum){
                MyApplication.getInstance().setDeviceNum(deviceNum);
                EventBus.getDefault().post(new UpdateSportView());
            }
            if (indexData==null){
                indexData = dataIndex;
                if (indexData.size()>0){
                    isSync = true;
                    synSmartDeviceData(0);
                }else {
                    indexData = null;
                }

            }
        }
    };

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
                Log.e("SZIP******", "......");
                return;
            }
        }

        if (recvState == 2) {
            Log.e("SZIP******", "recvLenght=" + recvLength + " dataLength=" + pkg_dataLen+" time = "+pkg_timeStamp);
            if (recvLength >= pkg_dataLen) {
                byte[] pkg_data = new byte[pkg_dataLen];
                System.arraycopy(recvBuffer, 0, pkg_data, 0, pkg_dataLen);
                System.arraycopy(recvBuffer, pkg_dataLen, recvBuffer, 0, recvLength - pkg_dataLen);
                recvLength -= pkg_dataLen;
                recvState = 0;
                DataParser.newInstance().parseData(pkg_type,pkg_data,pkg_timeStamp,recvLength > 0?false:true);
            } else {
                Log.e("SZIP******", "------");
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
            Log.e("SZIP******", "//////");
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
        if (connectState==3) {
            switch (type) {
                case 0x01:
                    //记步数据
                    Log.e("SZIP******", "sync step");
                    datas = CommandUtil.getCommandbyteArray(0x01, 8,
                            0, true);
                    break;

                case 0x02:
                    //心率数据
                    Log.e("SZIP******", "sync heart_rate");
                    datas = CommandUtil.getCommandbyteArray(0x02, 8,
                            0, true);
                    break;

                case 0x03:
                    //睡眠数据
                    Log.e("SZIP******", "sync sleep");
                    datas = CommandUtil.getCommandbyteArray(0x03, 8,
                            0, true);
                    break;

                case 0x04:
                    //跑步数据
                    Log.e("SZIP******", "sync run");
                    datas = CommandUtil.getCommandbyteArray(0x04, 8,
                            0, true);
                    break;

                case 0x05:
                    //徒步数据
                    Log.e("SZIP******", "sync onfoot");
                    datas = CommandUtil.getCommandbyteArray(0x05, 8,
                            0, true);
                    break;

                case 0x06:
                    //马拉松
                    Log.e("SZIP******", "sync marathon");
                    datas = CommandUtil.getCommandbyteArray(0x06, 8,
                            0, true);
                    break;

                case 0x07:
                    //跳绳
                    Log.e("SZIP******", "sync rope_shipping");
                    datas = CommandUtil.getCommandbyteArray(0x07, 8,
                            0, true);
                    break;

                case 0x08:
                    //户外游泳
                    Log.e("SZIP******", "sync swim");
                    datas = CommandUtil.getCommandbyteArray(0x08, 8,
                            0, true);
                    break;

                case 0x09:
                    //攀岩
                    Log.e("SZIP******", "sync rock_climbing");
                    datas = CommandUtil.getCommandbyteArray(0x09, 8,
                            0, true);
                    break;

                case 0x0A:
                    //滑雪
                    Log.e("SZIP******", "sync skking");
                    datas = CommandUtil.getCommandbyteArray(0x0A, 8,
                            0, true);
                    break;

                case 0x0B:
                    //骑行
                    Log.e("SZIP******", "sync riding");
                    datas = CommandUtil.getCommandbyteArray(0x0B, 8,
                            0, true);
                    break;

                case 0x0C:
                    //划船
                    Log.e("SZIP******", "sync rowing");
                    datas = CommandUtil.getCommandbyteArray(0x0C, 8,
                            0, true);
                    break;

                case 0x0D:
                    //蹦极
                    Log.e("SZIP******", "sync bungee");
                    datas = CommandUtil.getCommandbyteArray(0x0D, 8,
                            0, true);
                    break;

                case 0x0E:
                    //登山
                    Log.e("SZIP******", "sync mountaineer");
                    datas = CommandUtil.getCommandbyteArray(0x0E, 8,
                            0, true);
                    break;

                case 0x0F:
                    //跳伞
                    Log.e("SZIP******", "sync parachute");
                    datas = CommandUtil.getCommandbyteArray(0x0F, 8,
                            0, true);
                    break;

                case 0x10:
                    //高尔夫
                    Log.e("SZIP******", "sync golf");
                    datas = CommandUtil.getCommandbyteArray(0x10, 8,
                            0, true);
                    break;

                case 0x11:
                    //冲浪
                    Log.e("SZIP******", "sync surf");
                    datas = CommandUtil.getCommandbyteArray(0x11, 8,
                            0, true);
                    break;

                case 0x14:
                    //跑步机
                    Log.e("SZIP******", "sync treadmill");
                    datas = CommandUtil.getCommandbyteArray(0x14, 8,
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
                CommandUtil.getCommandbyteArray(0x30, 20, 12, true),bleWriteResponse);
    }

    public void writeForGetDeviceState(){
        if (indexData==null){
            ClientManager.getClient().write(mMac,serviceUUID,UUID.fromString(Config.char1),
                    CommandUtil.getCommandbyteArray(0x32, 8,
                            0, true),bleWriteResponse);
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
