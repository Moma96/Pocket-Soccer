package com.example.myapplication.view.activities;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.model.soccer.Player;
import com.example.myapplication.model.soccer.SoccerModel;
import com.example.myapplication.model.Vector;
import com.example.myapplication.view.listeners.SwipeGestureListener;
import com.example.myapplication.view.updaters.ViewUpdater;

public class GameplayActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetectorCompat;
    private SoccerModel soccerModel;
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

    private void setup(FrameLayout background) {
        soccerModel = new SoccerModel(0, 0, background.getWidth(), background.getHeight());

        viewUpdater = new ViewUpdater(this);
        viewUpdater.start();

        SwipeGestureListener gestureListener = new SwipeGestureListener();
        gestureListener.setActivity(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);
    }

    public SoccerModel getSoccerModel() {
        return soccerModel;
    }

    public void respondOnSwipe(float x1, float y1, float x2, float y2) {
        Player player = Player.getPlayer(new Vector(x1, y1));
        if (player != null) {
            player.push(new Vector(x2 - x1, y2 - y1));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return true;
    }

}
