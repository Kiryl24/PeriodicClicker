package com.example.periodicclicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.periodicclicker.R;

public class OptionsActivity extends AppCompatActivity {
    private SeekBar musicSeekBar;
    private SeekBar volumeSeekBar;
    private Switch vibrationSwitch;
    private SharedPreferences sharedPreferences;
    private AudioManager audioManager;
    private ImageButton backButton;
    private MusicManager musicManager;
    private GameActivity gameActivity;
    private ImageButton resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        GameActivity gameActivity = new GameActivity();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);

                musicManager = MusicManager.getInstance(this); 
                if (musicManager != null) {
            musicManager.startMusic();         }

                musicSeekBar = findViewById(R.id.musicSeekBar);
        int savedMusicVolume = sharedPreferences.getInt("musicVolume", 50);         musicSeekBar.setProgress(savedMusicVolume);

                float volume = savedMusicVolume / 100f;
        musicManager.setVolume(volume); 
                volumeSeekBar = findViewById(R.id.volumeSeekBar);
        int currentVolume = sharedPreferences.getInt("volume", 50);
        volumeSeekBar.setProgress(currentVolume);

                volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("volume", seekBar.getProgress());
                editor.apply();
            }
        });

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

                                float volume = seekBar.getProgress() / 100f;                 if (musicManager != null) {
                    musicManager.setVolume(volume);
                }
            }
        });

                vibrationSwitch = findViewById(R.id.vibrationSwitch);
        boolean isVibrationEnabled = sharedPreferences.getBoolean("vibration", true);
        vibrationSwitch.setChecked(isVibrationEnabled);

        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("vibration", isChecked);
            editor.apply();
        });

                backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("fromGame", false)) {
                                Intent intent = new Intent(OptionsActivity.this, GameActivity.class);
                startActivity(intent);
            } else {
                finish();             }
        });
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> {
            showResetConfirmationDialog();

        });

    }
    private void showResetConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Progress");
        builder.setMessage("Do you really want to erase your progress?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GameActivity gameActivity = GameActivity.getInstance();
                if (gameActivity != null) {
                    gameActivity.resetSharedPreferences();                 }
            }
        });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();             }
        });


        builder.create().show();
    }
    @Override
    protected void onPause() {
        super.onPause();
                if (musicManager != null) {
            musicManager.stopMusic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
                if (musicManager != null) {
            musicManager.startMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
                if (musicManager != null) {
            musicManager.stopMusic();
            musicManager.release();
            musicManager = null;
        }
    }
}
