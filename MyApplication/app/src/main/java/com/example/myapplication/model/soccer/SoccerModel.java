package com.example.myapplication.model.soccer;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.Vector;
import com.example.myapplication.view.activities.GameplayActivity;

import java.util.HashMap;

import static java.lang.Thread.sleep;

public class SoccerModel {

    public static final double GOAL_WIDTH = 300;
    public static final double GOAL_HEIGHT = 100;

    public static final double BALL_X = 1.0/2;
    public static final double BALL_Y = 1.0/2;

    public static final double[][] PLAYER_X = {{ 1.0/2, 1.0/5, 4.0/5 },
                                               { 1.0/2, 1.0/5, 4.0/5 }};

    public static final double[][] PLAYER_Y = {{ 1.0/4, 1.0/8, 1.0/8 },
                                              { 3.0/4, 7.0/8, 7.0/8 }};

    public static final int GOAL_WAIT = 3; //s

    private static final String GOAL_TAG = "Goal";

    private SoccerField field;
    private Goal[] goals = new Goal[2];

    private Ball ball;

    private Player[][] players = new Player[2][3];
    private int active = 0;

    private int[] scores = {0, 0};
    private boolean responsiveness = false;

    private double x;
    private double y;
    private double width;
    private double height;

    private GameplayActivity gameplay;

    public SoccerModel(GameplayActivity gameplay, double x, double y, double width, double height) {
        this.gameplay = gameplay;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        field = new SoccerField(x, y, width, height);
        Circle.setField(field);
        goals[0] = new Goal(GoalPost.Direction.NORTH, x + width/2 - GOAL_WIDTH/2, y, GOAL_WIDTH, GOAL_HEIGHT);
        goals[1] = new Goal(GoalPost.Direction.SOUTH, x + width/2 - GOAL_WIDTH/2, y + height - GOAL_HEIGHT, GOAL_WIDTH, GOAL_HEIGHT);

        ball = new Ball(new Vector(x + width*BALL_X, y + height*BALL_Y), this);

        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i] = new Player(new Vector(x + width * PLAYER_X[p][i], y + height * PLAYER_Y[p][i]));
        }

        ball.start();
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i].start();
        }
//*/
        //////TEST 1
/*
        ball = new Ball(new Vector(x + width/2, y + height/2), this);
        ball.setRadius(150);

        player[0][0] = new Player(new Vector(x + width/4, y + height/2));
        player[1][0] = new Player(new Vector(x + 3*(width/4), y + height/2));

        player[0][0].setSpeed(new Vector(10, 0));
        player[1][0].setSpeed(new Vector(-10, 0));

        ball.start();
        for (int p = 0; p <2; p++) {
            player[p][0].setRadius(200);
            player[p][0].setMass(200);
            player[p][0].start();
        }
//*/
        ///////TEST 2
/*
        ball = new Ball(new Vector(x + width/2, y + height/5), this);

        player[0][0] = new Player(new Vector(x + width/4, y + height/2));
        player[0][1] = new Player(new Vector(x + 3*(width/4), y + height/2));
        player[0][2] = new Player(new Vector(x + width/2, y + height/2));
        player[1][0] = new Player(new Vector(x + width/2, y + 1*(height/3)));
        player[1][1] = new Player(new Vector(x + width/2, y + 2*(height/3)));
        player[1][2] = new Player(new Vector(x + width/2, y + 4*(height/5)));

        ball.start();
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++) {
                player[p][i].setRadius(200);
                player[p][i].start();
            }
        }
//*/
        setResponsiveness();
    }

    private void changeActive() {
        active = (active + 1) % 2;
        darkenInactive();
    }

    private void disableResponsiveness(int wait) {
        resetResponsiveness();
        try {
            sleep(wait * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setResponsiveness();
    }

    public void goal(int p) {
        if (!responsive()) return;

        scores[p]++;
        Log.d(GOAL_TAG, "PLayer " + p + " scored! result: " + scores[0] + ":" + scores[1]);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( final Void ... params ) {
                disableResponsiveness(GOAL_WAIT);

                ball.setCenter(new Vector(x + width*BALL_X, y + height*BALL_Y));
                ball.clearSpeed();
                for (int p = 0; p < 2; p++) {
                    for (int i = 0; i < 3; i++) {
                        players[p][i].clearSpeed();
                        players[p][i].setCenter(new Vector(x + width * PLAYER_X[p][i], y + height * PLAYER_Y[p][i]));
                    }
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void darkenInactive() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Player[] active = getActivePlayers();
                Player[] non_active = getNonActivePlayers();
                HashMap<ActiveObject, ImageView> views = gameplay.getViewUpdater().getViews();

                for (Player player : active) {
                    ImageView view = views.get(player);
                    view.setAlpha((float) 1);
                }
                for (Player player : non_active) {
                    ImageView view = views.get(player);
                    view.setAlpha((float) 0.7);
                }
            }
        });
    }

    public void push(final float x1, final float y1, final float x2, final float y2) {
        if (!responsive()) return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( final Void ... params ) {
                Player player = getActivePlayer(new Vector(x1, y1));
                player.push(new Vector(x2 - x1, y2 - y1));
                changeActive();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized boolean responsive() {
        return responsiveness;
    }

    public synchronized void setResponsiveness() {
        responsiveness = true;
    }

    public synchronized void resetResponsiveness() {
        responsiveness = false;
    }

    public Ball getBall() {
        return ball;
    }

    public Player[][] getPlayers() {
        return players;
    }

    public Player[] getPlayers(int p) {
        return players[p];
    }

    public Player[] getActivePlayers() {
        return players[active];
    }

    public Player[] getNonActivePlayers() {
        return players[(active + 1) % 2];
    }

    public Player getActivePlayer(Vector dot) {
        Player[] players = getActivePlayers();
        double min_distance = Double.MAX_VALUE;
        Player player = null;
        for (Player p : players) {
            double distance = p.getDistance(dot);
            if (distance < min_distance) {
                min_distance = distance;
                player = p;
            }
        }
        return player;
    }

    public Goal[] getGoals() { return goals; }
}
