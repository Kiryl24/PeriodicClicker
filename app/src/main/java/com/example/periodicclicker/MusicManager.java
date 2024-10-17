package com.example.periodicclicker;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

public class MusicManager extends Service {
    private MediaPlayer mediaPlayer;
    private int currentTrack = 1;
    private final IBinder binder = new MusicBinder();
        public class MusicBinder extends Binder {
        MusicManager getService() {
            return MusicManager.this;         }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;     }

    @Override
    public void onCreate() {
        super.onCreate();
                mediaPlayer = MediaPlayer.create(this, R.raw.soundtrackclassic);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        Log.d("MusicManager", "Service created and started playing electronic music");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("track")) {
            int track = intent.getIntExtra("track", 1);             playTrack(track);
        }
        if (intent != null && intent.hasExtra("volume")) {
            float volume = intent.getFloatExtra("volume", 1.0f);
            setVolume(volume);
        }
        return START_STICKY;     }

    public void playTrack(int track) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); 
                if (track == 0) {
                mediaPlayer = MediaPlayer.create(this, R.raw.soundtrack);
                Log.d("MusicManager", "Playing electronic music");
            } else if (track == 1) {
                mediaPlayer = MediaPlayer.create(this, R.raw.soundtrackclassic);
                Log.d("MusicManager", "Playing classical music");
            }

            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            currentTrack = track;         }
    }

    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // Zatrzymaj muzykę po usunięciu zadania (zamknięciu aplikacji)
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        stopSelf();  // Zatrzymaj serwis
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();         }
        super.onDestroy();
        Log.d("MusicManager", "Service destroyed");
    }
}
