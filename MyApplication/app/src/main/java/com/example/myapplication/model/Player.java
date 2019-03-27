package com.example.myapplication.model;

public class Player extends Circle {

    private static final double MASS = 1; //1
    private static final double RADIUS = 100; // 100
    public static final double IMG_RADIUS = RADIUS*1.05;

    public static synchronized Player getPlayer(Vector dot) {
        Collidable collidable = Circle.getCollidable(dot);
        if (collidable instanceof Player) {
            return (Player)collidable;
        }
        return null;
    }

    public Player(Vector center) {
        super(MASS, RADIUS, IMG_RADIUS, center);
        addCollidable(this);
    }

    public void push(Vector force) {
        setSpeed(force);
    }
}
