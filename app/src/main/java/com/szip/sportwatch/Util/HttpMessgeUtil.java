package com.szip.sportwatch.Util;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


import com.szip.sportwatch.Contorller.LoginActivity;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.DB.SaveDataUtil;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.Interface.HttpCallbackWithBase;
import com.szip.sportwatch.Interface.HttpCallbackWithLogin;
import com.szip.sportwatch.Interface.HttpCallbackWithUserInfo;
import com.szip.sportwatch.Model.HttpBean.AvatarBean;
import com.szip.sportwatch.Model.HttpBean.BaseApi;
import com.szip.sportwatch.Model.HttpBean.CheckVerificationBean;
import com.szip.sportwatch.Model.HttpBean.DownloadDataBean;
import com.szip.sportwatch.Model.HttpBean.LoginBean;
import com.szip.sportwatch.Model.HttpBean.UserInfoBean;
import com.szip.sportwatch.Model.HttpBean.WeatherBean;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.szip.sportwatch.MyApplication.FILE;

public class HttpMessgeUtil {

    private static HttpMessgeUtil mInstance;
    private String url = "https://cloud.znsdkj.com:8443/sportWatch/";
    private String token = "null";
    private String language = "zh-CN";
    private String time;

    private Context mContext;

    private HttpCallbackWithBase httpCallbackWithBase;
    private HttpCallbackWithLogin httpCallbackWithLogin;
    private HttpCallbackWithUserInfo httpCallbackWithUserInfo;

    private int GET_VERIFICATION = 100;
    public static int UPDOWN_LOG = 101;
    public static int UPDOWN_DATA = 102;
    public static int UPDOWN_AVATAR = 103;
    public static int UPDATA_USERINFO = 104;

    public static HttpMessgeUtil getInstance(Context context)
    {
        if (mInstance == null)
        {
            synchronized (HttpMessgeUtil.class)
            {
                if (mInstance == null)
                {
                    mInstance = new HttpMessgeUtil(context);
                }
            }
        }
        return mInstance;
    }

    private HttpMessgeUtil(Context context){
        mContext = context;
        if (context.getResources().getConfiguration().locale.getCountry().equals("CN")){
            language = "zh-CN";
        }else{
            language = "en-US";
        }
        time = DateUtil.getGMTWithString();
    }

    public void setToken(String token){
        this.token = token;
    }

    public void setHttpCallbackWithBase(HttpCallbackWithBase httpCallbackWithBase) {
        this.httpCallbackWithBase = httpCallbackWithBase;
    }


    public void setHttpCallbackWithLogin(HttpCallbackWithLogin httpCallbackWithLogin) {
        this.httpCallbackWithLogin = httpCallbackWithLogin;
    }

//    public void setHttpCallbackWithUpdata(HttpCallbackWithUpdata httpCallbackWithUpdata) {
//        this.httpCallbackWithUpdata = httpCallbackWithUpdata;
//    }
//
//    public void setHttpCallbackWithReport(HttpCallbackWithReport httpCallbackWithReport) {
//        this.httpCallbackWithReport = httpCallbackWithReport;
//    }

    public void setHttpCallbackWithUserInfo(HttpCallbackWithUserInfo httpCallbackWithUserInfo) {
        this.httpCallbackWithUserInfo = httpCallbackWithUserInfo;
    }

//    public void setHttpCallbackWithAddClock(HttpCallbackWithAddClock httpCallbackWithAddClock) {
//        this.httpCallbackWithAddClock = httpCallbackWithAddClock;
//    }
//
//    public void setHttpCallbackWithClockData(HttpCallbackWithClockData httpCallbackWithClockData) {
//        this.httpCallbackWithClockData = httpCallbackWithClockData;
//    }

    /**
     * 注册接口
     * @param type                   注册类型1：手机注册 2：邮箱注册
     * @param areaCode               区号
     * @param phoneNumber            手机
     * @param email                  邮箱
     * @param verifyCode             验证码
     * @param password               密码
     * */
    private void _postRegister(String type,String areaCode,String phoneNumber,String email,String verifyCode,String password,
                               String phoneId,String phoneSystem) throws IOException {
        String url = this.url+"user/signUp";
        OkHttpUtils
                .jpost()
                .url(url)
                .addHeader("Accept-Language",language)
                .addHeader("Time-Diff",time)
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("verifyCode",verifyCode)
                .addParams("password",password)
                .addParams("phoneId",phoneId)
                .addParams("phoneSystem",phoneSystem)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 获取验证码接口
     * @param type                  验证码类型1：手机 2：邮箱
     * @param areaCode              区号
     * @param phoneNumber           手机号码
     * @param email                 邮箱
     * */
    private void _getVerificationCode(String type,String areaCode,String phoneNumber,String email)throws IOException{
        final String url = this.url+"user/sendVerifyCode";
        OkHttpUtils
                .jpost()
                .id(GET_VERIFICATION)
                .url(url)
                .addHeader("Time-Diff",time)
                .addHeader("Accept-Language",language)
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 验证验证码接口
     * @param type                  验证码类型1：手机 2：邮箱
     * @param areaCode              区号
     * @param phoneNumber           手机号码
     * @param email                 邮箱
     * @param verifyCode            验证码
     * */
    private void _postCheckVerifyCode(String type,String areaCode,String phoneNumber,String email,String verifyCode)throws IOException{
        final String url = this.url+"user/checkVerifyCode";
        OkHttpUtils
                .jpost()
                .url(url)
                .addHeader("Accept-Language",language)
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("verifyCode",verifyCode)
                .build()
                .execute(verificationBeanGenericsCallback);
    }

    /**
     * 登录接口
     * @param type                   注册类型1：手机注册 2：邮箱注册
     * @param areaCode               区号
     * @param phoneNumber            手机
     * @param email                  邮箱
     * @param password               密码
     * */
    private void _postLogin(String type,String areaCode, String phoneNumber, String email, String password,String phoneId,String phoneSystem)throws IOException{
        String url = this.url+"user/login";
        OkHttpUtils
                .jpost()
                .url(url)
                .addHeader("Time-Diff",time)
                .addHeader("Accept-Language",language)
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("password",password)
                .addParams("phoneId",phoneId)
                .addParams("phoneSystem",phoneSystem)
                .build()
                .execute(loginBeanGenericsCallback);
    }

    /**
     * 忘记密码接口
     * @param type               找回类型1：手机  2：邮箱
     * @param areaCode           区号
     * @param phoneNumber        手机号码
     * @param email              邮箱
     * @param verifyCode         验证码
     * @param password           密码
     * */
    private void _postForgotPassword(String type,String areaCode,String phoneNumber,String email,String verifyCode,
                                     String password)throws IOException{
        String url = this.url+"user/retrievePassword";
        OkHttpUtils
                .jpost()
                .url(url)
                .addHeader("Time-Diff",time)
                .addHeader("Accept-Language",language)
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("verifyCode",verifyCode)
                .addParams("password",password)
                .build()
                .execute(baseApiGenericsCallback);
    }



    /**
     * 发送意见反馈接口
     * */
    private void _postSendFeedback(String content)throws IOException{
        String url = this.url+"user/uploadFeedback";
        OkHttpUtils
                .jpost()
                .url(url)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("content",content)
                .build()
                .execute(baseApiGenericsCallback);
    }

//    /**
//     * 获取固件升级
//     * @param hardwareVersion   硬件版本
//     * @param id                回调标识
//     * */
//    private void _getForUpdata(String hardwareVersion,int id)throws IOException{
//        String url = this.url+"comm/upgrade";
//        OkHttpUtils
//                .get()
//                .url(url)
//                .id(id)
//                .addHeader("Time-Diff",time)
//                .addHeader("token",token)
//                .addHeader("Accept-Language",language)
//                .addParams("versionNumber",hardwareVersion)
//                .build()
//                .execute(updataBeanGenericsCallback);
//    }


    /**
     * 获取个人信息
     * */
    private void _getForGetInfo()throws IOException{
        String url = this.url+"user/getUserInfo";
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .build()
                .execute(userInfoBeanGenericsCallback);
    }


    /**
     * 设置用户信息
     * @param name             名字
     * @param sex              性别
     * @param birthday         生日
     * @param height           身高
     * @param weight           体重
     * */
    private void _postForSetUserInfo(String name,String sex,String birthday,String height,String weight)throws IOException{
        String url = this.url+"user/updateUserInfo";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(UPDATA_USERINFO)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("userName",name)
                .addParams("lastName","")
                .addParams("firstName","")
                .addParams("sex",sex)
                .addParams("birthday",birthday)
                .addParams("nation","")
                .addParams("height",height)
                .addParams("weight",weight)
                .addParams("blood","")
                .build()
                .execute(baseApiGenericsCallback);
    }

    private void _postForSetStepsPlan(String stepsPlan,int id)throws IOException{
        String url = this.url+"user/updateStepsPlan";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("stepsPlan",stepsPlan)
                .build()
                .execute(baseApiGenericsCallback);
    }

    private void _postForSetSleepPlan(String sleepPlan,int id)throws IOException{
        String url = this.url+"user/updateSleepPlan";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("sleepPlan",sleepPlan)
                .build()
                .execute(baseApiGenericsCallback);
    }

    private void _postForSetUnit(String unit)throws IOException{
        String url = this.url+"user/setUnit";
        OkHttpUtils
                .jpost()
                .url(url)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("unit",unit)
                .build()
                .execute(baseApiGenericsCallback);
    }

//    /**
//     * 绑定邮箱
//     * @param email                 邮箱
//     * @param verifyCode            验证码
//     * */
//    private void _postForBindEmail(String email,String verifyCode,int id)throws IOException{
//        String url = this.url+"user/bindEmail";
//        OkHttpUtils
//                .jpost()
//                .url(url)
//                .id(id)
//                .addHeader("Time-Diff",time)
//                .addHeader("token",token)
//                .addHeader("Accept-Language",language)
//                .addParams("email",email)
//                .addParams("verifyCode",verifyCode)
//                .build()
//                .execute(baseApiGenericsCallback);
//    }
//
//    /**
//     * 解绑邮箱
//     * */
//    private void _getForUnbindEmail(int id)throws IOException{
//        String url = this.url+"user/unbindEmail";
//        OkHttpUtils
//                .get()
//                .url(url)
//                .id(id)
//                .addHeader("Time-Diff",time)
//                .addHeader("token",token)
//                .addHeader("Accept-Language",language)
//                .build()
//                .execute(baseApiGenericsCallback);
//    }

//    /**
//     * 绑定手机
//     * @param phoneNumber           手机号码
//     * @param verifyCode            验证码
//     * */
//    private void _postForBindPhone(String areaCode,String phoneNumber,String verifyCode,int id)throws IOException{
//        String url = this.url+"user/bindPhoneNumber";
//        OkHttpUtils
//                .jpost()
//                .url(url)
//                .id(id)
//                .addHeader("Time-Diff",time)
//                .addHeader("token",token)
//                .addHeader("Accept-Language",language)
//                .addParams("areaCode",areaCode)
//                .addParams("phoneNumber",phoneNumber)
//                .addParams("verifyCode",verifyCode)
//                .build()
//                .execute(baseApiGenericsCallback);
//    }
//
//    /**
//     * 解绑手机
//     * */
//    private void _getForUnbindPhone(int id)throws IOException{
//        String url = this.url+"user/unbindPhoneNumber";
//        OkHttpUtils
//                .get()
//                .url(url)
//                .id(id)
//                .addHeader("Time-Diff",time)
//                .addHeader("token",token)
//                .addHeader("Accept-Language",language)
//                .build()
//                .execute(baseApiGenericsCallback);
//    }

//    /**
//     * 修改密码
//     * @param currentPassword      旧密码
//     * @param newPassword          新密码
//     * @param id                   回调标识
//     * */
//    private void _postForChangePassword(String currentPassword,String newPassword,int id)throws IOException{
//        String url = this.url+"user/changePassword";
//        OkHttpUtils
//                .jpost()
//                .url(url)
//                .id(id)
//                .addHeader("Time-Diff",time)
//                .addHeader("token",token)
//                .addHeader("Accept-Language",language)
//                .addParams("currentPassword",currentPassword)
//                .addParams("newPassword",newPassword)
//                .build()
//                .execute(baseApiGenericsCallback);
//
//    }
//
    /**
     * 上传头像
     * */
    private void _postUpdownAvatar(File avatar, GenericsCallback<AvatarBean> callback)throws IOException{
        String url = this.url+"user/setProfilePicture";
        OkHttpUtils
                .fpost()
                .url(url)
                .id(UPDOWN_AVATAR)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addFile("file","iSmarport_6.jpg",avatar)
                .build()
                .execute(callback);
    }


    /**
     * 绑定设备
     * */
    private void _getBindDevice(String deviceCode)throws IOException{
        String url = this.url+"device/bindDevice";
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("deviceCode",deviceCode)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 天气预报
     * */
    private void _getWeather(String lat,String lon,GenericsCallback<WeatherBean> callback)throws IOException{
        String url = this.url+"comm/weather";
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("lat",lat)
                .addParams("lon",lon)
                .build()
                .execute(callback);
    }


    /**
     * 解绑设备
     * */
    private void _getUnbindDevice()throws IOException{
        String url = this.url+"device/unbindDevice";
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .build()
                .execute(baseApiGenericsCallback);
    }


    /**
     * 上传log数据
     * */
    private void _postAppCrashLog(String appName,String appVersion,String systemInfo,String stackTrace)throws IOException{
        String url = this.url+"appCrashLog/upload";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(UPDOWN_LOG)
                .addParams("appName",appName)
                .addParams("appVersion",appVersion)
                .addParams("systemInfo",systemInfo)
                .addParams("stackTrace",stackTrace)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 上传数据到服务器
     * */
    private void _postForUpdownReportData(String data)throws IOException{
        String url = this.url+"data/upload";
        OkHttpUtils
                .listpost()
                .url(url)
                .id(UPDOWN_DATA)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("data",data)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 获取服务器上的数据
     * */
    private void _getForDownloadReportData(String time, String size)throws IOException{
        String url = this.url+"data/get";
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("time",time)
                .addParams("size",size)
                .build()
                .execute(reportDataBeanGenericsCallback);
    }

    public void postAppCrashLog(String appName,String appVersion,String systemInfo,String stackTrace)throws IOException{
        _postAppCrashLog(appName,appVersion,systemInfo,stackTrace);
    }


    /**
     * 提供给用户的方法
     * */
    public void postRegister(String type,String areaCode,String phoneNumber,String email,String verifyCode,String password,
                             String phoneId,String phoneSystem) throws IOException{
        _postRegister(type,areaCode,phoneNumber,email,verifyCode,password, phoneId, phoneSystem);
    }

    public void getVerificationCode(String type,String areaCode,String phoneNumber,String email)throws IOException{

        _getVerificationCode(type,areaCode,phoneNumber,email);
    }

    public void postCheckVerifyCode(String type,String areaCode,String phoneNumber,String email,String verifyCode)throws IOException{
        _postCheckVerifyCode(type,areaCode,phoneNumber,email,verifyCode);
    }

    public void postLogin(String type, String areaCode,String phoneNumber, String email, String password,String phoneId,String phoneSystem)throws IOException{
        _postLogin(type,areaCode,phoneNumber,email,password,phoneId,phoneSystem);
    }

    public void postForgotPassword(String type,String areaCode,String phoneNumber,String email,String verifyCode,
                                   String password)throws IOException{
        _postForgotPassword(type,areaCode,phoneNumber,email,verifyCode,password);
    }

//    public void getForUpdata(String hardwareVersion, int id)throws IOException{
//        _getForUpdata(hardwareVersion,id);
//    }

    public void postForSetUserInfo(String name,String sex,String birthday,String height,String weight)throws IOException{
        _postForSetUserInfo(name,sex,birthday,height,weight);
    }

    public void getForGetInfo()throws IOException{
        _getForGetInfo();
    }

    public void postForSetStepsPlan(String stepsPlan,int id)throws IOException{
        _postForSetStepsPlan(stepsPlan,id);
    }

    public void postForSetSleepPlan(String sleepPlan,int id)throws IOException{
        _postForSetSleepPlan(sleepPlan,id);
    }

    public void postForSetUnit(String unit)throws IOException{
        _postForSetUnit(unit);
    }
//    public void postForBindEmail(String email,String verifyCode,int id)throws IOException{
//        _postForBindEmail(email,verifyCode,id);
//    }
//
//    public void getForUnbindEmail(int id)throws IOException{
//        _getForUnbindEmail(id);
//    }
//
//    public void postForBindPhone(String areaCode,String phoneNumber,String verifyCode,int id)throws IOException{
//        _postForBindPhone(areaCode,phoneNumber,verifyCode,id);
//    }
//
//    public void getForUnbindPhone(int id)throws IOException{
//        _getForUnbindPhone(id);
//    }
//
//    public void postForChangePassword(String currentPassword,String newPassword,int id)throws IOException{
//        _postForChangePassword(currentPassword,newPassword,id);
//    }
//
    public void getBindDevice(String deviceCode)throws IOException{
        _getBindDevice(deviceCode);
    }

    public void getUnbindDevice()throws IOException{
        _getUnbindDevice();
    }

    public void postForUpdownReportData(String data)throws IOException{
        _postForUpdownReportData(data);
    }

    public void getForDownloadReportData(String time, String size)throws IOException{
        _getForDownloadReportData(time,size);
    }

    public void postUpdownAvatar(File avatar,GenericsCallback<AvatarBean> callback)throws IOException{
        _postUpdownAvatar(avatar,callback);
    }
//
//    public void postForChangeClock(String clockId,String type,String hour,String minute,String index,String isPhone,
//                                   String isOn,String repeat,String music,String isIntelligentWake,String remark,int id)throws IOException{
//        _postForChangeClock(clockId,type,hour,minute,index,isPhone,isOn,repeat,music,isIntelligentWake,remark,id);
//    }
//
//    public void getForDeleteClock(String clockId, GenericsCallback<BaseApi> callback, int id)throws IOException{
//        _getForDeleteClock(clockId,callback,id);
//    }
//
//    public void postForAddClock(String type,String hour,String minute,String index,String isPhone,
//                                String isOn,String repeat,String music,String isIntelligentWake,String remark,int id)throws IOException{
//        _postForAddClock(type,hour,minute,index,isPhone,isOn,repeat,music,isIntelligentWake,remark,id);
//    }
//
//    public void getForGetClockList(int id)throws IOException{
//        _getForGetClockList(id);
//    }
//
//
//
    public void postSendFeedback(String content)throws IOException{
        _postSendFeedback(content);
    }

    public void getWeather(String lat,String lon,GenericsCallback<WeatherBean> callback)throws IOException{
        _getWeather(lat,lon,callback);
    }

    /**
     * 接口回调
     * */
    private GenericsCallback<BaseApi> baseApiGenericsCallback = new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {
        }

        @Override
        public void onResponse(BaseApi response, int id) {
            if (response.getCode() == 200){
                if (id == UPDOWN_DATA){
                    MathUitl.saveLastTime(mContext.getSharedPreferences(FILE,MODE_PRIVATE));
                }else if (id != GET_VERIFICATION){//如果不是获取验证码的回调，则返回回调数据
                    if (httpCallbackWithBase != null)
                        httpCallbackWithBase.onCallback(response,id);
                }
            }else if (response.getCode() == 401&& id!=UPDOWN_LOG){
                tokenTimeOut();
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

    private GenericsCallback<CheckVerificationBean> verificationBeanGenericsCallback = new GenericsCallback<CheckVerificationBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(CheckVerificationBean response, int id) {
            if (response.getCode() == 200){
                if (response.getData().isValid()){
                    httpCallbackWithBase.onCallback(null,0);
                }
            }else if (response.getCode() == 401){
                tokenTimeOut();
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

    private GenericsCallback<LoginBean> loginBeanGenericsCallback = new GenericsCallback<LoginBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(LoginBean response, int id) {
            if (response.getCode() == 200){
                if (httpCallbackWithLogin!=null)
                    httpCallbackWithLogin.onLogin(response);
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

//    private GenericsCallback<UpdataBean> updataBeanGenericsCallback = new GenericsCallback<UpdataBean>(new JsonGenericsSerializator()) {
//        @Override
//        public void onError(Call call, Exception e, int id) {
//
//        }
//
//        @Override
//        public void onResponse(UpdataBean response, int id) {
//            if (response.getCode() == 200){
//                if (httpCallbackWithUpdata!=null)
//                    httpCallbackWithUpdata.onUpdata(response);
//            }else if (response.getCode() == 401){
//                tokenTimeOut();
//            }else {
//                ProgressHudModel.newInstance().diss();
//                MathUitl.showToast(mContext,response.getMessage());
//            }
//        }
//    };

    private GenericsCallback<UserInfoBean> userInfoBeanGenericsCallback = new GenericsCallback<UserInfoBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(UserInfoBean response, int id) {
            if (response.getCode() == 200 || response.getCode() == 401){
                if (httpCallbackWithUserInfo!=null)
                    httpCallbackWithUserInfo.onUserInfo(response);
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

    private GenericsCallback<DownloadDataBean> reportDataBeanGenericsCallback = new GenericsCallback<DownloadDataBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {
        }

        @Override
        public void onResponse(DownloadDataBean response, int id) {
            if (response.getCode() == 200){
                SaveDataUtil saveDataUtil = SaveDataUtil.newInstance(mContext);
                if (response.getData().getBloodOxygenData().size()!=0)
                    saveDataUtil.saveBloodOxygenDataListData(response.getData().getBloodOxygenData());

                if (response.getData().getBloodPressureDataList().size()!=0)
                    saveDataUtil.saveBloodPressureDataListData(response.getData().getBloodPressureDataList());

                if (response.getData().getHeartDataList().size()!=0)
                    saveDataUtil.saveHeartDataListData(response.getData().getHeartDataList(),false);

                if (response.getData().getSleepDataList().size()!=0)
                    saveDataUtil.saveSleepDataListData(response.getData().getSleepDataList());

                if (response.getData().getStepDataList().size()!=0)
                    saveDataUtil.saveStepDataListData(response.getData().getStepDataList());

                if (response.getData().getEcgDataList().size()!=0)
                    saveDataUtil.saveEcgDataListData(response.getData().getEcgDataList());

                if (response.getData().getAnimalHeatDataList().size()!=0)
                    saveDataUtil.saveAnimalHeatDataListData(response.getData().getAnimalHeatDataList());

                if (response.getData().getSportDataList().size()!=0)
                    saveDataUtil.saveSportDataListData(response.getData().getSportDataList());
//                for (SportData sportData:response.getData().getSportDataList()){
//                    saveDataUtil.saveSportData(sportData);
//                }
            }else if (response.getCode() == 401){
                tokenTimeOut();
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

//    private GenericsCallback<AddClockBean> addClockBeanGenericsCallback = new GenericsCallback<AddClockBean>(new JsonGenericsSerializator()) {
//        @Override
//        public void onError(Call call, Exception e, int id) {
//
//        }
//
//        @Override
//        public void onResponse(AddClockBean response, int id) {
//            if (id == -1){
//                ProgressHudModel.newInstance().diss();
//            }else {
//                if (response.getCode() == 200){
//                    if (httpCallbackWithAddClock!=null)
//                        httpCallbackWithAddClock.onAddClock(response,id);
//                }else if (response.getCode() == 401){
//                    tokenTimeOut();
//                }else {
//                    ProgressHudModel.newInstance().diss();
//                    MathUitl.showToast(mContext,response.getMessage());
//                }
//            }
//        }
//    };

//    private GenericsCallback<ClockDataBean> clockDataBeanGenericsCallback = new GenericsCallback<ClockDataBean>(new JsonGenericsSerializator()) {
//        @Override
//        public void onError(Call call, Exception e, int id) {
//
//        }
//
//        @Override
//        public void onResponse(ClockDataBean response, int id) {
//            if (response.getCode() == 200){
//                if (httpCallbackWithClockData!=null)
//                    httpCallbackWithClockData.onClockData(response);
//            }else if (response.getCode() == 401){
//                tokenTimeOut();
//            }else {
//                ProgressHudModel.newInstance().diss();
//                MathUitl.showToast(mContext,response.getMessage());
//            }
//        }
//    };

    private void tokenTimeOut(){
        SharedPreferences sharedPreferences ;
        ProgressHudModel.newInstance().diss();

        sharedPreferences = mContext.getSharedPreferences(FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin",false);
        editor.putBoolean("isBind",false);
        editor.putString("token",null);
        editor.commit();
        SaveDataUtil.newInstance(mContext).clearDB();
        MainService.getInstance().stopConnect();
        MathUitl.showToast(mContext,mContext.getString(R.string.tokenTimeOut));
        Intent intentmain=new Intent(mContext,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intentmain);
    }
}
