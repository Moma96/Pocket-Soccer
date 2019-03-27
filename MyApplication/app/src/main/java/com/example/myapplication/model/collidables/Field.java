package com.example.myapplication.model.collidables;

public abstract class Field {

    protected Wall walls[];

    public Wall[] getWalls() {
        return walls;
    }

    public void setWalls(Wall[] walls) {
        this.walls = walls;
    }
}
