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
    public Collidable beforeCollision(Circle active) {
        return this;
    }

    @Override
    public void duringCollision(Circle active) {
    }

    public String toString() {
        return "InactiveObject " + id;
    }
}
