package com.example.praynow;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    Switch swSubuh, swDzuhur, swAshar, swMaghrib, swIsya;
    ImageView navAlarm, navHome, navAdd;

    SharedPreferences prefs;
    boolean exactAlarmGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        prefs = getSharedPreferences("PrayNowPrefs", MODE_PRIVATE);

        initViews();
        loadSwitchState();
        setupSwitchListener();
        setupBottomNav();

        checkExactAlarmPermission();
    }

    // ================= EXACT ALARM =================
    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager =
                    (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                exactAlarmGranted = true;
            } else {
                Intent intent =
                        new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(
                        this,
                        "Izinkan alarm tepat waktu agar adzan dapat berjalan",
                        Toast.LENGTH_LONG
                ).show();
            }
        } else {
            exactAlarmGranted = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager =
                    (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                if (!exactAlarmGranted) {
                    exactAlarmGranted = true;
                    refreshAlarms();
                }
            }
        } else {
            refreshAlarms();
        }
    }

    // ================= INIT =================
    private void initViews() {
        swSubuh = findViewById(R.id.swSubuh);
        swDzuhur = findViewById(R.id.swDzuhur);
        swAshar = findViewById(R.id.swAshar);
        swMaghrib = findViewById(R.id.swMaghrib);
        swIsya = findViewById(R.id.swIsya);

        navAlarm = findViewById(R.id.navAlarm);
        navHome = findViewById(R.id.navHome);
        navAdd = findViewById(R.id.navAdd);
    }

    private void loadSwitchState() {
        swSubuh.setChecked(prefs.getBoolean("subuh", false));
        swDzuhur.setChecked(prefs.getBoolean("dzuhur", false));
        swAshar.setChecked(prefs.getBoolean("ashar", false));
        swMaghrib.setChecked(prefs.getBoolean("maghrib", false));
        swIsya.setChecked(prefs.getBoolean("isya", false));
    }

    // ================= SWITCH =================
    private void setupSwitchListener() {
        setupSwitch(swSubuh, "Subuh", "subuh");
        setupSwitch(swDzuhur, "Dzuhur", "dzuhur");
        setupSwitch(swAshar, "Ashar", "ashar");
        setupSwitch(swMaghrib, "Maghrib", "maghrib");
        setupSwitch(swIsya, "Isya", "isya");
    }

    private void setupSwitch(Switch sw, String sholat, String key) {
        sw.setOnCheckedChangeListener((btn, isChecked) -> {
            // CEK APAKAH JADWAL SUDAH ADA
            if (!prefs.getBoolean("jadwal_ready", false)) {
                Toast.makeText(
                        this,
                        "Ambil jadwal sholat dulu",
                        Toast.LENGTH_SHORT
                ).show();
                sw.setChecked(false);
                return;
            }

            prefs.edit().putBoolean(key, isChecked).apply();

            if (!exactAlarmGranted) {
                Toast.makeText(
                        this,
                        "Izin alarm belum diberikan",
                        Toast.LENGTH_SHORT
                ).show();
                sw.setChecked(false);
                return;
            }

            if (isChecked) {
                String time = getPrayerTime(sholat);
                if (time.equals("00:00")) {
                    Toast.makeText(
                            this,
                            "Waktu sholat belum tersedia",
                            Toast.LENGTH_SHORT
                    ).show();
                    sw.setChecked(false);
                } else {
                    setAdzanAlarm(sholat, time);
                }
            } else {
                cancelAdzanAlarm(sholat);
            }
        });
    }

    // ================= ALARM =================
    private void setAdzanAlarm(String sholat, String time) {
        String[] t = time.split(":");
        int hour = Integer.parseInt(t[0]);
        int minute = Integer.parseInt(t[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(this, AdzanReceiver.class);
        intent.putExtra("SHOLAT", sholat);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                sholat.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am =
                (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(),
                pi
        );

        Toast.makeText(
                this,
                "Alarm " + sholat + " disetel (" + time + ")",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void cancelAdzanAlarm(String sholat) {
        Intent intent = new Intent(this, AdzanReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                sholat.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am =
                (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }

    // ================= DATA =================
    private String getPrayerTime(String sholat) {
        String key = "time_" + sholat;
        String time = prefs.getString(key, "00:00");
        return time == null || time.isEmpty() ? "00:00" : time;
    }

    private void refreshAlarms() {
        if (swSubuh.isChecked()) setAdzanAlarm("Subuh", getPrayerTime("Subuh"));
        if (swDzuhur.isChecked()) setAdzanAlarm("Dzuhur", getPrayerTime("Dzuhur"));
        if (swAshar.isChecked()) setAdzanAlarm("Ashar", getPrayerTime("Ashar"));
        if (swMaghrib.isChecked()) setAdzanAlarm("Maghrib", getPrayerTime("Maghrib"));
        if (swIsya.isChecked()) setAdzanAlarm("Isya", getPrayerTime("Isya"));
    }

    // ================= NAV =================
    private void setupBottomNav() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, SholatActivity.class));
            finish();
        });

        navAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, BookActivity.class));
            finish();
        });
    }
}
