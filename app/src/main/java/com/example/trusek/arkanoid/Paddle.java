package com.example.trusek.arkanoid;

import android.graphics.RectF;

public class Paddle {

    private RectF rect;

    // długość oraz grubość paletki
    private float length;
    private float height;

    private float x;
    private float y;

    public Paddle(int screenX, int screenY) {

        length = 200;
        height = 23;

        x = screenX / 2;
        y = screenY - 30;

        rect = new RectF(x, y, x + length, y + height);
    }

    public RectF getRect() {
        return rect;
    }

    public void setX(float x) {
        this.x = x - length / 2;
    }

    public void update(long fps) {
        rect.left = x;
        rect.right = x + length;
    }

    public void addLength() {
        this.length += 50;
    }

    public void subLength() {
        if(this.length > 50)
            this.length -= 50;
    }

    public void resetPaddle(){
        length = 200;
    }
}