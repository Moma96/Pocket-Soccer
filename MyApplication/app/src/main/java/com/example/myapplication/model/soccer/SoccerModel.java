package com.example.myapplication.model.soccer;

import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.Vector;

public class SoccerModel {

    private static final double GOAL_WIDTH = 300;
    private static final double GOAL_HEIGHT = 100;

    private static final double BALL_X = 1.0/2;
    private static final double BALL_Y = 1.0/2;

    private static final double[] PLAYER_1_X = { 1.0/2, 1.0/5, 4.0/5 };
    private static final double[] PLAYER_1_Y = { 1.0/4, 1.0/8, 1.0/8 };

    private static final double[] PLAYER_2_X = { 1.0/2, 1.0/5, 4.0/5 };
    private static final double[] PLAYER_2_Y = { 3.0/4, 7.0/8, 7.0/8 };

    private SoccerField field;
    private Goal[] goals = new Goal[2];

    private Player[] player1 = new Player[3];
    private Player[] player2 = new Player[3];

    private Ball ball;

    public SoccerModel(double x, double y, double width, double height) {

        field = new SoccerField(x, y, width, height);
        Circle.setField(field);
        goals[0] = new Goal(GoalPost.Direction.NORTH, x + width/2 - GOAL_WIDTH/2, y, GOAL_WIDTH, GOAL_HEIGHT);
        goals[1] = new Goal(GoalPost.Direction.SOUTH, x + width/2 - GOAL_WIDTH/2, y + height - GOAL_HEIGHT, GOAL_WIDTH, GOAL_HEIGHT);

/*
        ball = new Ball(new Vector(x + width*BALL_X, y + height*BALL_Y), this);

        for (int i = 0; i < 3; i++) {
            player1[i] = new Player(new Vector(x + width * PLAYER_1_X[i], y + height * PLAYER_1_Y[i]));
            player2[i] = new Player(new Vector(x + width * PLAYER_2_X[i], y + height * PLAYER_2_Y[i]));
        }

        ball.start();

        for (int i = 0; i < 3; i++) {
            player1[i].start();
            player2[i].start();
        }
//*/
        //////TEST 1
/*
        ball = new Ball(new Vector(x + width/2, y + height/2), this);
        ball.setRadius(150);

        player1[0] = new Player(new Vector(x + width/4, y + height/2));
        player2[0] = new Player(new Vector(x + 3*(width/4), y + height/2));
        player1[0].setRadius(200);
        player2[0].setRadius(200);
        player1[0].setMass(100);
        player2[0].setMass(100);
        player1[0].setSpeed(new Vector(10, 0));
        player2[0].setSpeed(new Vector(-10, 0));

        ball.start();
        player1[0].start();
        player2[0].start();
//*/
        ///////TEST 2

        ball = new Ball(new Vector(x + width/2, y + height/5), this);

        player1[0] = new Player(new Vector(x + width/4, y + height/2));
        player1[1] = new Player(new Vector(x + 3*(width/4), y + height/2));
        player1[2] = new Player(new Vector(x + width/2, y + height/2));
        player2[0] = new Player(new Vector(x + width/2, y + 1*(height/3)));
        player2[1] = new Player(new Vector(x + width/2, y + 2*(height/3)));
        player2[2] = new Player(new Vector(x + width/2, y + 4*(height/5)));

        ball.start();

        for (int i = 0; i < 3; i++) {
            player1[i].setRadius(200);
            player2[i].setRadius(200);
            player1[i].start();
            player2[i].start();
        }
//*/
    }

    public Ball getBall() {
        return ball;
    }

    public Player[] getPlayer1() {
        return player1;
    }

    public Player[] getPlayer2() {
        return player2;
    }

    public Goal[] getGoals() { return goals; }
}
