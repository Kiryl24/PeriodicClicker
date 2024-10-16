package com.example.periodicclicker;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class Shop {
    private Context context;
    private GameActivity gA;
    private MediaPlayer mediaPlayer;     private float protonPrice = 10;
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
            }

    private void playSound(int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();         }
        mediaPlayer = MediaPlayer.create(context, soundResourceId);

        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();             });
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
            playSound(R.raw.approval);             neutronPrice *= 1.2;             return true;
        }
        playSound(R.raw.denial);         return false;
    }

    public boolean purchaseProton() {
        if (playerElectrons >= protonPrice && gA.getPurchasedProtons() < gA.getNeededProtons()) {
            playerElectrons -= protonPrice;
            gA.setPurchasedProtons(gA.getPurchasedProtons() + 1);
            playSound(R.raw.approval);             protonPrice *= 1.2;             return true;
        }
        playSound(R.raw.denial);         return false;
    }

    public void setPlayerElectron(int paramInt) {
        this.playerElectrons = paramInt;
    }

        public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
