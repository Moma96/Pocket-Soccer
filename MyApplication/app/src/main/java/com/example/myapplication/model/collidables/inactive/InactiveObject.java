package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.collidables.Collidable;
import com.example.myapplication.model.collidables.active.ActiveObject;

public abstract class InactiveObject implements Collidable {

    protected static int next_id = 0;
    protected int id;

    public InactiveObject() {
        id = next_id++;
    }

    @Override
    public Collidable beforeCollision(ActiveObject active) {
        return this;
    }

    @Override
    public void duringCollision(ActiveObject active) {
    }

    public String toString() {
        return "InactiveObject " + id;
    }
}
