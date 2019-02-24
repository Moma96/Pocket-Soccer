package com.example.myapplication.view;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.model.Circle;

import java.util.ArrayList;

public class ViewUpdater extends Thread {

    private static final int FPS = 100;

    private GameplayActivity gameplay;
    private static ArrayList<ImageView> views;

    public ViewUpdater(GameplayActivity gameplay) {
        this.gameplay = gameplay;

        FrameLayout frame_layout = gameplay.findViewById(R.id.frame_layout);
        views = new ArrayList<>();
        for (Circle circle : Circle.getCircles()) {

            ImageView img = new ImageView(gameplay);
            img.setBackgroundColor(Color.RED);
            //..load something inside the ImageView, we just set the background color

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(circle.getRadius()*2), (int)(circle.getRadius()*2));
            params.leftMargin = (int)(circle.getCenter().getX() - circle.getRadius());
            params.topMargin  = (int)(circle.getCenter().getY() - circle.getRadius());
            frame_layout.addView(img, params);

            views.add(img);
        }
    }


    private void refresh() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Circle> circles = Circle.getCircles();
                for (int i = 0; i < views.size(); i++) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) views.get(i).getLayoutParams();
                    params.leftMargin = (int)(circles.get(i).getCenter().getX() - circles.get(i).getRadius());
                    params.topMargin  = (int)(circles.get(i).getCenter().getY() - circles.get(i).getRadius());
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
