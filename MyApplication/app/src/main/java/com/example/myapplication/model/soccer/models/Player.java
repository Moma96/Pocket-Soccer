package com.example.myapplication.model.soccer.models;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.Vector;

import org.jetbrains.annotations.NotNull;

public class Player extends Circle {

    private static final double MASS = 10; //10
    private static final double RADIUS = 80; //80
    public static final double IMG_RADIUS_COEFFICIENT = 1.05;
    public static final double SELECTION_RADIUS_COEFFICIENT = 1.5;

    private double selectionRadius;

    public Player(@NotNull Vector center, SoccerField field) {
        super(MASS, RADIUS, IMG_RADIUS_COEFFICIENT, center, field);
        setSelectionRadius();
    }

    public Player(@NotNull Player player) {
        super(player);
        setSelectionRadius();
    }

    public Player(@NotNull Player player, @NotNull SoccerField field) {
        super(player, field);
        setSelectionRadius();
    }

    protected Player(Player player, boolean include) {
        super(player, include);
        selectionRadius = getRadius()*SELECTION_RADIUS_COEFFICIENT;
    }

    public double getSelectionRadius() {
        return selectionRadius;
    }

    public void setSelectionRadius() {
        selectionRadius = getRadius()*SELECTION_RADIUS_COEFFICIENT;
    }

    public void push(Vector force) {
        setSpeed(force.mul(MOVING_INCREMENT));
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
