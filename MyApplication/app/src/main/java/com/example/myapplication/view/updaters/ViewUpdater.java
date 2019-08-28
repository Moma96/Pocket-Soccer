package com.example.myapplication.view.updaters;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.controller.SoccerGameplay;
import com.example.myapplication.model.soccer.models.Ball;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.view.activities.GameplayActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ViewUpdater {

    private GameplayActivity gameplay;
    private SoccerGameplay soccer;

    private BallImageUpdater ballImageUpdater;
    private HashMap<Circle, ImageView> imgCircles = new HashMap<>();
    private ImageView imgSelected;
    private ImageView imgGoalposts;

    public ViewUpdater(GameplayActivity gameplay, SoccerGameplay soccer) {
        this.gameplay = gameplay;
        this.soccer = soccer;

        draw();
    }

    public GameplayActivity getGameplay() {
        return gameplay;
    }

    public SoccerGameplay getSoccer() {
        return soccer;
    }

    private void draw() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FrameLayout background = gameplay.findViewById(R.id.background);
                drawBackground(background);
                drawBall(background);
                drawPlayers(background);
                drawGoals(background);
                updateScores();
                updateTime();
                reorderViews();
            }
       });
        darkenInactive();
    }

    private void reorderViews() {
        imgGoalposts.bringToFront();
        gameplay.findViewById(R.id.pause_text).bringToFront();
        gameplay.findViewById(R.id.score_text).bringToFront();
        gameplay.findViewById(R.id.time_text).bringToFront();
        gameplay.findViewById(R.id.pause_text).bringToFront();
        gameplay.findViewById(R.id.main_menu_text).bringToFront();
    }

    private void drawBackground(@NotNull FrameLayout background) {
        int fieldimg = gameplay.getResources().getIdentifier("field" + soccer.getFieldImg(), "drawable", gameplay.getPackageName());
        background.setBackgroundResource(fieldimg);
    }

    private void drawBall(FrameLayout background) {
        Ball ball = soccer.getBall();
        ImageView ballImageView = drawCircle(background, ball, R.drawable.ball0);
        ballImageUpdater = new BallImageUpdater(this, ballImageView);
    }

    private void drawPlayers(FrameLayout background) {
        for (int p = 0; p < 2; p++) {
            int rotation = 90;
            if (p == 1) rotation = -90;
            for (Player player : soccer.getPlayers(p)) {
                drawCircle(background, player, gameplay.getResources().getIdentifier("t" + soccer.getTeamsImg()[p], "drawable", gameplay.getPackageName())).setRotation(rotation);
            }
        }
    }

    private void drawGoals(@NotNull FrameLayout background) {
        imgGoalposts = new ImageView(gameplay);
        imgGoalposts.setBackgroundResource(R.drawable.goals);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(background.getWidth(), background.getHeight());
        params.leftMargin = 0;
        params.topMargin  = 0;
        background.addView(imgGoalposts, params);
    }

    private ImageView drawCircle(@NotNull FrameLayout background, @NotNull Circle circle, int resid) {
        ImageView img = new ImageView(gameplay);
        img.setBackgroundResource(resid);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(circle.getImgRadius()*2), (int)(circle.getImgRadius()*2));
        params.leftMargin = (int)(circle.getCenter().getX() - circle.getImgRadius());
        params.topMargin  = (int)(circle.getCenter().getY() - circle.getImgRadius());
        background.addView(img, params);

        imgCircles.put(circle, img);
        return img;
    }

    public void refreshSelection() {
        final Player selected = soccer.getSelected();
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (selected != null) {
                    if (imgSelected != null) {
                        selected.drawSelection(imgSelected);
                        imgGoalposts.bringToFront();
                    } else {
                        FrameLayout background = gameplay.findViewById(R.id.background);
                        imgSelected = new ImageView(gameplay);
                        imgSelected.setBackgroundResource(R.drawable.selectplayer);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (selected.getSelectionRadius() * 2), (int) (selected.getSelectionRadius() * 2));
                        params.leftMargin = (int) (selected.getCenter().getX() - selected.getSelectionRadius());
                        params.topMargin = (int) (selected.getCenter().getY() - selected.getSelectionRadius());
                        background.addView(imgSelected, params);
                    }
                } else {
                    setOffSelection();
                }

            }
        });
    }

    public void refreshCircles() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Circle> circles = soccer.getField().getCircles();
                for (Circle circle : circles)
                    circle.draw(imgCircles.get(circle));
            }
        });

        refreshSelection();
    }

    public void refreshMovingCircles() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (soccer.getField()) {
                    HashSet<Circle> moving = soccer.getField().getMoving();
                    for (Circle circle : moving)
                        circle.draw(imgCircles.get(circle));
                }
            }
        });

        refreshSelection();
    }

    public void updateScores() {
        final int[] scores = soccer.getScores();

        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textScores = gameplay.findViewById(R.id.score_text);
                textScores.setText(String.format("%d:%d", scores[0] , scores[1]));
            }
        });
    }

    public void updateTime() {
        if (soccer.getFinishCriteria() != SoccerGameplay.FinishCriteria.TIME) return;
        final int time = (int)(soccer.getLimit()/10);

        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textTime = gameplay.findViewById(R.id.time_text);
                textTime.setText(String.format("%d:%d", time/60 , time%60));
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

    public void darkenInactive() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Player[] active = soccer.getActivePlayers();
                Player[] non_active = soccer.getNonActivePlayers();

                for (Player player : active) {
                    ImageView view = imgCircles.get(player);
                    view.setAlpha((float) 1);
                }
                for (Player player : non_active) {
                    ImageView view = imgCircles.get(player);
                    view.setAlpha((float) 0.7);
                }
            }
        });
    }

    private void removeView(@NotNull FrameLayout background, View view) {
        background.removeView(view);
    }

    public void start() {
        ballImageUpdater.start();
    }

    public void active() {
        ballImageUpdater.active();
    }

    public void inactive() {
        ballImageUpdater.inactive();
    }

    public void terminate() {
        ballImageUpdater.terminate();
    }
}
