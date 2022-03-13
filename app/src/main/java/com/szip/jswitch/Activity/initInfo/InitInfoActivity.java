package com.szip.jswitch.Activity.initInfo;

import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.szip.jswitch.Activity.BaseActivity;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.StatusBarCompat;

public class InitInfoActivity extends BaseActivity {

    private FragmentTransaction tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_init_info);
        StatusBarCompat.translucentStatusBar(InitInfoActivity.this,true);
        setAndroidNativeLightStatusBar(this,true);
        initView();
    }

    private void initView() {
        ProductFragment productFragment = new ProductFragment();
        tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment, productFragment, "product");
        tx.addToBackStack(null);
        tx.commit();
    }

    public void unitPage() {
        UnitFragment unitFragment = new UnitFragment();
        tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment, unitFragment, "unit");
        tx.addToBackStack(null);
        tx.commit();
    }

    public void infoPage() {
        UserFragment fragment = new UserFragment();
        tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment, fragment, "user");
        tx.addToBackStack(null);
        tx.commit();
    }
}