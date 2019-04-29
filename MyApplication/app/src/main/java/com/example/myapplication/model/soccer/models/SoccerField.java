package com.example.myapplication.model.soccer.models;

import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.inactive.Wall;

public class SoccerField extends Field {

    private static final double FRICTION_COEFFICIENT = 0.01;// 0.01;

    public SoccerField(double x, double y, double width, double height, double friction) {
        setFriction(friction);

        walls = new Wall[4];
        walls[0] = new Wall(Wall.Direction.NORTH, y);
        walls[1] = new Wall(Wall.Direction.SOUTH, y + height);
        walls[2] = new Wall(Wall.Direction.WEST, x);
        walls[3] = new Wall(Wall.Direction.EAST, x + width);

        for (Wall wall : walls) {
            addCollidable(wall);
        }
    }

    public SoccerField(double x, double y, double width, double height) {
        this(x, y, width, height, FRICTION_COEFFICIENT);
    }
}
