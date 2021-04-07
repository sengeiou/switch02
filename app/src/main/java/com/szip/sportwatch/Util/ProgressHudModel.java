package com.szip.sportwatch.Util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.szip.sportwatch.Interface.OnProgressTimeout;


/**
 * Created by Administrator on 2018/12/22.
 */

public class ProgressHudModel {
    private static ProgressHudModel progressHudModel;
    private KProgressHUD progressHUD;
    private Context mContext;
    private String error;
    private OnProgressTimeout onProgressTimeout;

    private ProgressHudModel(){

    }

    private Handler handler = new Handler();

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (progressHUD!=null){
                progressHUD.dismiss();
                progressHUD = null;
                if(error!=null)
                    Toast.makeText(mContext,error,Toast.LENGTH_SHORT).show();
                if (onProgressTimeout!=null)
                    onProgressTimeout.onTimeout();
            }
        }
    };

    public static ProgressHudModel newInstance(){                     // 单例模式，双重锁
        if( progressHudModel == null ){
            synchronized (ProgressHudModel.class){
                if( progressHudModel == null ){
                    progressHudModel = new ProgressHudModel();
                }
            }
        }
        return progressHudModel ;
    }

    public void show(final Context mContext, String title){
        progressHUD  = KProgressHUD.create(mContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        progressHUD.show();
        this.onProgressTimeout = null;
        this.mContext = mContext;
    }

    public void show(final Context mContext, String title, final String error,int delayMilis){
        progressHUD  = KProgressHUD.create(mContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        progressHUD.show();
        this.onProgressTimeout = null;
        this.mContext = mContext;
        this.error = error;
        if (delayMilis!=0)
            handler.postDelayed(run,delayMilis);
    }

    public void show(final Context mContext, String title, final String error, int delayMilis,boolean cancelAble,OnProgressTimeout onProgressTimeout){
        progressHUD  = KProgressHUD.create(mContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setCancellable(cancelAble);
        progressHUD.show();
        this.onProgressTimeout = onProgressTimeout;
        this.mContext = mContext;
        this.error = error;
        handler.postDelayed(run,delayMilis);
    }

    public void showWithPie(final Context mContext, String title,int max,final String error, int delayMilis){
        progressHUD  = KProgressHUD.create(mContext)
                .setMaxProgress(max)
                .setStyle(KProgressHUD.Style.PIE_DETERMINATE)
                .setLabel(title)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        progressHUD.show();
        this.mContext = mContext;
        this.error = error;
        handler.postDelayed(run,delayMilis);

    }

    public void setProgress(int num){
        if (progressHUD!=null)
            progressHUD.setProgress(num);
    }

    public void setLabel(String label){
        if (progressHUD!=null)
            progressHUD.setLabel(label);
    }

    public void diss(){
        if (progressHUD!=null){
            progressHUD.dismiss();
            progressHUD = null;
            handler.removeCallbacks(run);
        }
    }
}
