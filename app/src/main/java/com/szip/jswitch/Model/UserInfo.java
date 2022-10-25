package com.szip.jswitch.Model;

import android.text.TextUtils;

import com.szip.jswitch.BuildConfig;

/**
 * Created by Administrator on 2019/11/30.
 */

public class UserInfo implements Cloneable{
    private int id;
    private String areaCode;
    private String phoneNumber;
    private String email;
    private String userName;
    private String lastName;
    private String firstName;
    private String avatar;
    private int sex;
    private String birthday;
    private String nation;
    private int unit;
    private int height;
    private int weight;
    private int heightBritish;
    private int weightBritish;
    private String blood;
    private String deviceCode;
    private String bindId;
    private int stepsPlan;
    private int sleepPlan;
    private int tempUnit;
    private String product;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getBindId() {
        return bindId;
    }

    public void setBindId(String bindId) {
        this.bindId = bindId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        if (TextUtils.isEmpty(userName))
            return BuildConfig.APP_NAME;
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public int getStepsPlan() {
        return stepsPlan;
    }

    public void setStepsPlan(int stepsPlan) {
        this.stepsPlan = stepsPlan;
    }

    public int getSleepPlan() {
        return sleepPlan;
    }

    public void setSleepPlan(int sleepPlan) {
        this.sleepPlan = sleepPlan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTempUnit() {
        return tempUnit;
    }

    public void setTempUnit(int tempUnit) {
        this.tempUnit = tempUnit;
    }

    public int getHeightBritish() {
        return heightBritish;
    }

    public void setHeightBritish(int heightBritish) {
        this.heightBritish = heightBritish;
    }

    public int getWeightBritish() {
        return weightBritish;
    }

    public void setWeightBritish(int weightBritish) {
        this.weightBritish = weightBritish;
    }

    @Override
    public Object clone() {
        Object object = null;
        try {
            object = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return object;
    }


    public UserInfo copy(){

        UserInfo userInfo1 = new UserInfo();
        userInfo1.areaCode = this.areaCode;
        userInfo1.phoneNumber = this.phoneNumber;
        userInfo1.id = this.id;
        userInfo1.email = this.email;
        userInfo1.userName = this.userName;
        userInfo1.lastName = this.lastName;
        userInfo1.firstName = this.firstName;
        userInfo1.avatar = this.avatar;
        userInfo1.sex = this.sex;
        userInfo1.birthday = this.birthday;
        userInfo1.nation = this.nation;
        userInfo1.unit = this.unit;
        userInfo1.height = this.height;
        userInfo1.weight = this.weight;
        userInfo1.heightBritish = this.heightBritish;
        userInfo1.weightBritish = this.weightBritish;
        userInfo1.blood = this.blood;
        userInfo1.deviceCode = this.deviceCode;
        userInfo1.bindId = this.bindId;
        userInfo1.stepsPlan = this.stepsPlan;
        userInfo1.sleepPlan = this.sleepPlan;
        userInfo1.tempUnit = this.tempUnit;
        userInfo1.product = this.product;

        return userInfo1;

    }
}
