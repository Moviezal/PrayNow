package com.example.praynow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AdzanReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String sholat = intent.getStringExtra("SHOLAT");
        if (sholat == null) sholat = "Sholat";

        // ==== PLAY ADZAN ====
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.adzan);
        if (mediaPlayer != null) {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        }

        // ==== NOTIFICATION ====
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "adzan_channel",
                    "Notifikasi Adzan",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setSound(null, null); // audio dari MediaPlayer
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, "adzan_channel")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // AMAN
                .setContentTitle("Waktu Sholat")
                .setContentText("Saatnya sholat " + sholat)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        manager.notify(sholat.hashCode(), notification);
    }
}