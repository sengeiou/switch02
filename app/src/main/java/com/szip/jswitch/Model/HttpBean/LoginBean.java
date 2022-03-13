package com.szip.jswitch.Model.HttpBean;

import com.szip.jswitch.Model.UserInfo;

/**
 * Created by Administrator on 2019/11/30.
 */

public class LoginBean extends BaseApi {
    private Data data;

    public class Data{
        private String token;
        private UserInfo userInfo;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
