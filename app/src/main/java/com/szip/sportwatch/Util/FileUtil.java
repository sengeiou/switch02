package com.szip.sportwatch.Util;

import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStorageState;

public class FileUtil {
    private static FileUtil mInstance;

    private boolean isSdCard = true;

    private String path;

    private FileUtil(){

    }

    public static FileUtil getInstance()
    {
        if (mInstance == null)
        {
            synchronized (FileUtil.class)
            {
                if (mInstance == null)
                {
                    mInstance = new FileUtil();
                }
            }
        }
        return mInstance;
    }

    public void initFile(){
        if (getExternalStorageState().equals(MEDIA_MOUNTED)){
            this.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+
                    "/Camera";
            Log.d("SZIP******","path = "+path);
            isSdCard = true;
        } else{
            isSdCard = false;
        }



    }

    public String writeFileSdcardFile(String fileName, byte[] writeStr) throws IOException {
        Log.d("SZIP******","write file path= "+path+"/"+fileName+" ;file size = "+writeStr.length);
        try {
            FileOutputStream fout = new FileOutputStream(path+"/"+fileName,true);
            fout.write(writeStr);
            fout.close();
        } catch (Exception e) {
            Log.d("SZIP******","保存失败 = "+e.getMessage());
            e.printStackTrace();
        }
        return path+"/"+fileName;
    }

    public void writeLog(String logPath,byte[] datas){
        File file;
        FileOutputStream fos = null;

        try {
            file = new File(logPath);
            fos = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fos==null){
            return;
        }
        try {
            fos.write(datas);
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File renameFile(String name,String oldName,boolean isNull){
        if (isNull)
            new File(oldName).delete();
        else {
            if (new File(name).length()!=0)
                new File(name).delete();
            File file = new File(oldName);
            if (file.length()!=0)
                file.renameTo(new File(name));
            return new File(name);
        }
        return null;
    }

    public void deleteFile(String fileName){
        File file = new File(fileName);
        file.delete();
    }

    public String getPath() {
        return path;
    }
}
