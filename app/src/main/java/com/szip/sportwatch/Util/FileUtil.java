package com.szip.sportwatch.Util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public void initFile(String pathStr){
        this.path = pathStr+"/shgame";
        if (getExternalStorageState().equals(MEDIA_MOUNTED))
            isSdCard = true;
        else
            isSdCard = false;


        if (isSdCard){
            File file = new File(path);
            if (!file.exists()){
                file.mkdir();
                Log.d("SZIP******","创建文件夹成功"+path);
            }else {
                Log.d("SZIP******","文件夹已经存在"+path);
            }
        }
    }

    public String writeFileSdcardFile(String fileName, byte[] writeStr) throws IOException {
        try {
            FileOutputStream fout = new FileOutputStream(path+"/"+fileName,true);
            fout.write(writeStr);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path+"/"+fileName;
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

}
