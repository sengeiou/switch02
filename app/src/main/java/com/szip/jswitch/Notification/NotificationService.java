
package com.szip.jswitch.Notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mediatek.ctrl.notification.NotificationEventListener;
import com.szip.jswitch.MyApplication;

import java.util.HashSet;
import java.util.Map;

public class NotificationService implements NotificationEventListener {
    private static final String TAG = "AppManager/NotificationService";

    public void notifyBlockListChanged(String appId) {

        Map<Object, Object> applist = AppList.getInstance().getAppList();
        CharSequence appPackageName = (CharSequence) applist.get(Integer.parseInt(appId));
        Log.i(TAG, "notifyBlockListChanged, appPackageName is :" + appPackageName+" ;appID = "+appId);
        HashSet<CharSequence> blockList = BlockList.getInstance().getBlockList();
        if (!(blockList.contains(appPackageName)) && appPackageName != null) {
            BlockList.getInstance().addBlockItem(appPackageName);
            BlockList.getInstance().saveBlockList();
        } 
    }

    @Override
    public void notifyNotificationDeleted(String msgId) {
        // delete the notification
        Intent intent = new Intent();
        intent.setAction("android.deletion.notification");
        intent.putExtra("msgid", Integer.valueOf(msgId));
        Context context = MyApplication.getInstance().getApplicationContext();
        context.startService(intent);
    }

    @Override
    public void notifyNotificationActionOperate(String msgId, String actionId) {
        NotificationSyncList.getInstance().handleNotificationAction(msgId,actionId);
    }

    @Override
    public void clearAllNotificationData() {
        NotificationSyncList.getInstance().clearSyncList();
    }

}
