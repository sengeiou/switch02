package com.szip.sportwatch.Activity.diy;

import com.yalantis.ucrop.UCrop;

public interface IDiyView {
    void setView(boolean isCircle);
    void setDialView(int dial, int clock);
    void getCropPhoto(UCrop uCrop);
    void setDialProgress(int num);
}
