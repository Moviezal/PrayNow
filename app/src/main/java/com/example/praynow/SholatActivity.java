package com.example.praynow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton; // Tambahkan ini

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class SholatActivity extends AppCompatActivity {

    TextView txtClock;
    TextView timeSubuh, timeDzuhur, timeAshar, timeMaghrib, timeIsya;
    ImageView navAlarm, navHome, navAdd;
    FloatingActionButton fabAI; // 🔥 AI: Deklarasi Button AI

    Handler handler = new Handler();
    Runnable clockRunnable;

    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sholat);

        prefs = getSharedPreferences("PrayNowPrefs", MODE_PRIVATE);

        initViews();
        startClock();
        setupBottomNav();
        setupAIButton(); // 🔥 AI: Panggil fungsi setup AI
        fetchPrayerTimes();
    }

    private void initViews() {
        txtClock = findViewById(R.id.txtClock);

        timeSubuh   = findViewById(R.id.timeSubuh);
        timeDzuhur  = findViewById(R.id.timeDzuhur);
        timeAshar   = findViewById(R.id.timeAshar);
        timeMaghrib = findViewById(R.id.timeMaghrib);
        timeIsya    = findViewById(R.id.timeIsya);

        navAlarm = findViewById(R.id.navAlarm);
        navHome  = findViewById(R.id.navHome);
        navAdd   = findViewById(R.id.navAdd);

        fabAI = findViewById(R.id.fabAI); // 🔥 AI: Hubungkan ID dari XML
    }

    // 🔥 AI: Fungsi untuk pindah ke halaman Chat AI
    private void setupAIButton() {
        if (fabAI != null) {
            fabAI.setOnClickListener(v -> {
                Intent intent = new Intent(SholatActivity.this, ChatAiActivity.class);
                startActivity(intent);
            });
        }
    }

    private void startClock() {
        clockRunnable = () -> {
            txtClock.setText(
                    new SimpleDateFormat("HH:mm", Locale.getDefault())
                            .format(new Date())
            );
            handler.postDelayed(clockRunnable, 1000);
        };
        handler.post(clockRunnable);
    }

    private void setupBottomNav() {
        navAlarm.setOnClickListener(v ->
                startActivity(new Intent(this, AlarmActivity.class))
        );
        navHome.setOnClickListener(v ->
                Toast.makeText(this, "Sudah di halaman Sholat", Toast.LENGTH_SHORT).show()
        );
        navAdd.setOnClickListener(v ->
                startActivity(new Intent(this, BookActivity.class))
        );
    }

    // ================= ALADHAN =================
    private void fetchPrayerTimes() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.aladhan.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AladhanApi api = retrofit.create(AladhanApi.class);

        api.getPrayerTimes("Jakarta", "Indonesia", 2)
                .enqueue(new Callback<PrayerResponse>() {
                    @Override
                    public void onResponse(Call<PrayerResponse> call,
                                           Response<PrayerResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().data != null) {

                            PrayerResponse.Timings t =
                                    response.body().data.timings;

                            // TAMPILKAN
                            timeSubuh.setText(t.Fajr);
                            timeDzuhur.setText(t.Dhuhr);
                            timeAshar.setText(t.Asr);
                            timeMaghrib.setText(t.Maghrib);
                            timeIsya.setText(t.Isha);

                            // SIMPAN (CACHE)
                            prefs.edit()
                                    .putString("time_Subuh", t.Fajr)
                                    .putString("time_Dzuhur", t.Dhuhr)
                                    .putString("time_Ashar", t.Asr)
                                    .putString("time_Maghrib", t.Maghrib)
                                    .putString("time_Isya", t.Isha)
                                    .putBoolean("jadwal_ready", true)
                                    .apply();

                        } else {
                            loadCachedTimes("Server AlAdhan bermasalah");
                        }
                    }

                    @Override
                    public void onFailure(Call<PrayerResponse> call, Throwable t) {
                        loadCachedTimes("Internet tidak stabil");
                    }
                });
    }

    // ================= FALLBACK =================
    private void loadCachedTimes(String reason) {
        String subuh   = prefs.getString("time_Subuh", "04:30");
        String dzuhur  = prefs.getString("time_Dzuhur", "12:00");
        String ashar   = prefs.getString("time_Ashar", "15:15");
        String maghrib = prefs.getString("time_Maghrib", "18:00");
        String isya    = prefs.getString("time_Isya", "19:10");

        timeSubuh.setText(subuh);
        timeDzuhur.setText(dzuhur);
        timeAshar.setText(ashar);
        timeMaghrib.setText(maghrib);
        timeIsya.setText(isya);

        prefs.edit().putBoolean("jadwal_ready", true).apply();

        Toast.makeText(
                this,
                reason + ", menggunakan jadwal terakhir",
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(clockRunnable);
    }

    // ================= API =================
    interface AladhanApi {
        @GET("v1/timingsByCity")
        Call<PrayerResponse> getPrayerTimes(
                @Query("city") String city,
                @Query("country") String country,
                @Query("method") int method
        );
    }
}