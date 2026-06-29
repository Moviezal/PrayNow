package com.example.praynow;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PrayerTimeHelper {

    public static void fetchAndSave(Context context) {
        new Thread(() -> {
            try {
                String urlStr =
                        "https://api.aladhan.com/v1/timingsByCity?city=Jakarta&country=Indonesia&method=2";

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject timings =
                        new JSONObject(sb.toString())
                                .getJSONObject("data")
                                .getJSONObject("timings");

                SharedPreferences prefs =
                        context.getSharedPreferences("PrayNowPrefs", Context.MODE_PRIVATE);

                prefs.edit()
                        .putString("time_Subuh", timings.getString("Fajr").substring(0,5))
                        .putString("time_Dzuhur", timings.getString("Dhuhr").substring(0,5))
                        .putString("time_Ashar", timings.getString("Asr").substring(0,5))
                        .putString("time_Maghrib", timings.getString("Maghrib").substring(0,5))
                        .putString("time_Isya", timings.getString("Isha").substring(0,5))
                        .apply();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
