package com.example.myapplication.model.soccer.models;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.Vector;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Player extends Circle implements Serializable {

    private static final double MASS = 1;
    private static final double RADIUS = 80; //80
    public static final double IMG_RADIUS_COEFFICIENT = 1.05;
    public static final double SELECTION_RADIUS_COEFFICIENT = 1.5;

    private double selectionRadius;

    public Player(Vector center, Vector speed, SoccerField field) {
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center, speed, field);
    }

    public Player(Vector center, SoccerField field) {
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center, field);
    }

    public Player(@NotNull Player player) {
        super(player);
    }

    public Player(@NotNull Player player, @NotNull SoccerField field) {
        super(player, field);
    }

    @Override
    public void setRadius(double radius) {
        super.setRadius(radius);
        selectionRadius = getRadius()*SELECTION_RADIUS_COEFFICIENT;
    }

    protected Player(Player player, boolean include) {
        super(player, include);
    }

    public double getSelectionRadius() {
        return selectionRadius;
    }

    public void push(Vector force) {
        setSpeed(force.mul(MOVING_INCREMENT));
        //setSpeed(force);
    }

    public void drawSelection(ImageView view) {
        if (view == null) return;

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.leftMargin = (int) (getCenter().getX() - getSelectionRadius());
        params.topMargin = (int) (getCenter().getY() - getSelectionRadius());
        view.setLayoutParams(params);
    }

    @Override
    protected Player getNonInclusiveCopy() {
        return new Player(this, false);
    }

    @Override
    public String toString() {
        return "Player " + getCircleId();
    }
}
