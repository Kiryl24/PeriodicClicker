package com.example.periodicclicker;

import android.content.Context;
import android.media.MediaPlayer;
import com.example.periodicclicker.R;

public class MusicManager {
    private static MusicManager instance;
    private MediaPlayer mediaPlayer;

        private MusicManager(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.soundtrack);
        mediaPlayer.setLooping(true);
    }

        public static MusicManager getInstance(Context context) {
        if (instance == null) {
            instance = new MusicManager(context.getApplicationContext());         }
        return instance;
    }

    public void startMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
