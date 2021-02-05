package com.szip.sportwatch.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.szip.sportwatch.Contorller.MainActivity;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.FileUtil;

public class NotificationView {
    private Context context;
    private static NotificationView mInstance;



    private NotificationView(Context context){
        this.context = context;
    }

    public static NotificationView getInstance(Context context)
    {
        if (mInstance == null)
        {
            synchronized (FileUtil.class)
            {
                if (mInstance == null)
                {
                    mInstance = new NotificationView(context);
                }
            }
        }
        return mInstance;
    }
    private NotificationManager notificationManager;
    public void showNotify(boolean state){
//        if (notificationManager==null){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //        获取Notification实例
        Notification notification=new NotificationCompat.Builder(context,"0103")
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(state?context.getResources().getString(R.string.connected):context.getResources().getString(R.string.disConnect))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false) //点击通知栏后是否消失
                .setColor(Color.parseColor("#2CBDF2"))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.launch_logo))
                .setSmallIcon(R.mipmap.small)
                // .setCustomContentView(remoteView) // 设置自定义的RemoteView，需要API最低为24
                // 设置点击通知栏后跳转地址
                .setContentIntent(PendingIntent.getActivity(context, 1,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
//        添加渠道
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("0103", "iSmarport", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("description");
            notificationManager.createNotificationChannel(channel);
        }
        // 设置常驻 Flag
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        //展示通知栏
        notificationManager.notify(0103,notification);
    }


    public void setting() {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, 0103);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        context.startActivity(intent);
    }

}
