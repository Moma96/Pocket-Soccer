package com.example.myapplication.view;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.model.Ball;
import com.example.myapplication.model.Circle;
import com.example.myapplication.model.Player;

import java.util.ArrayList;

public class ViewUpdater extends Thread {

    private static final int FPS = 60;

    private GameplayActivity gameplay;
    private static ArrayList<ImageView> views;

    public ViewUpdater(GameplayActivity gameplay) {
        this.gameplay = gameplay;

        FrameLayout frame_layout = gameplay.findViewById(R.id.frame_layout);
        views = new ArrayList<>();

        Ball ball = gameplay.getSoccerModel().getBall();
        setAndAddImage(frame_layout, ball, R.drawable.ball0);

        for (Player player : gameplay.getSoccerModel().getPlayer1())
            setAndAddImage(frame_layout, player, R.drawable.img5);

        for (Player player : gameplay.getSoccerModel().getPlayer2())
            setAndAddImage(frame_layout, player, R.drawable.img25);
    }

    private void setAndAddImage(FrameLayout frame_layout, Circle circle, int resid) {
        ImageView img = new ImageView(gameplay);
        img.setBackgroundResource(resid);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(circle.getImgRadius()*2), (int)(circle.getImgRadius()*2));
        params.leftMargin = (int)(circle.getCenter().getX() - circle.getImgRadius());
        params.topMargin  = (int)(circle.getCenter().getY() - circle.getImgRadius());
        frame_layout.addView(img, params);

        views.add(img);
    }

    private void refresh() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Circle> circles = Circle.getCircles();
                for (int i = 0; i < views.size(); i++) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) views.get(i).getLayoutParams();
                    params.leftMargin = (int)(circles.get(i).getCenter().getX() - circles.get(i).getImgRadius());
                    params.topMargin  = (int)(circles.get(i).getCenter().getY() - circles.get(i).getImgRadius());
                    views.get(i).setLayoutParams(params);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void run() {
        while(!gameplay.isDestroyed()) {
            refresh();
            try {
                sleep(1000 / FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
