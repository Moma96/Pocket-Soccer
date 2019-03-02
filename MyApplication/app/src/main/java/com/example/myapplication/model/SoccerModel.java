package com.example.myapplication.model;


public class SoccerModel {

    Field field;

    Player[] player1 = new Player[3];
    Player[] player2 = new Player[3];

    Ball ball;

    public SoccerModel(int x, int y, int width, int height) {

        field = new Field(x, y, width, height);
        Circle.setField(field);

/*
        Player player1 = new Player(new Vector(x + width/2, y + height/4));
        Player player2 = new Player(new Vector(x + width/2, y + height/2));
        Player player3 = new Player(new Vector(x + width/2, y + 3*(height/4)));


        player1.start();
        player2.start();
        player3.start();
        //*/


        ball = new Ball(new Vector(x + width/2, y + height/2));

        player1[0] = new Player(new Vector(x + width/2, y + height/4));
        player1[1] = new Player(new Vector(x + width/4, y + height/4));
        player1[2] = new Player(new Vector(x + 3*(width/4), y + height/4));

        player2[0] = new Player(new Vector(x + width/2, y + 3*(height/4)));
        player2[1] = new Player(new Vector(x + width/4, y + 3*(height/4)));
        player2[2] = new Player(new Vector(x + 3*(width/4), y + 3*(height/4)));

        ball.start();

        for (int i = 0; i < 3; i++) {
            player1[i].start();
            player2[i].start();
        }

        //Player extraplayer1 = new Player(new Vector(x + width/4, y + height/2));
        //Player extraplayer2 = new Player(new Vector(x + 3*(width/4), y + height/2));

        //extraplayer1.start();
        //extraplayer2.start();

       // */
    }

}
