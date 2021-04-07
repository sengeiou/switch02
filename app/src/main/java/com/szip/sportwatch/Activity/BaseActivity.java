package com.szip.sportwatch.Activity;

import androidx.appcompat.app.AppCompatActivity;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.FileUtil;
import com.szip.sportwatch.Util.LogUtil;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.ScreenCapture;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

import static com.szip.sportwatch.MyApplication.FILE;

/**
 * Created by Administrator on 2019/11/28.
 */

public class BaseActivity extends AppCompatActivity {

    private String deleteStr = null;

    protected void showToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    /**
     * 分享
     * */
    protected void shareShow(View view){
        String str = ScreenCapture.getBitmap
                (this, view);
        deleteStr = str;
        OnekeyShare oks = new OnekeyShare();

        oks.setCallback(callback);
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("");

        // text是分享文本，所有平台都需要这个字段
        oks.setText("");

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(str);// 确保SDcard下面存在此张图片

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");

        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("");
        // oks.setViewToShare(viewToShare);

        // 启动分享GUI
        oks.show(this);
    }

    /**
     * 分享长图
     * */
    protected void shareShowLong(ScrollView view){
        String str = ScreenCapture.getScollerBitmap
                (this, view);
        deleteStr = str;
        OnekeyShare oks = new OnekeyShare();

        oks.setCallback(callback);
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("");

        // text是分享文本，所有平台都需要这个字段
        oks.setText("");

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(str);// 确保SDcard下面存在此张图片

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");

        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("");
        // oks.setViewToShare(viewToShare);

        // 启动分享GUI
        oks.show(this);
    }

    PlatformActionListener callback = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            // TODO 分享成功后的操作或者提示
            FileUtil.getInstance().deleteFile(deleteStr);
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            // TODO 失败，打印throwable为错误码
            FileUtil.getInstance().deleteFile(deleteStr);
        }

        @Override
        public void onCancel(Platform platform, int i) {
            // TODO 分享取消操作
            FileUtil.getInstance().deleteFile(deleteStr);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.getInstance().logd("SZIP******","退出保存");
        ProgressHudModel.newInstance().diss();
        getSharedPreferences(FILE,MODE_PRIVATE).edit().putInt("updownTime",((MyApplication)getApplication()).getUpdownTime()).commit();
    }

    /**
     * 适配Android字体（随着系统字体改变而改变）
     * */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    /**
     * 修改状态栏字体颜色
     * */
    protected void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    protected void setTitleText(String msg){
        TextView textView = findViewById(R.id.titleTv);
        if (textView!=null){
            textView.setText(msg);
        }
    }

}
