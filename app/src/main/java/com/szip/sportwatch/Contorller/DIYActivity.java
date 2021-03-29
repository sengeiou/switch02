package com.szip.sportwatch.Contorller;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.sportwatch.Adapter.DIYAdapter;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.Model.EvenBusModel.UpdateView;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.CircularImageView;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;

public class DIYActivity extends BaseActivity {
    private String tmpFile;
    private String fileName;

    private RecyclerView clockRv;
    private DIYAdapter diyAdapter;
    private int PAGENUM = 8000;//分包长度
    private Uri resultUri;
    private int progress = 0;

    private int[] clock_r = new int[]{R.mipmap.diy_preview_1_1,R.mipmap.diy_preview_2_1,R.mipmap.diy_preview_3_1,R.mipmap.diy_preview_4_1,
            R.mipmap.diy_preview_5_1,R.mipmap.diy_preview_6_1,R.mipmap.diy_preview_7_1,R.mipmap.diy_preview_9_1,
            R.mipmap.diy_preview_10_1,R.mipmap.diy_preview_11_1,R.mipmap.diy_preview_13_1,R.mipmap.diy_preview_14_1,R.mipmap.diy_28};
//,R.mipmap.diy_29
//            ,R.mipmap.diy_31,R.mipmap.diy_33
    private int[] clock_c = new int[]{R.mipmap.diy_preview_c_1_1,R.mipmap.diy_preview_c_2_1,R.mipmap.diy_preview_c_3_1,
            R.mipmap.diy_preview_c_4_1, R.mipmap.diy_preview_c_5_1,R.mipmap.diy_preview_c_6_1, R.mipmap.diy_preview_c_7_1,
            R.mipmap.diy_preview_c_8_1,R.mipmap.diy_preview_c_9_1, R.mipmap.diy_preview_c_10_1, R.mipmap.diy_preview_c_11_1,
            R.mipmap.diy_preview_c_12_1,R.mipmap.diy_preview_c_13_1,R.mipmap.diy_31,R.mipmap.diy_30,R.mipmap.diy_34};

    private int[] clock_num_r = new int[]{1,2,3,5,7,8,9,10,12,14,11,13,28};
//    ,29,31,33
    private int[] clock_num_c = new int[]{15,25,11,17,18,19,20,21,22,23,24,26,27,31,30,34};

    private CircularImageView backgroundIv_c;
    private ImageView clockIv,backgroundIv_r;
    private boolean isCircle = false;
    private int pos = -1;

    private boolean isSendPic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_diy);
        StatusBarCompat.translucentStatusBar(this,true);
        setAndroidNativeLightStatusBar(this,true);
        isCircle = ((MyApplication)getApplicationContext()).isCirlce();
        EventBus.getDefault().register(this);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isSendPic){
            progress = 0;
            ProgressHudModel.newInstance().diss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initEvent() {
        diyAdapter.setOnItemClickListener(new DIYAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                pos = position;
                clockIv.setImageResource(isCircle?clock_c[position]:clock_r[position]);
            }
        });

        findViewById(R.id.diyIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.rightIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 保存成功
                if (resultUri==null){
                    showToast(getString(R.string.upDownBackground));
                    return;
                }
                if (pos==-1){
                    showToast(getString(R.string.chooseClock));
                    return;
                }
                ProgressHudModel.newInstance().show(DIYActivity.this,getString(R.string.loading),
                        getString(R.string.connect_error),10000);
                EXCDController.getInstance().initDialInfo();
            }
        });
    }

    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.diyDail));
        clockRv = findViewById(R.id.clockRv);
        clockRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        diyAdapter = new DIYAdapter(isCircle?clock_c:clock_r);
        clockRv.setAdapter(diyAdapter);
        clockRv.setHasFixedSize(true);
        clockRv.setNestedScrollingEnabled(false);

        if (isCircle)
            ((ImageView)findViewById(R.id.diyIv)).setImageResource(R.mipmap.diy_c);

        backgroundIv_c = findViewById(R.id.backgroundIv_c);
        backgroundIv_r = findViewById(R.id.backgroundIv_r);
        clockIv = findViewById(R.id.clockIv);
    }
    String[] proj = { MediaStore.Images.Media.DATA };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == UCrop.RESULT_ERROR){
            showToast(getString(R.string.crop_pic_failed));
            return;
        }
        switch (requestCode){
            case 1:{
                if (data==null||data.getData()==null)
                    return;
                if(MathUitl.isJpgFile(getContentResolver().query(data.getData(), proj, null, null, null))){
                    startPhotoZoom(data.getData());
                }else {
                    showToast(getString(R.string.chooseJpg));
                }

            }
            break;
            case  UCrop.REQUEST_CROP:{
                if (data!=null){
                    resultUri = UCrop.getOutput(data);
                    if (isCircle) {
                        backgroundIv_c.setImageURI(resultUri);
                    } else {
                        backgroundIv_r.setImageURI(resultUri);
                    }
                }
            }
            break;
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        if (findViewById(R.id.bottomRl).getVisibility()==View.GONE){
            findViewById(R.id.bottomRl).setVisibility(View.VISIBLE);
        }
        try {
            tmpFile = null;
            Uri path;
            if (!MathUitl.isBlank(tmpFile))
                path = Uri.fromFile(new File(tmpFile));
            else
                path = uri;
            //临时用一个名字用来保存裁剪后的图片
            Log.d("IMAGE******","开始裁切");
            fileName = getExternalFilesDir(null).getPath()+"/"+ Calendar.getInstance().getTimeInMillis() + ".jpg";
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            Uri target = Uri.fromFile(file);
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(getResources().getColor(R.color.rayblue));
            options.setStatusBarColor(getResources().getColor(R.color.rayblue));
            options.setActiveWidgetColor(getResources().getColor(R.color.rayblue));
            UCrop.of(path, target)
                    .withAspectRatio(1f, 1f)
                    .withMaxResultSize(240, 240)
                    .withOptions(options)
                    .start(this);
        } catch (Exception e) {
        }
    }

    /**
     * 更新数据显示
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdataView(UpdateView updateView){
        if(updateView.getState().equals("0")){//进度+1
            progress++;
            ProgressHudModel.newInstance().setProgress(progress);
        }else if (updateView.getState().equals("1")){//完成
            isSendPic = false;
            progress = 0;
            ProgressHudModel.newInstance().diss();
            showToast(getString(R.string.diyDailOK));
        }else if (updateView.getState().equals("2")){//失败
            isSendPic = false;
            progress = 0;
            showToast(getString(R.string.diyDailError));
        }else {
            if(!isSendPic){
                isSendPic = true;
                ProgressHudModel.newInstance().diss();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                FileInputStream fis;
                try {
                    fis = new FileInputStream(new File(resultUri.getPath()));
                    byte[] buf = new byte[1024];
                    int n;
                    while (-1 != (n = fis.read(buf)))
                        baos.write(buf, 0, n);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] datas = baos.toByteArray();

                int num = datas.length/5;

                ProgressHudModel.newInstance().showWithPie(DIYActivity.this,getString(R.string.diyDailing),5,
                        getString(R.string.diyDailError),60*1000);

                byte[] newDatas;
                for (int i=0;i<5;i++){
                    if (i < 4){
                        newDatas = new byte[num];
                    }else {
                        newDatas = new byte[datas.length-4*num];
                    }
                    System.arraycopy(datas,i*num,newDatas,0,num);
                    EXCDController.getInstance().writeForSendImage(newDatas,i+1,5,isCircle?clock_num_c[pos]:clock_num_r[pos],
                            MathUitl.getClockStyle(isCircle?clock_num_c[pos]:clock_num_r[pos]));
                }

            }
        }
    }
}
