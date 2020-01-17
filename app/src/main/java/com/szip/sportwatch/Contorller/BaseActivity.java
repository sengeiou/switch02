package com.szip.sportwatch.Contorller;

import androidx.appcompat.app.AppCompatActivity;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

import android.widget.Toast;

import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.FileUtil;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Administrator on 2019/11/28.
 */

public class BaseActivity extends AppCompatActivity {

    private String deleteStr = null;
    protected void showToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    protected void shareShow(String str){
        deleteStr = str;
        OnekeyShare oks = new OnekeyShare();

        oks.setCallback(callback);
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("");

        // text是分享文本，所有平台都需要这个字段
        oks.setText("");

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        // oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
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
            showToast(getString(R.string.shareSuccess));
            FileUtil.getInstance().deleteFile(deleteStr);
            finish();
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            // TODO 失败，打印throwable为错误码
            showToast(getString(R.string.shareFail));
            FileUtil.getInstance().deleteFile(deleteStr);
            finish();
        }

        @Override
        public void onCancel(Platform platform, int i) {
            // TODO 分享取消操作
            showToast(getString(R.string.shareCancel));
            FileUtil.getInstance().deleteFile(deleteStr);
            finish();
        }
    };
}
