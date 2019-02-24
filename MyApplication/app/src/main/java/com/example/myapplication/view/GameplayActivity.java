package com.example.myapplication.view;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;

import com.example.myapplication.R;
import com.example.myapplication.model.Player;
import com.example.myapplication.model.SoccerModel;
import com.example.myapplication.model.Vector;

public class GameplayActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetectorCompat;
    private SoccerModel soccerModel;
    private ViewUpdater viewUpdater;
    //private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        soccerModel = new SoccerModel(0, 0, size.x, size.y - 38);

        viewUpdater = new ViewUpdater(this);
        viewUpdater.start();

        SwipeGestureListener gestureListener = new SwipeGestureListener();
        gestureListener.setActivity(this);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);
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
