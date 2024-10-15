package com.example.periodicclicker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log; // Import for logging
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Vibrator;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.periodicclicker.R;

public class GameActivity extends AppCompatActivity {
    private ElementsDatabaseHelper elementsDatabaseHelper;
    private PlayerDatabaseHelper playerDatabaseHelper;
    private Shop shop; // Declare Shop instance
    private Element currentElement; // Currently active element
    private MusicManager musicManager;
    private Vibrator vibrator;
    // Player stats variables
    private PlayerStats playerStats; // Changed to PlayerStats object
    private int purchasedProtons = 0;
    private int purchasedNeutrons = 0; // Starting neutrons
    private TextView protonPriceTextView;
    private TextView neutronPriceTextView;
    private ImageView elementImageView;
    private TextView protonsTextView;
    private TextView neutronsTextView;
    private TextView playerElectronsTextView;
    private TextView nextElementRequirementsTextView; // TextView for next element requirements
    private ImageButton buyProtonButton;
    private ImageButton buyNeutronButton;
    private ImageView optionsGearImageView;
    private ImageView arrowImageView;
    private ImageButton elementImageButton; // Changed from Button to ImageButton
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ; // Last accelerometer readings
    private long lastUpdate; // Last time the sensor was updated
    private static final float SHAKE_THRESHOLD = 800;
    private int playerElectrons;
    private int neutronsNeeded;
    private int protonsNeeded;
    private int atomicNumber = 1;
    private double protonPrice;
    private double neutronPrice;
    private PlayerStats playerStat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game);

        shop = new Shop(this);

        // Initialize the ElementsDatabaseHelper
        elementsDatabaseHelper = new ElementsDatabaseHelper(this);

        // Przykład: Inicjalizowanie aktualnego elementu (np. wodoru)
        currentElement = new Element(1, "Hydrogen", this); // Zmienna w zależności od twojej klasy Element
        elementsDatabaseHelper.setCurrentElement(currentElement);
        Log.d("GameActivity", "Current Element: " + currentElement);

        // Ensure you have a valid element ID
        elementsDatabaseHelper.setCurrentElement(currentElement);
        protonsNeeded = getNextElementProtons();
        neutronsNeeded = getNextElementNeutrons();
        playerDatabaseHelper = new PlayerDatabaseHelper(this);

        // Load player stats from the database
        playerStats = playerDatabaseHelper.loadPlayerStats(); // Changed to use PlayerStats object
        if (playerStats != null) {
            playerElectrons = playerStats.getElectrons();
            purchasedProtons = playerStats.getPurchasedProtons();
            purchasedNeutrons = playerStats.getPurchasedNeutrons();
            atomicNumber = playerStats.getAtomicNumber();
            protonPrice = playerStats.getProtonPrice();
            neutronPrice = playerStats.getNeutronPrice();
        } else {
            Log.d("GameActivity", "Player Stats is null");
            // Set default values if stats not found
            playerStats = new PlayerStats(0, 0, 0, 1, 10.0, 10.0); // Default stats
        }

        // Initialize MusicManager as singleton
        musicManager = MusicManager.getInstance(this); // Ensure we have MusicManager instance
        initializeUIComponents();

        // Initialize accelerometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Start music on activity launch
        if (musicManager != null) {
            musicManager.startMusic(); // Use MusicManager to play music
        }
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Your existing code...
        updateDisplay(); // Call to update the display

        // Call the checkFusion function to see if fusion is possible
        checkFusion();

    }
    private void checkFusion() {
        if (purchasedProtons == 0 && purchasedNeutrons == 0) {
            // Fusion is possible
            Toast.makeText(this, "Fusion is possible!", Toast.LENGTH_SHORT).show();
            vibrate(); // Trigger vibration
        }
    }

    // Method to trigger vibration
    private void vibrate() {
        if (vibrator != null) {
            // Vibrate for 500 milliseconds
            vibrator.vibrate(500);
            Log.d("GameActivity", "Vibration triggered!");
        } else {
            Log.e("GameActivity", "Vibrator not available");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save player stats when the activity is paused
        playerDatabaseHelper.updatePlayerStats(
                playerStats.getElectrons(),
                playerStats.getPurchasedProtons(),
                playerStats.getPurchasedNeutrons(),
                playerStats.getAtomicNumber(),
                playerStats.getProtonPrice(),
                playerStats.getNeutronPrice()
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicManager != null) {
            musicManager.stopMusic();
            musicManager.release();
            musicManager = null;
        }
        // Zamknij bazy danych
        elementsDatabaseHelper.close();
        playerDatabaseHelper.close();

    }

    private void initializeUIComponents() {
        // Initialize UI components
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
        playerElectronsTextView.setText(String.valueOf(playerElectrons));
        protonsTextView.setText(String.valueOf(purchasedProtons));
        neutronsTextView.setText(String.valueOf(purchasedNeutrons));
        protonPriceTextView.setText(String.format("Price: %.2f", protonPrice));
        neutronPriceTextView.setText(String.format("Price: %.2f", neutronPrice));

        // Set initial prices
        protonPriceTextView.setText("Proton Price: " + shop.getProtonPrice());
        neutronPriceTextView.setText("Neutron Price: " + shop.getNeutronPrice());

        // Set listeners for buttons
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
                return; // Prevent action if currentElement is null
            }

            currentElement.checkAndUpdateSprite();
        });

        elementImageButton.setOnClickListener(v -> {
            handleElementClick();
        });

        // Update the initial display state
        updateDisplay();
    }
    public void setPurchasedProtons(int purchasedProtons) {
        this.purchasedProtons = purchasedProtons;
        // Można dodać sprawdzenie limitu, aby upewnić się, że nie są ujemne
        if (this.purchasedProtons < 0) {
            this.purchasedProtons = 0; // Zapobiegaj wartościom ujemnym
        }
    }

    public void setPurchasedNeutrons(int purchasedNeutrons) {
        this.purchasedNeutrons = purchasedNeutrons;
        // Można dodać sprawdzenie limitu, aby upewnić się, że nie są ujemne
        if (this.purchasedNeutrons < 0) {
            this.purchasedNeutrons = 0; // Zapobiegaj wartościom ujemnym
        }
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
        // Decrease button size
        elementImageButton.setScaleX(0.9f);
        elementImageButton.setScaleY(0.9f);
        Log.d("GameActivity", "Player Electrons: " + shop.getPlayerElectron());

        // Increase electron count by current element's atomic number
        int newElectronCount = shop.getPlayerElectrons() + currentElement.getAtomicNumber();
        shop.setPlayerElectron(newElectronCount); // Save new value in Shop
        playerElectrons = newElectronCount; // Update local playerElectrons variable
        updateDisplay();

        // Save player stats every time an element is clicked
        playerDatabaseHelper.updatePlayerStats(playerElectrons, purchasedProtons, purchasedNeutrons, atomicNumber, protonPrice, neutronPrice);


        // Increase electron count by current element's atomic number
        shop.setPlayerElectron(newElectronCount); // Save new value in Shop
        playerElectrons = newElectronCount; // Update local playerElectrons variable
        updateDisplay();

        // Restore size after a short delay
        elementImageButton.postDelayed(() -> {
            elementImageButton.setScaleX(1.0f);
            elementImageButton.setScaleY(1.0f);
        }, 100);
    }

    void updateDisplay() {
        // Update the display with current player and element states
        protonsTextView.setText("Protons: " + (purchasedProtons));
        neutronsTextView.setText("Neutrons: " + purchasedNeutrons);
        playerElectronsTextView.setText("Electrons: " + playerElectrons);
        nextElementRequirementsTextView.setText("Next Element: " +
                Math.max(0, protonsNeeded - purchasedProtons) + "/" +
                Math.max(0, neutronsNeeded - purchasedNeutrons));
        elementImageButton.setImageDrawable(currentElement.getSprite());
        protonPriceTextView.setText("Proton Price: " + shop.getProtonPrice());
        neutronPriceTextView.setText("Neutron Price: " + shop.getNeutronPrice());
    }

    private int getNextElementProtons() {
        int[] nextElementProtonData = elementsDatabaseHelper.getNextElementData();

        if (nextElementProtonData != null && nextElementProtonData.length > 0) {
            int protons = nextElementProtonData[0];
            Log.d("GameActivity", "Next Element Protons: " + protons);
            return protons; // Return protons at index 0
        } else {
            Log.e("GameActivity", "No next element proton data found.");
            return 0; // Or some default value
        }
    }

    private int getNextElementNeutrons() {
        int[] nextElementNeutronData = elementsDatabaseHelper.getNextElementData();
        if (nextElementNeutronData != null && nextElementNeutronData.length > 1) {
            int neutrons = nextElementNeutronData[1];
            Log.d("GameActivity", "Next Element Neutrons: " + neutrons);
            return neutrons; // Return neutrons at index 1
        } else {
            Log.e("GameActivity", "No next element neutron data found.");
            return 0; // Or some default value
        }
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
                        return; // Prevent action if currentElement is null
                    }

                    currentElement.checkAndUpdateSprite();// Shake detection logic// Reset counts after sprite update
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle accuracy changes if needed
        }
    };

    void resetPurchasedCounts() {
        purchasedProtons = 0;
        purchasedNeutrons = 0;
        protonsNeeded = getNextElementProtons();
        neutronsNeeded = getNextElementNeutrons();

        updateDisplay(); // Update display to reflect reset counts
    }
}