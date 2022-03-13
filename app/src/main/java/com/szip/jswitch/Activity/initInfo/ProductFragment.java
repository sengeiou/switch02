package com.szip.jswitch.Activity.initInfo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.szip.jswitch.Activity.bodyFat.BodyFatActivity;
import com.szip.jswitch.Activity.main.MainActivity;
import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.Model.UserInfo;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.Util.ProgressHudModel;

import java.io.IOException;
import java.util.Calendar;

public class ProductFragment extends BaseFragment implements View.OnClickListener {
    private MyApplication app;
    private int productId = 0;
    private RadioGroup productRg;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_product;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();
        productRg = getView().findViewById(R.id.productRg);
        initEvent();
    }

    private void initEvent() {
        getView().findViewById(R.id.nextBtn).setOnClickListener(this);
        getView().findViewById(R.id.watchTv).setOnClickListener(this);
        getView().findViewById(R.id.scaleTv).setOnClickListener(this);
        getView().findViewById(R.id.bothTv).setOnClickListener(this);
        getView().findViewById(R.id.watchRb).setOnClickListener(this);
        getView().findViewById(R.id.scaleRb).setOnClickListener(this);
        getView().findViewById(R.id.bothRb).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.watchTv:
            case R.id.watchRb:
                productId = 0;
                productRg.check(R.id.watchRb);
                break;
            case R.id.scaleTv:
            case R.id.scaleRb:
                productId = 1;
                productRg.check(R.id.scaleRb);
                break;
            case R.id.bothTv:
            case R.id.bothRb:
                productId = 2;
                productRg.check(R.id.bothRb);
                break;
            case R.id.nextBtn:
                app.setProductId(productId);
                if (app.getUserInfo().getBirthday()==null){
                    ((InitInfoActivity)getActivity()).unitPage();
                }else {
                    MathUitl.saveIntData(getActivity(),"productId",productId).commit();
                    MathUitl.saveStringData(getActivity(),"token",HttpMessgeUtil.getInstance().getToken()).commit();
                    if (productId==1){
                        Intent intentmain = new Intent(getActivity(), BodyFatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentmain);
                    }else {
                        UserInfo userInfo = app.getUserInfo();
                        if (userInfo.getDeviceCode()!=null&&!userInfo.getDeviceCode().equals("")){
                            if (MyApplication.getInstance().getDialGroupId().equals("0")){
                                BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
                                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(userInfo.getDeviceCode());
                                if (device!=null&&device.getName()!=null)
                                    MyApplication.getInstance().setDeviceConfig(device.getName().indexOf("_LE")>=0?
                                            device.getName().substring(0,device.getName().length()-3):
                                            device.getName());
                            }
                        }

                        if (userInfo.getPhoneNumber()!=null||userInfo.getEmail()!=null){
                            //获取云端数据
                            try {
                                HttpMessgeUtil.getInstance().getForDownloadReportData(Calendar.getInstance().getTimeInMillis()/1000+"",30+"");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent intentmain = new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentmain);
                    }
                }
                break;
        }
    }
}
