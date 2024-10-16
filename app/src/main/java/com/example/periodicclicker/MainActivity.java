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
    private MusicManager musicManager;

    private ImageButton playButton;     private ImageButton optionsButton;

    private AudioManager audioManager;     private int maxVolume, currentVolume;     private SharedPreferences sharedPreferences;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);

                elementsDatabaseHelper = new ElementsDatabaseHelper(this);

                elementsDatabaseHelper.getWritableDatabase();


                playButton = findViewById(R.id.playButton);         optionsButton = findViewById(R.id.optionsButton);

                playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

                optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });

                musicManager = MusicManager.getInstance(this);

                if (musicManager != null) {
            musicManager.startMusic();

                        int savedMusicVolume = sharedPreferences.getInt("musicVolume", 50);             float volume = savedMusicVolume / 100f;             musicManager.setVolume(volume);         }

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
                elementsDatabaseHelper.close();
    }
}
