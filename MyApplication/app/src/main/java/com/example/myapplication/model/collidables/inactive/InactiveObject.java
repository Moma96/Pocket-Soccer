package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.collidables.Collidable;
import com.example.myapplication.model.collidables.active.Circle;

public abstract class InactiveObject implements Collidable {

    protected static int next_id = 0;
    protected int id;

    public InactiveObject() {
        id = next_id++;
    }

    @Override
    public boolean checkCollisionProcessed(Circle circle) {
        return false;
    }

    @Override
    public void collisionHappened(Circle circle) {
        collisionUpdateSpeed(circle);
    }

    @Override
    public boolean isClose(Circle circle) {                 /////////DISTRIBUTE THIS!!!!!!!!!!!!!!!!!!!!!!!!!
        return getDistance(circle) <= circle.getCollisionZoneRadius() - circle.getRadius();
    }

    public String toString() {
        return "InactiveObject " + id;
    }
}
