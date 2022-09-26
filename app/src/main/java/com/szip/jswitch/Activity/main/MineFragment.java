package com.szip.jswitch.Activity.main;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mediatek.wearable.WearableManager;
import com.szip.jswitch.Activity.NotificationActivity;
import com.szip.jswitch.Activity.bodyFat.BodyFatActivity;
import com.szip.jswitch.Activity.help.FaqActivity;
import com.szip.jswitch.BLE.BleClient;
import com.szip.jswitch.Activity.AboutActivity;
import com.szip.jswitch.Activity.LoginActivity;
import com.szip.jswitch.Activity.SeachingActivity;
import com.szip.jswitch.Activity.dial.SelectDialActivity;
import com.szip.jswitch.Activity.UnitSelectActivity;
import com.szip.jswitch.Activity.userInfo.UserInfoActivity;
import com.szip.jswitch.DB.SaveDataUtil;
import com.szip.jswitch.Fragment.BaseFragment;
import com.szip.jswitch.Model.EvenBusModel.ConnectState;
import com.szip.jswitch.Model.EvenBusModel.PlanModel;
import com.szip.jswitch.Model.EvenBusModel.UpdateElc;
import com.szip.jswitch.Model.HttpBean.BaseApi;
import com.szip.jswitch.Model.UserInfo;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.JsonGenericsSerializator;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.Util.ProgressHudModel;
import com.szip.jswitch.View.CharacterPickerWindow;
import com.szip.jswitch.View.CircularImageView;
import com.szip.jswitch.View.MyAlerDialog;
import com.szip.jswitch.View.character.OnOptionChangedListener;
import com.szip.jswitch.BLE.EXCDController;
import com.zhy.http.okhttp.callback.GenericsCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;
import static com.szip.jswitch.MyApplication.FILE;

/**
 * Created by Administrator on 2019/12/1.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener{
    private SharedPreferences sharedPreferencesp;
    private UserInfo userInfo;
    private MyApplication app;

    private CircularImageView pictureIv;
    private TextView userNameTv,stateTv,stepPlanTv,sleepPlanTv,deviceTv;

    private Switch blePhotoSwitch,heartSwitch;

    private CharacterPickerWindow window,window1;

    private int STEPFLAG = 1,SLEEPFLAG = 2;
    private int stepPlan = 0,sleepPlan = 0;
    private boolean unbind = false;
    private boolean isUpdatePlan = false;

    private View updateView;

    private LinearLayout elcLl;
    private ImageView elcIv;
    private TextView nameTv;

    /**
     * 下拉菜单实例
     * */
    private PopupWindow mPop;

    /**
     * 选项列表
     * */
    private ListView MenuItem;
    private ArrayAdapter<String> ItemAdapter;
    private List<String> ItemValue;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        app = (MyApplication) getActivity().getApplicationContext();
        initView();
        initEvent();
        initWindow();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onResume() {
        super.onResume();
        userInfo = app.getUserInfo();
        EventBus.getDefault().register(this);
        if (userInfo.getDeviceCode()==null){
            deviceTv.setText("");
            stateTv.setText(getString(R.string.addDevice));
        } else {
            if (MainService.getInstance().getState() == WearableManager.STATE_CONNECTED){
                deviceTv.setText(userInfo.getDeviceCode());
                stateTv.setText(getString(R.string.connected));
            }else if (MainService.getInstance().getState() == WearableManager.STATE_CONNECT_LOST||
                    MainService.getInstance().getState() == WearableManager.STATE_CONNECT_FAIL){
                deviceTv.setText("");
                stateTv.setText(getString(R.string.disConnect));
            }else if (MainService.getInstance().getState() == WearableManager.STATE_CONNECTING){
                deviceTv.setText("");
                stateTv.setText(getString(R.string.connectting));
            }else {
                deviceTv.setText("");
                stateTv.setText(getString(R.string.disConnect));
            }
        }
        updateElc(app.getElc());
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleConnectStateChange(ConnectState connectBean){
        if (connectBean.getState() == WearableManager.STATE_CONNECTED){
            deviceTv.setText(userInfo.getDeviceCode());
            stateTv.setText(getString(R.string.connected));
        }else if (connectBean.getState()  == WearableManager.STATE_CONNECT_LOST||
                connectBean.getState()  == WearableManager.STATE_CONNECT_FAIL){
            deviceTv.setText("");
            if (unbind){
                unbind = false;
                ProgressHudModel.newInstance().diss();
                userInfo.setDeviceCode(null);
                MainService.getInstance().stopConnect();
                SaveDataUtil.newInstance().clearDB();
                getActivity().startActivity(new Intent(getActivity(),SeachingActivity.class));
            }else
                stateTv.setText(getString(R.string.disConnect));
        }else if (connectBean.getState()  == WearableManager.STATE_CONNECTING){
            deviceTv.setText("");
            stateTv.setText(getString(R.string.connectting));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePlan(PlanModel planModel){
       if(stepPlanTv!=null){
           try {
               isUpdatePlan = false;
               HttpMessgeUtil.getInstance().postForSetStepsPlan(planModel.getData()+"",STEPFLAG,callback);
               stepPlan = Integer.valueOf(planModel.getData());
           } catch (IOException e) {
           }

       }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateElc(UpdateElc updateElc){
        updateElc(updateElc.getElc());
    }


    private void updateElc(int elc){
        if (elc==0){
            elcLl.setVisibility(View.GONE);
        }else if (elc==1){
            elcLl.setVisibility(View.VISIBLE);
            elcIv.setImageResource(R.mipmap.my_bat_0);
        }else if (elc==2){
            elcLl.setVisibility(View.VISIBLE);
            elcIv.setImageResource(R.mipmap.my_bat_1);
        }else if (elc==3){
            elcLl.setVisibility(View.VISIBLE);
            elcIv.setImageResource(R.mipmap.my_bat_2);
        }else if (elc==4){
            elcLl.setVisibility(View.VISIBLE);
            elcIv.setImageResource(R.mipmap.my_bat_3);
        }else if (elc==5){
            elcLl.setVisibility(View.VISIBLE);
            elcIv.setImageResource(R.mipmap.my_bat_4);
        }
    }

    /**
     * 初始化下拉菜单
     * */
    private void initSelectPopup() {
        if(MainService.getInstance().getState()== WearableManager.STATE_CONNECTED){
            ItemValue= new ArrayList<>(Arrays.asList(getString(R.string.unline),getString(R.string.unBind)));
        }else {
            ItemValue= new ArrayList<>(Arrays.asList(getString(R.string.line),getString(R.string.unBind)));
        }


        MenuItem = new ListView(getActivity());
        ItemAdapter = new ArrayAdapter<>(getActivity(),R.layout.popwindow_layout2,ItemValue);
        MenuItem.setAdapter(ItemAdapter);
        MenuItem.setBackgroundResource(R.color.space);

        MenuItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==1){
                    try {
                        String datas = MathUitl.getStringWithJson(getActivity().getSharedPreferences(FILE,MODE_PRIVATE));
                        HttpMessgeUtil.getInstance().postForUpdownReportData(datas);
                        ProgressHudModel.newInstance().show(getContext(),getString(R.string.waitting),getString(R.string.httpError)
                                ,3000);
                        if (MainService.getInstance().getState()==WearableManager.STATE_CONNECTED){
                            unbind = true;
                            MainService.getInstance().stopConnect();
                        }else {
                            unbind = false;
                            ProgressHudModel.newInstance().diss();
                            userInfo.setDeviceCode(null);
                            MainService.getInstance().stopConnect();
                            SaveDataUtil.newInstance().clearDB();
                            getActivity().startActivity(new Intent(getActivity(),SeachingActivity.class));
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    unbind = false;
                    if (MainService.getInstance().getState()== WearableManager.STATE_CONNECTED){
                        MainService.getInstance().stopConnect();
                    }else {
                        MainService.getInstance().startConnect();
                    }
                }
                mPop.dismiss();
            }
        });


        WindowManager wm = (WindowManager)getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        mPop = new PopupWindow(MenuItem, width/2, ActionBar.LayoutParams.WRAP_CONTENT, true);

        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.bg_corner);
        mPop.setBackgroundDrawable(drawable);
        mPop.setFocusable(true);
        mPop.setOutsideTouchable(true);
        mPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 关闭popup窗口
                mPop.dismiss();
            }
        });
    }

    /**
     * 初始化选择器
     * */
    private void initWindow() {
        //步行计划选择器
        window = new CharacterPickerWindow(getContext(),getString(R.string.stepPlan));

        final List<String> stepList = MathUitl.getStepPlanList();
        //初始化选项数据
        window.getPickerView().setPicker(stepList);
        //设置默认选中的三级项目
        window.setCurrentPositions(stepList.size()/2, 0, 0);
        //监听确定选择按钮
        window.setOnoptionsSelectListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                try {
                    isUpdatePlan = true;
                    ProgressHudModel.newInstance().show(getContext(),getString(R.string.waitting),getString(R.string.httpError),3000);
                    HttpMessgeUtil.getInstance().postForSetStepsPlan(stepList.get(option1),STEPFLAG,callback);
                    stepPlan = Integer.valueOf(stepList.get(option1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        //睡眠计划选择器
        window1 = new CharacterPickerWindow(getContext(),getString(R.string.sleepPlan));

        final ArrayList<String> sleepList = MathUitl.getSleepPlanList();
        window1.getPickerView().setText("h","");
        //初始化选项数据
        window1.getPickerView().setPicker(sleepList);
        //设置默认选中的三级项目
        window1.setCurrentPositions(sleepList.size()/2, 0, 0);
        //监听确定选择按钮
        window1.setOnoptionsSelectListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                try {
                    ProgressHudModel.newInstance().show(getContext(),getString(R.string.waitting),getString(R.string.httpError),3000);
                    HttpMessgeUtil.getInstance().postForSetSleepPlan((int)(Float.valueOf(sleepList.get(option1))*60)+"",SLEEPFLAG,callback);
                    sleepPlan = (int)(Float.valueOf(sleepList.get(option1))*60);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 初始化视图
     * */
    private void initView() {
        nameTv = getView().findViewById(R.id.nameTv);
        elcIv = getView().findViewById(R.id.elcIv);
        elcLl = getView().findViewById(R.id.elcLl);
        deviceTv = getView().findViewById(R.id.deviceTv);
        userNameTv = getView().findViewById(R.id.userNameTv);
        pictureIv = getView().findViewById(R.id.pictureIv);
        stateTv = getView().findViewById(R.id.deviceNameTv);
        stepPlanTv = getView().findViewById(R.id.stepPlanTv);
        sleepPlanTv = getView().findViewById(R.id.sleepPlanTv);
        blePhotoSwitch = getView().findViewById(R.id.blePhotoSwitch);
        heartSwitch = getView().findViewById(R.id.heartSwitch);
        updateView = getView().findViewById(R.id.updateView);
        if (app.isNewVersion()){
            updateView.setVisibility(View.VISIBLE);
        }else{
            updateView.setVisibility(View.GONE);
        }
        if (app.getProductId()!=2)
            getView().findViewById(R.id.bodyFatLl).setVisibility(View.GONE);
        else
            getView().findViewById(R.id.bodyFatLl).setVisibility(View.VISIBLE);
    }

    /**
     * 初始化事件监听
     * */
    private void initEvent() {
        getView().findViewById(R.id.pictureIv).setOnClickListener(this);
        getView().findViewById(R.id.userNameTv).setOnClickListener(this);
        getView().findViewById(R.id.deviceLl).setOnClickListener(this);
        getView().findViewById(R.id.stepPlanLl).setOnClickListener(this);
        getView().findViewById(R.id.sleepPlanLl).setOnClickListener(this);
        getView().findViewById(R.id.notificationLl).setOnClickListener(this);
        getView().findViewById(R.id.findLl).setOnClickListener(this);
        getView().findViewById(R.id.blePhoneLl).setOnClickListener(this);
        getView().findViewById(R.id.unitLl).setOnClickListener(this);
        getView().findViewById(R.id.aboutLl).setOnClickListener(this);
        getView().findViewById(R.id.faceLl).setOnClickListener(this);
        getView().findViewById(R.id.logoutLl).setOnClickListener(this);
        getView().findViewById(R.id.bodyFatLl).setOnClickListener(this);
        blePhotoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED||
                                    getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE
                                }, 103);
                            }else {
                                app.setCamerable(isChecked);
                            }
                        }else {
                            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                }, 102);
                            }else {
                                app.setCamerable(isChecked);
                            }
                        }
                    }else {
                        app.setCamerable(isChecked);
                    }
                }else {
                    app.setCamerable(isChecked);
                }
            }
        });

        heartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(MainService.getInstance().getState()!=3){
                    showToast(getString(R.string.lostDevice));
                    heartSwitch.setChecked(app.isHeartSwitch());
                } else {
                    app.setHeartSwitch(isChecked);
                    BleClient.getInstance().writeForSetHeartSwitch();
                }
            }
        });
    }

    /**
     * 初始化数据
     * */
    private void initData() {
        nameTv.setText(userInfo.getProduct());
        userNameTv.setText(userInfo.getUserName());
        stepPlanTv.setText(userInfo.getStepsPlan()+"");
        sleepPlanTv.setText(String.format(Locale.ENGLISH,"%.1fh",userInfo.getSleepPlan()/60f));

        if (userInfo.getAvatar()!=null)
            Glide.with(this).load(userInfo.getAvatar()).into(pictureIv);
        else
            pictureIv.setImageResource(R.mipmap.head);

        if (app.isMtk()){
            getView().findViewById(R.id.heartSwitchLl).setVisibility(View.GONE);
        }else {
            getView().findViewById(R.id.heartSwitchLl).setVisibility(View.VISIBLE);
        }


        if (app.isCamerable())
            blePhotoSwitch.setChecked(true);
        else
            blePhotoSwitch.setChecked(false);

        if (app.isHeartSwitch())
            heartSwitch.setChecked(true);
        else
            heartSwitch.setChecked(false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102){
            int code = grantResults[0];
            if (!(code == PackageManager.PERMISSION_GRANTED)){
                showToast(getString(R.string.permissionErrorForCamare));
                app.setCamerable(false);
                blePhotoSwitch.setChecked(false);
            }else {
                app.setCamerable(true);
            }
        }
    }

    /**
     * 点击事件监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bodyFatLl:
                startActivity(new Intent(getActivity(), BodyFatActivity.class));
                break;
            case R.id.pictureIv:
            case R.id.userNameTv:
                if (userInfo.getPhoneNumber()==null&&userInfo.getEmail()==null)
                    showToast(getString(R.string.visiter));
                else
                    startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
            case R.id.deviceLl:
                if (userInfo.getDeviceCode()==null){
                    startActivity(new Intent(getContext(),SeachingActivity.class));
                }else {
                    if (MainService.getInstance().getState()== WearableManager.STATE_CONNECTING){
                        showToast(getString(R.string.connectting));
                    }else {
                        initSelectPopup();
                        if (mPop != null && !mPop.isShowing()) {
                            mPop.showAsDropDown(getView().findViewById(R.id.device), 10, 10);
                        }
                    }
                }
                break;
            case R.id.stepPlanLl:
                window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.sleepPlanLl:
                window1.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.notificationLl:
                startActivity(new Intent(getActivity(), NotificationActivity.class));
                break;
            case R.id.findLl:
                if(MainService.getInstance().getState()!=3)
                    showToast(getString(R.string.lostDevice));
                else{
                    ((MainActivity)getActivity()).showMyToast(getString(R.string.sendOK));
                    if (app.isMtk())
                        EXCDController.getInstance().writeForFindDevice();
                    else
                        BleClient.getInstance().writeForFindWatch();
                }
                break;
            case R.id.blePhoneLl:
                startActivity(new Intent(getActivity(), FaqActivity.class));
                break;
            case R.id.unitLl:
                startActivity(new Intent(getActivity(), UnitSelectActivity.class));
                break;
            case R.id.aboutLl:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.faceLl:
                if(MainService.getInstance().getState()!=3)
                    showToast(getString(R.string.lostDevice));
                else{
                    startActivity(new Intent(getActivity(), SelectDialActivity.class));
                }
                break;
            case R.id.logoutLl:
                MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.logoutTip), null, null,
                        false, new MyAlerDialog.AlerDialogOnclickListener() {
                            @Override
                            public void onDialogTouch(boolean flag) {
                                if (flag){
                                    if (userInfo.getDeviceCode()!=null){
                                        String datas = MathUitl.getStringWithJson(getActivity().getSharedPreferences(FILE,MODE_PRIVATE));
                                        try {
                                            HttpMessgeUtil.getInstance().postForUpdownReportData(datas);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (sharedPreferencesp==null)
                                        sharedPreferencesp = getActivity().getSharedPreferences(FILE,MODE_PRIVATE);
                                    userInfo.setDeviceCode(null);
                                    SharedPreferences.Editor editor = sharedPreferencesp.edit();
                                    editor.putString("token",null);
                                    editor.commit();
                                    SaveDataUtil.newInstance().clearDB();
                                    Intent intent = new Intent();
                                    intent.setClass(getActivity(),LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }
                        },getActivity());
                break;
        }
    }
    private GenericsCallback<BaseApi> callback = new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(BaseApi response, int id) {
            if (response.getCode()==200){
                if (id == STEPFLAG){
                    ProgressHudModel.newInstance().diss();
                    stepPlanTv.setText(stepPlan+"");
                    if(isUpdatePlan){
                        userInfo.setStepsPlan(stepPlan);
                        MathUitl.saveIntData(getContext(),"stepsPlan",stepPlan).commit();
                        if (MainService.getInstance().getState()!=3){
                            showToast(getString(R.string.syceError));
                        }else {
                            if (app.isMtk())
                                EXCDController.getInstance().writeForSetInfo(app.getUserInfo());
                            else
                                BleClient.getInstance().writeForUpdateUserInfo();
                        }
                    }
                }else if (id == SLEEPFLAG){
                    ProgressHudModel.newInstance().diss();
                    sleepPlanTv.setText(String.format(Locale.ENGLISH,"%.1fh",sleepPlan/60f));
                    userInfo.setSleepPlan(sleepPlan);
                    MathUitl.saveIntData(getContext(),"sleepPlan",sleepPlan).commit();
                }
            }else {
                showToast(response.getMessage());
            }

        }
    };

}
