package com.example.myapplication.model;

public interface Collidable {

    double getDistance(Circle circle); // circle - active

    void collisionUpdateSpeed(Circle circle); //ActiveObject

    boolean isInside(Vector dot);

}
