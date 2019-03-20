package com.example.myapplication.model;

public class Ball extends Circle {

    private static final double MASS = 0.07; //0.3
    private static final double RADIUS = 40; //40
    private static final double IMG_RADIUS = RADIUS*2.5;

    public Ball(Vector center) {
        super(MASS, RADIUS, IMG_RADIUS, center);
        addCollidable(this);
    }
}
