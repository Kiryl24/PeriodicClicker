package com.example.periodicclicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.periodicclicker.R;

public class MainActivity extends AppCompatActivity {
    private ElementsDatabaseHelper elementsDatabaseHelper;
    private PlayerDatabaseHelper playerDatabaseHelper;
    private MusicManager musicManager;

    private ImageButton playButton; // Użyj ImageButton
    private ImageButton optionsButton;

    private AudioManager audioManager; // AudioManager do zarządzania dźwiękiem
    private int maxVolume, currentVolume; // Zmienna do przechowywania poziomu głośności
    private SharedPreferences sharedPreferences;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);

        // Inicjalizacja bazy danych
        elementsDatabaseHelper = new ElementsDatabaseHelper(this);
        playerDatabaseHelper = new PlayerDatabaseHelper(this);
        // Wywołanie getWritableDatabase dla inicjalizacji
        elementsDatabaseHelper.getWritableDatabase();
        playerDatabaseHelper.getWritableDatabase();

        // Inicjalizacja przycisków
        playButton = findViewById(R.id.playButton); // Upewnij się, że te ID są poprawne w pliku layout
        optionsButton = findViewById(R.id.optionsButton);

        // Obsługa kliknięcia przycisku "Play"
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        // Obsługa kliknięcia przycisku "Options"
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });

        // Inicjalizacja MusicManager jako singleton
        musicManager = MusicManager.getInstance(this);

        // Uruchom muzykę
        if (musicManager != null) {
            musicManager.startMusic();

            // Ustaw głośność
            int savedMusicVolume = sharedPreferences.getInt("musicVolume", 50); // Domyślna wartość to 50
            float volume = savedMusicVolume / 100f; // Przekształć do zakresu 0-1
            musicManager.setVolume(volume); // Ustaw głośność na podstawie SharedPreferences
        }

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
        // Zamknij bazy danych
        elementsDatabaseHelper.close();
        playerDatabaseHelper.close();
    }
}
