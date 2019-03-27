package com.example.myapplication.model.collidables;

import com.example.myapplication.model.Vector;

public class Circle extends ActiveObject {

    private double radius;
    private double img_radius;

    public Circle(double mass, double radius, double img_radius, Vector center, Vector speed) {
        super(mass, center, speed);
        this.radius = radius;
        this.img_radius = img_radius;
    }

    public Circle(Circle circle) {
        super(circle);
        if (circle == null) return;
        radius = circle.radius;
        img_radius = circle.img_radius;
    }

    public Circle(double mass, double radius, double img_radius, Vector center) {
        super(mass, center);
        this.radius = radius;
        this.img_radius = img_radius;
    }
/*
    @Override
    public ActiveObject getIdenticalCopy() {
        Circle copy = new Circle(this);
        copy.id = id;
        return copy;
    }*/

    public ActiveObject getCopy() {
        return new Circle(this);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getImgRadius() {
        return img_radius;
    }

    public void setImgRadius(double img_radius) {
        this.img_radius = img_radius;
    }
}
