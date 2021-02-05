package com.szip.sportwatch.Contorller;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.szip.sportwatch.BLE.BleClient;
import com.szip.sportwatch.Interface.HttpCallbackWithBase;
import com.szip.sportwatch.Model.HttpBean.AvatarBean;
import com.szip.sportwatch.Model.HttpBean.BaseApi;
import com.szip.sportwatch.Model.UserInfo;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.JsonGenericsSerializator;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.CharacterPickerWindow;
import com.szip.sportwatch.View.MyAlerDialog;
import com.szip.sportwatch.View.character.OnOptionChangedListener;
import com.szip.sportwatch.BLE.EXCDController;
import com.yalantis.ucrop.UCrop;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import okhttp3.Call;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener,HttpCallbackWithBase{

    /**
     * 头像、昵称、性别、身高、体重、生日
     * */
    private ImageView pictureIv;
    private TextView userNameTv;
    private TextView sexTv;
    private TextView heightTv;
    private TextView weightTv;
    private TextView birthdayTv;

    private Context mContext;

    private UserInfo userInfo;
    private MyApplication app;

    /**
     * 是否公制单位
     * */
    private boolean isMetric;

    private Dialog dialog;
    private String tmpFile;
    private String photoName = "";
    private String fileName;
    int height = 0;
    int weight = 0;
    int heightBritish = 0;
    int weightBritish = 0;
    private final int IMAGE_CAPTURE = 0;
    private final int IMAGE_MEDIA = 1;


    /**
     * 数据选择框
     * */
    private CharacterPickerWindow window;
    private CharacterPickerWindow window1;
    private CharacterPickerWindow window2;
    private CharacterPickerWindow window3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super. onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_user_info);
        mContext = this;
        app = (MyApplication) getApplicationContext();
        initView();
        initEvent();
        initData();
        initWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithBase(null);
    }

    /**
     * 初始化选择器
     * */
    private void initWindow() {


        //性别选择器
        window = new CharacterPickerWindow(UserInfoActivity.this,getString(R.string.sex));
        final List<String> sexList =new ArrayList<>(Arrays.asList(getString(R.string.female),getString(R.string.male)));
        //初始化选项数据
        window.getPickerView().setPicker(sexList);
        //设置默认选中的三级项目
        window.setCurrentPositions(app.getUserInfo().getSex(), 0, 0);
        //监听确定选择按钮
        window.setOnoptionsSelectListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                try {
                    sexTv.setText(sexList.get(option1));
                    if (userInfo.getAvatar()==null){
                        if (option1==0)
                            pictureIv.setImageResource(R.mipmap.my_head_female_36);
                        else
                            pictureIv.setImageResource(R.mipmap.my_head_male_36);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        //身高选择器
        window1 = new CharacterPickerWindow(UserInfoActivity.this,getString(R.string.height));
        if (isMetric){
            final ArrayList<String> list2 = DateUtil.getStature();
            window1.getPickerView().setText("cm","");
            //初始化选项数据
            window1.getPickerView().setPicker(list2);
            //设置默认选中的三级项目
            window1.setCurrentPositions(userInfo.getHeight()-50, 0, 0);
            //监听确定选择按钮
            window1.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    try {
                        heightTv.setText(list2.get(option1)+"cm");
                        height = Integer.valueOf(list2.get(option1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            final ArrayList<String> list2 = DateUtil.getStatureWithBritish();
            window1.getPickerView().setText("in","");
            //初始化选项数据
            window1.getPickerView().setPicker(list2);
            //设置默认选中的三级项目
            window1.setCurrentPositions(MathUitl.cm2Inch(userInfo.getHeight())-20, 0, 0);
            //监听确定选择按钮
            window1.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    try {
                        heightTv.setText(list2.get(option1)+"in");
                        heightBritish = Integer.valueOf(list2.get(option1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        //重量选择器
        window2 = new CharacterPickerWindow(UserInfoActivity.this,getString(R.string.weight));
        //初始化选项数据
        if (isMetric){
            window2.getPickerView().setText("kg","");
            final ArrayList<String> list3 = DateUtil.getWeight();
            window2.getPickerView().setPicker(list3);
            //设置默认选中的三级项目
            window2.setCurrentPositions(userInfo.getWeight()-30, 0, 0);

            //监听确定选择按钮
            window2.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    try {
                        weightTv.setText(list3.get(option1)+"kg");
                        weight = Integer.valueOf(list3.get(option1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            window2.getPickerView().setText("lb","");
            final ArrayList<String> list3 = DateUtil.getWeightWithBritish();
            window2.getPickerView().setPicker(list3);
            //设置默认选中的三级项目
            window2.setCurrentPositions(MathUitl.kg2Pound(userInfo.getWeight())-67, 0, 0);
            //监听确定选择按钮
            window2.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    try {
                        weightTv.setText(list3.get(option1)+"lb");
                        weightBritish = Integer.valueOf(list3.get(option1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        ArrayList<String> list1 = DateUtil.getYearList();
        //生日选择器
        window3 = new CharacterPickerWindow(UserInfoActivity.this,getString(R.string.birthday));
        //初始化选项数据
        window3.getPickerView().setPickerForDate(list1);
        //设置默认选中的三级项目
        if (userInfo.getBirthday()!=null){
            String [] strs = userInfo.getBirthday().split("-");
            window3.setCurrentPositions(Integer.valueOf(strs[0])-1930, Integer.valueOf(strs[1])-1, Integer.valueOf(strs[2])-1);
        }else {
            window3.setCurrentPositions(list1.size()/2, 0, 0);
        }

        //监听确定选择按钮
        window3.setOnoptionsSelectListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                try {
                    birthdayTv.setText(String.format(Locale.ENGLISH,"%4d-%02d-%02d",(1930+option1),(option2+1),(option3+1)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 初始化视图
     * */
    private void initView() {
        StatusBarCompat.translucentStatusBar(UserInfoActivity.this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.userInfo));
        pictureIv = findViewById(R.id.pictureIv);
        userNameTv = findViewById(R.id.userNameTv);
        sexTv = findViewById(R.id.sexTv);
        heightTv = findViewById(R.id.heightTv);
        weightTv = findViewById(R.id.weightTv);
        birthdayTv = findViewById(R.id.birthdayTv);
    }

    /**
     * 初始化事件监听
     * */
    private void initEvent() {
        findViewById(R.id.pictureRl).setOnClickListener(this);
        findViewById(R.id.userNameRl).setOnClickListener(this);
        findViewById(R.id.sexRl).setOnClickListener(this);
        findViewById(R.id.heightRl).setOnClickListener(this);
        findViewById(R.id.weightRl).setOnClickListener(this);
        findViewById(R.id.birthdayRl).setOnClickListener(this);
        findViewById(R.id.backIv).setOnClickListener(this);
        findViewById(R.id.rightIv).setOnClickListener(this);
    }

    /**
     * 初始化数据
     * */
    private void initData() {
        userInfo = app.getUserInfo();
        height = userInfo.getHeight();
        weight = userInfo.getWeight();
        heightBritish = userInfo.getHeightBritish();
        weightBritish = userInfo.getWeightBritish();
        userNameTv.setText(userInfo.getUserName());
        sexTv.setText(userInfo.getSex()==1?getString(R.string.male):getString(R.string.female));
        if (app.getUserInfo().getAvatar()!=null)
            Glide.with(this).load(app.getUserInfo().getAvatar()).into(pictureIv);
        else
            pictureIv.setImageResource(app.getUserInfo().getSex()==1?R.mipmap.my_head_male_52: R.mipmap.my_head_female_52);
        isMetric = userInfo.getUnit()==0;
        if (isMetric){
            heightTv.setText(height+"cm");
            weightTv.setText(weight+"kg");
        }else {
            heightTv.setText(heightBritish+"in");
            weightTv.setText(weightBritish+"lb");
        }

        birthdayTv.setText(userInfo.getBirthday());
    }

    /**
     * 点击事件监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                finish();
                break;
            case R.id.pictureRl:
                selectPhotoDialog();
                break;
            case R.id.userNameRl:
                MyAlerDialog.getSingle().showAlerDialogWithEdit(getString(R.string.userName), userNameTv.getText().toString(), getString(R.string.enterUserName),
                        null, null,false, new MyAlerDialog.AlerDialogEditOnclickListener() {
                            @Override
                            public void onDialogEditTouch(String edit1) {
                                if (edit1.length()<=12){
                                    userNameTv.setText(edit1);
                                }else {
                                    showToast(getString(R.string.nameLong));
                                }

                            }
                        },mContext);
                break;
            case R.id.sexRl:
                window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.heightRl:
                window1.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.weightRl:
                window2.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.birthdayRl:
                window3.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.rightIv:
                HttpMessgeUtil.getInstance(this).setHttpCallbackWithBase(this);
                ProgressHudModel.newInstance().show(mContext,getString(R.string.waitting),getString(R.string.httpError),
                        3000);
                try {
                    HttpMessgeUtil.getInstance(this).postForSetUserInfo(userNameTv.getText().toString(),
                            sexTv.getText().toString().equals(getString(R.string.male))?"1":"0",
                            birthdayTv.getText().toString()==""?null:birthdayTv.getText().toString(),
                            height+"", weight+"",heightBritish+"", weightBritish+"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void selectPhotoDialog() {
        if (dialog != null)
            dialog.cancel();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_choose_photo, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.slide_up_down);
        final WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView btn_album;
        TextView btn_camera;
        TextView btn_cancel;
        btn_album = view.findViewById(R.id.btn_album);
        btn_camera = view.findViewById(R.id.btn_camera);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_MEDIA);
                dialog.cancel();
            }
        });
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenCamera();
                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                // 设置显示位置
                dialog.onWindowAttributesChanged(wl);
                // 设置点击外围解散
                dialog.setCanceledOnTouchOutside(true);
            }
        });
        dialog.show();
    }

    private void tryToOpenCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED||
                    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        100);
            }else {
               takePhoto();
            }
        }else {
           takePhoto();
        }
    }


    private void takePhoto(){

        photoName = getExternalFilesDir(null).getPath()+"/"+ Calendar.getInstance().getTimeInMillis() + ".jpg";
        File file = new File(photoName);
        Log.d("IMAGE******","photoName = "+photoName);
        Uri photoURI = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoURI = FileProvider.getUriForFile(this,"com.szip.sportwatch.fileprovider", file);
        } else {
            photoURI = Uri.fromFile(file);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 下面这句指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            int code = grantResults[0];
            if (code == PackageManager.PERMISSION_GRANTED){
                takePhoto();
            }else {

                showToast(getString(R.string.permissionErrorForCamare));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
       if (resultCode == UCrop.RESULT_ERROR){
            showToast(getString(R.string.crop_pic_failed));
        }
        switch (requestCode){
            case IMAGE_CAPTURE:{// 相机
                Log.d("IMAGE******","拍照回调");
                File file = new File(photoName);
                Log.d("IMAGE******","photoName = "+photoName);
                if (file.exists()) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        uri = Uri.fromFile(file);
                    } else {
                        uri = FileProvider.getUriForFile(this, "com.szip.sportwatch.fileprovider", file);
                    }
                    startPhotoZoom(uri);
                }
            }
                break;
            case IMAGE_MEDIA:{
                Log.d("IMAGE******","相册回调");
                if (data!=null)
                startPhotoZoom(data.getData());
            }
                break;
            case  UCrop.REQUEST_CROP:{
                Log.d("IMAGE******","裁剪回调");
                if (data!=null){
                    try {
                        HttpMessgeUtil.getInstance(this).postUpdownAvatar(new File(fileName), new GenericsCallback<AvatarBean>(new JsonGenericsSerializator()) {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(AvatarBean response, int id) {
                                if (response.getCode()==200){
                                    //上传头像
                                    if (data!=null){
                                        Uri resultUri = UCrop.getOutput(data);
                                        pictureIv.setImageURI(resultUri);
                                        app.getUserInfo().setAvatar(response.getData().getUrl());
                                        MathUitl.saveInfoData(mContext,app.getUserInfo()).commit();//退出之前更新本地缓存的userInfo
                                    }
                                    //裁剪成功之后，删掉之前拍的照片
                                    new File(photoName).delete();
                                    new File(fileName).delete();
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
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
                    .withMaxResultSize(200, 200)
                    .withOptions(options)
                    .start(this);
        } catch (Exception e) {
        }
    }

    /**
     * 上传用户信息接口返回的回调
     * */
    @Override
    public void onCallback(BaseApi baseApi, int id) {
        ProgressHudModel.newInstance().diss();
        showToast(getString(R.string.saved));
        app.getUserInfo().setUserName(userNameTv.getText().toString());
        app.getUserInfo().setSex(sexTv.getText().toString().equals(getString(R.string.male))?1:0);
        app.getUserInfo().setBirthday(birthdayTv.getText().toString()==""?null:birthdayTv.getText().toString());
        app.getUserInfo().setHeight(height);
        app.getUserInfo().setWeight(weight);
        app.getUserInfo().setHeightBritish(heightBritish);
        app.getUserInfo().setWeightBritish(weightBritish);
        MathUitl.saveInfoData(mContext,app.getUserInfo()).commit();//退出之前更新本地缓存的userInfo
        if (MainService.getInstance().getState()!=3){
            showToast(getString(R.string.syceError));
        }else {
            if(app.isMtk()){
                EXCDController.getInstance().writeForSetInfo(app.getUserInfo());
            }else {
                BleClient.getInstance().writeForUpdateUserInfo();
            }

        }
        finish();

    }
}
