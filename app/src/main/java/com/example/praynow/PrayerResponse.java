package com.example.praynow;

import com.google.gson.annotations.SerializedName;

public class PrayerResponse {

    @SerializedName("data")
    public Data data;

    public static class Data {

        @SerializedName("timings")
        public Timings timings;
    }

    public static class Timings {

        @SerializedName("Fajr")
        public String Fajr;

        @SerializedName("Dhuhr")
        public String Dhuhr;

        @SerializedName("Asr")
        public String Asr;

        @SerializedName("Maghrib")
        public String Maghrib;

        @SerializedName("Isha")
        public String Isha;
    }
}
