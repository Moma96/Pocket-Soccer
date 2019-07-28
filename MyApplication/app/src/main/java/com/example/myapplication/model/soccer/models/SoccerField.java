package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.inactive.Wall;

import org.jetbrains.annotations.NotNull;

public class SoccerField extends Field {

    private static final double FRICTION_COEFFICIENT = 0.01;// 0.01;

    private SoccerModel soccer;
    private double gamespeed;

    public SoccerField(double x, double y, double width, double height, double friction, double gamespeed, @NotNull SoccerModel soccer) {
        super(friction, (int)(MOVING_DELAY/gamespeed));

        walls = new Wall[4];
        walls[0] = new Wall(Wall.Direction.NORTH, y);
        walls[1] = new Wall(Wall.Direction.SOUTH, y + height);
        walls[2] = new Wall(Wall.Direction.WEST, x);
        walls[3] = new Wall(Wall.Direction.EAST, x + width);

        for (Wall wall : walls) {
            addCollidable(wall);
        }
        this.soccer = soccer;
        this.gamespeed = gamespeed;
    }

    public SoccerModel getSoccer() {
        return soccer;
    }

    public double getGamespeed() {
        return gamespeed;
    }

    protected void allStopped() {
        super.allStopped();
        soccer.allStopped();
    }
}
