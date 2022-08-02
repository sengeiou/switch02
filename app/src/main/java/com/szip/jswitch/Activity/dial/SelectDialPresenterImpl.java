package com.szip.jswitch.Activity.dial;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.szip.jswitch.Adapter.DialAdapter;
import com.szip.jswitch.BLE.EXCDController;
import com.szip.jswitch.Model.HttpBean.DialBean;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.Util.FileUtil;
import com.szip.jswitch.Util.MathUitl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SelectDialPresenterImpl implements ISelectDialPresenter{

    private Context context;
    private ISelectDialView iSelectDialView;
    private int clock;

    public SelectDialPresenterImpl(Context context, ISelectDialView iSelectDialView) {
        this.context = context;
        this.iSelectDialView = iSelectDialView;
    }

    @Override
    public void getViewConfig(RecyclerView dialRv, final ArrayList<DialBean.Dial> dialArrayList) {
        dialRv.setLayoutManager(new GridLayoutManager(context, 3));
        DialAdapter dialAdapter = new DialAdapter(dialArrayList,context);
        dialRv.setAdapter(dialAdapter);
        dialRv.setHasFixedSize(true);
        dialRv.setNestedScrollingEnabled(false);

        if (iSelectDialView!=null&&dialArrayList.size()!=0){
            iSelectDialView.setView(dialArrayList.get(0).getPreviewUrl(),
                    dialArrayList.get(0).getPlateBgUrl());
            clock = dialArrayList.get(0).getPointerNumber();
        }


        dialAdapter.setOnItemClickListener(new DialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position==-1){
                    if (iSelectDialView!=null)
                        iSelectDialView.setDialView(null,null);
                } else{
                    if (iSelectDialView!=null){
                        iSelectDialView.setDialView(dialArrayList.get(position).getPreviewUrl(),
                                dialArrayList.get(position).getPlateBgUrl());
                        clock = dialArrayList.get(position).getPointerNumber();
                    }
                }
            }
        });
    }

    @Override
    public void startToSendDial() {
        EXCDController.getInstance().initDialInfo();
    }

    @Override
    public void sendDial(String resultUri, int address) {
        if (resultUri!=null){
            resultUri = MyApplication.getInstance().getPrivatePath()+resultUri;
            int PAGENUM = 8000;//分包长度
            InputStream in = null;
            try {
                in = new FileInputStream(resultUri);
                byte[] datas = FileUtil.getInstance().toByteArray(in);
                in.close();
                int num = datas.length/PAGENUM;
                num = datas.length%PAGENUM==0?num:num+1;

                if (iSelectDialView!=null)
                    iSelectDialView.setDialProgress(num);

                if (datas.length<=PAGENUM){
                    EXCDController.getInstance().writeForSendImage(datas,1,num,clock, MathUitl.getClockStyle(clock));
                }else {
                    byte[] newDatas;
                    for (int i=0;i<datas.length;i+=PAGENUM){
                        int len = (datas.length-i>PAGENUM)?PAGENUM:(datas.length-i);
                        newDatas = new byte[len];
                        System.arraycopy(datas,i,newDatas,0,len);
                        EXCDController.getInstance().writeForSendImage(newDatas,i/PAGENUM+1,num,clock,MathUitl.getClockStyle(clock));
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void resumeSendDial(int page) {

    }

    @Override
    public void setViewDeStory() {
        iSelectDialView = null;
    }


}
