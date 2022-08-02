package com.szip.jswitch.Activity.dial;

import androidx.recyclerview.widget.RecyclerView;


import com.szip.jswitch.Model.HttpBean.DialBean;

import java.util.ArrayList;

public interface ISelectDialPresenter {
    void getViewConfig(RecyclerView dialRv, ArrayList<DialBean.Dial> dialArrayList);
    void startToSendDial();
    void sendDial(String resultUri,int address);
    void resumeSendDial(int page);
    void setViewDeStory();
}
