package com.szip.sportwatch.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;

import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.MathUitl;

/**
 * Created by Administrator on 2019/12/12.
 */

public class HealthyProgressView extends View {

    private int mWidth;
    private int mHeight;

    private float maxValues     = 60;
    private float currentValues = 0;
    private int bgArcColor       = 0xff111111;
    private int []color;
    private float progressWidth = MathUitl.dipToPx(10,getContext());
    private Paint allArcPaint; //画底色的画笔
    private Paint progressPaint; //画当前进度的画笔
    private Paint squarePaint; //覆盖圆角用的画笔
    private Paint radioPaint; //画游标的画笔

    private RectF bgRect;
    private float bgArcWidth = MathUitl.dipToPx(10,getContext());

    private SweepGradient sweepGradient;//颜色渲染

    private boolean isAverage = false;

    private float radio;
    private int allTime;
    private int lightTime;

    public HealthyProgressView(Context context) {
        super(context);
        initView();
    }

    public HealthyProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
        initConfig(context, attrs);
        initView();
    }

    public HealthyProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig(context, attrs);
        initView();
    }

    private void initView() {

        allArcPaint = new Paint();
        allArcPaint.setAntiAlias(true);
        allArcPaint.setStyle(Paint.Style.STROKE);
        allArcPaint.setStrokeWidth(bgArcWidth);
        allArcPaint.setColor((bgArcColor));
        allArcPaint.setStrokeCap(Paint.Cap.ROUND);


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
        radioPaint.setStyle(Paint.Style.STROKE);
        radioPaint.setStrokeWidth(4);
        radioPaint.setColor(getContext().getResources().getColor(R.color.orange));

    }

    private void initConfig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HealthyProgressView);

        int color1 = a.getColor(R.styleable.HealthyProgressView_color1, Color.GREEN);
        int color2 = a.getColor(R.styleable.HealthyProgressView_color2, -1);
        int color3 = a.getColor(R.styleable.HealthyProgressView_color3, -1);
        if (color2!=-1)
            color = new int[]{color2,color1};
        if (color3!=-1)
            color = new int[]{color3,color2,color1};
        bgArcColor = a.getColor(R.styleable.HealthyProgressView_bg_color, 0xff111111);
        bgArcWidth = a.getDimension(R.styleable.HealthyProgressView_bg_width, MathUitl.dipToPx(10,getContext()));
        progressWidth = a.getDimension(R.styleable.HealthyProgressView_front_bar_width, MathUitl.dipToPx(10,getContext()));
        currentValues = a.getFloat(R.styleable.HealthyProgressView_current, 0);
        maxValues = a.getFloat(R.styleable.HealthyProgressView_max, 60);
        isAverage = a.getBoolean(R.styleable.HealthyProgressView_is_average,true);

        setCurrentValues(currentValues);
        setMaxValues(maxValues);

        a.recycle();
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
        if (isAverage){
            //当前进度
            float current = mWidth-bgArcWidth;
            for (int i  = 0;i<color.length;i++){
                progressPaint.setColor(color[i]);
                canvas.drawLine(bgArcWidth/2,mHeight/2,
                        current*(1-(float)i/color.length),mHeight/2, progressPaint);
            }
            for (int i = 1;i<color.length;i++){
                squarePaint.setColor(color[i]);
                canvas.drawLine(current*(1-(float)i/color.length)-bgArcWidth,mHeight/2,
                        current*(1-(float)i/color.length),mHeight/2, squarePaint);
            }
            if (radio>0){
                canvas.drawLine(mWidth*radio,0,mWidth*radio,mHeight, radioPaint);
            }

        }else {
            //画底色
            float current = mWidth-bgArcWidth;
            canvas.drawLine(bgArcWidth/2,mHeight/2,mWidth-bgArcWidth,
                    mHeight/2, allArcPaint);
            if (allTime!=0){
                progressPaint.setColor(color[0]);
                canvas.drawLine(bgArcWidth/2,mHeight/2,
                        current*radio,mHeight/2, progressPaint);
                if (lightTime!=0){
                    progressPaint.setColor(color[1]);
                    squarePaint.setColor(color[1]);
                    canvas.drawLine(bgArcWidth/2,mHeight/2,
                            current*radio*(lightTime/ (float)allTime),mHeight/2, progressPaint);
                    canvas.drawLine(current*radio*(lightTime/ (float)allTime)-bgArcWidth,mHeight/2,
                            current*radio*(lightTime/ (float)allTime),mHeight/2, squarePaint);
                }
            }
        }
        invalidate();
    }

    /**
     * 设置最大值
     *
     * @param maxValues
     */
    public void setMaxValues(float maxValues)
    {
        this.maxValues = maxValues;
    }

    /**
     * 设置当前值
     *
     * @param currentValues
     */
    public void setCurrentValues(float currentValues)
    {
        if(currentValues > maxValues)
        {
            currentValues = maxValues;
        }
        if(currentValues < 0)
        {
            currentValues = 0;
        }
        this.currentValues = currentValues;
    }

    /**
     * 设置游标位置
     * */
    public void setRadio(float radio) {
        this.radio = radio;
    }


    public void setSleepData(float radio,int deepTime,int lightTime) {
        this.radio = radio>1?1:radio;
        this.allTime = deepTime;
        this.lightTime = lightTime;
    }
}
