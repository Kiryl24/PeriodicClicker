package com.example.periodicclicker;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class Shop {
    private Context context;
    private GameActivity gA;
    private MediaPlayer mediaPlayer; // For playing approval or denial sounds
    private float protonPrice = 10;
    private float neutronPrice = 10;
    private int playerElectrons;

    public void setProtonPrice(float protonPrice) {
        this.protonPrice = protonPrice;
    }

    public void setNeutronPrice(float neutronPrice) {
        this.neutronPrice = neutronPrice;
    }



    public Shop(GameActivity paramGameActivity) {
        this.context = paramGameActivity;
        this.gA = paramGameActivity;
        this.playerElectrons = getPlayerElectron();
        // Initialize mediaPlayer for approval sound (set up later for denial)
    }

    private void playSound(int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release any existing instance before creating a new one
        }
        mediaPlayer = MediaPlayer.create(context, soundResourceId);

        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release(); // Release resources after playback
            });
            mediaPlayer.start();
        } else {
            Log.e("playSound", "Nie udało się utworzyć MediaPlayer dla zasobu: " + soundResourceId);
        }
    }

    public int getNeutronPrice() {
        return (int) this.neutronPrice;
    }

    public int getPlayerElectron() {
        return this.playerElectrons;
    }

    public int getPlayerElectrons() {
        return this.playerElectrons;
    }

    public int getProtonPrice() {
        return (int) this.protonPrice;
    }

    public boolean purchaseNeutron() {
        if (playerElectrons >= neutronPrice && gA.getPurchasedNeutrons() < gA.getNeededNeutrons()) {
            playerElectrons -= neutronPrice;
            gA.setPurchasedNeutrons(gA.getPurchasedNeutrons() + 1);
            playSound(R.raw.approval); // Play approval sound
            neutronPrice *= 1.2; // Increase price for the next purchase
            return true;
        }
        playSound(R.raw.denial); // Play denial sound
        return false;
    }

    public boolean purchaseProton() {
        if (playerElectrons >= protonPrice && gA.getPurchasedProtons() < gA.getNeededProtons()) {
            playerElectrons -= protonPrice;
            gA.setPurchasedProtons(gA.getPurchasedProtons() + 1);
            playSound(R.raw.approval); // Play approval sound
            protonPrice *= 1.2; // Increase price for the next purchase
            return true;
        }
        playSound(R.raw.denial); // Play denial sound
        return false;
    }

    public void setPlayerElectron(int paramInt) {
        this.playerElectrons = paramInt;
    }

    // Release MediaPlayer resources
    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
