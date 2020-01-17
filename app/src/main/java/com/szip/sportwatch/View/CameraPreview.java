package com.szip.sportwatch.View;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera; //通过构造方法，将获取的camera类传进来，进行绑定

        mHolder = getHolder(); //设置回调
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        //创建预览界面的方法，无需主动调用
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {

        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        //当预览界面发生改变时会自动调用的方法，

        if (mHolder.getSurface() == null){
            //预览界面不存在的情况
            return;
        }

        // 界面发生改变时，停止预览，比如按下拍照的按钮
        try {
            mCamera.stopPreview();
        } catch (Exception e){

        }

        //重新开始预览
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


}
