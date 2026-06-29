package com.example.praynow;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GreetingActivity extends BaseActivity {

    private FloatingActionButton btnMulai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        btnMulai = findViewById(R.id.btn_mulai);

        btnMulai.setOnClickListener(v -> {
            startActivity(new Intent(this, SholatActivity.class));
            finish();
        });
    }
}
