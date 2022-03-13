package com.szip.jswitch.Interface;

import com.szip.jswitch.Model.HttpBean.LoginBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithLogin {
    void onLogin(LoginBean loginBean);
}
