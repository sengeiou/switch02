package com.szip.sportwatch.bll;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by KB on 2017/5/9.
 */

public class ThreadPoolBLL {

    private static ExecutorService mExecutorService;
    private static ExecutorService mExecutorService1;
    private static ExecutorService mExecutorService_SynData;
    private static ExecutorService mExecutorService2;
    private static ExecutorService mExecutorService_UpdateSportData;

    /**
     * 获得一个专门用于上传数据的线程池
     * @return
     */
    public static ExecutorService getUpdataSportDataThreadPool(){
        if (mExecutorService_UpdateSportData==null){
            mExecutorService_UpdateSportData = syncUpdateInstance();
        }
        return mExecutorService_UpdateSportData;
    }

    private static synchronized ExecutorService syncUpdateInstance(){
        return Executors.newSingleThreadExecutor();
    }
    /**
     * 获得单一的线程池，这个单一线程是公用的，很多地方都在用这个单一线程
     * @return
     */
    public static ExecutorService getSingleTheadPool() {
        if (mExecutorService == null) {
            mExecutorService = syncSingleInstance();
        }
        return mExecutorService;
    }

    private static synchronized ExecutorService syncSingleInstance(){
        return Executors.newSingleThreadExecutor();
    }

    /**
     * 获得包含4线程的线程池
     * @return
     */
    /*public static ExecutorService get4ThreadPool(){
        if (mExecutorService1 == null){
            mExecutorService1 = Executors.newFixedThreadPool(4);
        }
        return mExecutorService1;
    }*/


    /**
     * 获得一个具有2个线程的线程池
     * @return
     */
    public static ExecutorService get2ThreadTool(){
        if (mExecutorService2==null){
            mExecutorService2 = sync2Instance();
        }
        return mExecutorService2;
    }

    private static synchronized ExecutorService sync2Instance(){
        return Executors.newFixedThreadPool(2);
    }

    public static ExecutorService newCacheThreadPool(){
        return new ThreadPoolExecutor(0,Integer.MAX_VALUE,0,
                TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
    }

    /**
     * 同步数据的单一线程，这个线程是独享的，只有同步数据时才使用该线程
     * @return
     */
    public static ExecutorService getSynDataThread(){
        if (mExecutorService_SynData==null){
            mExecutorService_SynData = sycnSynInstance();
        }
        return mExecutorService_SynData;
    }

    private static synchronized ExecutorService sycnSynInstance(){
        return Executors.newSingleThreadExecutor();
    }
}
