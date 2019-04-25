package com.example.myapplication.view;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.myapplication.model.soccer.SoccerFacade;
import com.example.myapplication.view.activities.GameplayActivity;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private SoccerFacade facade;

    public GestureListener(SoccerFacade facade) {
        super();
        this.facade = facade;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (facade != null)
            facade.respondOnSwipe(e1.getX(), e1.getY(), e2.getX(), e2.getY());
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (facade != null)
            facade.respondOnDown(e.getX(), e.getY());
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (facade != null)
            facade.respondOnTap(e.getX(), e.getY());
        return true;
    }

}