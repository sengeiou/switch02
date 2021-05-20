package com.szip.sportwatch.Activity.dial;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.szip.sportwatch.Adapter.DialAdapter;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.Util.ScreenCapture;

public class SelectDialPresenterImpl implements ISelectDialPresenter{

    private Handler handler;
    private Context context;
    private ISelectDialView iSelectDialView;

    public SelectDialPresenterImpl(Context context, ISelectDialView iSelectDialView) {
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
            dials = new int[]{R.mipmap.nowwatch_c_1,R.mipmap.nowwatch_c_2,R.mipmap.nowwatch_c_3,R.mipmap.nowwatch_c_4,
                    R.mipmap.nowwatch_c_5,R.mipmap.nowwatch_c_6,R.mipmap.nowwatch_c_7,R.mipmap.nowwatch_c_8,R.mipmap.nowwatch_c_9,
                    R.mipmap.nowwatch_c_10,R.mipmap.nowwatch_c_11,R.mipmap.nowwatch_c_12,R.mipmap.nowwatch_c_13,R.mipmap.nowwatch_c_14,
                    R.mipmap.nowwatch_c_15,R.mipmap.nowwatch_c_16,R.mipmap.nowwatch_c_17,R.mipmap.nowwatch_c_18,R.mipmap.nowwatch_32,
                    R.mipmap.nowwatch_30,R.mipmap.nowwatch_34};

            picture = new int[]{R.raw.bg5,R.raw.bg7,R.raw.bg22,R.raw.bg15,R.raw.bg23,R.raw.bg20,R.raw.bg21,R.raw.bg19,
                    R.raw.bg24,R.raw.bg25,R.raw.bg32,R.raw.bg26,R.raw.bg27,R.raw.bg28,R.raw.bg29,R.raw.bg30,R.raw.bg31,R.raw.bg10,
                    R.raw.bg35,R.raw.bg37,R.raw.bg39};

            clock = new int[]{15,7,16,11,17,11,11,11,18,19,20,21,22,23,24,25,26,27,31,30,34};

        }else {
            dials = new int[]{R.mipmap.nowwatch_1,R.mipmap.nowwatch_2,R.mipmap.nowwatch_3,R.mipmap.nowwatch_4,
                    R.mipmap.nowwatch_5,R.mipmap.nowwatch_6,R.mipmap.nowwatch_7,R.mipmap.nowwatch_8,R.mipmap.nowwatch_9,
                    R.mipmap.nowwatch_10,R.mipmap.nowwatch_11,R.mipmap.nowwatch_12,R.mipmap.nowwatch_13,
                    R.mipmap.nowwatch_15,R.mipmap.nowwatch_16,R.mipmap.nowwatch_17,R.mipmap.nowwatch_18,R.mipmap.nowwatch_28};

            picture = new int[]{R.raw.bg1,R.raw.bg2,R.raw.bg3,R.raw.bg4,R.raw.bg5,R.raw.bg6,R.raw.bg7,R.raw.bg8,
                    R.raw.bg9,R.raw.bg10,R.raw.bg11,R.raw.bg12,R.raw.bg13,R.raw.bg14,R.raw.bg15,R.raw.bg16,R.raw.bg17,R.raw.bg33};
            clock = new int[]{1,2,3,4,5,6,7,8,9,8,10,11,11,12,11,13,14,28};

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

    @Override
    public void sendDial(int pictureId, int clock) {
        if (pictureId!=-1){
            int PAGENUM = 8000;//分包长度
            byte[] datas = ScreenCapture.imageToByte(context, pictureId);
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
        }
    }
}
