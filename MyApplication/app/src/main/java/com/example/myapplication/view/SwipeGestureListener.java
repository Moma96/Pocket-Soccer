package com.example.myapplication.view;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    private GameplayActivity activity;

    public void setActivity(GameplayActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        activity.respondOnSwipe(e1.getX(), e1.getY(), e2.getX(), e2.getY());
        return true;
    }
}