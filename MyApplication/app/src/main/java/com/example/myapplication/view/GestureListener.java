package com.example.myapplication.view;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.myapplication.view.activities.GameplayActivity;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private GameplayActivity activity;

    public void setActivity(GameplayActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        activity.respondOnSwipe(e1.getX(), e1.getY(), e2.getX(), e2.getY());
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        activity.respondOnDown(e.getX(), e.getY());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        activity.respondOnTap(e.getX(), e.getY());
        return true;
    }
}