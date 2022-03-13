package com.szip.jswitch.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.szip.jswitch.DB.dbModel.BodyFatData;
import com.szip.jswitch.DB.dbModel.BodyFatData_Table;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.Util.DrawHelper;
import com.szip.jswitch.Util.MathUitl;
import com.vtrump.vtble.VTComUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BodyFatTable extends View {

    private int mWidth,mHeight;//本页面宽，高

    private Paint paint = new Paint();//表格画笔
    private Paint textYPaint = new Paint();//Y坐标画笔
    private Paint textXPaint = new Paint();//X坐标画笔
    private Paint linePaint = new Paint();//折线图画笔
    private Paint mPaintShader = new Paint();//阴影画笔
    private ArrayList<BodyFatData> bodyFatDataList;

    private float dpValue;
    private float xValueWidth;
    private float maxValue = 60,minValue = 40;

    private boolean isTouchAble = false;
    private int index;
    private Paint pointPaint,squareBackPaint,weightTextPaint,unitTextPaint,timeTextPaint;

    private OnTouchListener onTouchListener;

    public BodyFatTable(Context context) {
        super(context);
        initView();
    }

    public BodyFatTable(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BodyFatTable(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    private void initView(){

        dpValue = MathUitl.dip2Px(1,getContext());

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getContext().getResources().getColor(R.color.bodyGreen));
        paint.setStrokeWidth(1.0f);

        textXPaint.setColor(getContext().getResources().getColor(R.color.rayblue1));
        textXPaint.setTextSize(dpValue*10);

        textYPaint.setColor(getContext().getResources().getColor(R.color.rayblue1));
        textYPaint.setTextSize(dpValue*10);


        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeWidth(dpValue);
        linePaint.setColor(getContext().getResources().getColor(R.color.bodyGreen));

        squareBackPaint = new Paint();
        squareBackPaint.setColor(getContext().getResources().getColor(R.color.bodyGreen));
        weightTextPaint = new Paint();
        weightTextPaint.setColor(getContext().getResources().getColor(R.color.white));
        weightTextPaint.setTextSize(dpValue*16);
        unitTextPaint = new Paint();
        unitTextPaint.setTextSize(dpValue*10);
        unitTextPaint.setColor(getContext().getResources().getColor(R.color.white));
        timeTextPaint = new Paint();
        timeTextPaint.setTextSize(dpValue*10);
        timeTextPaint.setColor(getContext().getResources().getColor(R.color.white));
        pointPaint = new Paint();
        pointPaint.setColor(getContext().getResources().getColor(R.color.bodyGreen));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(bodyFatDataList!=null){
            DrawYView(canvas);
            DrawXView(canvas);
            DrawStepTable(canvas);
        }
    }

    /**
     * 画纵坐标
     * */
    private void DrawYView(Canvas canvas){
            textYPaint.setTextAlign(Paint.Align.LEFT);
            paint.setStrokeWidth(1f);
            DashPathEffect dashPathEffect = new DashPathEffect(new float[]{8,8},0);
            paint.setPathEffect(dashPathEffect);
            float diffCoordinate = (mHeight-dpValue*15)*2f/3f/2f;
            String[] yMsg = getYMsg();
            for(int i = 0; i<2; i++) {
                float levelCoordinate = mHeight-dpValue*15-diffCoordinate*(i+1);
                Path dashPath = new Path();
                dashPath.moveTo(dpValue*19, levelCoordinate);
                dashPath.lineTo(mWidth-dpValue*19, levelCoordinate);
                canvas.drawText(yMsg[i], dpValue*3,
                        levelCoordinate+dpValue*4, textYPaint);
                canvas.drawPath(dashPath, paint);
            }

        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setColor(getContext().getResources().getColor(R.color.bodyGreen));
        paint1.setStrokeWidth(1.5f);
        Path dashPath = new Path();
        dashPath.moveTo(dpValue*15, mHeight-dpValue*15);
        dashPath.lineTo(mWidth-dpValue*15, mHeight-dpValue*15);
        canvas.drawPath(dashPath, paint1);

    }

    /**
     * 画横坐标
     * */
    private void DrawXView(Canvas canvas){

        String[] xMsg = getXMsg();
        float w;

        canvas.drawText(xMsg[0], dpValue*15, mHeight-dpValue*3, textXPaint);
        w = textXPaint.measureText(xMsg[2]);
        canvas.drawText(xMsg[1], (mWidth-30*dpValue-w)/2, mHeight-dpValue*3, textXPaint);
        w = textXPaint.measureText(xMsg[2]);
        canvas.drawText(xMsg[2], mWidth-15*dpValue-w, mHeight-dpValue*3, textXPaint);
    }

    /**
     * 画报告
     */
    private void DrawStepTable(Canvas canvas){

        if (bodyFatDataList!=null){
            Path path = new Path();
            Path mPathShader = new Path();
            for (int i = 0; i< bodyFatDataList.size(); i++){
                float top;
                if (bodyFatDataList.get(i).weight<minValue)
                    top = mHeight-dpValue*20;
                else
                    top = mHeight-dpValue*15-(mHeight-dpValue*15)*2f/3f*(bodyFatDataList.get(i).weight-minValue)/20f;
                if (i==0){
                    path.moveTo(dpValue*19, top);
                    mPathShader.moveTo(dpValue*19, top);
                } else{
                    path.lineTo(dpValue*19+(mWidth-19*dpValue*2)/(bodyFatDataList.size()-1)*i,top);
                    mPathShader.lineTo(dpValue*19+(mWidth-19*dpValue*2)/(bodyFatDataList.size()-1)*i,top);
                    if (i == bodyFatDataList.size() - 1) {
                        mPathShader.lineTo(mWidth-dpValue*19, mHeight-dpValue*15);
                        mPathShader.lineTo(dpValue*19, mHeight-dpValue*15);
                        mPathShader.close();
                    }

                }
                Shader mShader = new LinearGradient(0, 0, 0, getHeight(), getContext().getResources().getColor(R.color.bodyGreen),
                        Color.TRANSPARENT, Shader.TileMode.MIRROR);
                mPaintShader.setShader(mShader);
            }

            canvas.drawPath(path, linePaint);
            canvas.drawPath(mPathShader, mPaintShader);

            if (isTouchAble){
                float x = dpValue*19+(mWidth-19*dpValue*2)/(bodyFatDataList.size()-1)*index;
                float y = mHeight-dpValue*15-(mHeight-dpValue*15)*2f/3f*(bodyFatDataList.get(index).weight-minValue)/20f;
                canvas.drawCircle(x,y,dpValue*2,pointPaint);
                Path line = new Path();
                line.moveTo(x,mHeight/3-15*dpValue);
                line.lineTo(x,mHeight-15*dpValue);
                canvas.drawPath(line,paint);

                if (x<40*dpValue)
                    x = 40*dpValue;
                if (x>mWidth-40*dpValue)
                    x = mWidth-40*dpValue;

                canvas.drawRoundRect(x-40*dpValue,10*dpValue,x+40*dpValue,mHeight/3-15*dpValue,10*dpValue,
                        10*dpValue,squareBackPaint);
                String weightStr = "",unitStr = "",timeStr = "";

                if (MyApplication.getInstance().getUserInfo().getUnit()==0){
                    weightStr = String.format("%.1f",bodyFatDataList.get(index).weight);
                    unitStr = "kg";
                }else {
                    weightStr = String.format("%.1f", VTComUtils.kg2Lb(bodyFatDataList.get(index).weight));
                    unitStr = "lb";
                }
                timeStr = DateUtil.getStringDateFromSecond(bodyFatDataList.get(index).time,"MM-dd HH:mm");
                float weightTextWidth = weightTextPaint.measureText(weightStr);
                float unitTextWidth =unitTextPaint.measureText(unitStr);
                float timeTextWidth = timeTextPaint.measureText(timeStr);
                canvas.drawText(weightStr, (x-(weightTextWidth+unitTextWidth)/2),
                        mHeight/3-23*dpValue,weightTextPaint);
                canvas.drawText(unitStr,(x-unitTextWidth/2+weightTextWidth/2),
                        mHeight/3-23*dpValue,unitTextPaint);
                canvas.drawText(timeStr,(x-timeTextWidth/2),
                        10*dpValue+18*dpValue,timeTextPaint);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                isTouchAble = true;
                if (x<dpValue*19){
                    index = 0;
                }else if (x>mWidth-dpValue*19){
                    index = bodyFatDataList.size()-1;
                }else {
                    for (int i=0,j = i+1;i<bodyFatDataList.size()-1;i++,j++){
                        if ((x-(dpValue*19+(mWidth-19*dpValue*2)/(bodyFatDataList.size()-1)*i))*
                                (x-(dpValue*19+(mWidth-19*dpValue*2)/(bodyFatDataList.size()-1)*j))<0){
                            index = Math.abs((x-(dpValue*19+(mWidth-19*dpValue*2)/(bodyFatDataList.size()-1)*i)))>
                                    Math.abs((x-(dpValue*19+(mWidth-19*dpValue*2)/(bodyFatDataList.size()-1)*j)))?j:i;
                            break;
                        }
                    }
                }
                break;
        }
        if (onTouchListener!=null)
            onTouchListener.onPosition(index);
        postInvalidate();
        return super.onTouchEvent(event);
    }


    public void setBodyFatDataList(ArrayList<BodyFatData> bodyFatDataList) {
        Log.i("DATA******","list = "+bodyFatDataList);
        if (this.bodyFatDataList!=null&&this.bodyFatDataList.size()!=bodyFatDataList.size()){
            index = 0;
            isTouchAble = false;
        }
        this.bodyFatDataList = bodyFatDataList;
        postInvalidate();
    }

    private String[] getYMsg(){
        String[] yMsg= new String[2];
        if (bodyFatDataList!=null){
            for (BodyFatData bodyFatData:bodyFatDataList){
                if (bodyFatData.weight!=0&&bodyFatData.weight>maxValue)
                    maxValue = bodyFatData.weight;
            }
        }
        int sub = ((int)maxValue)/10;
        minValue = (sub+1)*10-20;
        if (MyApplication.getInstance().getUserInfo().getUnit()==0){
            yMsg[0] = String.format("%d",sub*10);
            yMsg[1] = String.format("%d",(sub+1)*10);
        }else {
            yMsg[0] = String.format("%d",(int)VTComUtils.kg2Lb(sub*10));
            yMsg[1] = String.format("%d",(int)VTComUtils.kg2Lb((sub+1)*10));
        }
        return yMsg;
    }

    /**
     * 获取X轴坐标数据
     * */
    private String[] getXMsg(){
        String[] xMsg = new String[3];
        String str = "";
        int sub;
        if (bodyFatDataList!=null){
            if (bodyFatDataList.size()==7){
                sub = 3;
            }else {
                sub = 15;
            }
            long time = DateUtil.getTimeOfToday();
            for (int i = 0;i<2;i++){
                xMsg[i] = DateUtil.getStringDateFromSecond(time-24*60*60*(2-i)*sub,"MM-dd");
                str+=xMsg[i];
            }
            xMsg[2] = getContext().getResources().getString(R.string.today);
            str+=getContext().getResources().getString(R.string.today);
            xValueWidth = textXPaint.measureText(str);
        }
        return xMsg;
    }


    public interface OnTouchListener{
        void onPosition(int index);
    }

}
