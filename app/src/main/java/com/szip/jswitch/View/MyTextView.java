package com.szip.jswitch.View;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.szip.jswitch.R;
import com.szip.jswitch.Util.MathUitl;

public class MyTextView extends TextView {

    private TypedArray backIds;
    private TypedArray textIds;
    private TypedArray textColorIds;

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initConfig(context, attrs);
    }



    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig(context, attrs);
    }


    @SuppressLint("ResourceType")
    private void initConfig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyTextView);

        int styleId = a.getColor(R.styleable.MyTextView_style_array, 0);

        switch (styleId){
            case 0:
                backIds = context.getResources().obtainTypedArray(R.array.bmi_back);
                textIds = context.getResources().obtainTypedArray(R.array.bmi_text);
                textColorIds = context.getResources().obtainTypedArray(R.array.bmi_text_color);
                break;
            case 1:
                backIds = context.getResources().obtainTypedArray(R.array.bmr_back);
                textIds = context.getResources().obtainTypedArray(R.array.bmr_text);
                textColorIds = context.getResources().obtainTypedArray(R.array.bmr_text_color);
                break;
            case 2:
                backIds = context.getResources().obtainTypedArray(R.array.obesity_back);
                textIds = context.getResources().obtainTypedArray(R.array.obesity_text);
                textColorIds = context.getResources().obtainTypedArray(R.array.obesity_text_color);
                break;
            case 3:
                backIds = context.getResources().obtainTypedArray(R.array.visceral_back);
                textIds = context.getResources().obtainTypedArray(R.array.visceral_text);
                textColorIds = context.getResources().obtainTypedArray(R.array.visceral_text_color);
                break;
            case 4:
                backIds = context.getResources().obtainTypedArray(R.array.body_fat_back);
                textIds = context.getResources().obtainTypedArray(R.array.body_fat_text);
                textColorIds = context.getResources().obtainTypedArray(R.array.body_fat_text_color);
                break;
            case 5:
                backIds = context.getResources().obtainTypedArray(R.array.muscle_back);
                textIds = context.getResources().obtainTypedArray(R.array.muscle_text);
                textColorIds = context.getResources().obtainTypedArray(R.array.muscle_text_color);
                break;
            case 6:
                backIds = context.getResources().obtainTypedArray(R.array.protein_back);
                textIds = context.getResources().obtainTypedArray(R.array.protein_text);
                textColorIds = context.getResources().obtainTypedArray(R.array.protein_text_color);
                break;
            case 7:
                backIds = context.getResources().obtainTypedArray(R.array.skeletal_back);
                textIds = context.getResources().obtainTypedArray(R.array.skeletal_text);
                textColorIds = context.getResources().obtainTypedArray(R.array.skeletal_text_color);
                break;
            case 8:
                backIds = context.getResources().obtainTypedArray(R.array.subcutaneous_back);
                textIds = context.getResources().obtainTypedArray(R.array.subcutaneous_text);
                textColorIds = context.getResources().obtainTypedArray(R.array.subcutaneous_text_color);
                break;
            case 9:
                backIds = context.getResources().obtainTypedArray(R.array.bone_back);
                textIds = context.getResources().obtainTypedArray(R.array.bone_text);
                textColorIds = context.getResources().obtainTypedArray(R.array.bone_text_color);
                break;
        }

        a.recycle();
    }

    public void setStyle(int pos){
        if (backIds!=null){
            setBackground(backIds.getDrawable(pos));
            setText(textIds.getString(pos));
            setTextColor(textColorIds.getColor(pos,0));
        }
    }
}
