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

        // Inicjalizacja MusicManager jako singleton
        musicManager = MusicManager.getInstance(this); // Upewnij się, że mamy instancję MusicManager

        // Uruchom muzykę, jeśli musicManager nie jest null
        if (musicManager != null) {
            musicManager.startMusic(); // Uruchom muzykę
        }

        // Inicjalizacja suwaka głośności muzyki
        musicSeekBar = findViewById(R.id.musicSeekBar);
        int savedMusicVolume = sharedPreferences.getInt("musicVolume", 50); // Domyślna wartość głośności to 50
        musicSeekBar.setProgress(savedMusicVolume);

        // Ustawienie głośności na podstawie suwaka
        float volume = savedMusicVolume / 100f;
        musicManager.setVolume(volume); // Ustawienie głośności dla obu kanałów (lewy i prawy)

        // Inicjalizacja suwaka ogólnego głośności
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        int currentVolume = sharedPreferences.getInt("volume", 50);
        volumeSeekBar.setProgress(currentVolume);

        // Obsługa zmian suwaka głośności ogólnego
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

        // Obsługa zmian suwaka głośności muzyki
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                if (musicManager != null) {
                    musicManager.setVolume(volume); // Zmiana głośności muzyki
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("musicVolume", seekBar.getProgress());
                editor.apply();

                // Ustaw głośność w MusicManager
                float volume = seekBar.getProgress() / 100f; // Zakładam, że volume jest w zakresie 0-100
                if (musicManager != null) {
                    musicManager.setVolume(volume);
                }
            }
        });

        // Inicjalizacja przełącznika wibracji
        vibrationSwitch = findViewById(R.id.vibrationSwitch);
        boolean isVibrationEnabled = sharedPreferences.getBoolean("vibration", true);
        vibrationSwitch.setChecked(isVibrationEnabled);

        vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("vibration", isChecked);
            editor.apply();
        });

        // Przycisk powrotu
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("fromGame", false)) {
                // Wracamy do GameActivity
                Intent intent = new Intent(OptionsActivity.this, GameActivity.class);
                startActivity(intent);
            } else {
                finish(); // Wracamy do MainActivity
            }
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

        // Set positive button (YES)
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GameActivity gameActivity = GameActivity.getInstance();
                if (gameActivity != null) {
                    gameActivity.resetSharedPreferences(); // Reset data in GameActivity
                }
            }
        });

        // Set negative button (NO)
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog without doing anything
            }
        });


        builder.create().show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Zatrzymanie muzyki, jeśli aplikacja przechodzi w tło
        if (musicManager != null) {
            musicManager.stopMusic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Wznowienie muzyki, jeśli aplikacja wraca na pierwszy plan
        if (musicManager != null) {
            musicManager.startMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Zwolnienie zasobów MediaPlayer przy zamknięciu aplikacji
        if (musicManager != null) {
            musicManager.stopMusic();
            musicManager.release();
            musicManager = null;
        }
    }
}
