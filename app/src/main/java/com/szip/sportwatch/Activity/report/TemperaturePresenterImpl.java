package com.szip.sportwatch.Activity.report;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.szip.sportwatch.Adapter.MyPagerAdapter;
import com.szip.sportwatch.Fragment.ReportFragment.animalHeat.AnimalDayFragment;
import com.szip.sportwatch.Fragment.ReportFragment.animalHeat.AnimalMonthFragment;
import com.szip.sportwatch.Fragment.ReportFragment.animalHeat.AnimalWeekFragment;
import com.szip.sportwatch.Fragment.ReportFragment.animalHeat.AnimalYearFragment;
import com.szip.sportwatch.R;

import java.util.ArrayList;

public class TemperaturePresenterImpl implements ISportPresenter{
    private ReportActivity context;
    private ISportView iSportView;

    public TemperaturePresenterImpl(ReportActivity context, ISportView iSportView) {
        this.context = context;
        this.iSportView = iSportView;
    }

    @Override
    public void getPageView(FragmentManager fragmentManager) {
        ArrayList<Fragment> fragments = new ArrayList<>();
        AnimalDayFragment dayFragment =  new AnimalDayFragment();
        dayFragment.setActivity(context);
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
}
