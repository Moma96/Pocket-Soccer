package com.example.myapplication.model;

public class Ball extends Circle {

    private static final int MASS = 20;
    private static final int RADIUS = 60;

    public Ball(Vector center) {
        super(MASS, RADIUS, center);
        addCircle(this);
    }
}
