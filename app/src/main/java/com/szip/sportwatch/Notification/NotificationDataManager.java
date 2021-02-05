package com.szip.sportwatch.Notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.mediatek.ctrl.notification.NotificationActions;
import com.mediatek.ctrl.notification.NotificationController;
import com.mediatek.ctrl.notification.NotificationData;
import com.mediatek.wearable.WearableManager;
import com.szip.sportwatch.BLE.BleClient;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.Util.LogUtil;
import com.szip.sportwatch.Util.MathUitl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class NotificationDataManager {
    private static final String TAG = "notify******";
    // For get tile and content of notification
    private static final int NOTIFICATION_TITLE_TYPE = 9;
    private static final int NOTIFICATION_CONTENT_TYPE = 10;
    private Handler mHandler;
    private SendNotficationDataThread mSendThread = null;
    private Context mContext;

    public NotificationDataManager(Context context) {
        LogUtil.getInstance().logd(TAG, "NotificationDataManager created!");
        mContext = context;
        mSendThread = new SendNotficationDataThread();
        mSendThread.start();
        mHandler = mSendThread.getHandler();
    }

    public void sendNotificationData(NotificationData notificationData){
        // Filter notification according to ignore list and exclusion list
        HashSet<CharSequence> blockList = BlockList.getInstance().getBlockList();
        HashSet<String> ignoreList = IgnoreList.getInstance().getIgnoreList();
        HashSet<String> exclusionList = IgnoreList.getInstance().getExclusionList();
        if (!blockList.contains(notificationData.getPackageName()) && !ignoreList.contains(notificationData.getPackageName())
                && !exclusionList.contains(notificationData.getPackageName())) {
            Log.i(TAG, "Notice: notification need send, package name=" + notificationData.getPackageName());
            // mSNThread.sendNotfications();
            Message message = new Message();
            message.what = SendNotficationDataThread.MESSAGE_SEND_NOTIFICATION;
            message.obj = (Object) notificationData;
            mHandler = mSendThread.getHandler();
            if (mHandler != null) {
                mHandler.sendMessage(message);
            }
        } else {
            Log.i(TAG, "Notice: notification don't need send, package name=" + notificationData.getPackageName());
        }
    }

    public NotificationData getNotificationData(Notification notification, String packageName, String tag, int id ){
        int watchVersion = WearableManager.getInstance().getRemoteDeviceVersion();
        LogUtil.getInstance().logd(TAG, "watch version is " + watchVersion);

        NotificationData notificationData = new NotificationData();
        String[] textArray = getNotificationText(notification);
        String[] pageTextArray = getNotificationPageText(notification); //android 4.4w.2 support
        if(pageTextArray!=null && textArray!=null){
            textArray = concat(textArray,pageTextArray);
        }
        notificationData.setTextList(textArray);
        try {
            LogUtil.getInstance().logd(TAG, "textlist = " + Arrays.toString(textArray));
        } catch (Exception e) {
            LogUtil.getInstance().logd(TAG, "get textlist error");
        }
        notificationData.setGroupKey(getGroupKey(notification));
        notificationData.setActionsList(getNotificationActions(notification));
        notificationData.setPackageName(packageName);
        notificationData.setAppID(MathUitl.getKeyFromValue(notificationData.getPackageName()));

        if(!TextUtils.isEmpty(notification.tickerText)){
            notificationData.setTickerText(notification.tickerText.toString());
        } else{
            LogUtil.getInstance().logd(TAG, "get ticker is null or empty");
            notificationData.setTickerText("");
        }
        notificationData.setWhen(notification.when);
        if(id == 0){ //Maybe some app's id is 0. like: hangouts(com.google.android.talk)
            id = 1 + (int) (Math.random() * 1000000);
            LogUtil.getInstance().logd(TAG, "the id is 0 and need create a random number : " + id);
        }
        notificationData.setMsgId(id);
        notificationData.setTag(packageName);

        LogUtil.getInstance().logd(TAG, "notificationData = " +  notificationData.toString());
        return notificationData;
    }

    @SuppressLint("UseSparseArrays")
    @SuppressWarnings("unchecked")
    public String[] getNotificationText(Notification notification) {
        String[] textArray = null;
        RemoteViews remoteViews = notification.contentView;
//        if (remoteViews == null){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                remoteViews = Notification.Builder.recoverBuilder(mContext, notification).createContentView();
//            }
//        }
        if (remoteViews == null) {
            textArray = null;
            Log.i(TAG,"remoteViews is null, set title and content to be empty. ");
        } else {
            HashMap<Integer, String> text = new HashMap<Integer, String>();
            try {
                Class<?> remoteViewsClass = Class.forName(RemoteViews.class.getName());
                Field[] outerFields = remoteViewsClass.getDeclaredFields();
                Log.i(TAG,"outerFields.length = " + outerFields.length);
                Field actionField = null;
                for (Field outerField : outerFields) {
                    if (outerField.getName().equals("mActions")) {
                        actionField = outerField;
                        break;
                    }
                }
                if (actionField == null) {
                    Log.e(TAG,"actionField is null, return null");
                    return null;
                }
                actionField.setAccessible(true);
                ArrayList<Object> actions = (ArrayList<Object>) actionField.get(remoteViews);
                int viewId = 0;
                for (Object action : actions) {
                    /*
                     * Get notification tile and content
                     */
                    Field[] innerFields = action.getClass().getDeclaredFields();
    
                    // RemoteViews curr_action = (RemoteViews)action;
                    Object value = null;
                    Integer type = null;
                    for (Field field : innerFields) {
                        field.setAccessible(true);
                        if (field.getName().equals("value")) {
                            value = field.get(action);
                        } else if (field.getName().equals("type")) {
                            type = field.getInt(action);
                        } else if (field.getName().equals("methodName")) {
                            String method = (String) field.get(action);
                            if (method.equals("setProgress")) {
                                return null;
                            }
                        }
                    }
    
                    // If this notification filed is title or content, save it to
                    // text list
                    if ((type != null)
                            && ((type == NOTIFICATION_TITLE_TYPE) || (type == NOTIFICATION_CONTENT_TYPE))) {
                        if (value != null) {
                            viewId++;
                            text.put(viewId, value.toString());
                            if (viewId == 2) {
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().logd(TAG, "getText ERROR");
            }
    
            textArray = text.values().toArray(new String[0]);
            if(textArray == null){
                Log.i(TAG,"get title and content from notification is null.Set it to be empty string.");
                textArray = new String[]{"",""};
            } else{
                Log.i(TAG,"textArray is " + Arrays.toString(textArray));
            }
        }
        String[] bigTextArray = new String[2];
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340
                && Build.VERSION.SDK_INT  >= 19) {//android 4.4
            //get bigtextstyle title and content
            String EXTRA_TITLE = "android.title";
            String EXTRA_TITLE_BIG = EXTRA_TITLE + ".big";
            String EXTRA_BIG_TEXT = "android.bigText";
            CharSequence mBigTitle = notification.extras.getCharSequence(EXTRA_TITLE_BIG);
            CharSequence mBigText = notification.extras.getCharSequence(EXTRA_BIG_TEXT);
            if(!TextUtils.isEmpty(mBigTitle)){
                bigTextArray[0] = mBigTitle.toString();
            } else if (textArray != null && textArray.length > 0 && !TextUtils.isEmpty(textArray[0])){
                bigTextArray[0] = textArray[0];
            } else{
                bigTextArray[0] = "";
            }

            if(!TextUtils.isEmpty(mBigText)){
                bigTextArray[1] = mBigText.toString();
            } else if (textArray != null && textArray.length > 1 && !TextUtils.isEmpty(textArray[1])){
                bigTextArray[1] = textArray[1];
            } else{
                bigTextArray[1] = "";
            }

        } else{
            bigTextArray = textArray;
            Log.i(TAG,"Android platform is lower than android 4.4 and does not support bigtextstyle attribute.");
        }
        try {
            LogUtil.getInstance().logd(TAG, "getNotificationText(), text list = " + Arrays.toString(bigTextArray));
        } catch (Exception e) {
            LogUtil.getInstance().logd(TAG, "getNotificationText Exception");
        }
        return bigTextArray;
    }

    public String[] getNotificationPageText(Notification notification) {
        String[] textArray = null;
        // get title and content of Pages
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340
                && android.os.Build.VERSION.SDK_INT  >= 20) {//android 4.4w.2
            String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
            String KEY_PAGES = "pages";
            Bundle wearableBundle = notification.extras.getBundle(EXTRA_WEARABLE_EXTENSIONS);
            if (wearableBundle != null) {
                Notification[] pages = getNotificationArrayFromBundle(wearableBundle,KEY_PAGES);
                if(pages!=null){
                   Log.i(TAG, "pages num = " + pages.length);
                   for(int i=0; i<pages.length; i++){
                       String[] pageTextArray = getNotificationText(pages[i]);
                       if(pageTextArray!=null){
                               if(i==0){
                                   textArray = pageTextArray;
                               } else{
                                   textArray = concat(textArray,pageTextArray);
                               }
                           }
                       }
                   }
                }
        } else{
            Log.i(TAG,"Android platform is lower than android 4.4w.2 and does not support page attribute.");
        }
        try {
            LogUtil.getInstance().logd(TAG, "getNotificationPageText(), text list = " + Arrays.toString(textArray));
        } catch (Exception e) {
            LogUtil.getInstance().logd(TAG, "getNotificationPageText Exception");
        }
        return textArray;
    }

    public Notification[] getNotificationArrayFromBundle(Bundle bundle, String key) {
            Parcelable[] array = bundle.getParcelableArray(key);
            if (array instanceof Notification[] || array == null) {
                return (Notification[]) array;
            }
            Notification[] typedArray = Arrays.copyOf(array, array.length,
                    Notification[].class);
            bundle.putParcelableArray(key, typedArray);
            return typedArray;
     }

    public  String[] concat(String[] first, String[] second) {
        String[] result= new String[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public ArrayList<NotificationActions> getNotificationActions(Notification notification){
        ArrayList<NotificationActions> actionsList = new ArrayList<NotificationActions>();
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340) {
            try {
                // get contentIntent field(The intent to execute when the expanded status entry is clicked.)
                Field mContentIntentField = Notification.class.getDeclaredField("contentIntent");
                if(mContentIntentField!=null){
                    mContentIntentField.setAccessible(true);
                    PendingIntent contentIntent = (PendingIntent) mContentIntentField.get(notification);
                    // the contentIntent maybe is null, if the contentIntent is null do not add it to actionsList
                    if (contentIntent != null) {
                        NotificationActions notificationAction = new NotificationActions();
                        notificationAction.setActionId(String.valueOf(0)); // always is 0
//                        notificationAction.setActionTitle(mContext.getString(R.string.notification_action_open));
                        notificationAction.setActionTitle("test");
                        notificationAction.setActionIntent(contentIntent);
                        actionsList.add(notificationAction);
                    } else{
                        Log.i(TAG,"contentIntent is null.");
                    }
                } else{
                    Log.i(TAG,"get contentIntent field failed.");
                }
               
                if (android.os.Build.VERSION.SDK_INT >= 19) {//android 4.4
                    Field mActionField = Notification.class.getDeclaredField("actions");// get Action[] field
                    if (mActionField != null) {
                        mActionField.setAccessible(true);
                        Object[] actions = (Object[]) mActionField.get(notification);
                        int index = 1;
                        if (actions != null) {
                            for (Object action : actions) {
                                Field[] innerFields = action.getClass().getDeclaredFields();
                                NotificationActions notificationAction = new NotificationActions();
                                for (Field field : innerFields) {
                                    field.setAccessible(true);
                                    if (field.getType().getName()
                                            .equals(CharSequence.class.getName())) {
                                        // get Action title
                                        CharSequence title = (CharSequence) field.get(action);
                                        notificationAction.setActionTitle(title.toString());
                                        Log.i(TAG,
                                                "action title = "
                                                        + notificationAction.getActionTitle());
                                    } else if (field.getType().getName()
                                            .equals(PendingIntent.class.getName())) {
                                        // get Action PendingIntent
                                        PendingIntent intent = (PendingIntent) field.get(action);
                                        notificationAction.setActionIntent(intent);
                                        Log.i(TAG, "pendingintent = "
                                                + notificationAction.getActionIntent().toString());
                                    }
                                }
                                notificationAction.setActionId(String.valueOf(index));
                                actionsList.add(notificationAction);
                                index++;
                            }
                            Log.i(TAG, "action size = " + actionsList.size());
                        }
                    } else {
                        Log.i(TAG, "get Action field failed.");
                        return null;
                    }
                } else {
                    Log.i(TAG,"Android platform is lower than android 4.4 and does not support gction attribute.");
                }
            } catch (NoSuchFieldException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return actionsList;
    }

    public String getGroupKey(Notification notification){
        String groupKey = "";
        if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340
                && android.os.Build.VERSION.SDK_INT  >= 20) {//android 4.4w.2
            groupKey = notification.getGroup();
        } else{
            Log.i(TAG,"Android platform is lower than android 4.4w.2 and does not support group attribute.");
        }
        LogUtil.getInstance().logd(TAG, "groupKey = " + groupKey);
        return groupKey;
    }

    private String messTemp = "";
    private long msgTime;

    private class SendNotficationDataThread extends Thread {
        public static final int MESSAGE_SEND_NOTIFICATION = 1;
        private NotificationData notificationData = null;

        @SuppressLint("HandlerLeak")
        private Handler mHandler;

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            Looper.prepare();

            mHandler = new Handler() {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MESSAGE_SEND_NOTIFICATION:
                            notificationData = (NotificationData) msg.obj;
                            if (notificationData != null) {
//                                 Use the Content to Add Applist
                                Map<Object, Object> applist = AppList.getInstance().getAppList();
                                if (!applist.containsValue(notificationData.getPackageName())) {
                                    int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
                                    applist.remove(AppList.MAX_APP);
                                    max = max + 1;
                                    applist.put(AppList.MAX_APP, max);
                                    applist.put(max, notificationData.getPackageName());
                                    notificationData.setAppID(max + "");
                                    AppList.getInstance().saveAppList(applist);
                                }

                                // save notificationSyncList to file
                                if (WearableManager.getInstance().getRemoteDeviceVersion() >= WearableManager.VERSION_340
                                        && android.os.Build.VERSION.SDK_INT >= 18) {
                                    NotificationSyncList.getInstance().addNotificationData(
                                            notificationData);
                                    NotificationSyncList.getInstance().saveSyncList();
                                }
                                if (notificationData.getTextList()==null||Arrays.toString(notificationData.getTextList()).equals("[]")){
                                    String[] str1 = new String[]{"",""};
                                    String[] str = notificationData.getTickerText().split(":");
                                    if (notificationData.getTickerText().equals(""))
                                        return;
                                    if (str.length>=2){
                                        notificationData.setTextList(str);
                                    } else {
                                        str1[1] = str[0];
                                        notificationData.setTextList(str1);
                                    }


                                    if(Calendar.getInstance().getTimeInMillis()-msgTime<1000&&
                                            messTemp.equals(notificationData.getTickerText()) ){  // && messTemp.equals(tickerText)
                                        return ;
                                    }else {
                                        msgTime = Calendar.getInstance().getTimeInMillis();
                                        messTemp = notificationData.getTickerText();
                                    }

                                }
                                LogUtil.getInstance().logd(TAG, "SendNotficationThread mThreadNotfication = "
                                        + notificationData);
                                if (MyApplication.getInstance().isMtk()){
                                    NotificationController.getInstance(mContext)
                                            .sendNotfications(notificationData);
                                }else {
                                    PackageInfo info = null;
                                    PackageManager pm = mContext.getPackageManager();
                                    try {
                                        info = pm.getPackageInfo(notificationData.getPackageName(),PackageManager.GET_ACTIVITIES);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    BleClient.getInstance().writeForSendNotify(notificationData.getTickerText(),
                                            info.applicationInfo.loadLabel(pm).toString(),1);
                                }
                                notificationData = null;
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
            Looper.loop();  
        }

        public Handler getHandler() {
            return mHandler;
        }
    }
}
