package com.szip.sportwatch.Contorller;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.szip.sportwatch.Adapter.MyPagerAdapter;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sleep.SleepDayFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sleep.SleepMonthFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sleep.SleepWeekFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.sleep.SleepYearFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.step.StepDayFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.step.StepMonthFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.step.StepWeekFragment;
import com.szip.sportwatch.Contorller.Fragment.ReportFragment.step.StepYearFragment;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.Interface.CalendarListener;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.Model.ReportDataBean;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.CalendarPicker;
import com.szip.sportwatch.View.NoScrollViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class SleepReportActivity extends BaseActivity implements View.OnClickListener{

    private String[] tabs;
    private TabLayout mTab;
    private NoScrollViewPager mPager;

    public long reportDate = DateUtil.getTimeOfToday()-24*60*60;

    private MyPagerAdapter myPagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sleep_report);
        tabs = new String[]{getString(R.string.day),getString(R.string.week),getString(R.string.month),getString(R.string.year)};
        initView();
        initEvent();
        initPager();
    }

    private void initView() {
        StatusBarCompat.translucentStatusBar(SleepReportActivity.this,true);
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.sleepReport));
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
        SleepDayFragment dayFragment =  new SleepDayFragment();
        SleepWeekFragment weekFragment =  new SleepWeekFragment();
        SleepMonthFragment monthFragment =  new SleepMonthFragment();
        SleepYearFragment yearFragment =  new SleepYearFragment();

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
                        .setFlag(1)
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
                ReportDataBean reportDataBean = LoadDataUtil.newInstance().getSleepWithDay(reportDate);
                Intent intent = new Intent(this,ShareActivity.class);
                intent.putExtra("flag",1);
                intent.putExtra("time",reportDate);
                if (reportDataBean == null){
                    intent.putExtra("value",0);
                    intent.putExtra("value1",((MyApplication)getApplicationContext()).getUserInfo().getSleepPlan());
                    intent.putExtra("value2",0);
                    intent.putExtra("value3",0);
                }else {
                    intent.putExtra("value",reportDataBean.getValue1()+reportDataBean.getValue2());
                    intent.putExtra("value1",((MyApplication)getApplicationContext()).getUserInfo().getSleepPlan());
                    intent.putExtra("value2",reportDataBean.getValue1());
                    intent.putExtra("value3",reportDataBean.getValue2());
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
