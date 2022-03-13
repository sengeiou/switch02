package com.szip.jswitch.Model;

public class SendDialModel {
    boolean loadSuccess;

    public SendDialModel(boolean loadSuccess) {
        this.loadSuccess = loadSuccess;
    }

    public boolean isLoadSuccess() {
        return loadSuccess;
    }

    public void setLoadSuccess(boolean loadSuccess) {
        this.loadSuccess = loadSuccess;
    }
}
