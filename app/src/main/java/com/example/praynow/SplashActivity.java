package com.example.praynow;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000;
    private static final int REQ_NOTIFICATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imgPrayNow = findViewById(R.id.image_text_praynow);
        ImageView imgBulan   = findViewById(R.id.image_icon_moon);

        Animation animText = AnimationUtils.loadAnimation(this, R.anim.slide_left_to_center);
        Animation animMoon = AnimationUtils.loadAnimation(this, R.anim.drop_from_top);

        imgPrayNow.startAnimation(animText);
        imgBulan.startAnimation(animMoon);

        // 🔔 REQUEST NOTIFICATION PERMISSION (ANDROID 13+)
        requestNotificationPermission();

        new Handler().postDelayed(() -> {
            // langsung ke OpeningActivity
            startActivity(new Intent(SplashActivity.this, OpeningActivity.class));
            finish();
        }, SPLASH_DURATION);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQ_NOTIFICATION
                );
            }
        }
    }
}
