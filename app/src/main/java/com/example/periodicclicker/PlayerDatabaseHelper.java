package com.example.periodicclicker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlayerDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "player.db";
    private static final int DATABASE_VERSION = 9;
    private static final String TABLE_PLAYER = "player";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ELECTRONS = "electrons";
    private static final String COLUMN_ATOMIC_NUMBER = "atomicNumber";
    private static final String COLUMN_PURCHASED_PROTONS = "purchased_protons";
    private static final String COLUMN_PURCHASED_NEUTRONS = "purchased_neutrons";
    private static final String COLUMN_PROTON_PRICE = "proton_price";
    private static final String COLUMN_NEUTRON_PRICE = "neutron_price";

    public PlayerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PLAYER + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ELECTRONS + " INTEGER," +
                COLUMN_PURCHASED_PROTONS + " INTEGER DEFAULT 0," +
                COLUMN_PURCHASED_NEUTRONS + " INTEGER DEFAULT 0," +
                COLUMN_PROTON_PRICE + " REAL DEFAULT 0.0," +
                COLUMN_NEUTRON_PRICE + " REAL DEFAULT 0.0," +
                COLUMN_ATOMIC_NUMBER + " INTEGER DEFAULT 1" +
                ")";
        db.execSQL(createTable);

        // Insert default player stats
        ContentValues values = new ContentValues();
        values.put(COLUMN_ELECTRONS, 0);
        values.put(COLUMN_PURCHASED_PROTONS, 0);
        values.put(COLUMN_PURCHASED_NEUTRONS, 0);
        values.put(COLUMN_PROTON_PRICE, 1.0); // Default price
        values.put(COLUMN_NEUTRON_PRICE, 1.0); // Default price
        values.put(COLUMN_ATOMIC_NUMBER, 1); // Starting atomic number
        db.insert(TABLE_PLAYER, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
        onCreate(db);
    }

    public void updatePlayerStats(int electrons, int purchasedProtons, int purchasedNeutrons, int atomicNumber, double protonPrice, double neutronPrice) {
        Log.d("PlayerDatabaseHelper", "Updating player stats");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ELECTRONS, electrons);
        values.put(COLUMN_PURCHASED_PROTONS, purchasedProtons);
        values.put(COLUMN_PURCHASED_NEUTRONS, purchasedNeutrons);
        values.put(COLUMN_ATOMIC_NUMBER, atomicNumber);
        values.put(COLUMN_PROTON_PRICE, protonPrice);
        values.put(COLUMN_NEUTRON_PRICE, neutronPrice);

        // Check if there is at least one player entry
        int updatedRows = db.update(TABLE_PLAYER, values, COLUMN_ID + " = ?", new String[]{"1"});
        if (updatedRows == 0) {
            Log.e("PlayerDatabaseHelper", "Update failed: No rows affected. Ensure the player exists.");
        } else {
            Log.d("PlayerDatabaseHelper", "Player stats updated successfully.");
        }
        db.close();
    }

    public PlayerStats loadPlayerStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PLAYER + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        PlayerStats stats = null;

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int electrons = cursor.getInt(cursor.getColumnIndex(COLUMN_ELECTRONS));
            @SuppressLint("Range") int purchasedProtons = cursor.getInt(cursor.getColumnIndex(COLUMN_PURCHASED_PROTONS));
            @SuppressLint("Range") int purchasedNeutrons = cursor.getInt(cursor.getColumnIndex(COLUMN_PURCHASED_NEUTRONS));
            @SuppressLint("Range") int atomic_Number = cursor.getInt(cursor.getColumnIndex(COLUMN_ATOMIC_NUMBER));
            @SuppressLint("Range") double protonPrice = cursor.getDouble(cursor.getColumnIndex(COLUMN_PROTON_PRICE));
            @SuppressLint("Range") double neutronPrice = cursor.getDouble(cursor.getColumnIndex(COLUMN_NEUTRON_PRICE));

            stats = new PlayerStats(electrons, purchasedProtons, purchasedNeutrons, atomic_Number, protonPrice, neutronPrice);
            Log.d("PlayerStats", "Loaded: " + electrons + ", " + purchasedProtons + ", " + purchasedNeutrons + ", " + atomic_Number + ", " + protonPrice + ", " + neutronPrice);
        } else {
            Log.e("PlayerStats", "No data found");
        }

        cursor.close();
        db.close();
        return stats;
    }
}
