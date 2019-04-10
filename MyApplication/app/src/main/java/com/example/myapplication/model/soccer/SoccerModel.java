package com.example.myapplication.model.soccer;


import com.example.myapplication.model.collidables.Circle;
import com.example.myapplication.model.Vector;

public class SoccerModel {

    SoccerField field;

    Player[] player1 = new Player[3];
    Player[] player2 = new Player[3];

    Ball ball;

    public SoccerModel(int x, int y, int width, int height) {

        field = new SoccerField(x, y, width, height);
        Circle.setField(field);

        ball = new Ball(new Vector(x + width/2, y + height/2));

        player1[0] = new Player(new Vector(x + width/2, y + 3*(height/8)));
        player1[1] = new Player(new Vector(x + width/4, y + height/4));
        player1[2] = new Player(new Vector(x + 3*(width/4), y + height/4));

        player2[0] = new Player(new Vector(x + width/2, y + 5*(height/8)));
        player2[1] = new Player(new Vector(x + width/4, y + 3*(height/4)));
        player2[2] = new Player(new Vector(x + 3*(width/4), y + 3*(height/4)));

        ball.start();

        for (int i = 0; i < 3; i++) {
            player1[i].start();
            player2[i].start();
        }
//*/
        //TEST
/*
        ball = new Ball(new Vector(x + width/2, y + height/2));
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
/*
        ball = new Ball(new Vector(x + width/2, y + height/5));

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
}
