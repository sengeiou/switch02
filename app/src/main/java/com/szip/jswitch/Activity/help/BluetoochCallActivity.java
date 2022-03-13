package com.szip.jswitch.Activity.help;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.szip.jswitch.Activity.BaseActivity;
import com.szip.jswitch.Adapter.MyPagerAdapter;
import com.szip.jswitch.Fragment.BluetoochCallFragment;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.StatusBarCompat;
import com.szip.jswitch.View.NoScrollViewPager;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class BluetoochCallActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private NoScrollViewPager viewPager;


    private MyPagerAdapter vpAdapter;

    private ArrayList<Fragment> views = new ArrayList<>();

    private ImageView dotIv1,dotIv2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_bluetooch_call);
        initView();
        initData();
    }

    private void initView() {
        StatusBarCompat.translucentStatusBar(BluetoochCallActivity.this,true);
        setAndroidNativeLightStatusBar(this,true);
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.rightIv).setVisibility(View.GONE);
        setTitleText(getString(R.string.blePhone));
        dotIv1 = findViewById(R.id.dotIv1);
        dotIv2 = findViewById(R.id.dotIv2);
        viewPager = findViewById(R.id.viewpager);
    }

    /**
     * 初始化数据
     * */
    private void initData() {


        //根据睡眠段数绘制报告
        for (int i = 0;i<2;i++){
            BluetoochCallFragment bluetoochCallFragment = BluetoochCallFragment.newInstance(i);
            views.add(bluetoochCallFragment);

        }
        // 创建ViewPager适配器
        vpAdapter = new MyPagerAdapter(this.getSupportFragmentManager());
        vpAdapter.setFragmentArrayList(views);
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(vpAdapter);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0){
            dotIv1.setImageResource(R.mipmap.my_call_icon_circle_pre);
            dotIv2.setImageResource(R.mipmap.my_call_icon_circle);
        }else {
            dotIv1.setImageResource(R.mipmap.my_call_icon_circle);
            dotIv2.setImageResource(R.mipmap.my_call_icon_circle_pre);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
