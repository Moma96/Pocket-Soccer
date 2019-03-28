package com.example.myapplication.model.soccer;

import com.example.myapplication.model.collidables.Circle;
import com.example.myapplication.model.Vector;

public class Ball extends Circle {

    private static final double MASS = 0.3; //0.3
    private static final double RADIUS = 40; //40
    private static final double IMG_RADIUS = RADIUS*2.5;

    public Ball(Vector center) {
        super(MASS, RADIUS, IMG_RADIUS, center);
        addCollidable(this);
    }

    /*protected synchronized void () {
        notifyAll();
    }*/
}
