package com.example.myapplication.model;

public class Field {

    private Wall walls[] = new Wall[4];

    public Field (int x, int y, int width, int height) {
        walls[0] = new Wall(Wall.Direction.NORTH, y); //top
        walls[1] = new Wall(Wall.Direction.SOUTH, y + height); //bottom
        walls[2] = new Wall(Wall.Direction.WEST, x); //left
        walls[3] = new Wall(Wall.Direction.EAST, x + width); //right
        for (Wall wall : walls) {
            Circle.addCollidable(wall);
        }
    }

    public Wall[] getWalls() {
        return walls;
    }

    public void setWalls(Wall[] walls) {
        this.walls = walls;
    }

    /*
        public Field(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    */
    /*
    public double top() {
        //return y;
        return walls[0].getXY();
    }

    public double bottom() {

        //return y + height;
        return walls[1].getXY();
    }

    public double left() {
        //return x;
        return walls[2].getXY();
    }

    public double right() {
        //return x + width;
        return walls[3].getXY();
    }*/
}
