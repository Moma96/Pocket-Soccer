package com.example.myapplication.model.soccer.models;

import android.util.Log;
import com.example.myapplication.model.Vector;
import static java.lang.Thread.sleep;

public class SoccerModel {

    public static final int AFTER_GOAL_WAIT = 2; //s

    public static final double GOAL_WIDTH = 100;
    public static final double GOAL_HEIGHT = 300;

    public static final double BALL_X = 1.0/2;
    public static final double BALL_Y = 1.0/2;

    public static final double[][] PLAYER_X = {{ 1.0/4, 1.0/8, 1.0/8 },
                                               { 3.0/4, 7.0/8, 7.0/8 }};

    public static final double[][] PLAYER_Y = {{ 1.0/2, 1.0/5, 4.0/5 },
                                               { 1.0/2, 1.0/5, 4.0/5 }};

    private static final String GOAL_TAG = "Goal";

    protected SoccerField field;
    private Goal[] goals = new Goal[2];

    protected Ball ball;

    protected Player[][] players = new Player[2][3];
    private int active = 0;
    private Player selected = null;
    private boolean responsiveness = false;

    private int[] scores = {0, 0};

    private double x;
    private double y;
    private double width;
    private double height;

    public SoccerModel() {
        setGoals();
    }

    public SoccerModel(double x, double y, double width, double height) {
        super();
        setField(x, y, width, height);

        ball = new Ball(new Vector(x + width*BALL_X, y + height*BALL_Y), this);

        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i] = new Player(new Vector(x + width * PLAYER_X[p][i], y + height * PLAYER_Y[p][i]), this);
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

        for (int p = 0; p <2; p++) {
            player[p][0].setRadius(200);
            player[p][0].setMass(200);
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

        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++) {
                player[p][i].setRadius(200);
            }
        }
//*/
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setGoals() {
        goals[0] = new Goal(GoalPost.Direction.WEST, x, y + height/2 - GOAL_HEIGHT/2, GOAL_WIDTH, GOAL_HEIGHT, field);
        goals[1] = new Goal(GoalPost.Direction.EAST, x + width - GOAL_WIDTH, y + height/2 - GOAL_HEIGHT/2, GOAL_WIDTH, GOAL_HEIGHT, field);
    }

    public void setField(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        field = new SoccerField(x, y, width, height);
    }

    public void start() {
        ball.start();
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i].start();
        }
        setResponsiveness();
    }

    public void terminate() {
        resetResponsiveness();
        ball.terminate();
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i].terminate();
        }
    }

    public void changeActive() {
        active = (active + 1) % 2;
    }

    public Player getSelected() {
        return selected;
    }

    private void disableAndSleep(int wait) {
        resetResponsiveness();
        resetSelection();
        try {
            sleep(wait * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setResponsiveness();
    }

    public boolean score(int player) {
        if (!responsive()) return false;

        scores[player]++;
        Log.d(GOAL_TAG, "PLayer " + player + " scored! result: " + scores[0] + ":" + scores[1]);

        disableAndSleep(AFTER_GOAL_WAIT);

        reset();

        active = (player + 1) % 2;
        return true;
    }

    public void reset() {
        ball.setCenter(new Vector(x + width*BALL_X, y + height*BALL_Y));
        ball.clearSpeed();
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++) {
                players[p][i].clearSpeed();
                players[p][i].setCenter(new Vector(x + width * PLAYER_X[p][i], y + height * PLAYER_Y[p][i]));
            }
        }
    }

    public boolean push(final float x1, final float y1, final float x2, final float y2) {
        if (!responsive()) return false;

        if (selected != null) {
            selected.push(new Vector(x2 - x1, y2 - y1));
            selected = null;
            changeActive();
        }
        return true;
    }

    public boolean select(final float x, final float y) {
        if (!responsive()) return false;

        selected = getActivePlayer(new Vector(x, y));
        return true;
    }

    public boolean selectIfNothingSelected(final float x, final float y) {
        if (selected != null) return false;

        return select(x, y);
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

    public synchronized void resetSelection() { selected = null; }

    public Ball getBall() {
        return ball;
    }

    protected void setBall(Ball ball) {
        this.ball = ball;
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

    public Goal[] getGoals() {
        return goals;
    }

    public int[] getScores() {
        return scores;
    }

    public SoccerField getField() {
        return field;
    }
}
