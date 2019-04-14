package com.example.myapplication.model.soccer;

import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.view.activities.GameplayActivity;

import java.util.HashMap;

public class SoccerFacade {

    private GameplayActivity gameplay;
    private SoccerModel model;

    public SoccerFacade(GameplayActivity gameplay, SoccerModel model) {
        this.gameplay = gameplay;
        this.model = model;
    }

    public void darkenInactive() {
        gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Player[] active = model.getActivePlayers();
                Player[] non_active = model.getNonActivePlayers();
                HashMap<ActiveObject, ImageView> views = gameplay.getViewUpdater().getViews();

                for (Player player : active) {
                    ImageView view = views.get(player);
                    view.setAlpha((float) 1);
                }
                for (Player player : non_active) {
                    ImageView view = views.get(player);
                    view.setAlpha((float) 0.7);
                }
            }
        });
    }

    public void score(final int player) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground( final Void ... params ) {
                    if (model.score(player))
                        darkenInactive();
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void push(final float x1, final float y1, final float x2, final float y2) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( final Void ... params ) {
                if (model.push(x1, y1, x2, y2))
                    darkenInactive();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
