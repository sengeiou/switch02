package com.szip.sportwatch.Model.HttpBean;

import com.szip.sportwatch.Model.UserInfo;

/**
 * Created by Administrator on 2019/11/30.
 */

public class UserInfoBean extends BaseApi {
    private UserInfo data;

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }
}
