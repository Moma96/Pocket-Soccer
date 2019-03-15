package com.example.myapplication.view;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.model.Ball;
import com.example.myapplication.model.Circle;

public class BallImageUpdater extends Thread {

    private static final int[] images = { R.drawable.ball0, R.drawable.ball1, R.drawable.ball2, R.drawable.ball3, R.drawable.ball4, R.drawable.ball5, R.drawable.ball6, R.drawable.ball7, R.drawable.ball8, R.drawable.ball9 };

    private static final String STATE_TAG = "Ball image updater";

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
                ballImageView.setBackgroundResource(images[current]);
                current = ++current % 10;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void run() {
        Log.d(STATE_TAG, "Ball image updater started!");

        try {
            Ball ball = viewUpdater.getGameplay().getSoccerModel().getBall();

            while(!viewUpdater.getGameplay().isDestroyed()) {
                while (ball.getSpeed().isZeroVector()) {
                    synchronized (Circle.getCircles()) {
                        Circle.getCircles().wait();
                    }
                }
                updateImage();
                sleep((int)(10000 / ball.getSpeed().intensity()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(STATE_TAG, "Ball image updater finished!");
    }

}
