package com.example.periodicclicker;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ServiceConnection;


public class MainActivity extends AppCompatActivity {
    private ElementsDatabaseHelper elementsDatabaseHelper;
    private MusicManager musicManager;

    private ImageButton playButton;     private ImageButton optionsButton;

    private AudioManager audioManager;     private int maxVolume, currentVolume;     private SharedPreferences sharedPreferences;

    private boolean isBound = false; 
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicManager.MusicBinder binder = (MusicManager.MusicBinder) service;
            musicManager = binder.getService();             isBound = true; 
                        int savedMusicVolume = sharedPreferences.getInt("musicVolume", 50);             float volume = savedMusicVolume / 100f;             musicManager.setVolume(volume);         }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicManager = null;
            isBound = false;         }
    };

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                elementsDatabaseHelper = new ElementsDatabaseHelper(this);
        elementsDatabaseHelper.getWritableDatabase();         if (!isMusicServiceRunning(MusicManager.class)) {
            Intent musicIntent = new Intent(this, MusicManager.class);
            startService(musicIntent);          }

        bindService(new Intent(this, MusicManager.class), musicServiceConnection, Context.BIND_AUTO_CREATE);

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
    }
    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicManager.MusicBinder binder = (MusicManager.MusicBinder) service;
            musicManager = binder.getService();              isBound = true;
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
    protected void onStart() {
        super.onStart();
                Intent startMusicIntent = new Intent(this, MusicManager.class);
        startService(startMusicIntent); 
                bindService(startMusicIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
                if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
            }

    @Override
    protected void onResume() {
        super.onResume();
                if (isBound && musicManager != null) {
            musicManager.setVolume(1);         }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
                if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
                elementsDatabaseHelper.close();
    }
}
