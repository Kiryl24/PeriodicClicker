package com.example.periodicclicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log; import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Vibrator;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.periodicclicker.R;

public class GameActivity extends AppCompatActivity {
    private ElementsDatabaseHelper elementsDatabaseHelper;
    private Shop shop;     private Element currentElement;     private MusicManager musicManager;
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
    private float lastX, lastY, lastZ;     private long lastUpdate;     private static final float SHAKE_THRESHOLD = 800;
    private int playerElectrons;
    private int neutronsNeeded;
    private int protonsNeeded;
    private int atomicNumber = 1;
    private double protonPrice;
    private double neutronPrice;

    private static GameActivity instance;

    public static GameActivity getInstance() {
        return instance;
    }

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

        shop = new Shop(this);
        Log.d("GameActivity", "Initializing PlayerDatabaseHelper");
        Log.d("GameActivity", "PlayerDatabaseHelper initialized");


                elementsDatabaseHelper = new ElementsDatabaseHelper(this);

                currentElement = new Element(1, "Hydrogen", this, this); 
        elementsDatabaseHelper.setCurrentElement(currentElement);
        Log.d("GameActivity", "Current Element: " + currentElement);

        protonsNeeded = getNextElementProtons();
        neutronsNeeded = getNextElementNeutrons();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        loadSavedData();


                musicManager = MusicManager.getInstance(this); 
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                if (musicManager != null) {
            musicManager.startMusic();         }
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                 
                initializeUIComponents();
        updateDisplay();


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
                        vibrate();         }
    }

        private void vibrate() {
        if (vibrator != null) {
                        vibrator.vibrate(500);
            Log.d("GameActivity", "Vibration triggered!");
        } else {
            Log.e("GameActivity", "Vibrator not available");
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
    protected void onPause() {
        super.onPause();
        savePlayerStats();
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
        savePlayerStats();

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
            if (currentElement == null) {
                Log.e("GameActivity", "Current element is null. Cannot update sprite.");
                return;             }

            currentElement.checkAndUpdateSprite();
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
        shop.setPlayerElectron(newElectronCount);         updateDisplay();
        
                shop.setPlayerElectron(newElectronCount);         playerElectrons = newElectronCount;         updateDisplay();
        savePlayerStats();

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

    private int getNextElementNeutrons() {
        int[] nextElementNeutronData = elementsDatabaseHelper.getNextElementData();
        if (nextElementNeutronData != null && nextElementNeutronData.length > 1) {
            int neutrons = nextElementNeutronData[1];
            Log.d("GameActivity", "Next Element Neutrons: " + neutrons);
            return neutrons;         } else {
            Log.e("GameActivity", "No next element neutron data found.");
            return 0;         }
    }




    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastUpdate) > 100) {
                long diffTime = (currentTime - lastUpdate);
                lastUpdate = currentTime;

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    currentElement.checkAndUpdateSprite();
                    if (currentElement == null) {
                        Log.e("GameActivity", "Current element is null. Cannot update sprite.");
                        return;                     }

                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    }
    };

    void resetPurchasedCounts() {
        purchasedProtons = 0;
        purchasedNeutrons = 0;
        protonsNeeded = getNextElementProtons();
        neutronsNeeded = getNextElementNeutrons();

        updateDisplay();     }
}