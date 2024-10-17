package com.example.periodicclicker;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
import android.util.Log; import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.periodicclicker.R;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    private final float[] gravity = new float[3];
    private final float[] linearAcceleration = new float[3];
    private static final float ALPHA = 0.8f;
    private ElementsDatabaseHelper elementsDatabaseHelper;
    private Shop shop;     private Element currentElement;
    private MusicManager musicManager;
    private  OptionsActivity optionsActivity;
    private Vibrator vibrator;
        private int purchasedProtons = 0;
    private int purchasedNeutrons = 0;     private TextView protonPriceTextView;
    private TextView neutronPriceTextView;
    private ImageView elementImageView;
    private TextView protonsTextView;
    private TextView neutronsTextView;
    private TextView playerElectronsTextView;
    private TextView nextElementRequirementsTextView;     private ImageButton buyProtonButton;
    private ImageButton buyNeutronButton;
    private ImageView optionsGearImageView;
    private ImageView arrowImageView;
    private ImageButton elementImageButton;     private SensorManager sensorManager;
    private Sensor accelerometer;
    float ax,ay,az;;     private static final float SHAKE_THRESHOLD = 800;
    private int playerElectrons;
    private int neutronsNeeded;
    private int protonsNeeded;
    private int atomicNumber = 1;
    private double protonPrice;
    private double neutronPrice;
    private static final float THRESHOLD = 10.0f;
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 500;
    private boolean isBound = false;
    private static GameActivity instance;

    public static GameActivity getInstance() {
        return instance;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        instance = this;
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("resetPrefs", false)) {
            resetSharedPreferences();
        }
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        shop = new Shop(this);
        Log.d("GameActivity", "Initializing PlayerDatabaseHelper");
        Log.d("GameActivity", "PlayerDatabaseHelper initialized");

        Intent serviceIntent = new Intent(this, MusicManager.class);
        startService(serviceIntent);
        elementsDatabaseHelper = new ElementsDatabaseHelper(this);
        optionsActivity = new OptionsActivity();

                currentElement = new Element(1, "Hydrogen", this, this); 
        elementsDatabaseHelper.setCurrentElement(currentElement);
        Log.d("GameActivity", "Current Element: " + currentElement);

        protonsNeeded = getNextElementProtons();
        neutronsNeeded = getNextElementNeutrons();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        loadSavedData();

        if (!isMusicServiceRunning(MusicManager.class)) {
            Intent musicIntent = new Intent(this, MusicManager.class);
            startService(musicIntent);          }

                bindService(new Intent(this, MusicManager.class), musicServiceConnection, Context.BIND_AUTO_CREATE);

        
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
                        Log.d("Sensor", "Akcelerometr nie jest dostępny.");
        }

        

                 
                initializeUIComponents();
        updateDisplay();


    }
    private boolean isMusicServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void resetSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.clear();
        editor.apply();
        setPurchasedProtons(0);
        setPurchasedNeutrons(0);
        shop.setPlayerElectron(0);
        currentElement.setAtomicNumber(1);         shop.setNeutronPrice(10.0f);         shop.setProtonPrice(10.0f);
                Toast.makeText(this, "Data has been reset", Toast.LENGTH_SHORT).show();
        savePlayerStats();
        updateDisplay();
    }
    private void loadSavedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

                setPurchasedProtons(sharedPreferences.getInt("protons", 0));
        setPurchasedNeutrons(sharedPreferences.getInt("neutrons", 0));
        shop.setPlayerElectron(sharedPreferences.getInt("electrons", 0));
        currentElement.setAtomicNumber(sharedPreferences.getInt("atomicNumber", 1));         shop.setNeutronPrice(sharedPreferences.getFloat("neutronPrice", 10.0f));         shop.setProtonPrice(sharedPreferences.getFloat("protonPrice", 10.0f)); 
        Log.d("GameActivity", "Loaded Data - Protons: " + purchasedProtons + ", Neutrons: " + purchasedNeutrons +
                ", Electrons: " + playerElectrons + ", Atomic Number: " + atomicNumber);
    }
    private void checkFusion() {
        if (purchasedProtons == 0 && purchasedNeutrons == 0) {
                                 }
    }

        private void savePlayerStats() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("protons", getPurchasedProtons());
        editor.putInt("neutrons", getPurchasedNeutrons());
        editor.putInt("electrons", shop.getPlayerElectrons());
        editor.putInt("atomicNumber", currentElement.getAtomicNumber());
        editor.putFloat("protonPrice", shop.getProtonPrice());
        editor.putFloat("neutronPrice", shop.getNeutronPrice());
        editor.apply();     }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MyActivity", "onStop() called");
        if (isBound) {
            unbindService(musicServiceConnection);
            isBound = false;
        }
        Intent serviceIntent = new Intent(this, MusicManager.class);
        stopService(serviceIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(musicServiceConnection);
            isBound = false;
        }
                elementsDatabaseHelper.close();
        savePlayerStats();
        Intent serviceIntent = new Intent(this, MusicManager.class);
        sensorManager.unregisterListener(this);
        stopService(serviceIntent);
    }

    @SuppressLint("SetTextI18n")
    private void initializeUIComponents() {
        protonsTextView = findViewById(R.id.protonsTextView);
        neutronsTextView = findViewById(R.id.neutronsTextView);
        playerElectronsTextView = findViewById(R.id.playerElectronsTextView);
        nextElementRequirementsTextView = findViewById(R.id.nextElementRequirementsTextView);
        buyProtonButton = findViewById(R.id.buyProtonButton);
        buyNeutronButton = findViewById(R.id.buyNeutronButton);
        optionsGearImageView = findViewById(R.id.optionsGearImageView);
        elementImageView = findViewById(R.id.elementImageView);
        elementImageButton = findViewById(R.id.hiddenButton);
        protonPriceTextView = findViewById(R.id.protonPriceTextView);
        neutronPriceTextView = findViewById(R.id.neutronPriceTextView);
        arrowImageView = findViewById(R.id.arrowImageView);

                playerElectronsTextView.setText(String.valueOf(playerElectrons));         protonsTextView.setText(String.valueOf(purchasedProtons));               neutronsTextView.setText(String.valueOf(purchasedNeutrons));     
                protonPriceTextView.setText("Proton Price: " + shop.getProtonPrice());         neutronPriceTextView.setText("Neutron Price: " + shop.getNeutronPrice());         updateDisplay();
                buyProtonButton.setOnClickListener(v -> {
            shop.purchaseProton();
            playerElectrons = shop.getPlayerElectrons();
            updateDisplay();
        });


        buyNeutronButton.setOnClickListener(v -> {
            shop.purchaseNeutron();
            playerElectrons = shop.getPlayerElectrons();
            updateDisplay();
        });

        optionsGearImageView.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, OptionsActivity.class);
            intent.putExtra("fromGame", true);
            startActivity(intent);
        });

        arrowImageView.setOnClickListener(v -> {
            destroy();
            
        });

        elementImageButton.setOnClickListener(v -> {
            handleElementClick();
        });

                updateDisplay();
    }
    public void setPurchasedProtons(int purchasedProtons) {
        this.purchasedProtons = purchasedProtons;
                if (this.purchasedProtons < 0) {
            this.purchasedProtons = 0;         }
    }

    public void setPurchasedNeutrons(int purchasedNeutrons) {
        this.purchasedNeutrons = purchasedNeutrons;
                if (this.purchasedNeutrons < 0) {
            this.purchasedNeutrons = 0;         }
    }
    public void destroy() {
        sensorManager.unregisterListener(this);
        Intent serviceIntent = new Intent(this, MusicManager.class);
        stopService(serviceIntent);
        finishAffinity();
    }

    public int getPurchasedProtons() {
        return purchasedProtons;
    }

    public int getPurchasedNeutrons() {
        return purchasedNeutrons;
    }
    public int getNeededProtons() {
        return getNextElementProtons();

    }
    public int getNeededNeutrons() {
        return getNextElementNeutrons();

    }

    private void handleElementClick() {
                elementImageButton.setScaleX(0.9f);
        elementImageButton.setScaleY(0.9f);
        Log.d("GameActivity", "Player Electrons: " + shop.getPlayerElectron());

                int newElectronCount = shop.getPlayerElectrons() + currentElement.getAtomicNumber();
        shop.setPlayerElectron(newElectronCount);         
                shop.setPlayerElectron(newElectronCount);         playerElectrons = newElectronCount;         updateDisplay();


                elementImageButton.postDelayed(() -> {
            elementImageButton.setScaleX(1.0f);
            elementImageButton.setScaleY(1.0f);
        }, 100);
    }

    @SuppressLint("SetTextI18n")
    void updateDisplay() {
                protonsTextView.setText("Protons: " + (purchasedProtons));
        neutronsTextView.setText("Neutrons: " + purchasedNeutrons);
        playerElectronsTextView.setText("Electrons: " + shop.getPlayerElectrons());
        nextElementRequirementsTextView.setText("Next Element: " +
                Math.max(0, getNeededProtons() - getPurchasedProtons()) + "/" +
                Math.max(0, getNeededNeutrons() - getPurchasedNeutrons()));
        elementImageButton.setImageDrawable(currentElement.getSprite());
        protonPriceTextView.setText("Proton Price: " + shop.getProtonPrice());
        neutronPriceTextView.setText("Neutron Price: " + shop.getNeutronPrice());
        savePlayerStats();
    }

    private int getNextElementProtons() {
        int[] nextElementProtonData = elementsDatabaseHelper.getNextElementData();

        if (nextElementProtonData != null && nextElementProtonData.length > 0) {
            int protons = nextElementProtonData[0];
            Log.d("GameActivity", "Next Element Protons: " + protons);
            return protons;         } else {
            Log.e("GameActivity", "No next element proton data found.");
            return 0;         }
    }
    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (isVibrationEnabled()) {             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                Log.d("Game Activity", "Vibrate on uppers sdk");
            } else {
                                v.vibrate(500);
                Log.d("Game Activity", "Vibrate on lower sdk");
            }
        } else {
            Log.d("Game Activity", "Vibration is turned off sdk");
        }
    }

    private boolean isVibrationEnabled() {
        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        return sharedPreferences.getBoolean("vibration", true);     }

    private int getNextElementNeutrons() {
        int[] nextElementNeutronData = elementsDatabaseHelper.getNextElementData();
        if (nextElementNeutronData != null && nextElementNeutronData.length > 1) {
            int neutrons = nextElementNeutronData[1];
            Log.d("GameActivity", "Next Element Neutrons: " + neutrons);
            return neutrons;         } else {
            Log.e("GameActivity", "No next element neutron data found.");
            return 0;         }
    }




    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis(); 
                        if ((currentTime - lastUpdate) > UPDATE_INTERVAL) {
                lastUpdate = currentTime; 
                                float rawX = event.values[0];
                float rawY = event.values[1];
                float rawZ = event.values[2];

                                gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * rawX;
                gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * rawY;
                gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * rawZ;

                                linearAcceleration[0] = rawX - gravity[0];
                linearAcceleration[1] = rawY - gravity[1];
                linearAcceleration[2] = rawZ - gravity[2];

                                double totalAcceleration = Math.sqrt(Math.pow(linearAcceleration[0], 2) +
                        Math.pow(linearAcceleration[1], 2) +
                        Math.pow(linearAcceleration[2], 2));

                                if (totalAcceleration > THRESHOLD) {                                         Log.d("SensorEvent", "Przyspieszenie bez grawitacji - X: " + linearAcceleration[0]
                            + ", Y: " + linearAcceleration[1] + ", Z: " + linearAcceleration[2]);

                                        ax = linearAcceleration[0];
                    ay = linearAcceleration[1];
                    az = linearAcceleration[2];

                    Log.d("SensorEvent", "Aktualizacja checkAndUpdateSprite() oraz updateDisplay()");
                    currentElement.checkAndUpdateSprite();
                    updateDisplay();
                } else {
                                        Log.d("SensorEvent", "Zmiana poniżej progu. Ignorowane.");
                }
            }
        }
    }

    void resetPurchasedCounts() {
        purchasedProtons = 0;
        purchasedNeutrons = 0;
        protonsNeeded = getNextElementProtons();
        neutronsNeeded = getNextElementNeutrons();
        vibrate();

        updateDisplay();     }

}