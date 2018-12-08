package com.example.trusek.arkanoid;

import android.graphics.RectF;

import java.util.Random;

public class Ball {
    private RectF rect;
    private float xVelocity;
    private float yVelocity;
    private float ballWidth = 10;
    private float ballHeight = 10;

    public Ball(int screenX, int screenY) {

        xVelocity = 200;
        yVelocity = -400;

        rect = new RectF();

    }

    public RectF getRect() {
        return rect;
    }

    public void update(long fps) {
        rect.left += xVelocity / fps;
        rect.top += yVelocity / fps;
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

    public void reverseYVelocity() {
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity() {
        xVelocity = -xVelocity;
    }

    public void setRandomXVelocity() {
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if (answer == 0) {
            reverseXVelocity();
        }
    }

    public void fixY(float y) {
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    public void fixX(float x) {
        rect.left = x;
        rect.right = x + ballWidth;
    }

    public void reset(int x, int y) {
        rect.left = x / 2;
        rect.top = y - 20;
        rect.right = x / 2 + ballWidth;
        rect.bottom = y - 20 - ballHeight;
    }

}