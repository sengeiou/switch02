package com.szip.sportwatch.Contorller;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.szip.sportwatch.Adapter.MyPagerAdapter;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.heart.HeartDayFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.heart.HeartMonthFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.heart.HeartWeekFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.heart.HeartYearFragment;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Interface.CalendarListener;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.Model.ReportDataBean;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.ScreenCapture;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.CalendarPicker;
import com.szip.sportwatch.View.NoScrollViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class HeartReportActivity extends BaseActivity implements View.OnClickListener{

    private String[] tabs;
    private TabLayout mTab;
    private NoScrollViewPager mPager;

    public long reportDate = DateUtil.getTimeOfToday();

    private MyPagerAdapter myPagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_heart_report);
        tabs = new String[]{getString(R.string.day),getString(R.string.week),getString(R.string.month),getString(R.string.year)};
        LoadDataUtil.newInstance().initCalendarPoint(3);
        initView();
        initEvent();
        initPager();
    }
    private void initView() {
        StatusBarCompat.translucentStatusBar(HeartReportActivity.this,true);
        setAndroidNativeLightStatusBar(this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.heartReport));
        mTab = findViewById(R.id.reportTl);
        mPager = findViewById(R.id.reportVp);
    }

    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(this);
        findViewById(R.id.image1).setOnClickListener(this);
        findViewById(R.id.image2).setOnClickListener(this);
    }

    private void initPager() {
        // 创建一个集合,装填Fragment
        HeartDayFragment dayFragment =  new HeartDayFragment();
        HeartWeekFragment weekFragment =  new HeartWeekFragment();
        HeartMonthFragment monthFragment =  new HeartMonthFragment();
        HeartYearFragment yearFragment =  new HeartYearFragment();

        // 装填
        fragments.add(dayFragment);
        fragments.add(weekFragment);
        fragments.add(monthFragment);
        fragments.add(yearFragment);
        // 创建ViewPager适配器
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragmentArrayList(fragments);
        // 给ViewPager设置适配器
        mPager.setAdapter(myPagerAdapter);

        // 使用 TabLayout 和 ViewPager 相关联
        mTab.setupWithViewPager(mPager);

        // TabLayout 指示器 (记得自己手动创建3个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        for (int i = 0; i < myPagerAdapter.getCount(); i++) {
            TabLayout.Tab tab = mTab.getTabAt(i);//获得每一个tab
            tab.setCustomView(R.layout.main_top_layout);//给每一个tab设置view
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(true);//第一个tab被选中
            }
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.main_tv);
            textView.setText(tabs[i]);//设置tab上的文字
        }
        mTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(true);
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                finish();
                break;
            case R.id.image2:
                CalendarPicker.getInstance()
                        .enableAnimation(true)
                        .setFragmentManager(getSupportFragmentManager())
                        .setAnimationStyle(R.style.CustomAnim)
                        .setFlag(2)
                        .setDate(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM-dd"))
                        .setCalendarListener(new CalendarListener() {
                            @Override
                            public void onClickDate(String date) {
                                if (DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd")>DateUtil.getTimeOfToday()){
                                    showToast(getString(R.string.tomorrow));
                                }else {
                                    reportDate = DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd");
                                    EventBus.getDefault().post(new UpdateReport());
                                }
                            }
                        })
                        .show();
                break;
            case R.id.image1:{
                checkPermission();
            }
            break;
        }
    }

    private void checkPermission() {
        /**
         * 获取权限·
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }else {
                shareShow(findViewById(R.id.reportLl));
            }
        }else {
            shareShow(findViewById(R.id.reportLl));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            int code = grantResults[0];
            if (code == PackageManager.PERMISSION_GRANTED){
                shareShow(findViewById(R.id.reportLl));
            }else {
                showToast(getString(R.string.shareFailForPermission));
            }
        }
    }



}
