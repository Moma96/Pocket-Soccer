package com.example.myapplication.model.soccer.models;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.Vector;

public class Player extends Circle {

    private static final double MASS = 10; //10
    private static final double RADIUS = 80; //80
    public static final double IMG_RADIUS_COEFFICIENT = 1.05;
    public static final double SELECTION_RADIUS_COEFFICIENT = 1.5;

    private double selectionRadius;

    public Player(Vector center, SoccerModel soccer) {
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center, soccer.getField());
        selectionRadius = getRadius()*SELECTION_RADIUS_COEFFICIENT;
        soccer.getField().addCollidable(this);
    }

    public Player(Player player) {
        super(player);
    }

    public double getSelectionRadius() {
        return selectionRadius;
    }

    public void push(Vector force) {
        setSpeed(force.mul(MOVING_INCREMENT));
    }

    public String toString() {
        return "Player " + getActiveId();
    }

    public ActiveObject getCopy() {
        return new Player(this);
    }

    public void drawSelection(ImageView view) {
        if (view == null) return;

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.leftMargin = (int) (getCenter().getX() - getSelectionRadius());
        params.topMargin = (int) (getCenter().getY() - getSelectionRadius());
        view.setLayoutParams(params);
    }
}
