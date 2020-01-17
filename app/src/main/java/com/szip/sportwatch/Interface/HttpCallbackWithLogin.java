package com.szip.sportwatch.Interface;

import com.szip.sportwatch.Model.HttpBean.LoginBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithLogin {
    void onLogin(LoginBean loginBean);
}
