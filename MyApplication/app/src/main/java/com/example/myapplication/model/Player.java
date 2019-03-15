package com.example.myapplication.model;

import java.util.ArrayList;

public class Player extends Circle {

    private static final double MASS = 1;
    private static final double RADIUS = 100;
    public static final double IMG_RADIUS = RADIUS*1.05;

    public static synchronized Player getPlayer(Vector dot) {
        Circle circle = Circle.getCircle(dot);
        if (circle instanceof Player) {
            return (Player)circle;
        }
        return null;
    }

    public Player(Vector center) {
        super(MASS, RADIUS, IMG_RADIUS, center);
        addCircle(this);
    }

    public void push(Vector force) {
        setSpeed(force);
        ArrayList<Circle> circles = Circle.getCircles();
        synchronized (circles) {
            circles.notifyAll();
        }
    }
}
