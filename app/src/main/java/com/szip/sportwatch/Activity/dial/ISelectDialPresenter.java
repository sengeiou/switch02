package com.szip.sportwatch.Activity.dial;

import androidx.recyclerview.widget.RecyclerView;

public interface ISelectDialPresenter {
    void getViewConfig(RecyclerView dialRv);
    void sendDial(int pictureId,int clock);
}
