package com.szip.jswitch.Activity.dial;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.szip.jswitch.Adapter.DialAdapter;
import com.szip.jswitch.BLE.BleClient;
import com.szip.jswitch.Model.HttpBean.DialBean;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.Util.FileUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SelectDialPresenterImpl06 implements ISelectDialPresenter{

    private Context context;
    private ISelectDialView iSelectDialView;
    private int clock;

    public SelectDialPresenterImpl06(Context context, ISelectDialView iSelectDialView) {
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
        BleClient.getInstance().writeForSendPicture(0,clock,0,0,new byte[0]);
    }

    private int i = 0;
    private byte datas[];

    @Override
    public void sendDial(String resultUri, int address) {
        if (resultUri != null) {
            resultUri = MyApplication.getInstance().getPrivatePath()+resultUri;
            final int PAGENUM = 200;//分包长度
            InputStream in = null;
            try {
                in = new FileInputStream(resultUri);
                byte[] datas =  FileUtil.getInstance().toByteArray(in);
                in.close();
                int num = datas.length / PAGENUM;
                num = datas.length % PAGENUM == 0 ? num : num + 1;
                if (iSelectDialView != null)
                    iSelectDialView.setDialProgress(num);
                this.datas = datas;
                this.i = 0;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        sendByte();
    }

    @Override
    public void resumeSendDial(int page) {

    }

    @Override
    public void setViewDeStory() {
        iSelectDialView = null;
    }

    private void sendByte(){
        byte[] newDatas;
        int len = (datas.length- i >200)?200:(datas.length- i);
        newDatas = new byte[len];
        System.arraycopy(datas, i,newDatas,0,len);
        BleClient.getInstance().writeForSendPicture(1,0,0, i/200,newDatas);
        i+=200;
        if (i>=datas.length){
            BleClient.getInstance().writeForSendPicture(2,0,0,0,new byte[0]);
        }
    }
}
