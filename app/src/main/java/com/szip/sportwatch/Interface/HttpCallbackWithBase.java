package com.szip.sportwatch.Interface;


import com.szip.sportwatch.Model.HttpBean.BaseApi;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithBase {
    void onCallback(BaseApi baseApi, int id);
}
