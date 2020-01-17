package com.szip.sportwatch.Contorller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.Interface.OnCameraListener;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.FileUtil;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.CameraPreview;

import java.io.File;
import java.io.IOException;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.STREAM_MUSIC;

public class CameraActivity extends BaseActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout preview;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_camera);
        StatusBarCompat.translucentStatusBar(CameraActivity.this,true);
        mCamera = getCameraInstance(); //通过自己封装的方法，获取Camera类

        // 创建自己创建的预览类
        mPreview = new CameraPreview(this, mCamera);
        preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);//将预览类加入显示界面

    }

    @Override
    protected void onResume() {
        super.onResume();
        EXCDController.getInstance().setOnCameraListener(onCameraListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EXCDController.getInstance().setOnCameraListener(null);
    }

    public static Camera getCameraInstance(){  //安全获取camera类
        Camera camera = null;
        try {
            camera = Camera.open();
        }
        catch (Exception e){

        }
        return camera;
    }


    private OnCameraListener onCameraListener = new OnCameraListener() {
        @Override
        public void onCamera(int flag) {
            if (flag == 0)
                finish();
            else {
                mCamera.autoFocus(new Camera.AutoFocusCallback() { //自动聚焦
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        // 从Camera捕获图片
                        mCamera.takePicture(null, null, mPicture);
                        final AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
                        final int volume = am.getStreamVolume(STREAM_MUSIC);//保存手机原来的音量
                        am.setStreamVolume (STREAM_MUSIC, am.getStreamMaxVolume(STREAM_MUSIC), FLAG_PLAY_SOUND);//设置系统音乐最大
                        if (mediaPlayer==null){
                            mediaPlayer = MediaPlayer.create(CameraActivity.this, R.raw.camera);
                            mediaPlayer.start();
                            mediaPlayer.setVolume(1f,1f);
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    am.setStreamVolume (STREAM_MUSIC, volume, FLAG_PLAY_SOUND);//播放完毕，设置回之前的音量
                                    mediaPlayer = null;
                                }
                            });
                        }

                    }
                });
            }
        }
    };

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        //添加拍照的回调对象mPicture，mCamera.takePicture的必要参数
        //实现将拍照的图片存储到自己想要的目录
        @Override
        public void onPictureTaken(byte[] data, final Camera camera) {
            try {
                String photoFile = FileUtil.getInstance().writeFileSdcardFile(System.currentTimeMillis()+".jpg",data);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(photoFile));
                intent.setData(uri);
                sendBroadcast(intent);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onDestroy() {
        // 回收Camera资源，必须的
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
        super.onDestroy();
    }
}
