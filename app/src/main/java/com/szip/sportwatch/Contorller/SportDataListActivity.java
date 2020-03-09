package com.szip.sportwatch.Contorller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.szip.sportwatch.Adapter.SportDataAdapter;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.Interface.CalendarListener;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.CalendarPicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SportDataListActivity extends BaseActivity implements View.OnClickListener {

    private ListView listView;
    private SportDataAdapter sportDataAdapter;
    private List<SportData> dataList = new ArrayList<>();
    public long reportDate = DateUtil.getTimeOfToday();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sport_data_list);
        initData();
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(this);
        findViewById(R.id.rightIv).setOnClickListener(this);
    }

    private void initData() {
        if (MainService.getInstance().getConnectState()==3){
            ProgressHudModel.newInstance().show(this,getString(R.string.loading),getString(R.string.connect_error),10000);
            EXCDController.getInstance().writeForSportIndex();
        }else {
            dataList = LoadDataUtil.newInstance().getBestSportData(reportDate);
        }

    }

    private void updateView() {
        ((TextView)findViewById(R.id.timeTv)).setText(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM"));
        sportDataAdapter.setList(dataList);
    }

    private void initView() {
        StatusBarCompat.translucentStatusBar(SportDataListActivity.this,true);
        listView = findViewById(R.id.sportList);
        ((TextView)findViewById(R.id.timeTv)).setText(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM"));
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.history));
        ((ImageView)findViewById(R.id.rightIv)).setImageResource(R.mipmap.report_icon_calendar);

        sportDataAdapter = new SportDataAdapter(dataList,this);
        listView.setAdapter(sportDataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SportDataListActivity.this,SportTrackActivity.class);
                intent.putExtra("time",dataList.get(position).time);
                intent.putExtra("distance",dataList.get(position).distance);
                intent.putExtra("speed",dataList.get(position).speed);
                intent.putExtra("calorie",dataList.get(position).calorie);
                intent.putExtra("sportTime",dataList.get(position).sportTime);
                startActivity(intent);
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReport(UpdateReport updateReport){
        ProgressHudModel.newInstance().diss();
        dataList = LoadDataUtil.newInstance().getBestSportData(reportDate);
        updateView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIv:
                finish();
                break;
            case R.id.rightIv:
                CalendarPicker.getInstance()
                        .enableAnimation(true)
                        .setFragmentManager(getSupportFragmentManager())
                        .setAnimationStyle(R.style.CustomAnim)
                        .setFlag(6)
                        .setDate(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM-dd"))
                        .setCalendarListener(new CalendarListener() {
                            @Override
                            public void onClickDate(String date) {
                                reportDate = DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd");
                                dataList = LoadDataUtil.newInstance().getBestSportData(reportDate);
                                updateView();
                            }
                        })
                        .show();
                break;
        }
    }
}
