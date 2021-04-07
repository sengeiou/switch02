package com.szip.sportwatch.Util;

import android.os.Handler;
import android.os.Looper;

import com.szip.sportwatch.MyApplication;

import java.io.IOException;


import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;


public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        try {
            Response response = chain.proceed(request);
            ResponseBody responseBody = response.body();
            String string = responseBody.string();
            LogUtil.getInstance().loge("DATA******","response body = "+string);
            if (string.indexOf("\"code\":401")>0){
                LogUtil.getInstance().loge("DATA******","登陆过期，拦截");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MyApplication.getInstance().tokenTimeOut();
                    }
                });
            }else {
                response = chain.proceed(chain.request());
                LogUtil.getInstance().loge("DATA******","登陆未过期，放行");
            }
            return response;
        }catch (Exception e){
            throw  e;
        }
    }

    private void logForRequest(Request request)
    {
        try
        {
            String url = request.url().toString();
            Headers headers = request.headers();

            LogUtil.getInstance().loge("DATA******", "========request'log=======");
            LogUtil.getInstance().loge("DATA******", "method : " + request.method());
            LogUtil.getInstance().loge("DATA******", "url : " + url);
            if (headers != null && headers.size() > 0)
            {
                LogUtil.getInstance().loge("DATA******", "headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (requestBody != null)
            {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null)
                {
                    LogUtil.getInstance().loge("DATA******", "requestBody's contentType : " + mediaType.toString());
                    if (isText(mediaType))
                    {
                        LogUtil.getInstance().loge("DATA******", "requestBody's content : " + bodyToString(request));
                    } else
                    {
                        LogUtil.getInstance().loge("DATA******", "requestBody's content : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }
            LogUtil.getInstance().loge("DATA******", "========request'log=======end");
        } catch (Exception e)
        {
//            e.printStackTrace();
        }
    }

    private boolean isText(MediaType mediaType)
    {
        if (mediaType.type() != null && mediaType.type().equals("text"))
        {
            return true;
        }
        if (mediaType.subtype() != null)
        {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
            )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request)
    {
        try
        {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e)
        {
            return "something error when show requestBody.";
        }
    }
}
