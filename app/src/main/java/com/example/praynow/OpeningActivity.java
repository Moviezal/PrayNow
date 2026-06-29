package com.example.praynow;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class OpeningActivity extends BaseActivity {

    ImageView imgFlag;
    TextView btnSkip;
    FloatingActionButton btnNext;

    FusedLocationProviderClient fusedClient;
    boolean locationReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        imgFlag = findViewById(R.id.img_flag);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        showSavedFlag();
        requestLocation();

        btnSkip.setOnClickListener(v -> goNext());
        btnNext.setOnClickListener(v -> goNext());
    }

    // ================= NAVIGATION =================
    private void goNext() {
        // ❗ kalau lokasi belum ready, tetap boleh lanjut
        startActivity(new Intent(this, GreetingActivity.class));
        finish();
    }

    // ================= FLAG =================
    private void showSavedFlag() {
        SharedPreferences prefs =
                getSharedPreferences("settings", MODE_PRIVATE);

        int flag = prefs.getInt("flag", R.drawable.ic_idn);
        imgFlag.setImageResource(flag);
    }

    // ================= LOCATION =================
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100
            );
            return;
        }

        LocationRequest request =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY, 0
                )
                        .setMaxUpdates(1)
                        .build();

        fusedClient.requestLocationUpdates(
                request,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(
                            @NonNull LocationResult result) {

                        Location loc = result.getLastLocation();
                        if (loc != null) {
                            saveLanguage(loc);
                        }
                        fusedClient.removeLocationUpdates(this);
                    }
                },
                Looper.getMainLooper()
        );
    }

    // ================= SAVE LANGUAGE =================
    private void saveLanguage(Location loc) {

        boolean isID =
                loc.getLatitude() >= -11 && loc.getLatitude() <= 6 &&
                        loc.getLongitude() >= 95 && loc.getLongitude() <= 141;

        String lang = isID ? "id" : "en";
        int flag = isID ? R.drawable.ic_idn : R.drawable.ic_eng;

        SharedPreferences.Editor ed =
                getSharedPreferences("settings", MODE_PRIVATE).edit();

        ed.putString("lang", lang);
        ed.putInt("flag", flag);
        ed.apply();

        applyLanguage(lang);
        showSavedFlag();

        locationReady = true;
    }

    // ================= APPLY LANGUAGE =================
    private void applyLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        android.content.res.Configuration config =
                new android.content.res.Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(
                config,
                getResources().getDisplayMetrics()
        );
    }

    // ================= PERMISSION =================
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == 100 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
    }
}
