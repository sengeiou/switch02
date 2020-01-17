package com.szip.sportwatch.Interface;

import com.szip.sportwatch.Model.HttpBean.UserInfoBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithUserInfo {
    void onUserInfo(UserInfoBean userInfoBean);
}
