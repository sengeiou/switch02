package com.szip.jswitch.Activity.report;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.szip.jswitch.Adapter.MyPagerAdapter;
import com.szip.jswitch.Fragment.ReportFragment.animalHeat.AnimalDayFragment;
import com.szip.jswitch.Fragment.ReportFragment.animalHeat.AnimalMonthFragment;
import com.szip.jswitch.Fragment.ReportFragment.animalHeat.AnimalWeekFragment;
import com.szip.jswitch.Fragment.ReportFragment.animalHeat.AnimalYearFragment;
import com.szip.jswitch.R;

import java.util.ArrayList;

public class TemperaturePresenterImpl implements ISportPresenter{
    private Context context;
    private ISportView iSportView;

    public TemperaturePresenterImpl(Context context, ISportView iSportView) {
        this.context = context;
        this.iSportView = iSportView;
    }

    @Override
    public void getPageView(FragmentManager fragmentManager) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        AnimalDayFragment dayFragment =  new AnimalDayFragment();
        AnimalWeekFragment weekFragment =  new AnimalWeekFragment();
        AnimalMonthFragment monthFragment =  new AnimalMonthFragment();
        AnimalYearFragment yearFragment =  new AnimalYearFragment();
        // 装填
        fragments.add(dayFragment);
        fragments.add(weekFragment);
        fragments.add(monthFragment);
        fragments.add(yearFragment);
        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(fragmentManager);
        myPagerAdapter.setFragmentArrayList(fragments);
        if (iSportView!=null)
            iSportView.initPager(myPagerAdapter,context.getString(R.string.animalHeatReport));
    }

    @Override
    public void setViewDestory() {
        iSportView = null;
    }
}
