package com.example.myapplication.model.soccer.models;

import android.util.Log;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.active.Circle;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public abstract class SoccerModel implements Serializable {

    protected static final String GOAL_TAG = "Goal";

    public static final double DEFAULT_GAME_SPEED = 1;
    public static final double DEFAULT_FRICTION = 0.1;
    public static final double DEFAULT_BALL_MASS = 0.4;

    public static final double GOAL_WIDTH = 100;
    public static final double GOAL_HEIGHT = 300;

    public static final double BALL_POSITION_X = 1.0/2;
    public static final double BALL_POSITION_Y = 1.0/2;

    public static final double[][] PLAYER_POSITION_X = {{ 1.0/4, 1.0/8, 1.0/8 },
                                                      { 3.0/4, 7.0/8, 7.0/8 }};

    public static final double[][] PLAYER_POSITION_Y = {{ 1.0/2, 1.0/5, 4.0/5 },
                                                      { 1.0/2, 1.0/5, 4.0/5 }};

    protected SoccerField field;
    transient private Goal[] goals = new Goal[2];

    protected Ball ball;
    protected Player[][] players;

    private double x;
    private double y;
    private double width;
    private double height;

    public SoccerModel() {}

    public SoccerModel(double x, double y, double width, double height, double friction, double gamespeed, double ballMass) {
        this(x, y, width, height, friction, gamespeed, ballMass, null, null);
    }

    public SoccerModel(@NotNull SoccerModel soccer) {
        this(soccer.x, soccer.y, soccer.width, soccer.height, soccer.getField().getFriction(),
                soccer.getField().getGamespeed(), soccer.getBall().getMass(), soccer.getBall(), soccer.players);
    }

    public SoccerModel(double x, double y, double width, double height, double friction, double gamespeed, double ballMass, Ball ball, Player[][] players) {
        setParameters(x, y, width, height);
        field = new SoccerField(x, y, width, height, friction,  gamespeed, this);
        setGoals();

        int length1 = (players == null ? PLAYER_POSITION_X.length : players.length);
        int length2 = (players == null ? PLAYER_POSITION_X[0].length : players[0].length);


        if (ball == null) {
            this.ball = new Ball(new Vector(x + width*BALL_POSITION_X, y + height*BALL_POSITION_Y), ballMass, this);
        } else {
            this.ball = new Ball(ball, field,this);
        }

        this.players = new Player[length1][length2];
        for (int p = 0; p < length1; p++) {
            for (int i = 0; i < length2; i++) {
                if (players == null) {
                    this.players[p][i] = new Player(new Vector(x + width * PLAYER_POSITION_X[p][i], y + height * PLAYER_POSITION_Y[p][i]), field);
                } else {
                    this.players[p][i] = new Player(players[p][i], field);
                }
            }
        }

//*/
        //////TEST 1
/*
        ball = new Ball(new Vector(x + width/2, y + height/2), Circle.MOVING_DELAY, this);
        ball.setRadius(100);

        players = new Player[2][1];
        players[0][0] = new Player(new Vector(x + width/4, y + height/2), Circle.MOVING_DELAY, field);
        players[1][0] = new Player(new Vector(x + 3*(width/4), y + height/2), Circle.MOVING_DELAY, field);

        players[0][0].push(new Vector(100, 0));
        players[1][0].push(new Vector(-100, 0));

        for (int p = 0; p <2; p++) {
            players[p][0].setRadius(150);
            players[p][0].setMass(1000);
        }
//*/
        ///////TEST 2
/*
        ball = new Ball(new Vector(x + width/2, y + height/5), Circle.MOVING_DELAY, this);

        players = new Player[2][3];
        players[0][0] = new Player(new Vector(x + width/2, y + height/4), Circle.MOVING_DELAY, field);
        players[0][1] = new Player(new Vector(x + width/2, y + 3*(height/4)), Circle.MOVING_DELAY, field);
        players[0][2] = new Player(new Vector(x + width/2, y + height/2), Circle.MOVING_DELAY, field);
        players[1][0] = new Player(new Vector(x + 1*(width/3), y + height/2), Circle.MOVING_DELAY, field);
        players[1][1] = new Player(new Vector(x + 2*(width/3), y + height/2), Circle.MOVING_DELAY, field);
        players[1][2] = new Player(new Vector(x + 4*(width/5), y + height/2), Circle.MOVING_DELAY, field);

        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < players[p].length; i++) {
                players[p][i].setRadius(200);
            }
        }
//*/
        ///////TEST 3
/*
        ball = new Ball(new Vector(x + width/2, y + height/5), Circle.MOVING_DELAY, this);

        players = new Player[2][2];
        players[0][0] = new Player(new Vector(x + width/2, y + height/2), Circle.MOVING_DELAY, field);
        players[0][1] = new Player(new Vector(x + 1*(width/3), y + height/2), Circle.MOVING_DELAY, field);
        players[1][0] = new Player(new Vector(x + 2*(width/3), y + height/2), Circle.MOVING_DELAY, field);
        players[1][1] = new Player(new Vector(x + 4*(width/5), y + height/2), Circle.MOVING_DELAY, field);

        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < players[p].length; i++) {
                players[p][i].setRadius(150);
            }
        }

        players[0][0].push(new Vector(1000, 0));
//*/
/*
        ///////TEST 4 - goalpost collision testing -> GOAL_WIDTH = 120;

        ball = new Ball(new Vector(x + width/2, y + height/2), 300, this);

        players = new Player[2][1];
        players[0][0] = new Player(new Vector(x + width/4, y + height/2), 300, field);
        players[1][0] = new Player(new Vector(x + 3*(width/4), y + height/2), 300, field);
        players[0][0].setRadius(30);
        players[1][0].setRadius(30);

        players[0][0].push(new Vector(-2000, -700));
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
        field.start();
    }

    public void terminate() {
        field.terminate();
    }

    public void pause() {
        field.inactive();
    }

    public void resume() {
        field.active();
    }

    public void reset() {   //OVO ISPRAVI LEPO SABANE :)))
        synchronized (field) {
            ball.clearSpeed();
            ball.setCenter(new Vector(x + width * BALL_POSITION_X, y + height * BALL_POSITION_Y));
            for (int p = 0; p < players.length; p++) {
                for (int i = 0; i < players[p].length; i++) {
                    players[p][i].clearSpeed();
                    players[p][i].setCenter(new Vector(x + width * PLAYER_POSITION_X[p][i], y + height * PLAYER_POSITION_Y[p][i]));
                }
            }
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

    public void circlesMoved() {}
}
