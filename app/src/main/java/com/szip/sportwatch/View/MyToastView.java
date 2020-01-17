package com.szip.sportwatch.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.HttpMessgeUtil;
import com.szip.sportwatch.Util.MathUitl;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by Administrator on 2019/12/16.
 */

public class MyToastView {
    private static MyToastView mInstance;
    private Context mContext;

    private ImageView imageView;
    private TextView textView;
    private LinearLayout myLayout;

    public static MyToastView getInstance(Context context)
    {
        if (mInstance == null)
        {
            synchronized (MyToastView.class)
            {
                if (mInstance == null)
                {
                    mInstance = new MyToastView(context);
                }
            }
        }
        return mInstance;
    }

    public MyToastView(Context context) {
        mContext = context;
        initView(mContext);
    }

    @SuppressLint("ResourceType")
    private void initView(Context mContext) {

        //定义好父容器并设置相关属性
        myLayout = new LinearLayout(mContext);
        myLayout.setOrientation(VERTICAL);
        myLayout.setBackground(mContext.getDrawable(R.drawable.bg_item_normal));

        //设置ImageView的布局参数
        imageView = new ImageView(mContext);
        imageView.setId(1);
        imageView.setImageResource(R.mipmap.my_box_ok);
        LinearLayout.LayoutParams imagePara =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        imagePara.gravity = Gravity.CENTER_HORIZONTAL;
        imagePara.setMargins(MathUitl.dipToPx(40,mContext),MathUitl.dipToPx(35,mContext),
                MathUitl.dipToPx(40,mContext),0);


        //设置TextView的布局参数
        textView = new TextView(mContext);
        textView.setId(2);
        textView.setTextColor(mContext.getResources().getColor(R.color.rayblue));
        textView.setTextSize(16);
        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.gravity = Gravity.CENTER_HORIZONTAL;
        textParams.setMargins(MathUitl.dipToPx(40,mContext),MathUitl.dipToPx(20,mContext),
                MathUitl.dipToPx(40,mContext),MathUitl.dipToPx(35,mContext));

        //将布局添加到父容器中
        myLayout.addView(imageView, imagePara);
        myLayout.addView(textView, textParams);
    }

    public LinearLayout showToast(String msg){
        textView.setText(msg);
        //设置显示父容器
        return myLayout;
    }
}
