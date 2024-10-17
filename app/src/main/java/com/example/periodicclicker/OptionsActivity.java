package com.example.periodicclicker;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {
    private SeekBar musicSeekBar;
    private Switch vibrationSwitch;
    private SharedPreferences sharedPreferences;
    private AudioManager audioManager;
    private ImageButton backButton;
    private MusicManager musicManager;
    private boolean isBound = false;

    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicManager.MusicBinder binder = (MusicManager.MusicBinder) service;
            musicManager = binder.getService();
            isBound = true;

                        int savedMusicVolume = sharedPreferences.getInt("musicVolume", 50);
            musicSeekBar.setProgress(savedMusicVolume);
            float volume = savedMusicVolume / 100f;
            musicManager.setVolume(volume);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
    private boolean isMusicServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_options);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                if (!isMusicServiceRunning(MusicManager.class)) {
            Intent musicIntent = new Intent(this, MusicManager.class);
            startService(musicIntent);          }

        bindService(new Intent(this, MusicManager.class), musicServiceConnection, Context.BIND_AUTO_CREATE);

                Button classicalMusicButton = findViewById(R.id.classicalMusicButton);
        Button electronicMusicButton = findViewById(R.id.electronicMusicButton);

        classicalMusicButton.setOnClickListener(v -> changeMusic(1));         electronicMusicButton.setOnClickListener(v -> changeMusic(0)); 
        

                musicSeekBar = findViewById(R.id.musicSeekBar);
        setupMusicSeekBar();

                vibrationSwitch = findViewById(R.id.vibrationSwitch);
        setupVibrationSwitch();

                backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

                ImageButton resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> showResetConfirmationDialog());
    }

    private void changeMusic(int track) {
        Intent intent = new Intent(this, MusicManager.class);
        intent.putExtra("track", track);
        startService(intent);
    }


    private void setupMusicSeekBar() {
        int savedMusicVolume = sharedPreferences.getInt("musicVolume", 50);
        musicSeekBar.setMax(100);
        musicSeekBar.setProgress(savedMusicVolume);

        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                if (musicManager != null) {
                    musicManager.setVolume(volume);                 }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("musicVolume", seekBar.getProgress());
                editor.apply();

                float volume = seekBar.getProgress() / 100f;
                if (musicManager != null) {
                    musicManager.setVolume(volume);
                }
            }
        });
    }

    private void setupVibrationSwitch() {
        boolean isVibrationEnabled = sharedPreferences.getBoolean("vibration", true);
        vibrationSwitch.setChecked(isVibrationEnabled);

        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("vibration", isChecked);
            editor.apply();
        });
    }

    private void showResetConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Progress");
        builder.setMessage("Do you really want to erase your progress?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            GameActivity gameActivity = GameActivity.getInstance();
            if (gameActivity != null) {
                gameActivity.resetSharedPreferences();             }
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound) {
            unbindService(musicServiceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent musicIntent = new Intent(this, MusicManager.class);
        bindService(musicIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(musicServiceConnection);
            isBound = false;
        }
    }
}
