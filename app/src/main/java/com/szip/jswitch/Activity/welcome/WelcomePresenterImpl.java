package com.szip.jswitch.Activity.welcome;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mediatek.leprofiles.LocalBluetoothLEManager;
import com.mediatek.wearable.WearableManager;
import com.szip.jswitch.DB.SaveDataUtil;
import com.szip.jswitch.DB.dbModel.HealthyConfig;
import com.szip.jswitch.DB.dbModel.SportWatchAppFunctionConfigDTO;
import com.szip.jswitch.Model.HttpBean.DeviceConfigBean;
import com.szip.jswitch.Model.HttpBean.UserInfoBean;
import com.szip.jswitch.Model.UserInfo;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.JsonGenericsSerializator;
import com.szip.jswitch.Util.LogUtil;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.View.MyAlerDialog;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;
import static com.szip.jswitch.MyApplication.FILE;
import static com.szip.jswitch.MyApplication.getInstance;

public class WelcomePresenterImpl implements IWelcomePresenter{

    private IWelcomeView iWelcomeView;
    private Handler handler;

    public WelcomePresenterImpl(IWelcomeView iWelcomeView) {
        this.iWelcomeView = iWelcomeView;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void checkPrivacy(Context context) {

        final SharedPreferences sharedPreferences = context.getSharedPreferences(FILE,MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isFirst",true)){
            MyAlerDialog.getSingle().showAlerDialogWithPrivacy(context.getString(R.string.privacy1), context.getString(R.string.privacyTip),
                    null, null, false, new MyAlerDialog.AlerDialogOnclickListener() {
                        @Override
                        public void onDialogTouch(boolean flag) {
                            if (flag){
                                if (iWelcomeView!=null)
                                    iWelcomeView.checkPrivacyResult(true);
                            }else{
                                if (iWelcomeView!=null)
                                    iWelcomeView.checkPrivacyResult(false);
                            }
                        }
                    },context);
        }else {
            if (iWelcomeView!=null)
                iWelcomeView.checkPrivacyResult(true);
        }

    }

    @Override
    public void initBle(Context context) {
        //切换成GATT模式
        if (WearableManager.getInstance().getWorkingMode() == WearableManager.MODE_SPP)
            WearableManager.getInstance().switchMode();
        if (!MainService.isMainServiceActive()) {
            context.startService(
                    new Intent(context, MainService.class));
        }
        if (iWelcomeView!=null)
            iWelcomeView.initBleFinish();
        LogUtil.getInstance().logd("SZIP******","初始化蓝牙");
    }

    @Override
    public void initDeviceConfig() {
        try {
            HttpMessgeUtil.getInstance().getDeviceConfig(new GenericsCallback<DeviceConfigBean>(new JsonGenericsSerializator()) {
                @Override
                public void onError(Call call, Exception e, int id) {
                    if (iWelcomeView!=null)
                        iWelcomeView.initDeviceConfigFinish();
                }

                @Override
                public void onResponse(DeviceConfigBean response, int id) {
                    if (response.getCode()==200){
                        ArrayList<HealthyConfig> data = new ArrayList<>();
                        SaveDataUtil.newInstance().saveConfigListData(response.getData());
                        for (SportWatchAppFunctionConfigDTO configDTO:response.getData()){
                            configDTO.getHealthMonitorConfig().identifier = configDTO.identifier;
                            data.add(configDTO.getHealthMonitorConfig());
                        }
                        SaveDataUtil.newInstance().saveHealthyConfigListData(data);
                        if (iWelcomeView!=null)
                            iWelcomeView.initDeviceConfigFinish();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initUserInfo(final Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(FILE,MODE_PRIVATE);
        //判断登录状态
        String token = sharedPreferences.getString("token",null);
        if (token!=null){//已登录
            HttpMessgeUtil.getInstance().setToken(token);
            try {
                HttpMessgeUtil.getInstance().getForGetInfo(new GenericsCallback<UserInfoBean>(new JsonGenericsSerializator()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        getInstance().setUserInfo(MathUitl.loadInfoData(sharedPreferences));
                        if (iWelcomeView!=null)
                            iWelcomeView.initUserinfoFinish(false);
                    }

                    @Override
                    public void onResponse(UserInfoBean response, int id) {
                        if (response.getCode() == 200){
                            getInstance().setUserInfo(response.getData());
                            if (response.getData().getDeviceCode()!=null&&!response.getData().getDeviceCode().equals("")){
                                uploadData(context,response.getData());
                                if (MyApplication.getInstance().getDialGroupId().equals("0")){
                                    BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                                    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(response.getData().getDeviceCode());
                                    if (device!=null&&device.getName()!=null)
                                        MyApplication.getInstance().setDeviceConfig(device.getName().indexOf("_LE")>=0?
                                                device.getName().substring(0,device.getName().length()-3):
                                                device.getName());
                                }
                            }
                            if (iWelcomeView!=null)
                                iWelcomeView.initUserinfoFinish(false);
                        }else if (response.getCode() == 401){
                            sharedPreferences.edit().putString("token",null).commit();
                            MathUitl.showToast(context,context.getString(R.string.tokenTimeOut));
                            if (iWelcomeView!=null)
                                iWelcomeView.initUserinfoFinish(true);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                getInstance().setUserInfo(MathUitl.loadInfoData(sharedPreferences));
                if (iWelcomeView!=null)
                    iWelcomeView.initUserinfoFinish(false);
            }
        }else {
            if (iWelcomeView!=null)
                iWelcomeView.initUserinfoFinish(true);
        }
    }

    @Override
    public void setViewDestory() {
        iWelcomeView = null;
    }

    /**
     * 每次打开APP且用户已经登陆的时候上传数据
     * */
    private void uploadData(Context context, UserInfo userInfo) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(FILE,MODE_PRIVATE);
        if (userInfo!=null&&userInfo.getDeviceCode()!=null){
            try {
                String datas = MathUitl.getStringWithJson(sharedPreferences);
                HttpMessgeUtil.getInstance().postForUpdownReportData(datas);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
