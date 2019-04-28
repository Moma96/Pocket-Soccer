package com.example.myapplication.view.activities;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.example.myapplication.R;
import com.example.myapplication.model.soccer.SoccerFacade;
import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.view.GestureListener;
import com.example.myapplication.view.updaters.ViewUpdater;

public class GameplayActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetectorCompat;
    private SoccerModel soccerModel;
    private SoccerFacade soccerFacade;
    private ViewUpdater viewUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        final FrameLayout background = findViewById(R.id.background);

        int field = getResources().getIdentifier("field" + 3, "drawable", getPackageName());
        background.setBackgroundResource(field);

        background.post(new Runnable() {
            @Override
            public void run() {
                setup(background);
            }
        });
    }

    public ViewUpdater getViewUpdater() {
        return viewUpdater;
    }

    private void setup(FrameLayout background) {

        soccerModel = new SoccerModel(0, 0, background.getWidth(), background.getHeight());
        viewUpdater = new ViewUpdater(this, soccerModel);
        soccerFacade = new SoccerFacade(this, soccerModel, viewUpdater);

        soccerModel.start();
        viewUpdater.start();

        GestureListener gestureListener = new GestureListener(soccerFacade);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);
    }

    public SoccerModel getSoccerModel() {
        return soccerModel;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return true;
    }

}
