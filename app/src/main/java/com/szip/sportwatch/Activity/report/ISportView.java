package com.szip.sportwatch.Activity.report;

import androidx.fragment.app.Fragment;

import com.szip.sportwatch.Adapter.MyPagerAdapter;

import java.util.ArrayList;

public interface ISportView {
    void initPager(MyPagerAdapter myPagerAdapter,String title);
}
