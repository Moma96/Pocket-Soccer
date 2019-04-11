package com.example.myapplication.model.soccer;

import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.Vector;

public class Ball extends Circle {

    private static final double MASS = 1;
    private static final double RADIUS = 25;
    private static final double IMG_RADIUS_COEFFICIENT = 2.5;

    public Ball(Vector center) {
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center);
        addCollidable(this);
    }

    private void Work() { // ckeck if inside goaL

    }
}
