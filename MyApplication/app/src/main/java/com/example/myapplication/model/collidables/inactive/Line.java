package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.active.ActiveObject;

public class Line extends InactiveObject {

    @Override
    public double getDistance(ActiveObject active) {
        return 0;
    }

    @Override
    public void collisionUpdateSpeed(ActiveObject active) {

    }

    public enum Orientation { HORIZONTAL, VERTICAL };

    private Orientation orientation;
    private double x;
    private double y;
    private double length;

    public Line(Orientation orientation, double x, double y, double length) {
        this.orientation = orientation;
        this.x = x;
        this.y = y;
        this.length = length;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String toString() {
        return "Wall";
    }
}
