package com.example.periodicclicker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.example.periodicclicker.R;

import java.lang.ref.WeakReference;


public class Element {
    private static final int[] ELEMENT_SPRITES = new int[] {
            R.drawable.h, R.drawable.he, R.drawable.li, R.drawable.be, R.drawable.b, R.drawable.c, R.drawable.n, R.drawable.o, R.drawable.f, R.drawable.ne,
            R.drawable.na, R.drawable.mg, R.drawable.al, R.drawable.si, R.drawable.p, R.drawable.s, R.drawable.cl, R.drawable.ar, R.drawable.k, R.drawable.ca,
            R.drawable.sc, R.drawable.ti, R.drawable.v, R.drawable.cr, R.drawable.mn, R.drawable.fe, R.drawable.co, R.drawable.ni, R.drawable.cu, R.drawable.zn,
            R.drawable.ga, R.drawable.ge, R.drawable.as, R.drawable.se, R.drawable.br, R.drawable.kr, R.drawable.rb, R.drawable.sr, R.drawable.y, R.drawable.zr,
            R.drawable.nb, R.drawable.mo, R.drawable.tc, R.drawable.ru, R.drawable.rh, R.drawable.pd, R.drawable.ag, R.drawable.cd, R.drawable.in, R.drawable.sn,
            R.drawable.sb, R.drawable.te, R.drawable.i, R.drawable.xe, R.drawable.cs, R.drawable.ba, R.drawable.la, R.drawable.ce, R.drawable.pr, R.drawable.nd,
            R.drawable.pm, R.drawable.sm, R.drawable.eu, R.drawable.gd, R.drawable.tb, R.drawable.dy, R.drawable.ho, R.drawable.er, R.drawable.tm, R.drawable.yb,
            R.drawable.lu, R.drawable.hf, R.drawable.ta, R.drawable.w, R.drawable.re, R.drawable.os, R.drawable.ir, R.drawable.pt, R.drawable.au, R.drawable.hg,
            R.drawable.tl, R.drawable.pb, R.drawable.bi, R.drawable.po, R.drawable.at, R.drawable.rn, R.drawable.fr, R.drawable.ra, R.drawable.ac, R.drawable.th,
            R.drawable.pa, R.drawable.u, R.drawable.np, R.drawable.pu, R.drawable.am, R.drawable.cm, R.drawable.bk, R.drawable.cf, R.drawable.es, R.drawable.fm,
            R.drawable.md, R.drawable.no, R.drawable.lr };

    private int atomicNumber;
    private ElementsDatabaseHelper elementsDatabaseHelper;
    private GameActivity gameActivity;
    private int clickCount;

    private Context context;


    private int neutrons;

    private int protons;

    private Drawable sprite;

    private String symbol;

    public Element(int paramInt, String paramString, Context paramContext, GameActivity gameActivity) {
        if (paramContext == null) {
            Log.e("Element", "Context is null in Element constructor");
            return;
        }
        this.gameActivity = gameActivity;
        this.atomicNumber = paramInt;
        this.symbol = paramString;
        this.context = paramContext;
        this.protons = 1;
        this.neutrons = 0;
        this.clickCount = 0;
        this.elementsDatabaseHelper = new ElementsDatabaseHelper(paramContext);
        updateSprite();
    }


    private Drawable getDrawableForAtomicNumber(int paramInt) {
        if (--paramInt >= 0) {
            int[] arrayOfInt = ELEMENT_SPRITES;
            if (paramInt < arrayOfInt.length)
                return ContextCompat.getDrawable(this.context, arrayOfInt[paramInt]);
        }
        return ContextCompat.getDrawable(this.context, R.drawable.h);
    }

    public void setAtomicNumber(int atomicNumber) {
        this.atomicNumber = atomicNumber;
        elementsDatabaseHelper.setCurrentElement(this);         updateSprite();     }

    public void checkAndUpdateSprite() {
                elementsDatabaseHelper.setCurrentElement(this);

                int[] nextElementData = elementsDatabaseHelper.getNextElementData();
        if (nextElementData != null) {
            int requiredProtons = nextElementData[0];              int requiredNeutrons = nextElementData[1]; 
                        if (gameActivity != null) {
                                if (gameActivity.getPurchasedProtons() >= requiredProtons && gameActivity.getPurchasedNeutrons() >= requiredNeutrons) {
                    Toast.makeText(this.context, "Fusion successful!", Toast.LENGTH_SHORT).show();

                                        setAtomicNumber(getAtomicNumber() + 1);

                                        gameActivity.resetPurchasedCounts();

                } else {
                    Toast.makeText(this.context, "Not enough nucleons", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Element", "GameActivity is null!");
            }
        } else {
            Log.e("Database Error", "No data found for the next element.");
        }
    }


    public int getAtomicNumber() {
        return this.atomicNumber;
    }

    public int getNeutrons() {
        return this.neutrons;
    }

    public int getProtons() {
        return this.protons;
    }

    public Drawable getSprite() {
        return this.sprite;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setNeutrons(int paramInt) {
        this.neutrons = paramInt;
        checkAndUpdateSprite();
    }

    public void setProtons(int paramInt) {
        this.protons = paramInt;
        checkAndUpdateSprite();
    }

    public void setSpriteToImageView(ImageView paramImageView) {
        paramImageView.setImageDrawable(this.sprite);
    }

    public void updateSprite() {
        this.sprite = getDrawableForAtomicNumber(this.atomicNumber);
    }
}
