package com.example.myapplication.model.soccer;

import com.example.myapplication.model.collidables.ActiveObject;
import com.example.myapplication.model.collidables.Circle;
import com.example.myapplication.model.Vector;

public class Player extends Circle {

    private static final double MASS = 3; //3
    private static final double RADIUS = 100; // 100
    public static final double IMG_RADIUS = RADIUS*1.05;

    public static synchronized Player getPlayer(Vector dot) {
        ActiveObject active = ActiveObject.getActive(dot);
        if (active instanceof Player) {
            return (Player)active;
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
