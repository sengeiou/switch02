package com.szip.jswitch.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.CommandUtil;
import com.szip.jswitch.Util.MathUitl;
import com.vtrump.vtble.VTComUtils;

public class BodyFatLevelProgress extends View {


    private int mWidth;
    private int mHeight;
    private int []color;
    private Paint progressPaint,squarePaint; //画当前进度的画笔
    private Paint radioPaint,radioPaint1; //画游标的画笔
    private Paint trianglePaint,squareBackPaint,weightTextPaint,unitTextPaint;
    private float squareWidth = MathUitl.dipToPx(80,getContext());
    private float squareHeight = MathUitl.dipToPx(40,getContext());
    private float squareCorners = MathUitl.dipToPx(10,getContext());

    private float bgArcWidth = MathUitl.dipToPx(10,getContext());
    private float[] ranges={0,10,20,30,40,50};
    private int index;
    private float radio;


    public BodyFatLevelProgress(Context context) {
        super(context);
        initView(context);
    }

    public BodyFatLevelProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BodyFatLevelProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        color = new int[]{context.getResources().getColor(R.color.bodyOrange),context.getResources().getColor(R.color.bodyYellow),
                context.getResources().getColor(R.color.bodyGrass),context.getResources().getColor(R.color.bodyGreen),
                        context.getResources().getColor(R.color.bodyBlue)};
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(bgArcWidth);
        progressPaint.setColor(Color.GREEN);

        squarePaint = new Paint();
        squarePaint.setAntiAlias(true);
        squarePaint.setStyle(Paint.Style.STROKE);
        squarePaint.setStrokeCap(Paint.Cap.SQUARE);
        squarePaint.setStrokeWidth(bgArcWidth);


        radioPaint = new Paint();
        radioPaint1 = new Paint();
        radioPaint1.setColor(context.getResources().getColor(R.color.white));

        trianglePaint = new Paint();
        trianglePaint.setColor(context.getResources().getColor(R.color.bodyGreen));
        squareBackPaint = new Paint();
        squareBackPaint.setColor(context.getResources().getColor(R.color.bodyGreen));
        weightTextPaint = new Paint();
        weightTextPaint.setColor(context.getResources().getColor(R.color.white));
        weightTextPaint.setTextSize(MathUitl.dipToPx(20,context));
        unitTextPaint = new Paint();
        unitTextPaint.setTextSize(MathUitl.dipToPx(14,context));
        unitTextPaint.setColor(context.getResources().getColor(R.color.white));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float current = mWidth-bgArcWidth;
        //画进度条
        for (int i  = 0;i<color.length;i++){
            progressPaint.setColor(color[i]);
            canvas.drawLine(bgArcWidth/2,mHeight-bgArcWidth,
                    current*(ranges[5-i]/50),mHeight-bgArcWidth, progressPaint);
            if (i!=0){
                squarePaint.setColor(color[i]);
                canvas.drawLine(current*(ranges[5-i]/50)-bgArcWidth,mHeight-bgArcWidth,
                        current*(ranges[5-i]/50),mHeight-bgArcWidth, squarePaint);
            }
        }


        if (radio>0){
            //画游标
            int color = R.color.bodyBlue;;
            switch (index){
                case 0:
                    color = R.color.bodyBlue;
                    break;
                case 1:
                    color = R.color.bodyGreen;
                    break;
                case 2:
                    color = R.color.bodyGrass;
                    break;
                case 3:
                    color = R.color.bodyYellow;
                    break;
                case 4:
                    color = R.color.bodyOrange;
                    break;
            }
            radioPaint.setColor(getContext().getResources().getColor(color));
            canvas.drawCircle(current*((index*10+5)/50f),mHeight-bgArcWidth,
                    bgArcWidth/2+4, radioPaint);
            canvas.drawCircle(current*((index*10+5)/50f),mHeight-bgArcWidth,
                    bgArcWidth/2, radioPaint1);

            //画圆角矩形背景
            canvas.drawRoundRect((current*((index*10+5)/50f)-squareWidth/2),mHeight-squareHeight-bgArcWidth*3,
                    (current*((index*10+5)/50f)+squareWidth/2),mHeight-bgArcWidth*3,
                    squareCorners,squareCorners,squareBackPaint);

            //画倒三角形
            Path path1 = new Path();
            path1.moveTo((current*((index*10+5)/50f)-bgArcWidth/2), mHeight-bgArcWidth*3);
            path1.lineTo((current*((index*10+5)/50f)+bgArcWidth/2), mHeight-bgArcWidth*3);
            path1.lineTo((current*((index*10+5)/50f)), mHeight-bgArcWidth*2);
            path1.close();
            canvas.drawPath(path1, trianglePaint);

            String weightStr = "",unitStr = "";

            if (MyApplication.getInstance().getUserInfo().getUnit()==0){
                weightStr = String.format("%.1f",radio);
                unitStr = "kg";
            }else {
                weightStr = String.format("%.1f", VTComUtils.kg2Lb(radio));
                unitStr = "lb";
            }
            float weightTextWidth = weightTextPaint.measureText(weightStr);
            float unitTextWidth =unitTextPaint.measureText(unitStr);
            float textHeight = weightTextPaint.measureText(weightStr,0,1);
            canvas.drawText(weightStr, (current*((index*10+5)/50f)-(weightTextWidth+unitTextWidth)/2),
                    mHeight-squareHeight/2-bgArcWidth*3+textHeight/2,weightTextPaint);
            canvas.drawText(unitStr,(current*((index*10+5)/50f)-unitTextWidth/2+weightTextWidth/2),
                    mHeight-squareHeight/2-bgArcWidth*3+textHeight/2,unitTextPaint);
        }



        invalidate();
    }

    /**
     * 设置游标位置
     * */
    public void setRadio(String range,float radio) {
        this.index = MathUitl.getBodyFatStateIndex(range,radio);
        this.radio = radio;
        postInvalidate();
    }

}
