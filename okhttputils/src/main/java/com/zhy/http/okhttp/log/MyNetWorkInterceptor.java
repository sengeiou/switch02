package com.zhy.http.okhttp.log;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class MyNetWorkInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Log.d("NET******","进入网络拦截器");
        return chain.proceed(chain.request());
    }
}
