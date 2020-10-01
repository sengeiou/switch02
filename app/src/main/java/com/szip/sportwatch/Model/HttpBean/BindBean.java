package com.szip.sportwatch.Model.HttpBean;

public class BindBean extends BaseApi{

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        String bindId;

        public String getBindId() {
            return bindId;
        }

        public void setBindId(String bindId) {
            this.bindId = bindId;
        }
    }
}
