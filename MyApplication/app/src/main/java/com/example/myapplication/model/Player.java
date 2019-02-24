package com.example.myapplication.model;

import java.util.ArrayList;

public class Player extends Circle {

    private static final int MASS = 50;
    private static final int RADIUS = 100;

    public static synchronized Player getPlayer(Vector dot) {
        Circle circle = Circle.getCircle(dot);
        if (circle instanceof Player) {
            return (Player)circle;
        }
        return null;
    }

    public Player(Vector center) {
        super(MASS, RADIUS, center);
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
