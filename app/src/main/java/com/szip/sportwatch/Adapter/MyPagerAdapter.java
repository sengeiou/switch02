package com.szip.sportwatch.Adapter;

import android.content.Context;


import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {


    private ArrayList<Fragment> fragmentArrayList;
    private Context context;


    public void setFragmentArrayList(ArrayList<Fragment> list){
        this.fragmentArrayList = list;
    }

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

}
