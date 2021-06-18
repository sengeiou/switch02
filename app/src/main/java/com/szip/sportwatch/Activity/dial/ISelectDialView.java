package com.szip.sportwatch.Activity.dial;

public interface ISelectDialView {
    void initList(boolean initSuccess);
    void setView(String id,String pictureId,int clock);
    void setDialView(String dialId,String pictureId,int clock);
    void setDialProgress(int max);
}
