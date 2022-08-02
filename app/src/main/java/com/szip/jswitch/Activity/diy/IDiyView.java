package com.szip.jswitch.Activity.diy;

import com.yalantis.ucrop.UCrop;

public interface IDiyView {
    void setView(boolean isCircle);
    void setDialView(String dial,String pictureUrl, int clock);
    void getCropPhoto(UCrop uCrop);
    void setDialProgress(int num,String str);
}
