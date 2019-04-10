package com.example.myapplication.view.updaters;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.soccer.Ball;
import com.example.myapplication.model.collidables.active.Circle;
import com.example.myapplication.model.soccer.Player;
import com.example.myapplication.view.activities.GameplayActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewUpdater extends Thread {

    private static final int FPS = 30;
    private static final String STATE_TAG = "View updater";

    private GameplayActivity gameplay;
    private BallImageUpdater ballImageUpdater;
    private HashMap<ActiveObject, ImageView> views = new HashMap<>();

    public ViewUpdater(GameplayActivity gameplay) {
        this.gameplay = gameplay;

        FrameLayout frame_layout = gameplay.findViewById(R.id.background);

        Ball ball = gameplay.getSoccerModel().getBall();
        ImageView ballImageView = setAndAddImage(frame_layout, ball, R.drawable.ball0);
        ballImageUpdater = new BallImageUpdater(this, ballImageView);
        views.put(ball, ballImageView);

        int team1 = gameplay.getResources().getIdentifier("team" + 25, "drawable", gameplay.getPackageName());
        int team2 = gameplay.getResources().getIdentifier("team" + 5, "drawable", gameplay.getPackageName());

        for (Player player : gameplay.getSoccerModel().getPlayer1())
            views.put(player, setAndAddImage(frame_layout, player, team1));

        for (Player player : gameplay.getSoccerModel().getPlayer2())
            views.put(player, setAndAddImage(frame_layout, player, team2));
    }

    public GameplayActivity getGameplay() {
        return gameplay;
    }

    private ImageView setAndAddImage(FrameLayout frame_layout, Circle circle, int resid) {
        ImageView img = new ImageView(gameplay);
        img.setBackgroundResource(resid);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(circle.getImgRadius()*2), (int)(circle.getImgRadius()*2));
        params.leftMargin = (int)(circle.getCenter().getX() - circle.getImgRadius());
        params.topMargin  = (int)(circle.getCenter().getY() - circle.getImgRadius());
        frame_layout.addView(img, params);

        return img;
    }

    private void refresh() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ActiveObject> activeObjects = ActiveObject.getActiveCollidables();
                for (ActiveObject active : activeObjects)
                    active.draw(views.get(active));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void run() {
        Log.d(STATE_TAG, "View updater started");
        setPriority(MAX_PRIORITY - 1);

        ballImageUpdater.start();
        while(!gameplay.isDestroyed()) {
            refresh();
            try {
                sleep( 1000 / FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(STATE_TAG, "View updater finished");
    }
}
