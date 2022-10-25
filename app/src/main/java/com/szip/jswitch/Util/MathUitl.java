package com.szip.jswitch.Util;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.szip.jswitch.DB.dbModel.AnimalHeatData;
import com.szip.jswitch.DB.dbModel.AnimalHeatData_Table;
import com.szip.jswitch.DB.dbModel.BloodOxygenData;
import com.szip.jswitch.DB.dbModel.BloodOxygenData_Table;
import com.szip.jswitch.DB.dbModel.BloodPressureData;
import com.szip.jswitch.DB.dbModel.BloodPressureData_Table;
import com.szip.jswitch.DB.dbModel.EcgData;
import com.szip.jswitch.DB.dbModel.EcgData_Table;
import com.szip.jswitch.DB.dbModel.HeartData;
import com.szip.jswitch.DB.dbModel.HeartData_Table;
import com.szip.jswitch.DB.dbModel.SleepData;
import com.szip.jswitch.DB.dbModel.SleepData_Table;
import com.szip.jswitch.DB.dbModel.SportData;
import com.szip.jswitch.DB.dbModel.SportData_Table;
import com.szip.jswitch.DB.dbModel.StepData;
import com.szip.jswitch.DB.dbModel.StepData_Table;
import com.szip.jswitch.Model.UserInfo;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.Notification.AppList;
import com.szip.jswitch.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static android.text.TextUtils.isEmpty;
import static com.szip.jswitch.MyApplication.FILE;
import static com.szip.jswitch.MyApplication.getInstance;

/**
 * Created by Administrator on 2019/1/28.
 */

/**
 * 处理数据的工具类
 * */
public class MathUitl {

    public static boolean isBlank(Object object) {
        if (object == null) {
            return true;
        }
        int strLen;
        String str = object.toString();
        if ((strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getRealFilePath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }

            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }

            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }

        else {
            return uri.getPath();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static String getKeyFromValue(CharSequence charSequence) {
        Map<Object, Object> appList = AppList.getInstance().getAppList();
        for (Object ob:appList.keySet()){
            Log.d("key******","key = "+ob.toString()+" ;value = "+appList.get(ob).toString());
        }
        Set<?> set = appList.entrySet();
        Iterator<?> it = set.iterator();
        String key = "";
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue() != null && entry.getValue().equals(charSequence)) {
                key = entry.getKey().toString();
                break;
            }
        }
        return key;
    }

    /**
     * 格式化字符串显示样式
     * */
    public static Spannable initText(String text, int flag,String split,String split1){
        if (flag == 0){
            Spannable span = new SpannableString(text);
            if (split!=null){
                int i = text.indexOf(split);
                int m = text.indexOf(split1);
                span.setSpan(new RelativeSizeSpan(1.5f), i+2, m, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else {
                int i = text.indexOf("steps");
                span.setSpan(new RelativeSizeSpan(2f), 0, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return span;
        }else if (flag == 1){
            Spannable span = new SpannableString(text);
            if (split!=null){
                if (split1!=null){
                    int i = text.indexOf(split);
                    int m = text.indexOf(split1);
                    span.setSpan(new RelativeSizeSpan(1.5f), i+2, m, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else {
                    int m = text.indexOf(':');
                    int i = text.indexOf('H');
                    if (i>=0){
                        span.setSpan(new RelativeSizeSpan(2f), m, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    i = text.indexOf("Min");
                    if (i>=0){
                        span.setSpan(new RelativeSizeSpan(2f), i-2, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }else {
                int i = text.indexOf('H');
                if (i>=0){
                    span.setSpan(new RelativeSizeSpan(2f), 0, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                i = text.indexOf("Min");
                if (i>=0){
                    span.setSpan(new RelativeSizeSpan(2f), i-2, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return span;
        }else if (flag == 2){
            Spannable span = new SpannableString(text);
            if (split!=null){
                if (split1!=null){
                    int i = text.indexOf(split);
                    int m = text.indexOf(split1);
                    span.setSpan(new RelativeSizeSpan(1.5f), i+2, m, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else {
                    int i = text.indexOf(split);
                    span.setSpan(new RelativeSizeSpan(1.5f), i+2,span.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }else {
                int i = text.indexOf("bpm");
                span.setSpan(new RelativeSizeSpan(2f), 0, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return span;
        }else if (flag == 3){
            Spannable span = new SpannableString(text);
            if (split!=null){
                int i = text.indexOf(split);
                span.setSpan(new RelativeSizeSpan(1.5f), i+2,span.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else {
                int i = text.indexOf("mmHg");
                span.setSpan(new RelativeSizeSpan(2f), 0, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return span;
        }else{
            Spannable span = new SpannableString(text);
            if (split!=null){
                int i = text.indexOf(split);
                span.setSpan(new RelativeSizeSpan(1.5f), i+2,span.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else {
                span.setSpan(new RelativeSizeSpan(2f), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return span;
        }
    }

    /**
     * Returns whether the mobile phone screen is locked.
     *
     * @param context
     * @return Return true, if screen is locked, otherwise, return false.
     */
    public static boolean isScreenLocked(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        Boolean isScreenLocked = km.inKeyguardRestrictedInputMode();

        return isScreenLocked;
    }

    /**
     * Returns whether the mobile phone screen is currently on.
     *
     * @param context
     * @return Return true, if screen is on, otherwise, return false.
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        Boolean isScreenOn = pm.isScreenOn();
        return isScreenOn;
    }

    /**
     * Returns whether the application is system application.
     *
     * @param appInfo
     * @return Return true, if the application is system application, otherwise,
     *         return false.
     */
    public static boolean isSystemApp(ApplicationInfo appInfo) {
        boolean isSystemApp = false;
        if (((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                || ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)) {
            isSystemApp = true;
        }

        // Log.i(LOG_TAG, "isSystemApp(), packageInfo.packageName=" +
        // appInfo.packageName
        // + ", isSystemApp=" + isSystemApp);
        return isSystemApp;
    }



    /**
     * Array转换成Stirng
     * */
    static public String ArrayToString(ArrayList<String> repeatList){
        StringBuilder repeatString = new StringBuilder();
        if (repeatList.contains("1")){
            repeatString.append("1,");
        }
        if (repeatList.contains("2")){
            repeatString.append("2,");
        }
        if (repeatList.contains("3")){
            repeatString.append("3,");
        }
        if (repeatList.contains("4")){
            repeatString.append("4,");
        }
        if (repeatList.contains("5")){
            repeatString.append("5,");
        }
        if (repeatList.contains("6")){
            repeatString.append("6,");
        }
        if (repeatList.contains("7")){
            repeatString.append("7,");
        }
        if (repeatString.length()>0)
            return repeatString.substring(0,repeatString.length()-1);
        else
            return "";
    }

    private static ArrayList<Long> longs = new ArrayList<>();//异常数据所在的时间戳列表



    /**
     * 判断字符串是不是数字
     * */
    public static boolean isNumeric(String str){
        String strPattern = "[0-9]*";
        if (isEmpty(strPattern)) {
            return false;
        } else {
            return str.matches(strPattern);
        }
    }
    /**
     * 判断邮箱是否合法
     * */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        if (isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

    public static void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }


    public static int inch2Cm(int height){
        float data = (height / 0.3937008f);
        return (int)data+(data-(int)data>0.44444?1:0);
    }


    public static int cm2Inch(int height){
        int data;
        data = (int)(height * 0.3937008);
        return data;
    }


    public static int kg2Pound(int weight){
        float data;
        data = (int)(weight / 0.4536);
        return (int)data+(data-(int)data>0.44444?1:0);
    }

    public static float c2f(float temp){
        int data;
        data = (int)((temp * 1.8+32)*10);
        return data/10f;
    }

    public static int pound2Kg(int weight){
        int data;
        data = (int)(weight * 0.4536);
        return data;
    }


    /**
     * 英制转公制
     * */
    public static int british2MetricWeight(int weight){
        float data;
        data = (weight / 2.2046226f);
        return (int)data+(data-(int)data>0.44444?1:0);
    }

    /**
     * 公制转英制
     * */
    public static double km2Miles(int height){
        int mile = height*3281/5280;
        return ((mile+5)/10)/100f;
    }


    /**
     * 公制转英制
     * */
    public static int kmPerHour2MilesPerHour(int speed){
        int mile = (speed*3281+2640)/5280;
        return mile;
    }


    /**
     * 公制转英制
     * */
    public static String[] kmPerHour2MilesPerHour(String[] speed){
        StringBuffer inchSpeed = new StringBuffer();
        if (speed.length>0){
            for (int i = 0;i<speed.length;i++){
                if(speed[i].equals(""))
                    continue;
                inchSpeed.append(String.format(",%d",kmPerHour2MilesPerHour(Integer.valueOf(speed[i]))));
            }
            if (inchSpeed.length()>0)
                return inchSpeed.toString().substring(1).split(",");
        }

        return new String[0];
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */
    public static int dipToPx(float dip,Context context)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */
    public static float dip2Px(float dip,Context context)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return  dip * density + 0.5f * (dip >= 0 ? 1 : -1);
    }


    public static ArrayList<String> getStepPlanList(){
        ArrayList<String> list  = new ArrayList<>();
        for (int i = 8;i<=40;i++){
            list.add(String.format(Locale.ENGLISH,"%d",i*500));
        }
        return list;
    }

    public static ArrayList<String> getSleepPlanList(){
        ArrayList<String> list  = new ArrayList<>();
        for (int i = 300;i<=720;i+=30){
            list.add(String.format(Locale.ENGLISH,"%.1f",(float)i/60));
        }
        return list;
    }

    /**
     * 统计日计步数据,日详情计步格式
     * str = hour1:step,hour2:step,....
     * 一天为24小时，hour代表的是第几个小时，step代表该小时里生成的总步数
     * */
    public static StepData mathStepDataForDay(ArrayList<String> steps){
        int hour[] = new int[24];
        String data[] = new String[0];
        for (int i = 0;i<steps.size();i++){
            data = steps.get(i).split("\\|");
            Log.d("SZIP******","DATE = "+steps.get(i));
            hour[Integer.valueOf(data[1].substring(0,data[1].indexOf(':')))==24?23:
                    Integer.valueOf(data[1].substring(0,data[1].indexOf(':')))] += Integer.valueOf(data[3]);
        }
        StringBuffer stepString = new StringBuffer();
        for (int i = 0;i<hour.length;i++){
            if (hour[i]!=0){
                stepString.append(String.format(Locale.ENGLISH,",%02d:%d",i,hour[i]));
            }
        }
        String step = stepString.toString();
        Log.d("SZIP******","详情计步数据 = "+"time = "+DateUtil.getTimeScopeForDay(data[0],"yyyy-MM-dd")+"str = "+step.substring(1));
        return new StepData(DateUtil.getTimeScopeForDay(data[0],"yyyy-MM-dd"),0,0,
                0,step.equals("")?null:step.substring(1));
    }

    /**
     * 统计日睡眠数据,日详情睡眠格式
     * str = startTime,"time:model"...,sleepTime
     * startTime代表开始睡眠的时间，"time:model"代表状态组，time为该状态持续时间，model微睡眠状态，sleepTime为总睡眠时间
     * */
    public static SleepData mathSleepDataForDay(ArrayList<String> sleeps,String date){
        String data[];
        StringBuffer sleepString = new StringBuffer();
        for (int i = 0;i<sleeps.size()-1;i++){
            data = sleeps.get(i).split("\\|");
            if (i == 0){//第一条数据，代表睡眠起始时间
                sleepString.append(data[1]);//初始化startTime
            }
            int time = (DateUtil.getMinue(sleeps.get(i+1).split("\\|")[1])
                    -DateUtil.getMinue(data[1]));
            if (time<0)
                time+=1440;
            sleepString.append(String.format(Locale.ENGLISH,",%d:",time)+data[2]);

        }
        Log.d("SZIP******","详情睡眠数据 = "+"time = "+DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd")+"str = "+sleepString.toString());
        return new SleepData(DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd"),0,0,
                sleepString.toString().equals("")?null:sleepString.toString());
    }

    /**
     * 统计心率数据
     * */
    public static HeartData mathHeartDataForDay(ArrayList<String> hearts){
        int heart = 0;
        int sum = 0;
        StringBuffer heartStr = new StringBuffer();
        String data[];
        for (int i = 0;i<hearts.size();i++){
            data = hearts.get(i).split("\\|");
            if (!Integer.valueOf(data[1]).equals("0")){
                heart+=Integer.valueOf(data[1]);
                sum++;
                heartStr.append(","+data[1]);
            }
        }
        Log.d("SZIP******","心率数据 = "+"time = "+DateUtil.getTimeScopeForDay(hearts.get(0).split(" ")[0],"yyyy-MM-dd")
                +"heart = "+(sum==0?0:heart/sum)+" ;heartStr = "+heartStr.toString().substring(1));
        return new HeartData(DateUtil.getTimeScopeForDay(hearts.get(0).split(" ")[0],"yyyy-MM-dd"),sum==0?0:heart/sum,
                heartStr.toString().substring(1));
    }

    /**
     * 把手表的数据换成json格式字符串用于上传到服务器
     * */
    public static String getStringWithJson(SharedPreferences sharedPreferences){

        long lastTime = sharedPreferences.getLong("lastTime",0);
        long lastTimeBp = sharedPreferences.getLong("lastTimeBp",0);
        long lastTimeBo = sharedPreferences.getLong("lastTimeBo",0);
        long lastTimeEcg = sharedPreferences.getLong("lastTimeEcg",0);
        long lastTimeSport = sharedPreferences.getLong("lastTimeSport",0);
        long lastTimeAh = sharedPreferences.getLong("lastTimeAh",0);



        List<StepData> stepDataList = SQLite.select()
                .from(StepData.class)
                .where(StepData_Table.time.greaterThanOrEq(lastTime))
                .queryList();

        List<SleepData> sleepDataList = SQLite.select()
                .from(SleepData.class)
                .where(SleepData_Table.time.greaterThanOrEq(lastTime))
                .queryList();

        List<HeartData> heartDataList = SQLite.select()
                .from(HeartData.class)
                .where(HeartData_Table.time.greaterThanOrEq(lastTime))
                .queryList();

        List<BloodPressureData> bloodPressureDataList = SQLite.select()
                .from(BloodPressureData.class)
                .where(BloodPressureData_Table.time.greaterThan(lastTimeBp))
                .queryList();

        List<BloodOxygenData> bloodOxygenDataList = SQLite.select()
                .from(BloodOxygenData.class)
                .where(BloodOxygenData_Table.time.greaterThan(lastTimeBo))
                .queryList();

        List<EcgData> ecgDataList = SQLite.select()
                .from(EcgData.class)
                .where(EcgData_Table.time.greaterThan(lastTimeEcg))
                .queryList();

        List<SportData> sportDataList = SQLite.select()
                .from(SportData.class)
                .where(SportData_Table.time.greaterThan(lastTimeSport))
                .queryList();

        List<AnimalHeatData> animalHeatDataList = SQLite.select()
                .from(AnimalHeatData.class)
                .where(AnimalHeatData_Table.time.greaterThan(lastTimeAh))
                .queryList();
        JSONArray array = new JSONArray();
        JSONObject data = new JSONObject();

        /**
         * 遍历数据库里面的数据
         * */
        try {
            for (int i = 0;i<bloodOxygenDataList.size();i++){
                JSONObject object = new JSONObject();
                object.put("time",bloodOxygenDataList.get(i).time);
                object.put("bloodOxygenData",bloodOxygenDataList.get(i).bloodOxygenData);
                array.put(object);
            }
            if (bloodOxygenDataList.size()!=0)
                data.put("bloodOxygenDataList",array);

            array = new JSONArray();
            for (int i = 0;i<bloodPressureDataList.size();i++){
                JSONObject object = new JSONObject();
                object.put("time",bloodPressureDataList.get(i).time);
                object.put("sbpDate",bloodPressureDataList.get(i).sbpDate);
                object.put("dbpDate",bloodPressureDataList.get(i).dbpDate);
                array.put(object);
            }
            if (bloodPressureDataList.size()!=0)
                data.put("bloodPressureDataList",array);

            array = new JSONArray();
            for (int i = 0;i<ecgDataList.size();i++){
                JSONObject object = new JSONObject();
                object.put("time",ecgDataList.get(i).time);
                object.put("heart",ecgDataList.get(i).heart);
                array.put(object);
            }
            if (ecgDataList.size()!=0)
                data.put("ecgDataList",array);

            array = new JSONArray();
            for (int i = 0;i<heartDataList.size();i++){
                JSONObject object = new JSONObject();
                object.put("time",heartDataList.get(i).time);
                object.put("averageHeart",heartDataList.get(i).averageHeart);
                object.put("heartArray",heartDataList.get(i).getHeartArray());
                array.put(object);
            }
            if (heartDataList.size()!=0)
            data.put("heartDataList",array);

            array = new JSONArray();
            for (int i = 0;i<sleepDataList.size();i++){
                JSONObject object = new JSONObject();
                object.put("time",sleepDataList.get(i).time);
                object.put("deepTime",sleepDataList.get(i).deepTime);
                object.put("lightTime",sleepDataList.get(i).lightTime);
                object.put("dataForHour",sleepDataList.get(i).dataForHour);
                array.put(object);
            }
            if (sleepDataList.size()!=0)
            data.put("sleepDataList",array);

            array = new JSONArray();
            for (int i = 0;i<sportDataList.size();i++){
                JSONObject object = new JSONObject();
                object.put("type",sportDataList.get(i).type);
                object.put("time",sportDataList.get(i).time);
                object.put("sportTime",sportDataList.get(i).sportTime);
                object.put("distance",sportDataList.get(i).distance);
                object.put("calorie",sportDataList.get(i).calorie);
                object.put("speed",sportDataList.get(i).speed);
                object.put("speedArray",sportDataList.get(i).speedArray);
                object.put("heart",sportDataList.get(i).heart);
                object.put("heartArray",sportDataList.get(i).heartArray);
                object.put("stride",sportDataList.get(i).stride);
                object.put("strideArray",sportDataList.get(i).strideArray);
                object.put("step",sportDataList.get(i).step);
                object.put("altitude",sportDataList.get(i).altitude);
                object.put("altitudeArray",sportDataList.get(i).altitudeArray);
                object.put("temp",sportDataList.get(i).temp);
                object.put("tempArray",sportDataList.get(i).tempArray);
                object.put("height",sportDataList.get(i).height);
                object.put("speedPerHour",sportDataList.get(i).speedPerHour);
                object.put("speedPerHourArray",sportDataList.get(i).speedPerHourArray);
                object.put("lngArray",sportDataList.get(i).lngArray);
                object.put("latArray",sportDataList.get(i).latArray);
                array.put(object);
            }
            if (sportDataList.size()!=0)
            data.put("sportDataList",array);

            array = new JSONArray();
            for (int i = 0;i<stepDataList.size();i++){
                JSONObject object = new JSONObject();
                object.put("time",stepDataList.get(i).time);
                object.put("steps",stepDataList.get(i).steps);
                object.put("distance",stepDataList.get(i).distance);
                object.put("calorie",stepDataList.get(i).calorie);
                object.put("dataForHour",stepDataList.get(i).dataForHour);
                array.put(object);
            }

            if (stepDataList.size()!=0)
            data.put("stepDataList",array);

            array = new JSONArray();
            for (int i = 0;i<animalHeatDataList.size();i++){
                JSONObject object = new JSONObject();
                object.put("time",animalHeatDataList.get(i).time);
                object.put("tempData",animalHeatDataList.get(i).tempData);
                array.put(object);
            }
            if (animalHeatDataList.size()!=0)
            data.put("tempDataList",array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString().equals("{}")?null:data.toString();
    }


    /**
     * 获取手机唯一标识
     * */
    public static String getDeviceId(Context context) {
        //如果上面都没有， 则生成一个id：随机码
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        if(!isEmpty(ANDROID_ID)){
//            Log.d("SZIP******","uuid = "+ANDROID_ID);
            return ANDROID_ID;
        }
        return null;
    }
    /**
     * 得到全局唯一UUID
     */
    public static String getUUID(Context context){
        String uuid = "";
        SharedPreferences mShare = context.getSharedPreferences(FILE,MODE_PRIVATE);
        uuid = mShare.getString("uuid", null);
        if(uuid==null){
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid",uuid).commit();
        }
        return uuid;
    }

    public static void saveLastTime(SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StepData stepDataList = SQLite.select()
                .from(StepData.class)
                .orderBy(OrderBy.fromString(StepData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();
        if (stepDataList!=null)
            editor.putLong("lastTime",stepDataList.time);

        BloodPressureData bloodPressureDataList = SQLite.select()
                .from(BloodPressureData.class)
                .orderBy(OrderBy.fromString(BloodPressureData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();
        if (bloodPressureDataList!=null)
            editor.putLong("lastTimeBp",bloodPressureDataList.time);

        BloodOxygenData bloodOxygenDataList = SQLite.select()
                .from(BloodOxygenData.class)
                .orderBy(OrderBy.fromString(BloodOxygenData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();
        if (bloodOxygenDataList!=null)
            editor.putLong("lastTimeBo",bloodOxygenDataList.time);

        EcgData ecgDataList = SQLite.select()
                .from(EcgData.class)
                .orderBy(OrderBy.fromString(EcgData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();
        if (ecgDataList!=null)
            editor.putLong("lastTimeEcg",ecgDataList.time);

        SportData sportDataList = SQLite.select()
                .from(SportData.class)
                .orderBy(OrderBy.fromString(SportData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();
        if (sportDataList!=null)
            editor.putLong("lastTimeSport",sportDataList.time);

        AnimalHeatData animalHeatDataList = SQLite.select()
                .from(AnimalHeatData.class)
                .orderBy(OrderBy.fromString(AnimalHeatData_Table.time+OrderBy.DESCENDING))
                .limit(0)
                .querySingle();
        if (animalHeatDataList!=null)
            editor.putLong("lastTimeAh",animalHeatDataList.time);
        editor.commit();
    }

    public static SharedPreferences.Editor saveInfoData(Context context, UserInfo info){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("birthday",info.getBirthday());
        editor.putString("phoneNumber",info.getPhoneNumber());
        editor.putString("email",info.getEmail());
        editor.putString("userName",info.getUserName());
        editor.putInt("height1",info.getHeight());
        editor.putInt("weight1",info.getWeight());
        editor.putInt("heightBritish",info.getHeightBritish());
        editor.putInt("weightBritish",info.getWeightBritish());
        editor.putInt("unit1",info.getUnit());
        editor.putInt("temp",info.getTempUnit());
        editor.putInt("sex",info.getSex());
        editor.putInt("stepsPlan",info.getStepsPlan());
        editor.putInt("sleepPlan",info.getSleepPlan());
        editor.putInt("id",info.getId());
        editor.putString("deviceCode",info.getDeviceCode());
        editor.putString("avatar",info.getAvatar());
//        editor.putString("bindId",info.getBindId());
        return editor;
    }

    public static UserInfo loadInfoData(SharedPreferences sharedPreferences){
        UserInfo info = new UserInfo();
        info.setBirthday(sharedPreferences.getString("birthday",""));
        info.setUserName(sharedPreferences.getString("userName",""));
        info.setHeight(sharedPreferences.getInt("height1",170));
        info.setWeight(sharedPreferences.getInt("weight1",60));
        info.setHeightBritish(sharedPreferences.getInt("heightBritish",66));
        info.setWeightBritish(sharedPreferences.getInt("weightBritish",132));
        info.setUnit(sharedPreferences.getInt("unit1",0));
        info.setTempUnit(sharedPreferences.getInt("temp",0));
        info.setSex(sharedPreferences.getInt("sex",1));
        info.setStepsPlan(sharedPreferences.getInt("stepsPlan",6000));
        info.setSleepPlan(sharedPreferences.getInt("sleepPlan",480));
        info.setId(sharedPreferences.getInt("id",0));
        info.setDeviceCode(sharedPreferences.getString("deviceCode",null));
        info.setAvatar(sharedPreferences.getString("avatar",null));
        info.setPhoneNumber(sharedPreferences.getString("phoneNumber",null));
        info.setEmail(sharedPreferences.getString("email",null));
//        info.setBindId(sharedPreferences.getString("bindId",null));
        return info;
    }

    public static SharedPreferences.Editor saveStringData(Context context,String dataKey,String data){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(dataKey,data);
        return editor;
    }

    public static SharedPreferences.Editor saveIntData(Context context,String dataKey,int data){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(dataKey,data);
        return editor;
    }

    public static String FloatToInt(String floatData){
        int index = floatData.indexOf('.');
        if (index == -1)
            return floatData;
        return floatData.substring(0,index);
    }


    public static int getClockStyle(int num){
        switch (num){
            case 2:
            case 7:
            case 16:
            case 21:
            case 22:
            case 25:
            case 26:
            case 31:
            case 36:
            case 38:
            case 50:
            case 52:
            case 53:
            case 55:
            case 60:
            case 61:
            case 63:
                return 0;
            default:
                return 1;
        }
    }

    public static boolean isJpgFile(Cursor cursor){
        String res = null;

        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        Log.d("SZIP******","res = "+res);

        try {

            FileInputStream bin = new FileInputStream(new File(res));
            int b[] = new int[4];
            b[0] = bin.read();
            b[1] = bin.read();
            bin.skip(bin.available() - 2);
            b[2] = bin.read();
            b[3] = bin.read();
            bin.close();

            return b[0] == 255;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean toJpgFile(){
        String filePath = MyApplication.getInstance().getPrivatePath()+"crop";
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (!MyApplication.getInstance().checkFaceType(bitmap.getWidth(),bitmap.getHeight()))
            return false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            while (baos.toByteArray().length>40960) { // 循环判断如果压缩后图片是否大于40kb,大于继续压缩
                baos.reset(); // 重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 5;// 每次都减少5
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, options, bos)) {
                bos.flush();
            }
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bitmap.recycle();
        }
        return true;
    }


    public static int getApplicationCode(String pakeName){
        switch (pakeName){
            case "com.tencent.mm":
                return 1;
            case "com.tencent.mobileqq":
                return 2;
            case "com.whatsapp":
                return 5;
            case "com.twitter.android":
                return 6;
            case "com.instagram.android":
                return 7;
            case "com.facebook.katana":
            case "com.facebook.orca":
                return 8;
            case "com.skype.rover":
                return 9;
            case "com.linkedin.android":
                return 10;
            case "jp.naver.line.android":
                return 11;
            case "com.snapchat.android":
                return 12;
            case "com.pinterest":
                return 13;
            case "com.google.android.apps.plus":
                return 14;
            case "com.tumblr":
                return 15;
            case "com.viber.voip":
                return 16;
            case "com.vkontakte.android":
                return 17;
            case "org.telegram.messenger":
                return 18;
            case "com.zhiliaoapp.musically":
                return 20;
            default:
                return -1;
        }
    }

    public static String getApplicationName(String pakeName){
        switch (pakeName){
            case "com.tencent.mm":
                return getInstance().getString(R.string.wechat);
            case "com.tencent.mobileqq":
                return getInstance().getString(R.string.qq);
            case "com.twitter.android":
                return getInstance().getString(R.string.twitter);
            case "com.instagram.android":
                return getInstance().getString(R.string.instagram);
            case "com.whatsapp":
                return getInstance().getString(R.string.whatsApp);
            case "com.facebook.katana":
            case "com.facebook.orca":
                return getInstance().getString(R.string.facebook);
            default:
                return getInstance().getString(R.string.other);
        }
    }

    public static void speaker(AudioManager mAudioManager) {
        //关闭Sco
        if (isBluetoothHeadsetConnected()) {
            mAudioManager.setBluetoothA2dpOn(false);
        }
        //打开扬声器
        mAudioManager.setSpeakerphoneOn(true);
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    public static void offSpeaker(AudioManager mAudioManager) {
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        if (isBluetoothHeadsetConnected()) {
            mAudioManager.setBluetoothA2dpOn(true);
        }
        mAudioManager.setSpeakerphoneOn(false);
    }

    private static boolean isBluetoothHeadsetConnected() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (BluetoothProfile.STATE_CONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
            return true;
        }
        return false;
    }

    public static int getBodyFatStateIndex(String arrays,float data){
        if (arrays == null)
            return 0;
        String[] range = arrays.split(",");
        int index = 0;
        for (int i = 1;i<range.length-1;i++){
            if (data>Float.valueOf(range[i]))
                index = i;
            else
                break;
        }
        return index;
    }

    public static double[]getMapOption(String[] lats,String[] lngs){
        double[] option= new double[3];
        if (lats.length<2)
            return option;
        int minLat = Integer.valueOf(lats[1]),maxLat = Integer.valueOf(lats[1]),
                minLng=Integer.valueOf(lngs[1]),maxLng=Integer.valueOf(lngs[1]);
        int minLatIndex = 1,maxLatIndex = 1,minLngIndex = 1,maxLngIndex = 1;
        for (int i = 1;i<lats.length;i++){
            if (Integer.valueOf(lats[i]) > maxLat) {
                maxLat = Integer.valueOf(lats[i]);
                maxLatIndex = i;
            }

            //当前遍历的数如果比 min 小，就将该数赋值给 min
            if (Integer.valueOf(lats[i]) < minLat) {
                minLat = Integer.valueOf(lats[i]);
                minLatIndex = i;
            }

            if (Integer.valueOf(lngs[i]) > maxLng) {
                maxLng = Integer.valueOf(lngs[i]);
                maxLngIndex = i;
            }

            //当前遍历的数如果比 min 小，就将该数赋值给 min
            if (Integer.valueOf(lngs[i]) < minLng) {
                minLng = Integer.valueOf(lngs[i]);
                minLngIndex = i;
            }
        }

        float latDistance = AMapUtils.calculateLineDistance(new LatLng((minLat+Integer.valueOf(lats[0]))/1000000.0,0),
                new LatLng((maxLat+Integer.valueOf(lats[0]))/1000000.0,0));
        float lngDistance = AMapUtils.calculateLineDistance(new LatLng(0,(minLng+Integer.valueOf(lngs[0]))/1000000.0),
                new LatLng(0,(maxLng+Integer.valueOf(lngs[0]))/1000000.0));
        double distance;
        if (latDistance>lngDistance){
            if (latDistance<70)
                option[2] =18.5;
            else
                option[2] = 20-(int)(Math.log(latDistance/70)/Math.log(2))-1.5;
            distance = 70*Math.pow(2,19-option[2])/100000.0/2;
            option[0] = (Integer.valueOf(lats[0])+((Integer.valueOf(lats[minLatIndex])+
                    Integer.valueOf(lats[maxLatIndex]))/2))/1000000.0-distance;
            option[1] = (Integer.valueOf(lngs[0])+((Integer.valueOf(lngs[minLatIndex])+
                    Integer.valueOf(lngs[maxLatIndex]))/2))/1000000.0;
        }else {
            if (lngDistance<70)
                option[2] = 18.5;
            else
                option[2] = 20-(int)(Math.log(lngDistance/70)/Math.log(2))-1.5;
            distance = 70*Math.pow(2,19-option[2])/100000.0/2;
            option[0] = (Integer.valueOf(lats[0])+((Integer.valueOf(lats[minLngIndex])+
                    Integer.valueOf(lats[maxLngIndex]))/2))/1000000.0-distance;
            option[1] = (Integer.valueOf(lngs[0])+((Integer.valueOf(lngs[minLngIndex])+
                    Integer.valueOf(lngs[maxLngIndex]))/2))/1000000.0;

        }
        return option;
    }
}
