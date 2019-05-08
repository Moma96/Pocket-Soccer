package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.Vector;

public abstract class SoccerModel {

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
    protected Player[][] players = new Player[2][3];

    private double x;
    private double y;
    private double width;
    private double height;

    public SoccerModel() {}

    public SoccerModel(double x, double y, double width, double height) {
        setField(x, y, width, height);
        setGoals();

        ball = new Ball(new Vector(x + width*BALL_X, y + height*BALL_Y), this);

        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
                players[p][i] = new Player(new Vector(x + width * PLAYER_X[p][i], y + height * PLAYER_Y[p][i]), field);
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

    public void setField(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        field = new SoccerField(x, y, width, height, this);
    }

    public void setGoals() {
        goals[0] = new Goal(GoalPost.Direction.WEST, x, y + height/2 - GOAL_HEIGHT/2, GOAL_WIDTH, GOAL_HEIGHT, field);
        goals[1] = new Goal(GoalPost.Direction.EAST, x + width - GOAL_WIDTH, y + height/2 - GOAL_HEIGHT/2, GOAL_WIDTH, GOAL_HEIGHT, field);
    }

    public void start() {
        ball.start();
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++)
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

    public void reset() {
        ball.clearSpeed();
        ball.setCenter(new Vector(x + width*BALL_X, y + height*BALL_Y));
        for (int p = 0; p < 2; p++) {
            for (int i = 0; i < 3; i++) {
                players[p][i].clearSpeed();
                players[p][i].setCenter(new Vector(x + width * PLAYER_X[p][i], y + height * PLAYER_Y[p][i]));
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

    public abstract boolean score(int player);
}
