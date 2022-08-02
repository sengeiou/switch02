package com.szip.jswitch.Activity.bodyFat;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.szip.jswitch.Activity.dial.ISelectDialView;
import com.szip.jswitch.DB.SaveDataUtil;
import com.szip.jswitch.Model.BodyFatModel;
import com.szip.jswitch.Model.HttpBean.DialBean;
import com.szip.jswitch.Model.UserInfo;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.Util.MathUitl;
import com.vtrump.vtble.VTDevice;
import com.vtrump.vtble.VTDeviceManager;
import com.vtrump.vtble.VTDeviceScale;
import com.vtrump.vtble.VTModelIdentifier;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class BodyFatPresenterImp implements IBodyFatPresenter{

    private Context context;
    private IBodyFatView iBodyFatView;
    private VTDeviceManager mBleManager;
    private VTDeviceScale mDevice;
    private JSONObject userJson;
    private BodyFatModel bodyFatModel;

    public BodyFatPresenterImp(Context context, IBodyFatView iBodyFatView) {
        this.context = context;
        this.iBodyFatView = iBodyFatView;
    }


    @Override
    public void initBle() {
        mBleManager = VTDeviceManager.getInstance();
//         设置您的key，具体参数请联系相关人员
        mBleManager.setKey("B3WHRF2YT58KKJGW");
        mBleManager.setDeviceManagerListener(vtDeviceManagerListener);
        boolean isInitSuccess = mBleManager.startBle(context);
        if (iBodyFatView!=null)
            iBodyFatView.initBleFinish(isInitSuccess);
    }

    @Override
    public void startScan(UserInfo userInfo) {
        if (iBodyFatView!=null)
            iBodyFatView.updateState(context.getString(R.string.stating));
        ArrayList<VTModelIdentifier> list = new ArrayList<>();
        list.add(new VTModelIdentifier((byte) 0x03, (byte) 0x03, (byte) 0x06, (byte) 0x0f));
        mBleManager.disconnectAll();
        mBleManager.startScan(30, list);
        // 构造一个有效用户
        userJson = new JSONObject();
        try {
            userJson.put("age", DateUtil.getAge(userInfo.getBirthday(),"yyyy-MM-dd"));
            userJson.put("height", userInfo.getUnit()==0?userInfo.getHeight():MathUitl.inch2Cm(userInfo.getHeightBritish()));
            // 男:0; 女:1; 男运动员:2; 女运动员:3
            userJson.put("gender", userInfo.getSex()==1?0:1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnectDevice() {
        if (mBleManager != null) {
            mBleManager.releaseBleManager();
        }
    }

    @Override
    public void saveData() {
        boolean isSccuess = SaveDataUtil.newInstance().saveBodyFat(bodyFatModel);
        if(isSccuess&&iBodyFatView!=null)
            iBodyFatView.updateView();

    }

    /**
     * 数据回调
     */
    private VTDeviceScale.VTDeviceScaleListener listener = new VTDeviceScale.VTDeviceScaleListener() {
        @Override
        public void onDataAvailable(final String res) {
            super.onDataAvailable(res);
            Log.i("DATA******","data = "+res);
            Gson gson = new Gson();
            bodyFatModel = gson.fromJson(res, BodyFatModel.class);
            if (bodyFatModel!=null&&bodyFatModel.getCode()==0){
                if (iBodyFatView!=null)
                    iBodyFatView.showTipDialog(bodyFatModel.getDetails().getWeight(),2);
            }else if (bodyFatModel!=null&&bodyFatModel.getCode()!=200){
                Toast.makeText(context,context.getString(R.string.measurement),Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onRssiReceived(int i) {
            super.onRssiReceived(i);
        }
    };

    private VTDeviceManager.VTDeviceManagerListener vtDeviceManagerListener = new VTDeviceManager.VTDeviceManagerListener() {
        @Override
        public void onInited() {

        }

        @Override
        public void onDeviceDiscovered(VTDevice vtDevice, int i) {

        }

        @Override
        public void onDeviceConnected(VTDevice vtDevice) {

        }

        @Override
        public void onDeviceDisconnected(VTDevice vtDevice) {

        }

        @Override
        public void onDeviceServiceDiscovered(VTDevice vtDevice) {
            mDevice = (VTDeviceScale) vtDevice;
            mDevice.setScaleDataListener(listener);
            mDevice.setmUserInfo(userJson);
        }

        @Override
        public void onScanStop() {
            if (iBodyFatView!=null)
                iBodyFatView.updateState(context.getString(R.string.stated));
            Log.i("DATA******","scan is stopped");
        }

        @Override
        public void onScanTimeOut() {
            Log.i("DATA******","scan is timeout");
        }

        @Override
        public void onDeviceAdvDiscovered(VTDevice vtDevice) {
            mDevice = (VTDeviceScale) vtDevice;
            mDevice.setScaleDataListener(listener);
            mDevice.setmUserInfo(userJson);
        }
    };
}
