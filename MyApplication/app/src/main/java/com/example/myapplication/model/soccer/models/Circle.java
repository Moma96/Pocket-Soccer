package com.example.myapplication.model.soccer.models;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.active.ActiveObject;

public class Circle extends ActiveObject {

    private double img_radius_coefficient;
    private double img_radius;

    public Circle(double mass, double radius, double img_radius_coefficient, Vector center, Vector speed, Field field) {
        super(mass, center, speed, field);
        this.img_radius_coefficient = img_radius_coefficient;
        setRadius(radius);
    }

    public Circle(Circle circle) {
        super(circle);
        if (circle == null) return;
        this.img_radius_coefficient = circle.img_radius_coefficient;
        setRadius(circle.getRadius());
    }

    public Circle(double mass, double radius, double img_radius_coefficient, Vector center, Field field) {
        super(mass, center, field);
        this.img_radius_coefficient = img_radius_coefficient;
        setRadius(radius);
    }

    @Override
    public void setRadius(double radius) {
        super.setRadius(radius);
        img_radius = radius*img_radius_coefficient;
    }

    public double getImgRadius() {
        return img_radius;
    }

    public String toString() {
        return "Circle " + getActiveId();
    }

    public void draw(ImageView view) {
        if (view == null) return;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.leftMargin = (int) (getCenter().getX() - getImgRadius());
        params.topMargin = (int) (getCenter().getY() - getImgRadius());
        view.setLayoutParams(params);
    }

    public ActiveObject getCopy() {
        return new Circle(this);
    }
}
