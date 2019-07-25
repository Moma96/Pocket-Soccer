package com.example.myapplication.view.updaters;

import android.util.Log;
import android.widget.ImageView;

import com.example.myapplication.model.Active;
import com.example.myapplication.model.soccer.models.Ball;

public class BallImageUpdater extends Active {

    private static final String STATE_TAG = "Ball image updater";

    private static final int BALL_IMAGES = 11;

    private ViewUpdater viewUpdater;
    private Ball ball;
    private ImageView ballImageView;
    private int current = 0;

    public BallImageUpdater(ViewUpdater viewUpdater, ImageView ballImageView) {
        this.viewUpdater = viewUpdater;
        ball = viewUpdater.getSoccer().getBall();
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

    @Override
    protected void before() {
        Log.d(STATE_TAG, "Ball image updater started!");
    }

    @Override
    protected void after() {
        Log.d(STATE_TAG, "Ball image updater finished!");
    }

    @Override
    protected void iterate() {
        try {
            synchronized (ball) {
                while (ball.getSpeed().isZeroVector()) {
                    ball.wait();
                }
            }
            updateImage();
            sleep((int)(100 / ball.getSpeed().intensity()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
