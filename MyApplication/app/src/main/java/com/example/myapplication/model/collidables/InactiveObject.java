package com.example.myapplication.model.collidables;

public abstract class InactiveObject implements Collidable {

    @Override
    public Collidable beforeCollision(ActiveObject active) {
        return this;
    }

    @Override
    public void duringCollision(ActiveObject active) {
    }
}
