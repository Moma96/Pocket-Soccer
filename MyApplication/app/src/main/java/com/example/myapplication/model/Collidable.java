package com.example.myapplication.model;

public interface Collidable {

    double getDistance(Circle circle);

    void collisionUpdateSpeed(Circle circle); //ActiveObject

    boolean isInside(Vector dot);

    //void preCollision(Collidable collided);

}
