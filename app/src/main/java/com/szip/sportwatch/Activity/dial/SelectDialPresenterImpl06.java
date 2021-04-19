package com.szip.sportwatch.Activity.dial;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.szip.sportwatch.Adapter.DialAdapter;
import com.szip.sportwatch.BLE.BleClient;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.ScreenCapture;

import java.util.Timer;
import java.util.TimerTask;

public class SelectDialPresenterImpl06 implements ISelectDialPresenter{

    private Handler handler;
    private Context context;
    private ISelectDialView iSelectDialView;

    public SelectDialPresenterImpl06(Context context, ISelectDialView iSelectDialView) {
        this.context = context;
        this.iSelectDialView = iSelectDialView;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void getViewConfig(RecyclerView dialRv) {
        dialRv.setLayoutManager(new GridLayoutManager(context, 3));
        final int [] dials;
        final int [] picture;
        final int [] clock;
        final boolean isCircle = MyApplication.getInstance().isCirlce();
        if (isCircle){
            dials = new int[]{R.mipmap.nowwatch_c_1};
            picture = new int[]{R.raw.bg5};
            clock = new int[]{15};
        }else {
            dials = new int[]{R.mipmap.watch_2523_1,R.mipmap.watch_2523_2,R.mipmap.watch_2523_3,R.mipmap.watch_2523_4,
                    R.mipmap.watch_2523_5,R.mipmap.watch_2523_6,R.mipmap.watch_2523_7,R.mipmap.watch_2523_8,R.mipmap.watch_2523_9};
            picture =  new int[]{R.raw.bg_2523_1,R.raw.bg_2523_2,R.raw.bg_2523_3,R.raw.bg_2523_4,R.raw.bg_2523_5,R.raw.bg_2523_6,
                    R.raw.bg_2523_7,R.raw.bg_2523_8,R.raw.bg_2523_9};
            clock = new int[]{1,2,3,4,5,6,7,8,9};

        }
        DialAdapter dialAdapter = new DialAdapter(dials);
        dialRv.setAdapter(dialAdapter);
        dialRv.setHasFixedSize(true);
        dialRv.setNestedScrollingEnabled(false);

        if (iSelectDialView!=null)
            iSelectDialView.setView(isCircle,dials[0],picture[0],clock[0]);

        dialAdapter.setOnItemClickListener(new DialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position==-1){
                    if (iSelectDialView!=null)
                        iSelectDialView.setDialView(-1,-1,-1);
                } else{
                    if (iSelectDialView!=null){
                        iSelectDialView.setDialView(dials[position],picture[position],clock[position]);
                    }
                }
            }
        });
    }

    private int i = 0;
    private byte datas[];

    @Override
    public void sendDial(int pictureId, int clock) {
        if (pictureId != -1) {
            final int PAGENUM = 128;//分包长度
            final byte[] datas = ScreenCapture.imageToByte(context, pictureId);
            int num = datas.length / PAGENUM;
            num = datas.length % PAGENUM == 0 ? num : num + 1;
            if (iSelectDialView != null)
                iSelectDialView.setDialProgress(num);
            this.datas = datas;
            this.i = 0;
        }
        sendByte();

    }

    private void sendByte(){
        byte[] newDatas;
        int len = (datas.length- i >128)?128:(datas.length- i);
        newDatas = new byte[len];
        System.arraycopy(datas, i,newDatas,0,len);
        BleClient.getInstance().writeForSendPicture(1,0,0, i/128,newDatas);
        i+=128;
        if (i>=datas.length){
            BleClient.getInstance().writeForSendPicture(2,0,0,0,new byte[0]);
        }
    }
}
