package com.example.myapplication.model.soccer.models;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.active.ActiveObject;

import org.jetbrains.annotations.NotNull;

public class Circle extends ActiveObject {

    private double img_radius_coefficient;
    private double img_radius;

    public Circle(double mass, double radius, double img_radius_coefficient, Vector center, Vector speed, Field field) {
        super(mass, center, speed, field);
        this.img_radius_coefficient = img_radius_coefficient;
        setRadius(radius);
    }

    public Circle(@NotNull Circle circle) {
        super(circle);
        this.img_radius_coefficient = circle.img_radius_coefficient;
        setRadius(circle.getRadius());
    }

    public Circle(double mass, double radius, double img_radius_coefficient, Vector center, Field field) {
        super(mass, center, field);
        this.img_radius_coefficient = img_radius_coefficient;
        setRadius(radius);
    }

    public Circle(@NotNull Circle circle, Field field) {
        super(circle, field);
        this.img_radius_coefficient = circle.img_radius_coefficient;
        setRadius(circle.getRadius());
    }

    protected Circle(@NotNull Circle circle, boolean include) {
        super(circle, include);
        this.img_radius_coefficient = circle.img_radius_coefficient;
        setRadius(circle.getRadius());
    }

    @Override
    public void setRadius(double radius) {
        super.setRadius(radius);
        img_radius = radius*img_radius_coefficient;
    }

    public double getImgRadius() {
        return img_radius;
    }

    @Override
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

    @Override
    protected Circle getNonInclusiveCopy() {
        return new Circle(this, false);
    }
}
