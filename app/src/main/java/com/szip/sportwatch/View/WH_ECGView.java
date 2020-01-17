package com.szip.sportwatch.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.szip.sportwatch.R;

import java.util.ArrayList;


/**
 * Created by wenh on 16/10/2511:09.
 */

public class WH_ECGView extends View {


    private int allData[] = new int[0];
    private float gap_grid;//网格间距
    private int width,height;//本页面宽，高
    private float gap_x;//两点间横坐标间距
    private float dataNum_per_grid = 2;//每小格内的数据个数
    private float y_center;//中心y值




    public WH_ECGView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public WH_ECGView(Context context){
        super(context);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed){
            gap_grid = 30.0f;
            width = getWidth();
            height = getHeight();
            y_center = height*2/3;
            gap_x = gap_grid/dataNum_per_grid;
            Log.e("json","本页面宽： " + width +"  高:" + height);
        }

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawECGWave(canvas);
    }

    /**
     * 画心电图
     */
    public void DrawECGWave(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.0f);
        CornerPathEffect cornerPathEffect = new CornerPathEffect(100);
        paint.setPathEffect(cornerPathEffect);
        paint.setColor(getResources().getColor(R.color.blue));
        Path path = new Path();
        path.moveTo(0, getY_coordinate(allData[0]));
        for (int i = 1; i < this.allData.length; i ++){
                path.lineTo( gap_x * i, getY_coordinate(allData[i]));
        }
        canvas.drawPath(path,paint);
    }

    /**
     * 将数值转换为y坐标，中间大的显示心电图的区域
     */
    private float getY_coordinate(int data){
        float y_coor = 0.0f;
        y_coor = data*-1 + y_center;
        return y_coor;
    }

    public void addData(int data[]){
        allData = data;
        invalidate();
    }

}
