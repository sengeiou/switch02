package com.szip.sportwatch.Interface;

public enum SportType {
    OnFoot(1),
    ;
    private final int value;
    private SportType(int value) {
        this.value = value;
    }


    public SportType valueOf(int value) {
        switch (value) {
            case 0:
                return SportType.OnFoot;
            case 1:
                return SportType.OnFoot;
            default:
                return null;
        }
    }
}
