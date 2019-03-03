package com.example.myapplication.model;

public class Ball extends Circle {

    private static final double MASS = 0.3;
    private static final double RADIUS = 70;

    public Ball(Vector center) {
        super(MASS, RADIUS, center);
        addCircle(this);
    }
}
