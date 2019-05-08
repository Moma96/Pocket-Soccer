package com.example.myapplication.model.collidables;

import com.example.myapplication.model.collidables.active.Circle;

public interface Collidable {

    double getDistance(Circle active);

    void collisionUpdateSpeed(Circle active); //Circle

    Collidable beforeCollision(Circle active);

    void duringCollision(Circle active);
}
