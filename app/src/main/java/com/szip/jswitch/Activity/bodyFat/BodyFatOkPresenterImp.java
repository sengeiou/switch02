package com.szip.jswitch.Activity.bodyFat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.szip.jswitch.BLE.ClientManager;
import com.szip.jswitch.DB.SaveDataUtil;
import com.szip.jswitch.DB.dbModel.BodyFatData;
import com.szip.jswitch.Model.BodyFatModel;
import com.szip.jswitch.Model.UserInfo;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.Util.LogUtil;
import com.szip.jswitch.Util.MathUitl;


import java.util.Calendar;

import chipsea.bias.v235.CSBiasAPI;


public class BodyFatOkPresenterImp implements IBodyFatPresenter{

    private long subTime = 0;

    private Context context;
    private IBodyFatView iBodyFatView;


    private float weight = 20;
    private int r = 2000;

    public BodyFatOkPresenterImp(Context context, IBodyFatView iBodyFatView) {
        this.context = context;
        this.iBodyFatView = iBodyFatView;
    }


    @Override
    public void initBle() {
        if (iBodyFatView!=null)
            iBodyFatView.initBleFinish(true);
    }

    @Override
    public void startScan(UserInfo userInfo) {
        if (iBodyFatView!=null)
            iBodyFatView.updateState(context.getString(R.string.stating));
        final SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(30000, 1).build();
        ClientManager.getClient().search(request, mSearchResponseDevice);
    }

    @Override
    public void disconnectDevice() {
        ClientManager.getClient().stopSearch();
    }

    @Override
    public void saveData() {
        UserInfo info = MyApplication.getInstance().getUserInfo();

        CSBiasAPI.CSBiasV235Resp cSBiasV235Resp = CSBiasAPI.cs_bias_v235(0, info.getSex(), DateUtil.getAge(info.getBirthday(),"yyyy-MM-dd"),
                info.getUnit()==0?info.getHeight():MathUitl.inch2Cm(info.getHeightBritish()),
                (int) (weight*10), r, 2018);

//        StringBuilder sb = new StringBuilder();
        BodyFatData bodyFatData = new BodyFatData();
        if (cSBiasV235Resp.result == 0) {
            //??????
            try {
//                sb.append("??????\r\n");
//                sb.append("??????:" + info.getSex() + " ??????:" + (info.getUnit()==0?info.getHeight():MathUitl.inch2Cm(info.getHeightBritish()))
//                        + " ??????:" + DateUtil.getAge(info.getBirthday(),"yyyy-MM-dd") + " ??????:" + r + " ??????:" + weight + "\r\n");
//                sb.append("**************************************\r\n");
//                sb.append("**************************************\r\n");
                bodyFatData.time = System.currentTimeMillis()/1000;
                bodyFatData.weight = weight;
                bodyFatData.idealWeight = weight+(float) cSBiasV235Resp.data.WC;
                bodyFatData.weightRange = "0,50.4,56.7,69.3,75.6,113.4";
//                sb.append("?????????%:" + cSBiasV235Resp.data.BFP + "\r\n");
                bodyFatData.ratioOfFat = (float) cSBiasV235Resp.data.BFP;
                bodyFatData.ratioOfFatRange = "1.0,11.0,17.0,22.0,27.0,40.5";
                bodyFatData.weightOfFat = (float) (cSBiasV235Resp.data.BFP*weight/100);
                bodyFatData.fatFreeBodyWeight = weight-bodyFatData.weightOfFat;
//                sb.append("?????????kg:" + cSBiasV235Resp.data.SLM + "\r\n");
                bodyFatData.weightOfMuscle = (float) cSBiasV235Resp.data.SLM;
                bodyFatData.ratioOfMuscle = (float) (cSBiasV235Resp.data.SLM/weight*100);
                bodyFatData.ratioOfMuscleRange = "0,42,54,100";
//                sb.append("?????????%:" + cSBiasV235Resp.data.BWP  + "\r\n");
                bodyFatData.weightOfWater = (float) (cSBiasV235Resp.data.BWP*weight/100f);
//                sb.append("?????????:" + cSBiasV235Resp.data.BMC + "\r\n");
                bodyFatData.weightOfBone = (float) cSBiasV235Resp.data.BMC;
                bodyFatData.weightOfBoneRange = "0,2.3,2.7,4.1";
//                sb.append("??????????????????:" + cSBiasV235Resp.data.VFR + "\r\n");
                bodyFatData.levelOfVisceralFat = (float) cSBiasV235Resp.data.VFR;
                bodyFatData.levelOfVisceralFatRange = "0,10,14,21";
//                sb.append("?????????%:" + cSBiasV235Resp.data.PP+ "\r\n");
                bodyFatData.ratioOfProtein = (float) cSBiasV235Resp.data.PP;
                bodyFatData.ratioOfProteinRange = "0,16.0,18.0,27.0";
//                sb.append("?????????kg:" + cSBiasV235Resp.data.SMM+ "\r\n");
                bodyFatData.ratioOfSkeletalMuscle = (float) (cSBiasV235Resp.data.SMM/weight*100);
                bodyFatData.ratioOfSkeletalMuscleRange = "0,35.0,45.0,100.0";
//                sb.append("????????????:" + cSBiasV235Resp.data.BMR + "\r\n");
                bodyFatData.bmr = cSBiasV235Resp.data.BMR;
                bodyFatData.bmrRange = "0,1329.6,1994.4";
//                sb.append("??????????????????:" + cSBiasV235Resp.data.BMI + "\r\n");
                bodyFatData.bmi = (float) cSBiasV235Resp.data.BMI;
                bodyFatData.bmiRange = "0,18.5,24.0,28.0,42.0";
//                sb.append("????????????:" + cSBiasV235Resp.data.MA + "\r\n");
                bodyFatData.ageOfBody = cSBiasV235Resp.data.MA;
//                sb.append("??????:" + cSBiasV235Resp.data.SBC + "\r\n");
                bodyFatData.score = cSBiasV235Resp.data.SBC;

//                sb.append("????????????:" + cSBiasV235Resp.data.MC + "\r\n");
//                sb.append("????????????:" + cSBiasV235Resp.data.WC + "\r\n");
//                sb.append("????????????:" + cSBiasV235Resp.data.FC + "\r\n");

//                sb.append("**************************************\r\n");
            } catch (Exception ex) {
//                sb.append("???????????????????????????" + ex.getLocalizedMessage());
            }
        } else {
//            sb.append("???????????????????????????" + cSBiasV235Resp.result);
        }

//        LogUtil.getInstance().logd("data******","str = "+sb.toString());

        boolean isSccuess = SaveDataUtil.newInstance().saveBodyFat(bodyFatData);
        if(isSccuess&&iBodyFatView!=null)
            iBodyFatView.updateView();
    }


    //????????????
    private final SearchResponse mSearchResponseDevice = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            subTime = Calendar.getInstance().getTimeInMillis();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if (device.scanRecord!=null&&(device.scanRecord[9]==0x11||device.scanRecord[9]==0x21||device.scanRecord[9]==0x01)){
                LogUtil.getInstance().logd("data******","raw = "+DateUtil.byteToHexString(device.scanRecord));
                byte[] raw = device.scanRecord;
                if ((raw[10]&0x01)==0x01){
                    LogUtil.getInstance().logd("data******","???????????????");
                    ClientManager.getClient().stopSearch();
                    byte tag = (byte) (raw[10]>>1&0x3);
                    weight = (raw[5] & 0xff) + ((raw[4] & 0xFF) << 8);
                    if (tag == 0){
                        weight = weight/10;
                    }else if (tag == 2){
                        weight = weight/100;
                    }
                    r = (raw[7] & 0xff) + ((raw[6] & 0xFF) << 8);
                    LogUtil.getInstance().logd("data******","?????? = "+String.format("%.1f",weight)+" ;?????? = "+r);
                    if (r==0||weight<20){
                        Toast.makeText(context,context.getString(R.string.measurement),Toast.LENGTH_SHORT).show();
                    }else {
                        if (iBodyFatView!=null)
                            iBodyFatView.showTipDialog(weight,1);
                    }

                }
            }
        }



        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSearchStopped() {
            if (Calendar.getInstance().getTimeInMillis()-subTime<2500){
                final SearchRequest request = new SearchRequest.Builder()
                        .searchBluetoothLeDevice(30000, 1).build();
                ClientManager.getClient().search(request, mSearchResponseDevice);
            }
            else{
                if (iBodyFatView!=null)
                    iBodyFatView.updateState(context.getString(R.string.stated));
            }

        }

        @Override
        public void onSearchCanceled() {
            if (iBodyFatView!=null)
                iBodyFatView.updateState(context.getString(R.string.stated));
        }
    };

}
