package com.szip.sportwatch.Model.HttpBean;

import java.util.ArrayList;

public class WeatherBean extends BaseApi{


    private Data data;

    public Data getData() {
        return data;
    }

    public class Data{
        private Location location;
        private ArrayList<Condition> forecasts;

        public Location getLocation() {
            return location;
        }

        public ArrayList<Condition> getForecasts() {
            return forecasts;
        }
    }

    public class Location{
        private String country;
        private String city;

        public String getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }
    }

    public class Condition{
        private float temperature;
        private float low;
        private float high;
        private String text;
        private int code;
        private String iconUrl;

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

        public float getLow() {
            return low;
        }

        public float getHigh() {
            return high;
        }
    }

}