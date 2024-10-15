package com.example.periodicclicker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ElementsDatabaseHelper extends SQLiteOpenHelper {
    private static final String COLUMN_ID = "atomic_number";


    private static final String COLUMN_NAME = "element";

    private static final String COLUMN_NEUTRONS = "neutrons";

    private static final String COLUMN_PROTONS = "protons";

    private static final String COLUMN_SYMBOL = "symbol";

    private static final String CREATE_TABLE_ELEMENTS = "CREATE TABLE elements (atomic_number INTEGER PRIMARY KEY, symbol TEXT, element TEXT, protons INTEGER, neutrons INTEGER);";

    private static final String DATABASE_NAME = "elements.db";

    private static final int DATABASE_VERSION = 2;
    private Element currentElement;

    private static final String TABLE_NAME = "elements" ;

    public ElementsDatabaseHelper(Context paramContext) {
        super(paramContext, "elements.db", null, 2);
    }

    private void insertElement(SQLiteDatabase paramSQLiteDatabase, int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("atomic_number", Integer.valueOf(paramInt1));
        contentValues.put("symbol", paramString1);
        contentValues.put("element", paramString2);
        contentValues.put("protons", Integer.valueOf(paramInt2));
        contentValues.put("neutrons", Integer.valueOf(paramInt3));
        paramSQLiteDatabase.insert("elements", null, contentValues);
    }

    private void insertInitialElementsData(SQLiteDatabase paramSQLiteDatabase) {
        insertElement(paramSQLiteDatabase, 1, "H", "Hydrogen", 1, 0);
        insertElement(paramSQLiteDatabase, 2, "He", "Helium", 2, 2);
        insertElement(paramSQLiteDatabase, 3, "Li", "Lithium", 3, 4);
        insertElement(paramSQLiteDatabase, 4, "Be", "Beryllium", 4, 5);
        insertElement(paramSQLiteDatabase, 5, "B", "Boron", 5, 6);
        insertElement(paramSQLiteDatabase, 6, "C", "Carbon", 6, 6);
        insertElement(paramSQLiteDatabase, 7, "N", "Nitrogen", 7, 7);
        insertElement(paramSQLiteDatabase, 8, "O", "Oxygen", 8, 8);
        insertElement(paramSQLiteDatabase, 9, "F", "Fluorine", 9, 10);
        insertElement(paramSQLiteDatabase, 10, "Ne", "Neon", 10, 10);
        insertElement(paramSQLiteDatabase, 11, "Na", "Sodium", 11, 12);
        insertElement(paramSQLiteDatabase, 12, "Mg", "Magnesium", 12, 12);
        insertElement(paramSQLiteDatabase, 13, "Al", "Aluminium", 13, 14);
        insertElement(paramSQLiteDatabase, 14, "Si", "Silicon", 14, 14);
        insertElement(paramSQLiteDatabase, 15, "P", "Phosphorus", 15, 16);
        insertElement(paramSQLiteDatabase, 16, "S", "Sulfur", 16, 16);
        insertElement(paramSQLiteDatabase, 17, "Cl", "Chlorine", 17, 18);
        insertElement(paramSQLiteDatabase, 18, "Ar", "Argon", 18, 22);
        insertElement(paramSQLiteDatabase, 19, "K", "Potassium", 19, 20);
        insertElement(paramSQLiteDatabase, 20, "Ca", "Calcium", 20, 20);
        insertElement(paramSQLiteDatabase, 21, "Sc", "Scandium", 21, 24);
        insertElement(paramSQLiteDatabase, 22, "Ti", "Titanium", 22, 26);
        insertElement(paramSQLiteDatabase, 23, "V", "Vanadium", 23, 28);
        insertElement(paramSQLiteDatabase, 24, "Cr", "Chromium", 24, 28);
        insertElement(paramSQLiteDatabase, 25, "Mn", "Manganese", 25, 30);
        insertElement(paramSQLiteDatabase, 26, "Fe", "Iron", 26, 30);
        insertElement(paramSQLiteDatabase, 27, "Co", "Cobalt", 27, 32);
        insertElement(paramSQLiteDatabase, 28, "Ni", "Nickel", 28, 31);
        insertElement(paramSQLiteDatabase, 29, "Cu", "Copper", 29, 35);
        insertElement(paramSQLiteDatabase, 30, "Zn", "Zinc", 30, 35);
        insertElement(paramSQLiteDatabase, 31, "Ga", "Gallium", 31, 39);
        insertElement(paramSQLiteDatabase, 32, "Ge", "Germanium", 32, 41);
        insertElement(paramSQLiteDatabase, 33, "As", "Arsenic", 33, 42);
        insertElement(paramSQLiteDatabase, 34, "Se", "Selenium", 34, 45);
        insertElement(paramSQLiteDatabase, 35, "Br", "Bromine", 35, 45);
        insertElement(paramSQLiteDatabase, 36, "Kr", "Krypton", 36, 48);
        insertElement(paramSQLiteDatabase, 37, "Rb", "Rubidium", 37, 48);
        insertElement(paramSQLiteDatabase, 38, "Sr", "Strontium", 38, 50);
        insertElement(paramSQLiteDatabase, 39, "Y", "Yttrium", 39, 50);
        insertElement(paramSQLiteDatabase, 40, "Zr", "Zirconium", 40, 51);
        insertElement(paramSQLiteDatabase, 41, "Nb", "Niobium", 41, 52);
        insertElement(paramSQLiteDatabase, 42, "Mo", "Molybdenum", 42, 54);
        insertElement(paramSQLiteDatabase, 43, "Tc", "Technetium", 43, 0);
        insertElement(paramSQLiteDatabase, 44, "Ru", "Ruthenium", 44, 57);
        insertElement(paramSQLiteDatabase, 45, "Rh", "Rhodium", 45, 58);
        insertElement(paramSQLiteDatabase, 46, "Pd", "Palladium", 46, 60);
        insertElement(paramSQLiteDatabase, 47, "Ag", "Silver", 47, 61);
        insertElement(paramSQLiteDatabase, 48, "Cd", "Cadmium", 48, 64);
        insertElement(paramSQLiteDatabase, 49, "In", "Indium", 49, 66);
        insertElement(paramSQLiteDatabase, 50, "Sn", "Tin", 50, 69);
        insertElement(paramSQLiteDatabase, 51, "Sb", "Antimony", 51, 71);
        insertElement(paramSQLiteDatabase, 52, "Te", "Tellurium", 52, 76);
        insertElement(paramSQLiteDatabase, 53, "I", "Iodine", 53, 74);
        insertElement(paramSQLiteDatabase, 54, "Xe", "Xenon", 54, 77);
        insertElement(paramSQLiteDatabase, 55, "Cs", "Cesium", 55, 78);
        insertElement(paramSQLiteDatabase, 56, "Ba", "Barium", 56, 81);
        insertElement(paramSQLiteDatabase, 57, "La", "Lanthanum", 57, 82);
        insertElement(paramSQLiteDatabase, 58, "Ce", "Cerium", 58, 82);
        insertElement(paramSQLiteDatabase, 59, "Pr", "Praseodymium", 59, 82);
        insertElement(paramSQLiteDatabase, 60, "Nd", "Neodymium", 60, 84);
        insertElement(paramSQLiteDatabase, 61, "Pm", "Promethium", 61, 84);
        insertElement(paramSQLiteDatabase, 62, "Sm", "Samarium", 62, 88);
        insertElement(paramSQLiteDatabase, 63, "Eu", "Europium", 63, 89);
        insertElement(paramSQLiteDatabase, 64, "Gd", "Gadolinium", 64, 93);
        insertElement(paramSQLiteDatabase, 65, "Tb", "Terbium", 65, 94);
        insertElement(paramSQLiteDatabase, 66, "Dy", "Dysprosium", 66, 97);
        insertElement(paramSQLiteDatabase, 67, "Ho", "Holmium", 67, 98);
        insertElement(paramSQLiteDatabase, 68, "Er", "Erbium", 68, 99);
        insertElement(paramSQLiteDatabase, 69, "Tm", "Thulium", 69, 100);
        insertElement(paramSQLiteDatabase, 70, "Yb", "Ytterbium", 70, 103);
        insertElement(paramSQLiteDatabase, 71, "Lu", "Lutetium", 71, 104);
        insertElement(paramSQLiteDatabase, 72, "Hf", "Hafnium", 72, 106);
        insertElement(paramSQLiteDatabase, 73, "Ta", "Tantalum", 73, 108);
        insertElement(paramSQLiteDatabase, 74, "W", "Tungsten", 74, 110);
        insertElement(paramSQLiteDatabase, 75, "Re", "Rhenium", 75, 111);
        insertElement(paramSQLiteDatabase, 76, "Os", "Osmium", 76, 114);
        insertElement(paramSQLiteDatabase, 77, "Ir", "Iridium", 77, 115);
        insertElement(paramSQLiteDatabase, 78, "Pt", "Platinum", 78, 117);
        insertElement(paramSQLiteDatabase, 79, "Au", "Gold", 79, 118);
        insertElement(paramSQLiteDatabase, 80, "Hg", "Mercury", 80, 121);
        insertElement(paramSQLiteDatabase, 81, "Tl", "Thallium", 81, 123);
        insertElement(paramSQLiteDatabase, 82, "Pb", "Lead", 82, 125);
        insertElement(paramSQLiteDatabase, 83, "Bi", "Bismuth", 83, 126);
        insertElement(paramSQLiteDatabase, 84, "Po", "Polonium", 84, 125);
        insertElement(paramSQLiteDatabase, 85, "At", "Astatine", 85, 125);
        insertElement(paramSQLiteDatabase, 86, "Rn", "Radon", 86, 136);
        insertElement(paramSQLiteDatabase, 87, "Fr", "Francium", 87, 136);
        insertElement(paramSQLiteDatabase, 88, "Ra", "Radium", 88, 138);
        insertElement(paramSQLiteDatabase, 89, "Ac", "Actinium", 89, 138);
        insertElement(paramSQLiteDatabase, 90, "Th", "Thorium", 90, 142);
        insertElement(paramSQLiteDatabase, 91, "Pa", "Protactinium", 91, 140);
        insertElement(paramSQLiteDatabase, 92, "U", "Uranium", 92, 146);
        insertElement(paramSQLiteDatabase, 93, "Np", "Neptunium", 93, 144);
        insertElement(paramSQLiteDatabase, 94, "Pu", "Plutonium", 94, 148);
        insertElement(paramSQLiteDatabase, 95, "Am", "Americium", 95, 148);
        insertElement(paramSQLiteDatabase, 96, "Cm", "Curium", 96, 151);
        insertElement(paramSQLiteDatabase, 97, "Bk", "Berkelium", 97, 150);
        insertElement(paramSQLiteDatabase, 98, "Cf", "Californium", 98, 153);
        insertElement(paramSQLiteDatabase, 99, "Es", "Einsteinium", 99, 153);
        insertElement(paramSQLiteDatabase, 100, "Fm", "Fermium", 100, 157);
        insertElement(paramSQLiteDatabase, 101, "Md", "Mendelevium", 101, 157);
        insertElement(paramSQLiteDatabase, 102, "No", "Nobelium", 102, 148);
        insertElement(paramSQLiteDatabase, 103, "Lr", "Lawrencium", 103, 157);
        insertElement(paramSQLiteDatabase, 104, "Rf", "Rutherfordium", 104, 157);
        insertElement(paramSQLiteDatabase, 105, "Db", "Dubnium", 105, 157);
        insertElement(paramSQLiteDatabase, 106, "Sg", "Seaborgium", 106, 157);
        insertElement(paramSQLiteDatabase, 107, "Bh", "Bohrium", 107, 155);
        insertElement(paramSQLiteDatabase, 108, "Hs", "Hassium", 108, 147);
        insertElement(paramSQLiteDatabase, 109, "Mt", "Meitnerium", 109, 147);
        insertElement(paramSQLiteDatabase, 110, "Ds", "Darmstadtium", 110, 159);
        insertElement(paramSQLiteDatabase, 111, "Rg", "Roentgenium", 111, 161);
        insertElement(paramSQLiteDatabase, 112, "Cn", "Copernicium", 112, 165);
    }


    public int[] getNextElementData() {
        SQLiteDatabase db = this.getReadableDatabase();
        int nextAtomicNumber = currentElement.getAtomicNumber() + 1;
        Cursor cursor = db.query(TABLE_NAME, null, "atomic_number = ?", new String[]{String.valueOf(nextAtomicNumber)}, null, null, null);
        if (currentElement != null) {
            // Zdobądź dane dla następnego elementu, np. na podstawie atomicNumber

            // Logika do uzyskania następnego elementu z bazy danych...
        } else {
            Log.e("ElementsDatabaseHelper", "currentElement is null, unable to get next element data.");// lub możesz rzucić wyjątek
        }
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int protons = cursor.getInt(cursor.getColumnIndex("protons"));
            @SuppressLint("Range") int neutrons = cursor.getInt(cursor.getColumnIndex("neutrons"));
            Log.d("ElementsDatabaseHelper", "Retrieved Protons: " + protons + ", Neutrons: " + neutrons);
            cursor.close();
            return new int[]{protons, neutrons};
        } else {
            Log.e("ElementsDatabaseHelper", "Cursor is null or empty for atomic number: " + nextAtomicNumber);
            return new int[]{0, 0}; // lub inna wartość domyślna
        }
    }


    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase.execSQL("CREATE TABLE elements (atomic_number INTEGER PRIMARY KEY, symbol TEXT, element TEXT, protons INTEGER, neutrons INTEGER);");
        insertInitialElementsData(paramSQLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS elements");
        onCreate(paramSQLiteDatabase);
    }

    public void setCurrentElement(Element currentElement) {
        this.currentElement = currentElement;
    }
}
