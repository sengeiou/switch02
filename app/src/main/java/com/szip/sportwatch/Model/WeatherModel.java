package com.szip.sportwatch.Model;

public class WeatherModel {

    private String city;
    private float temperature;
    private String text;
    private int code;
    private String iconUrl;

    public WeatherModel(String city, float temperature, String text, int code, String iconUrl) {
        this.city = city;
        this.temperature = temperature;
        this.text = text;
        this.code = code;
        this.iconUrl = iconUrl;
    }

    public String getCity() {
        return city;
    }

    public float getTemperature() {
        return temperature;
    }

    public String getText() {
        return text;
    }

    public int getCode() {
        return code;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
