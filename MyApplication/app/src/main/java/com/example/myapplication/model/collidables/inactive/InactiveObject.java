package com.example.myapplication.model.collidables.inactive;

import com.example.myapplication.model.collidables.Collidable;
import com.example.myapplication.model.collidables.active.ActiveObject;

public abstract class InactiveObject implements Collidable {

    @Override
    public Collidable beforeCollision(ActiveObject active) {
        return this;
    }

    @Override
    public void duringCollision(ActiveObject active) {
    }
}
