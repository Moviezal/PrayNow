package com.example.praynow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            SharedPreferences prefs =
                    context.getSharedPreferences("PrayNowPrefs", Context.MODE_PRIVATE);

            // Di sini nanti kita panggil ulang alarm
            // (sementara kosong dulu, supaya ERROR HILANG)
        }
    }
}
