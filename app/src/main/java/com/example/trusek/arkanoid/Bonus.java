package com.example.trusek.arkanoid;

import android.graphics.Color;
import android.graphics.RectF;

import java.util.Random;

public class Bonus {

    private RectF rect;

    // cel bonusu
    private int target;

    // kolor bonusu
    private int color;

    private float height;
    private float width;

    private boolean isActive;
    private int speed;

    public Bonus() {

        rect = new RectF();
        isActive = false;
        speed = 200;
        height = 20;
        width = 20;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    private void setBonusCords(float x, float y) {
        rect.left = x / 2;
        rect.top = y - 20;
        rect.right = x / 2 + width;
        rect.bottom = y - 20 - height;
    }

    public void update(long fps) {
        rect.top += speed / fps;
        rect.bottom = rect.top - height;
    }

    public RectF getRect() {
        return rect;
    }

    public void randomBonus(float x, float y) {
        Random generator = new Random();
        int answer = generator.nextInt(7);
        switch (answer) {
            case 1: //zycie
                color = Color.GREEN;
                target = 0;
                isActive = true;
                break;
            case 2: // zmniejszenie paletki
                color = Color.RED;
                target = 1;
                isActive = true;
                break;
            case 3: // zwiększenie paletki
                color = Color.DKGRAY;
                target = 2;
                isActive = true;
                break;
        }
        if (isActive) {
            setBonusCords(x, y);
        }
    }

    public int getColor() {
        return color;
    }

    public int getTarget() {
        return target;
    }

    public void fixY(float y){
        rect.bottom = y;
        rect.top = y - height;
    }
}
