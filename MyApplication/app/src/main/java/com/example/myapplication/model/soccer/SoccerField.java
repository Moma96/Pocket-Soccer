package com.example.myapplication.model.soccer;

import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.collidables.Field;
import com.example.myapplication.model.collidables.inactive.Wall;

public class SoccerField extends Field {

    public SoccerField(double x, double y, double width, double height) {

        walls = new Wall[4];
        walls[0] = new Wall(Wall.Direction.NORTH, y);
        walls[1] = new Wall(Wall.Direction.SOUTH, y + height);
        walls[2] = new Wall(Wall.Direction.WEST, x);
        walls[3] = new Wall(Wall.Direction.EAST, x + width);

        for (Wall wall : walls) {
            Circle.addCollidable(wall);
        }
    }
}
