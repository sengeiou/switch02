package com.szip.jswitch.Model.HttpBean;

import com.szip.jswitch.DB.dbModel.SportWatchAppFunctionConfigDTO;

import java.util.ArrayList;

public class DeviceConfigBean extends BaseApi{
    ArrayList<SportWatchAppFunctionConfigDTO> data;

    public ArrayList<SportWatchAppFunctionConfigDTO> getData() {
        return data;
    }
}
