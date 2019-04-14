package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.Vector;

public class Player extends Circle {

    private static final double MASS = 10;
    private static final double RADIUS = 80; //80;
    public static final double IMG_RADIUS_COEFFICIENT = 1.05;

    public static synchronized Player getPlayer(Vector dot) {
        ActiveObject active = ActiveObject.getActive(dot);
        if (active instanceof Player) {
            return (Player)active;
        }
        return null;
    }

    public Player(Vector center) {
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center);
        addCollidable(this);
    }

    public Player(Player player) {
        super(player);
    }

    public void push(Vector force) {
        setSpeed(force.mul(MOVING_INCREMENT));
    }

    public String toString() {
        return "Player " + id;
    }

    public ActiveObject getCopy() {
        return new Player(this);
    }
}
