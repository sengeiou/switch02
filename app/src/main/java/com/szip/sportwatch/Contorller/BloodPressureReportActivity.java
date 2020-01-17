package com.szip.sportwatch.Contorller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.szip.sportwatch.Adapter.MyPagerAdapter;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.bloodPressure.BloodPressureDayFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.bloodPressure.BloodPressureMonthFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.bloodPressure.BloodPressureWeekFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.bloodPressure.BloodPressureYearFragment;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Interface.CalendarListener;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.Model.ReportDataBean;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.CalendarPicker;
import com.szip.sportwatch.View.NoScrollViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BloodPressureReportActivity extends BaseActivity implements View.OnClickListener{
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
        setContentView(R.layout.activity_blood_pressure_report);
        tabs = new String[]{getString(R.string.day),getString(R.string.week),getString(R.string.month),getString(R.string.year)};
        initView();
        initEvent();
        initPager();
    }

    private void initView() {
        StatusBarCompat.translucentStatusBar(BloodPressureReportActivity.this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.bloodPressureReport));
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
        BloodPressureDayFragment dayFragment =  new BloodPressureDayFragment();
        dayFragment.setActivity(this);
        BloodPressureWeekFragment weekFragment =  new BloodPressureWeekFragment();
        BloodPressureMonthFragment monthFragment =  new BloodPressureMonthFragment();
        BloodPressureYearFragment yearFragment =  new BloodPressureYearFragment();

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

    public void setViewPagerScroll(boolean isScroll){
        mPager.setScroll(isScroll);
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
                        .setFlag(3)
                        .setDate(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM-dd"))
                        .setCalendarListener(new CalendarListener() {
                            @Override
                            public void onClickDate(String date) {
                                reportDate = DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd");
                                EventBus.getDefault().post(new UpdateReport());
                            }
                        })
                        .show();
                break;
            case R.id.image1:{
                ReportDataBean reportDataBean = LoadDataUtil.newInstance().getBloodPressureWithDay(reportDate);
                Intent intent = new Intent(this,ShareActivity.class);
                intent.putExtra("flag",3);
                intent.putExtra("time",reportDate);
                if (reportDataBean.getDrawDataBeans().size()!=0){
                    intent.putExtra("value",reportDataBean.getDrawDataBeans().get(0).getValue()+45);
                    intent.putExtra("value1",reportDataBean.getDrawDataBeans().get(0).getValue1()+45);
                    if ((reportDataBean.getDrawDataBeans().get(0).getValue()+5)/150f<0.333){
                        intent.putExtra("value2",1);
                    }else if ((reportDataBean.getDrawDataBeans().get(0).getValue()+5)/150f<0.666){
                        if ((reportDataBean.getDrawDataBeans().get(0).getValue1()+15)/90f<0.333){
                            intent.putExtra("value2",1);
                        }else if ((reportDataBean.getDrawDataBeans().get(0).getValue1()+15)/90f<0.666){
                            intent.putExtra("value2",0);
                        }else {
                            intent.putExtra("value2",2);
                        }
                    }else {
                        intent.putExtra("value2",2);
                    }
                    intent.putExtra("value3",reportDataBean.getDrawDataBeans().size());
                }else {
                    intent.putExtra("value",0);
                    intent.putExtra("value1",1);
                    intent.putExtra("value2",0);
                    intent.putExtra("value3",0);
                }
                startActivityForResult(intent,100);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100&&resultCode == 101){
            shareShow(data.getStringExtra("filePath"));
        }
    }
}
