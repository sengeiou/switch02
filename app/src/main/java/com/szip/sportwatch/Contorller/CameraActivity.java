package com.szip.sportwatch.Contorller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.STREAM_MUSIC;

public class CameraActivity extends BaseActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout preview;
    private MediaPlayer mediaPlayer;
    private int angle;
    private SensorManager sm = null;

    private boolean cameraAble = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_camera);
        StatusBarCompat.translucentStatusBar(CameraActivity.this,true);
        mCamera = getCameraInstance(); //通过自己封装的方法，获取Camera类

        preview = findViewById(R.id.camera_preview);
        // 创建自己创建的预览类
        mPreview = new CameraPreview(this, mCamera);


        preview.addView(mPreview);//将预览类加入显示界面

    }

    @Override
    protected void onResume() {
        super.onResume();
        EXCDController.getInstance().setOnCameraListener(onCameraListener);
        if (sm == null) {
            sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }
        sm.registerListener(sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager
                .SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EXCDController.getInstance().setOnCameraListener(null);
        if (sm == null) {
            sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }
        sm.unregisterListener(sensorEventListener);
    }


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

    public static Camera getCameraInstance(){  //安全获取camera类
        Camera camera = null;
        try {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
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
                if (cameraAble){
                    cameraAble = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cameraAble = true;
                        }
                    },1000);
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
                                mediaPlayer.setVolume(0.5f,0.5f);
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
        }
    };

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        //添加拍照的回调对象mPicture，mCamera.takePicture的必要参数
        //实现将拍照的图片存储到自己想要的目录
        @Override
        public void onPictureTaken(byte[] data, final Camera camera) {
            Bitmap bMap;
            bMap = BitmapFactory.decodeByteArray(data, 0, data.length);

            Bitmap bMapRotate;
            if (angle==0||angle==180) {  //竖拍
                Matrix matrix = new Matrix();
                matrix.reset();
                matrix.postRotate(90);
                bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(),
                        bMap.getHeight(), matrix, true);
                bMap = bMapRotate;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataFile = baos.toByteArray();
            try {
                String photoFile = FileUtil.getInstance().writeFileSdcardFile(System.currentTimeMillis()+".jpg",dataFile);
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



    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
                return;
            }
            float[] values = event.values;
            angle = getSensorAngle(values[0], values[1]);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private int getSensorAngle(float x, float y) {
        if (Math.abs(x) > Math.abs(y)) {
            /**
             * 横屏倾斜角度比较大
             */
            if (x > 4) {
                /**
                 * 左边倾斜
                 */
                return 270;
            } else if (x < -4) {
                /**
                 * 右边倾斜
                 */
                return 90;
            } else {
                /**
                 * 倾斜角度不够大
                 */
                return 0;
            }
        } else {
            if (y > 7) {
                /**
                 * 左边倾斜
                 */
                return 0;
            } else if (y < -7) {
                /**
                 * 右边倾斜
                 */
                return 180;
            } else {
                /**
                 * 倾斜角度不够大
                 */
                return 0;
            }
        }
    }
}
