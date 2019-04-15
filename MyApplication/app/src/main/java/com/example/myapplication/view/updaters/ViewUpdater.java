package com.example.myapplication.view.updaters;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.soccer.models.Ball;
import com.example.myapplication.model.soccer.models.Circle;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.view.activities.GameplayActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewUpdater extends Thread {

    private static final int FPS = 60;
    private static final String STATE_TAG = "View updater";

    private GameplayActivity gameplay;
    private SoccerModel soccer;

    private BallImageUpdater ballImageUpdater;
    private HashMap<ActiveObject, ImageView> activeViews = new HashMap<>();
    private ImageView goalposts;
    private TextView[] score = new TextView[2];

    public ViewUpdater(GameplayActivity gameplay, SoccerModel soccer) {
        this.gameplay = gameplay;
        this.soccer = soccer;

        FrameLayout background = gameplay.findViewById(R.id.background);

        Ball ball = soccer.getBall();
        ImageView ballImageView = drawCircle(background, ball, R.drawable.ball0);
        ballImageUpdater = new BallImageUpdater(this, ballImageView);

        int[] teams = { 25, 5 };

        for (int p = 0; p < 2; p++) {
            for (Player player : soccer.getPlayers(p))
                drawCircle(background, player, gameplay.getResources().getIdentifier("team" + teams[p], "drawable", gameplay.getPackageName()));//teamresid[p]);
        }

        drawScores(background);
        drawGoals(background);
    }

    public GameplayActivity getGameplay() {
        return gameplay;
    }

    public HashMap<ActiveObject, ImageView> getViews() {
        return activeViews;
    }

    private void drawScores(FrameLayout background) {

    }

    private void drawGoals(FrameLayout background) {
        goalposts = new ImageView(gameplay);
        goalposts.setBackgroundResource(R.drawable.goals);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(background.getWidth(), background.getHeight());
        params.leftMargin = 0;
        params.topMargin  = 0;
        background.addView(goalposts, params);
    }

    private ImageView drawCircle(FrameLayout background, Circle circle, int resid) {
        ImageView img = new ImageView(gameplay);
        img.setBackgroundResource(resid);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(circle.getImgRadius()*2), (int)(circle.getImgRadius()*2));
        params.leftMargin = (int)(circle.getCenter().getX() - circle.getImgRadius());
        params.topMargin  = (int)(circle.getCenter().getY() - circle.getImgRadius());
        background.addView(img, params);

        activeViews.put(circle, img);
        return img;
    }

    private void refresh() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ActiveObject> activeObjects = ActiveObject.getActiveCollidables();
                for (ActiveObject active : activeObjects)
                    active.draw(activeViews.get(active));

                goalposts.setBackgroundResource(R.drawable.goals);
            }
        });
    }

    public void updateScore() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void run() {
        Log.d(STATE_TAG, "View updater started");

        ballImageUpdater.start();
        while(!gameplay.isDestroyed()) {
            refresh();
            try {
                sleep( 1000 / FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(STATE_TAG, "View updater finished");
    }
}
