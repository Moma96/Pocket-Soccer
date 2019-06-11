package com.example.myapplication.view.activities;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.example.myapplication.R;
import com.example.myapplication.model.soccer.SoccerFacade;
import com.example.myapplication.model.soccer.SoccerGameplay;
import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.view.GestureListener;
import com.example.myapplication.view.updaters.ViewUpdater;

public class GameplayActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetectorCompat;
    private SoccerGameplay soccer;
    private SoccerFacade soccerFacade;
    private ViewUpdater viewUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        final FrameLayout background = findViewById(R.id.background);

        final int[] teamsimg = { 0, 1 };
        final int fieldimg = 4;

        final double friction = 0.01;
        final double gamespeed = 1;

        final boolean[] botplay = { true, true };

        int field = getResources().getIdentifier("field" + fieldimg, "drawable", getPackageName());
        background.setBackgroundResource(field);

        background.post(new Runnable() {
            @Override
            public void run() {
                setup(background, teamsimg, friction, gamespeed, botplay);
            }
        });
    }

    private void setup(FrameLayout background, final int[] teams, final double friction, final double gamespeed, final boolean[] botplay) {

        soccer = new SoccerGameplay(0, 0, background.getWidth(), background.getHeight(), friction, gamespeed, botplay);
        viewUpdater = new ViewUpdater(this, soccer, teams);
        soccerFacade = new SoccerFacade(this, soccer, viewUpdater);

        soccer.start();
        viewUpdater.start();

        GestureListener gestureListener = new GestureListener(soccerFacade);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);
    }

    public SoccerModel getSoccer() {
        return soccer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetectorCompat != null) {
            gestureDetectorCompat.onTouchEvent(event);
        }
        return true;
    }

}
