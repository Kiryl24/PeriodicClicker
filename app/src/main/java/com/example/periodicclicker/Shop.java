package com.example.periodicclicker;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import com.example.periodicclicker.R;

public class Shop {
    private Context context;

    private GameActivity gA;

    private MediaPlayer mediaPlayer;

    private int neutronPrice = 10;

    private int playerElectrons;

    private int protonPrice = 10;

    public Shop(GameActivity paramGameActivity) {
        this.context = (Context)paramGameActivity;
        this.gA = paramGameActivity;
        this.playerElectrons = getPlayerElectron();
    }

    private void playSound(int soundResourceId) {

        MediaPlayer mediaPlayer = MediaPlayer.create(context, soundResourceId);


        if (mediaPlayer != null) {

            mediaPlayer.setOnCompletionListener(mp -> {

                mp.release();
            });


            mediaPlayer.start();

            Log.e("playSound", "Nie udało się utworzyć MediaPlayer dla zasobu: " + soundResourceId);
        }
    }


    public int getNeutronPrice() {
        return this.neutronPrice;
    }

    public int getPlayerElectron() {
        return this.playerElectrons;
    }

    public int getPlayerElectrons() {
        return this.playerElectrons;
    }

    public int getProtonPrice() {
        return this.protonPrice;
    }

    public boolean purchaseNeutron() {
        int i = this.playerElectrons;
        int j = this.neutronPrice;
        if (i >= j) {
            this.playerElectrons = i - j;
            GameActivity gameActivity = this.gA;
            gameActivity.setPurchasedNeutrons(gameActivity.getPurchasedNeutrons() + 1);
            playSound(R.raw.approval);
            this.neutronPrice *= 1.2;
            return true;
        }
        playSound(R.raw.denial);
        return false;
    }

    public boolean purchaseProton() {
        int i = this.playerElectrons;
        int j = this.protonPrice;
        if (i >= j) {
            this.playerElectrons = i - j;
            GameActivity gameActivity = this.gA;
            gameActivity.setPurchasedProtons(gameActivity.getPurchasedProtons() + 1);
            playSound(R.raw.approval);
            this.protonPrice *= 1.2;
            return true;
        }
        playSound(R.raw.denial);
        return false;
    }

    public void setPlayerElectron(int paramInt) {
        this.playerElectrons = paramInt;
    }
}
