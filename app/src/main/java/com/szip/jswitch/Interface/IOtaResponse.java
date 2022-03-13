package com.szip.jswitch.Interface;

public interface IOtaResponse {
    void onStartToSendFile(int type, int address);
    void onSendProgress();
    void onSendSccuess();
    void onSendFail();
}
