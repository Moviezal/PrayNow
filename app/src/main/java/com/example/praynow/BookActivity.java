package com.example.praynow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class BookActivity extends BaseActivity {

    ImageView btnBack;
    ImageView btnRukunIslam, btnRukunIman, btnIhsan, btnPilarAmal;
    ImageView navAlarm, navHome, navAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        initView();
        setupClick();
    }

    private void initView() {
        btnBack = findViewById(R.id.btnBack);

        btnRukunIslam = findViewById(R.id.btnRukunIslam);
        btnRukunIman  = findViewById(R.id.btnRukunIman);
        btnIhsan      = findViewById(R.id.btnIhsan);
        btnPilarAmal  = findViewById(R.id.btnPilarAmal);

        navAlarm = findViewById(R.id.navAlarm);
        navHome  = findViewById(R.id.navHome);
        navAdd   = findViewById(R.id.navAdd);
    }

    private void setupClick() {
        // BACK
        btnBack.setOnClickListener(v -> finish());

        // MENU BUKU
        btnRukunIslam.setOnClickListener(v ->
                startActivity(new Intent(this, IslamActivity.class))
        );
        btnRukunIman.setOnClickListener(v ->
                startActivity(new Intent(this, ImanActivity.class))
        );
        btnIhsan.setOnClickListener(v ->
                startActivity(new Intent(this, IhsanActivity.class))
        );
        btnPilarAmal.setOnClickListener(v ->
                startActivity(new Intent(this, PilarActivity.class))
        );

        // BOTTOM NAV
        navAlarm.setOnClickListener(v ->
                startActivity(new Intent(this, AlarmActivity.class))
        );

        navHome.setOnClickListener(v ->
                startActivity(new Intent(this, SholatActivity.class))
        );

        navAdd.setOnClickListener(v ->
                Toast.makeText(this, "Sudah di halaman Buku", Toast.LENGTH_SHORT).show()
        );
    }
}
