package com.szip.sportwatch.Contorller.Fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by Administrator on 2019/12/1.
 */

public abstract class BaseFragment extends Fragment {
    protected View mRootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mRootView == null){
            mRootView = inflater.inflate(getLayoutId(), container, false);
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        afterOnCreated(savedInstanceState);
    }

    protected abstract int getLayoutId();
    protected abstract void afterOnCreated(Bundle savedInstanceState);
    protected void showToast(String string){
        Toast.makeText(getActivity(), string,Toast.LENGTH_LONG).show();
    }
}
