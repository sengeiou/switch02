package com.szip.sportwatch.Activity.dial;

public interface ISelectDialView {
    void setView(boolean isCircle,int id,int pictureId,int clock);
    void setDialView(int dialId,int pictureId,int clock);
    void setDialProgress(int max);
}
