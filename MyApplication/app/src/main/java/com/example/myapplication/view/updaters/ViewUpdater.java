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

public class ViewUpdater extends Thread {

    private static final int FPS = 30;
    private static final String STATE_TAG = "View updater";

    private GameplayActivity gameplay;
    private BallImageUpdater ballImageUpdater;
    private static ArrayList<ImageView> views;

    public ViewUpdater(GameplayActivity gameplay) {
        this.gameplay = gameplay;

        FrameLayout frame_layout = gameplay.findViewById(R.id.background);
        views = new ArrayList<>();

        Ball ball = gameplay.getSoccerModel().getBall();
        ImageView ballImageView = setAndAddImage(frame_layout, ball, R.drawable.ball0);
        ballImageUpdater = new BallImageUpdater(this, ballImageView);
        views.add(ballImageView);

        for (Player player : gameplay.getSoccerModel().getPlayer1())
            views.add(setAndAddImage(frame_layout, player, R.drawable.img5));

        for (Player player : gameplay.getSoccerModel().getPlayer2())
            views.add(setAndAddImage(frame_layout, player, R.drawable.img25));
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
                for (int i = 0; i < views.size(); i++) {
                   // if (activeObjects.get(i) instanceof Circle) {
                        Circle circle = (Circle)activeObjects.get(i);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) views.get(i).getLayoutParams();
                        params.leftMargin = (int) (circle.getCenter().getX() - circle.getImgRadius());
                        params.topMargin = (int) (circle.getCenter().getY() - circle.getImgRadius());
                        views.get(i).setLayoutParams(params);
                   // }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void run() {
        Log.d(STATE_TAG, "View updater started!");
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

        Log.d(STATE_TAG, "View updater finished!");
    }
}
