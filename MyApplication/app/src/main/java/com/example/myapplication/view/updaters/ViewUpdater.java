package com.example.myapplication.view.updaters;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
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
    private HashMap<ActiveObject, ImageView> imgActives = new HashMap<>();
    private ImageView imgSelected;
    private ImageView imgGoalposts;
    //private TextView[] scores = new TextView[2];
    private TextView imgScores;

    public ViewUpdater(GameplayActivity gameplay, SoccerModel soccer) {
        this.gameplay = gameplay;
        this.soccer = soccer;

        int[] teams = { 25, 5 };

        draw(teams);
    }

    public GameplayActivity getGameplay() {
        return gameplay;
    }

    public HashMap<ActiveObject, ImageView> getViews() {
        return imgActives;
    }

    private void draw(final int[] teams) {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FrameLayout background = gameplay.findViewById(R.id.background);
                drawBall(background);
                drawPlayers(background, teams);
                drawGoals(background);
                drawScores(background);
            }
       });
    }

    private void drawBall(FrameLayout background) {
        Ball ball = soccer.getBall();
        ImageView ballImageView = drawCircle(background, ball, R.drawable.ball0);
        ballImageUpdater = new BallImageUpdater(this, ballImageView);
    }

    private void drawPlayers(FrameLayout background, int[] teams) {
        for (int p = 0; p < 2; p++) {
            for (Player player : soccer.getPlayers(p))
                drawCircle(background, player, gameplay.getResources().getIdentifier("team" + teams[p], "drawable", gameplay.getPackageName()));
        }
    }

    private void drawScores(FrameLayout background) {
        /*for (int p = 0; p < 2; p++) {
            scores[p] = new TextView(gameplay);
            scores[p].setText(soccer.getScores()[p]);
        }*/

        imgScores = new TextView(gameplay);
        int[] scores = soccer.getScores();
        imgScores.setText(scores[0] + ":" + scores[1]);
        imgScores.setTextSize(50);
        imgScores.setRotation(-90);
        imgScores.setTextColor(Color.WHITE);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 0;
        params.topMargin = background.getHeight() / 2;
        background.addView(imgScores, params);
    }

    private void drawGoals(FrameLayout background) {
        imgGoalposts = new ImageView(gameplay);
        imgGoalposts.setBackgroundResource(R.drawable.goals);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(background.getWidth(), background.getHeight());
        params.leftMargin = 0;
        params.topMargin  = 0;
        background.addView(imgGoalposts, params);
    }

    private ImageView drawCircle(FrameLayout background, Circle circle, int resid) {
        ImageView img = new ImageView(gameplay);
        img.setBackgroundResource(resid);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(circle.getImgRadius()*2), (int)(circle.getImgRadius()*2));
        params.leftMargin = (int)(circle.getCenter().getX() - circle.getImgRadius());
        params.topMargin  = (int)(circle.getCenter().getY() - circle.getImgRadius());
        background.addView(img, params);

        imgActives.put(circle, img);
        return img;
    }

    public void refreshSelection() {
        Player selected = soccer.getSelected();
        if (selected != null) {
            if (imgSelected != null) {
                selected.drawSelection(imgSelected);
            } else {
                FrameLayout background = gameplay.findViewById(R.id.background);
                imgSelected = new ImageView(gameplay);
                imgSelected.setBackgroundResource(R.drawable.selectplayer);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(selected.getSelectionRadius()*2), (int)(selected.getSelectionRadius()*2));
                params.leftMargin = (int) (selected.getCenter().getX() - selected.getSelectionRadius());
                params.topMargin = (int) (selected.getCenter().getY() - selected.getSelectionRadius());
                background.addView(imgSelected, params);
            }
        } else {
            setOffSelection();
        }
    }

    public void refreshGoals() {
        imgGoalposts.setBackgroundResource(R.drawable.goals);
    }

    public void refresh() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ActiveObject> activeObjects = soccer.getField().getActiveCollidables();
                for (ActiveObject active : activeObjects)
                    active.draw(imgActives.get(active));

                refreshSelection();
                refreshGoals();
            }
        });
    }

    public void updateScores() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int[] scores = soccer.getScores();
                imgScores.setText(scores[0] + ":" + scores[1]);
            }
        });
    }

    public void setOffSelection() {
        if (imgSelected != null) {
            FrameLayout background = gameplay.findViewById(R.id.background);
            removeView(background, imgSelected);
            imgSelected = null;
        }
    }

    private void removeView(FrameLayout background, View view) {
        background.removeView(view);
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
