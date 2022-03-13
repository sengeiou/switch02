package com.szip.jswitch.Interface;

import com.szip.jswitch.Model.HttpBean.UserInfoBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithUserInfo {
    void onUserInfo(UserInfoBean userInfoBean);
}
