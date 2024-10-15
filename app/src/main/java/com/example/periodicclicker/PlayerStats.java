package com.example.periodicclicker;

import android.util.Log;

public class PlayerStats {
    private int electrons;
    private int purchasedProtons;
    private int purchasedNeutrons;
    private int atomicNumber;
    private double protonPrice;
    private double neutronPrice;

    // Konstruktor
    public PlayerStats(int electrons, int purchasedProtons, int purchasedNeutrons,
                       int atomicNumber, double protonPrice, double neutronPrice) {
        this.electrons = electrons;
        this.purchasedProtons = purchasedProtons;
        this.purchasedNeutrons = purchasedNeutrons;
        this.atomicNumber = atomicNumber;
        this.protonPrice = protonPrice;
        this.neutronPrice = neutronPrice;
    }

    // Gettery
    public int getElectrons() {
        return electrons;
    }

    public int getPurchasedProtons() {
        return purchasedProtons;
    }

    public int getPurchasedNeutrons() {
        return purchasedNeutrons;
    }

    public int getAtomicNumber() {
        return atomicNumber;
    }

    public double getProtonPrice() {
        return protonPrice;
    }

    public double getNeutronPrice() {
        return neutronPrice;
    }

    // Settery
    public void setElectrons(int electrons) {
        if (electrons < 0) {
            Log.e("PlayerStats", "Attempted to set electrons to a negative value: " + electrons);
            return;
        }
        this.electrons = electrons;
    }

    public void setPurchasedProtons(int purchasedProtons) {
        if (purchasedProtons < 0) {
            Log.e("PlayerStats", "Attempted to set purchasedProtons to a negative value: " + purchasedProtons);
            return;
        }
        this.purchasedProtons = purchasedProtons;
    }

    public void setPurchasedNeutrons(int purchasedNeutrons) {
        if (purchasedNeutrons < 0) {
            Log.e("PlayerStats", "Attempted to set purchasedNeutrons to a negative value: " + purchasedNeutrons);
            return;
        }
        this.purchasedNeutrons = purchasedNeutrons;
    }

    public void setAtomicNumber(int atomicNumber) {
        if (atomicNumber < 1) {
            Log.e("PlayerStats", "Attempted to set atomicNumber to an invalid value: " + atomicNumber);
            return;
        }
        this.atomicNumber = atomicNumber;
    }

    public void setProtonPrice(double protonPrice) {
        if (protonPrice < 0) {
            Log.e("PlayerStats", "Attempted to set protonPrice to a negative value: " + protonPrice);
            return;
        }
        this.protonPrice = protonPrice;
    }

    public void setNeutronPrice(double neutronPrice) {
        if (neutronPrice < 0) {
            Log.e("PlayerStats", "Attempted to set neutronPrice to a negative value: " + neutronPrice);
            return;
        }
        this.neutronPrice = neutronPrice;
    }
}
