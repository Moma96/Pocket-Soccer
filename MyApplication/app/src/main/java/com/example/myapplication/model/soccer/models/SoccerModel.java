package com.example.myapplication.model.soccer.models;

import android.util.Log;

import com.example.myapplication.model.Vector;

public abstract class SoccerModel {

    protected static final String GOAL_TAG = "Goal";

    public static final double GOAL_WIDTH = 100;
    public static final double GOAL_HEIGHT = 300;

    public static final double BALL_X = 1.0/2;
    public static final double BALL_Y = 1.0/2;

    public static final double[][] PLAYER_X = {{ 1.0/4, 1.0/8, 1.0/8 },
                                               { 3.0/4, 7.0/8, 7.0/8 }};

    public static final double[][] PLAYER_Y = {{ 1.0/2, 1.0/5, 4.0/5 },
                                               { 1.0/2, 1.0/5, 4.0/5 }};

    protected SoccerField field;
    private Goal[] goals = new Goal[2];

    protected Ball ball;
    protected Player[][] players;// = new Player[2][3];

    private double x;
    private double y;
    private double width;
    private double height;

    public SoccerModel() {}

    public SoccerModel(double x, double y, double width, double height) {
        setParameters(x, y, width, height);
        field = new SoccerField(x, y, width, height, this);
        setGoals();


        ball = new Ball(new Vector(x + width*BALL_X, y + height*BALL_Y), this);

        players = new Player[2][3];
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i] = new Player(new Vector(x + width * PLAYER_X[p][i], y + height * PLAYER_Y[p][i]), field);
        }

//*/
        //////TEST 1
/*
        ball = new Ball(new Vector(x + width/2, y + height/2), this);
        ball.setRadius(150);

        players = new Player[2][1];
        players[0][0] = new Player(new Vector(x + width/4, y + height/2), field);
        players[1][0] = new Player(new Vector(x + 3*(width/4), y + height/2), field);

        players[0][0].push(new Vector(100, 0));
        players[1][0].push(new Vector(-100, 0));

        for (int p = 0; p <2; p++) {
            players[p][0].setRadius(200);
            players[p][0].setMass(200);
        }
//*/
        ///////TEST 2
/*
        ball = new Ball(new Vector(x + width/2, y + height/5), this);

        players = new Player[2][3];
        players[0][0] = new Player(new Vector(x + width/2, y + height/4), field);
        players[0][1] = new Player(new Vector(x + width/2, y + 3*(height/4)), field);
        players[0][2] = new Player(new Vector(x + width/2, y + height/2), field);
        players[1][0] = new Player(new Vector(x + 1*(width/3), y + height/2), field);
        players[1][1] = new Player(new Vector(x + 2*(width/3), y + height/2), field);
        players[1][2] = new Player(new Vector(x + 4*(width/5), y + height/2), field);

        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < players[p].length; i++) {
                players[p][i].setRadius(200);
            }
        }
//*/
        ///////TEST 3
/*
        ball = new Ball(new Vector(x + width/2, y + height/5), this);

        players = new Player[2][2];
        players[0][0] = new Player(new Vector(x + width/2, y + height/2), field);
        players[0][1] = new Player(new Vector(x + 1*(width/3), y + height/2), field);
        players[1][0] = new Player(new Vector(x + 2*(width/3), y + height/2), field);
        players[1][1] = new Player(new Vector(x + 4*(width/5), y + height/2), field);

        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < players[p].length; i++) {
                players[p][i].setRadius(200);
            }
        }

        players[0][0].push(new Vector(1000, 0));
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

    public void setParameters(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setGoals() {
        goals[0] = new Goal(Goal.Direction.WEST, x, y + height/2 - GOAL_HEIGHT/2, GOAL_WIDTH, GOAL_HEIGHT, field);
        goals[1] = new Goal(Goal.Direction.EAST, x + width - GOAL_WIDTH, y + height/2 - GOAL_HEIGHT/2, GOAL_WIDTH, GOAL_HEIGHT, field);
    }

    public void start() {
        ball.start();
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < players[p].length; i++)
                players[p][i].start();
        }
    }

    public void terminate() {
        ball.terminate();
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i].terminate();
        }
    }

    public void reset() {   //OVO ISPRAVI LEPO SABANE :)))
        synchronized (field) {
            ball.reset();
            ball.setCenter(new Vector(x + width * BALL_X, y + height * BALL_Y));
            for (int p = 0; p < 2; p++) {
                for (int i = 0; i < 3; i++) {
                    players[p][i].reset();
                    players[p][i].setCenter(new Vector(x + width * PLAYER_X[p][i], y + height * PLAYER_Y[p][i]));
                }
            }

            field.reset();
        }
    }

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

    public Goal[] getGoals() {
        return goals;
    }

    public SoccerField getField() {
        return field;
    }

    public void allStopped() {}

    public void goalMissed(int player, double missed) {
        Log.d(GOAL_TAG, "Ball missed goal " + player + " by " + missed);
    }

    public abstract void score(int player);
}
