package com.example.myapplication.model;

public class Vector {
    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Vector v) {
        if (v == null) return;
        x = v.x;
        y = v.y;
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

    public double intensity() {
        return Math.sqrt(x*x + y*y);
    }

    public Vector invert() {
        return new Vector(-x, -y);
    }

    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    public Vector sub(Vector v) {
        v = v.invert();
        return add(v);
    }

    public Vector mul(double d) {
        return new Vector(x * d, y * d);
    }

    public boolean isZeroVector() { return (x == 0 && y == 0); }

    public void clear() {
        x = 0;
        y = 0;
    }

    public double dotProduct(Vector v) {
        return x*v.x + y*v.y;
    }

    public boolean isEqual(Vector v) {
        return (x == v.x && y == v.y);
    }

    public void scaleIntensity(double scaled) {
        double coefficient = scaled / intensity();
        x *= coefficient;
        y *= coefficient;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
