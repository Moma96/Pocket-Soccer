package com.example.myapplication.model.collidables;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.collidables.ActiveObject;

public interface Collidable {

    double getDistance(ActiveObject active); // circle - active

    void collisionUpdateSpeed(ActiveObject active); //ActiveObject

    boolean isInside(Vector dot);

}