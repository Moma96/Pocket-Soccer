package com.example.myapplication.model.collidables;

import com.example.myapplication.model.collidables.inactive.Wall;

public abstract class Field {

    protected Wall walls[];

    public Wall[] getWalls() {
        return walls;
    }

    public void setWalls(Wall[] walls) {
        this.walls = walls;
    }
}
