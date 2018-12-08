package com.example.trusek.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends Activity {

    // widok będzie odpowiadał za wyświetlanie gry
    // oraz za jej całą logikę
    ArkanoidView arkanoidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ustawienie widoku
        arkanoidView = new ArkanoidView(this);
        setContentView(arkanoidView);

    }

    class ArkanoidView extends SurfaceView implements Runnable {

        Thread gameThread = null;
        SurfaceHolder ourHolder;

        // logiczna ustawiana na czas gry
        volatile boolean loop;

        // logiczna pałzy
        boolean paused = true;

        Canvas canvas;
        Paint paint;

        // zmianna do pomiaru fps
        long fps;

        // pomocna do liczenia fps
        private long timeThisFrame;

        // wymiary ekranu
        int screenX;
        int screenY;

        // paletka
        private Paddle paddle;
        // kulka
        Ball ball;

        // Up to 200 bricks
        Brick[] bricks = new Brick[200];
        int numBricks = 0;

        // wynik
        int score;

        // życie
        int lives;

        // bonus
        Bonus bonus;

        public ArkanoidView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();

            // pobranie rozmiaru ekranu
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            paddle = new Paddle(screenX, screenY);
            ball = new Ball(screenX, screenY);

            bonus = new Bonus();

            createBricksAndRestart();
        }

        @Override
        public void run() {
            while (loop) {

                // pobranie aktualnego czasu
                long startFrameTime = System.currentTimeMillis();

                // nie aktalizuj jeśli pauza
                if (!paused) {
                    update();
                }
                // rysuj
                draw();
                // obliczanie fps
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        // wykrywanie ruchu i kolizji
        public void update() {
            paddle.update(fps);
            ball.update(fps);

            if (bonus.isActive()) {
                bonus.update(fps);
            }

            // czy kulka trafiła w ścianę klocków
            for (int i = 0; i < numBricks; i++) {

                if (bricks[i].getVisibility()) {

                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score += 10;
                        if (!bonus.isActive()) {
                            bonus.randomBonus(bricks[i].getRect().left, bricks[i].getRect().top);
                        }
                    }
                }
            }

            // kolizja z paletką
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.fixY(paddle.getRect().top - 2);
            }
            // uderzenie w spód ekranu
            else if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();
                ball.fixY(screenY - 2);

                // utrata życia
                lives--;

                if (lives == 0) {
                    paused = true;
                }
            }


            if (bonus.isActive()) {
                // zderzenie bonusu z paletką
                if (RectF.intersects(paddle.getRect(), bonus.getRect())) {
                    bonus.fixY(screenY - 2);
                    bonus.setActive(false);
                    switch (bonus.getTarget()) {
                        case 0:
                            lives++;
                            break;
                        case 1:
                            paddle.subLength();
                            break;
                        case 2:
                            paddle.addLength();
                            break;
                    }
                }
                //zderzenie bonusu ze spodem ekranu
                else if (bonus.getRect().bottom > screenY) {
                    bonus.setActive(false);
                }
            }

            // uderzenie w sufit
            if (ball.getRect().top < 0) {
                ball.reverseYVelocity();
                ball.fixY(12);
            }

            // uderzenie w ścianę z lewej
            if (ball.getRect().left < 0) {
                ball.reverseXVelocity();
                ball.fixX(2);
            }

            // uderzenie w ściane z prawej
            if (ball.getRect().right > screenX - 10) {
                ball.reverseXVelocity();
                ball.fixX(screenX - 22);
            }

            // pauza jesli wyczyszczono ścianę z klocków
            if (score == numBricks * 10) {
                paused = true;
            }
        }

        // rysuj
        public void draw() {

            if (ourHolder.getSurface().isValid()) {

                canvas = ourHolder.lockCanvas();

                // rysowanie tła
                canvas.drawColor(Color.argb(255, 26, 128, 182));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                // rysowanie paletki
                canvas.drawRect(paddle.getRect(), paint);

                // rysowanie kulki
                canvas.drawRect(ball.getRect(), paint);

                // rysowanie sciany klocków
                paint.setColor(Color.argb(255, 249, 129, 0));
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                //rysowanie bonusu
                if (bonus.isActive()) {
                    paint.setColor(bonus.getColor());
                    canvas.drawOval(bonus.getRect(), paint);
                }

                // rysowanie interfejsu
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(40);
                canvas.drawText("Wynik: " + score + "   Życia: " + lives, 10, 50, paint);

                // wygrana
                if (score == numBricks * 10) {
                    paint.setTextSize(90);
                    canvas.drawText("yes we did it!", 10, screenY / 2, paint);
                }

                // przegrana
                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("omae wa mou", 10, screenY / 2, paint);
                    canvas.drawText("shindeiru!", 10, screenY / 2 + 80, paint);
                }

                // rysuj wszystko
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void createBricksAndRestart() {

            // ustawienie rozmiarów kulki
            ball.reset(screenX, screenY);

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;

            // budowanie ściany kolcków
            numBricks = 0;

            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 3; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }

            // reset punktów oraz życia
            score = 0;
            lives = 3;
            paddle.resetPaddle();
        }

        public void pause() {
            loop = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
            }

        }

        public void resume() {
            loop = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_MOVE:
                    paddle.setX(motionEvent.getX());

                case MotionEvent.ACTION_DOWN:
                    if (paused) {
                        createBricksAndRestart();
                        paused = false;
                    }
                    break;
            }
            return true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        arkanoidView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        arkanoidView.pause();
    }

}