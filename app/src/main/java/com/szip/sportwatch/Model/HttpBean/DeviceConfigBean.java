package com.szip.sportwatch.Model.HttpBean;

import com.szip.sportwatch.DB.dbModel.SportWatchAppFunctionConfigDTO;

import java.util.ArrayList;

public class DeviceConfigBean extends BaseApi{
    ArrayList<SportWatchAppFunctionConfigDTO> data;

    public ArrayList<SportWatchAppFunctionConfigDTO> getData() {
        return data;
    }
}
