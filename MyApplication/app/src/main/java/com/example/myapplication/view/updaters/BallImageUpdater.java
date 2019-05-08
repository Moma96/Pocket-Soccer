package com.example.myapplication.view.updaters;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ImageView;

import com.example.myapplication.model.soccer.models.Ball;

public class BallImageUpdater extends Thread {

    private static final String STATE_TAG = "Ball image updater";

    private static final int BALL_IMAGES = 11;

    private ViewUpdater viewUpdater;
    private ImageView ballImageView;
    private int current = 0;

    public BallImageUpdater(ViewUpdater viewUpdater, ImageView ballImageView) {
        this.viewUpdater = viewUpdater;
        this.ballImageView = ballImageView;
    }

    private void updateImage() {
        viewUpdater.getGameplay().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ballImageView.setBackgroundResource(viewUpdater.getGameplay().getResources().getIdentifier("ball" + current, "drawable", viewUpdater.getGameplay().getPackageName()));
                current = ++current % BALL_IMAGES;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void run() {
        Log.d(STATE_TAG, "Ball image updater started!");
        try {
            Ball ball = viewUpdater.getSoccer().getBall();

            while(!viewUpdater.getGameplay().isDestroyed()) {
                while (ball.getSpeed().isZeroVector()) {
                    synchronized (ball) {
                        ball.wait();
                    }
                }
                updateImage();
                sleep((int)(100 / ball.getSpeed().intensity()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(STATE_TAG, "Ball image updater finished!");
    }

}
