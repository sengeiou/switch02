package com.szip.jswitch.Activity.userInfo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.szip.jswitch.BLE.BleClient;
import com.szip.jswitch.BLE.EXCDController;
import com.szip.jswitch.BuildConfig;
import com.szip.jswitch.Model.HttpBean.AvatarBean;
import com.szip.jswitch.Model.HttpBean.BaseApi;
import com.szip.jswitch.Model.UserInfo;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Service.MainService;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.Util.HttpMessgeUtil;
import com.szip.jswitch.Util.JsonGenericsSerializator;
import com.szip.jswitch.Util.MathUitl;
import com.szip.jswitch.View.CharacterPickerWindow;
import com.szip.jswitch.View.character.OnOptionChangedListener;
import com.yalantis.ucrop.UCrop;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;


public class UserInfoPresenterImpl implements IUserInfoPresenter{

    private Context context;
    private Handler handler;
    private IUserInfoView iUserInfoView;

    public UserInfoPresenterImpl(Context context, IUserInfoView iUserInfoView) {
        this.context = context;
        this.iUserInfoView = iUserInfoView;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void getSex(final CharacterPickerWindow window, final UserInfo userInfo) {
        final List<String> sexList =new ArrayList<>(Arrays.asList(context.getString(R.string.female),context.getString(R.string.male)));
        window.setTitleTv(context.getString(R.string.sex));
        window.getPickerView().setText("","");
        //?????????????????????
        window.getPickerView().setPicker(sexList);
        //?????????????????????????????????
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                window.setCurrentPositions(userInfo.getSex(), 0, 0);
            }
        },100);

        //????????????????????????
        window.setOnoptionsSelectListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                if (iUserInfoView!=null)
                    iUserInfoView.setSex(sexList.get(option1),option1);
            }
        });
    }

    @Override
    public void getHeight(final CharacterPickerWindow window, final UserInfo userInfo) {
        window.setTitleTv(context.getString(R.string.height));
        if (userInfo.getUnit()==0){
            final ArrayList<String> list = DateUtil.getStature();
            window.getPickerView().setText("cm","");
            //?????????????????????
            window.getPickerView().setPicker(list);
            //?????????????????????????????????
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    window.setCurrentPositions(userInfo.getHeight()-50, 0, 0);
                }
            },100);

            //????????????????????????
            window.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    if (iUserInfoView!=null)
                        iUserInfoView.setHeight(list.get(option1)+"cm",Integer.valueOf(list.get(option1)),0);
                }
            });
        }else {
            final ArrayList<String> list = DateUtil.getStatureWithBritish();
            window.getPickerView().setText("in","");
            //?????????????????????
            window.getPickerView().setPicker(list);
            //?????????????????????????????????
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    window.setCurrentPositions(userInfo.getHeightBritish()-20, 0, 0);
                }
            },100);

            //????????????????????????
            window.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    if (iUserInfoView!=null)
                        iUserInfoView.setHeight(list.get(option1)+"in",Integer.valueOf(list.get(option1)),1);
                }
            });
        }
    }

    @Override
    public void getWeight(final CharacterPickerWindow window, final UserInfo userInfo) {
        window.setTitleTv(context.getString(R.string.weight));
        //?????????????????????
        if (userInfo.getUnit()==0){
            window.getPickerView().setText("kg","");
            final ArrayList<String> list = DateUtil.getWeight();
            window.getPickerView().setPicker(list);
            //?????????????????????????????????
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    window.setCurrentPositions(userInfo.getWeight()-30, 0, 0);
                }
            },100);


            //????????????????????????
            window.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    if (iUserInfoView!=null)
                        iUserInfoView.setWeight(list.get(option1)+"kg",Integer.valueOf(list.get(option1)),0);
                }
            });
        }else {
            window.getPickerView().setText("lb","");
            final ArrayList<String> list = DateUtil.getWeightWithBritish();
            window.getPickerView().setPicker(list);
            //?????????????????????????????????
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    window.setCurrentPositions(userInfo.getWeightBritish()-67, 0, 0);
                }
            },100);

            //????????????????????????
            window.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    if (iUserInfoView!=null)
                        iUserInfoView.setWeight(list.get(option1)+"lb",Integer.valueOf(list.get(option1)),1);
                }
            });
        }
    }

    @Override
    public void getBirthday(final CharacterPickerWindow window, final UserInfo userInfo) {
        window.setTitleTv(context.getString(R.string.birthday));
        final ArrayList<String> list1 = DateUtil.getYearList();
        window.getPickerView().setText(null,null);
        //?????????????????????
        window.getPickerView().setPickerForDate(list1);
        //?????????????????????????????????
        if (userInfo.getBirthday()!=null){
            final String [] strs = userInfo.getBirthday().split("-");
            if(strs.length!=3)
                return;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    window.setCurrentPositions(Integer.valueOf(strs[0])-1930, Integer.valueOf(strs[1])-1, Integer.valueOf(strs[2])-1);
                }
            },100);

        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    window.setCurrentPositions(list1.size()/2, 0, 0);
                }
            },100);

        }

        //????????????????????????
        window.setOnoptionsSelectListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                if (iUserInfoView!=null)
                    iUserInfoView.setBirthday(String.format(Locale.ENGLISH,"%4d-%02d-%02d",(1930+option1),(option2+1),(option3+1)));
            }
        });
    }

    @Override
    public void saveUserInfo(final UserInfo userInfo) {
        try {
            HttpMessgeUtil.getInstance().postForSetUserInfo(userInfo.getUserName(), userInfo.getSex() + "",
                    userInfo.getBirthday(), userInfo.getHeight() + "", userInfo.getWeight() + "",
                    userInfo.getHeightBritish() + "", userInfo.getWeightBritish() + "",
                    new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (iUserInfoView!=null)
                                iUserInfoView.saveSeccuss(false);
                        }

                        @Override
                        public void onResponse(BaseApi response, int id) {
                            if (response.getCode()==200){

                                MyApplication.getInstance().setUserInfo(userInfo.copy());
                                if (iUserInfoView!=null)
                                    iUserInfoView.saveSeccuss(true);
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectPhoto(final Dialog dialog,int y) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_photo, null);

        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // ??????????????????
        window.setWindowAnimations(R.style.slide_up_down);
        final WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = y;
        // ??????????????????????????????????????????????????????
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
                if (iUserInfoView!=null)
                    iUserInfoView.getPhotoPath(intent,false);

                dialog.cancel();
            }
        });
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String photoName = MyApplication.getInstance().getPrivatePath()+"camera";
                File file = new File(photoName);
                Uri photoURI = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider", file);
                } else {
                    photoURI = Uri.fromFile(file);
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // ???????????????????????????????????????????????????????????????
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if (iUserInfoView!=null)
                    iUserInfoView.getPhotoPath(intent,true);
                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                // ??????????????????
                dialog.onWindowAttributesChanged(wl);
                // ????????????????????????
                dialog.setCanceledOnTouchOutside(true);
            }
        });
        dialog.show();
    }

    @Override
    public void cropPhoto(Uri uri) {
        try {
            Uri path = uri;
            //???????????????????????????????????????????????????
            String fileName = MyApplication.getInstance().getPrivatePath()+"crop.jpg";
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            Uri target = Uri.fromFile(file);
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(context.getResources().getColor(R.color.rayblue));
            options.setStatusBarColor(context.getResources().getColor(R.color.rayblue));
            options.setActiveWidgetColor(context.getResources().getColor(R.color.rayblue));
            UCrop uCrop = UCrop.of(path, target)
                    .withAspectRatio(1f, 1f)
                    .withMaxResultSize(200, 200)
                    .withOptions(options);
            if (iUserInfoView!=null){
                iUserInfoView.getCropPhoto(uCrop);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void updownPhoto(File file) {
        try {
            HttpMessgeUtil.getInstance().postUpdownAvatar(file, new GenericsCallback<AvatarBean>(new JsonGenericsSerializator()) {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(AvatarBean response, int id) {
                    if (response.getCode()==200){
                        //????????????
                        if (iUserInfoView!=null)
                            iUserInfoView.setPhoto(response.getData().getUrl());
                        //?????????????????????????????????????????????
                        new File(MyApplication.getInstance().getPrivatePath()+"camera").delete();
                        new File(MyApplication.getInstance().getPrivatePath()+"crop.jpg").delete();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setViewDestory() {
        iUserInfoView = null;
    }
}
