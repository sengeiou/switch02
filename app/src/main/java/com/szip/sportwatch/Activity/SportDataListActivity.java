package com.szip.sportwatch.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.szip.sportwatch.Adapter.SportDataAdapter;
import com.szip.sportwatch.BLE.BleClient;
import com.szip.sportwatch.BLE.EXCDController;
import com.szip.sportwatch.DB.LoadDataUtil;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.Interface.CalendarListener;
import com.szip.sportwatch.Model.EvenBusModel.UpdateReport;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Service.MainService;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.ProgressHudModel;
import com.szip.sportwatch.Util.StatusBarCompat;
import com.szip.sportwatch.View.CalendarPicker;
import com.szip.sportwatch.View.MyAlerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
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
        LoadDataUtil.newInstance().initCalendarPoint(7);
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
        if (MyApplication.getInstance().isMtk()){
            if (MainService.getInstance().getState()==3){
                ProgressHudModel.newInstance().show(this,getString(R.string.loading),getString(R.string.connect_error),50000);
                EXCDController.getInstance().writeForSportIndex();
            }else {
                dataList = LoadDataUtil.newInstance().getBestSportData(reportDate);
                Collections.sort(dataList);
            }
        }else {
            if (BleClient.getInstance().isSync()){
                ProgressHudModel.newInstance().show(this,getString(R.string.loading),getString(R.string.connect_error),40000);
            }else {
                dataList = LoadDataUtil.newInstance().getBestSportData(reportDate);
                Collections.sort(dataList);
            }
        }
    }

    private void updateView() {
        ((TextView)findViewById(R.id.timeTv)).setText(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM"));
        sportDataAdapter.setList(dataList);
    }

    private void initView() {
        StatusBarCompat.translucentStatusBar(SportDataListActivity.this,true);
        setAndroidNativeLightStatusBar(this,true);
        listView = findViewById(R.id.sportList);
        ((TextView)findViewById(R.id.timeTv)).setText(DateUtil.getStringDateFromSecond(reportDate,"yyyy-MM"));
        setTitleText(getString(R.string.history));
        ((ImageView)findViewById(R.id.rightIv)).setImageResource(R.mipmap.report_icon_calendar);

        sportDataAdapter = new SportDataAdapter(dataList,this);
        listView.setAdapter(sportDataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SportDataListActivity.this,SportTrackActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("sport",dataList.get(position));
                intent.putExtra("data",bundle);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                MyAlerDialog.getSingle().showAlerDialog(getString(R.string.tip), getString(R.string.delete),
                        null, null, false, new MyAlerDialog.AlerDialogOnclickListener() {
                            @Override
                            public void onDialogTouch(boolean flag) {
                                if (flag){
                                    LoadDataUtil.newInstance().removeSportData(dataList.get(position));
                                    dataList.remove(position);
                                    sportDataAdapter.setList(dataList);
                                }
                            }
                        },SportDataListActivity.this);
                return true;
            }
        });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReport(UpdateReport updateReport){
        ProgressHudModel.newInstance().diss();
        dataList = LoadDataUtil.newInstance().getBestSportData(reportDate);
        Collections.sort(dataList);
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
                                if (DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd")>DateUtil.getTimeOfToday()){
                                    showToast(getString(R.string.tomorrow));
                                }else {
                                    reportDate = DateUtil.getTimeScopeForDay(date,"yyyy-MM-dd");
                                    dataList = LoadDataUtil.newInstance().getBestSportData(reportDate);
                                    Collections.sort(dataList);
                                    updateView();
                                }

                            }
                        })
                        .show();
                break;
        }
    }
}
