package com.example.myapplication.model.collidables;

import com.example.myapplication.model.collidables.active.Circle;

public interface Collidable {

    double getDistance(Circle circle);

    void collisionUpdateSpeed(Circle circle);

    double nextCollisionTime(Circle circle);

}
