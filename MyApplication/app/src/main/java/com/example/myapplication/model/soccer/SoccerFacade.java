package com.example.myapplication.model.soccer;

import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.myapplication.model.collidables.active.ActiveObject;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.view.activities.GameplayActivity;
import com.example.myapplication.view.updaters.ViewUpdater;

import java.util.HashMap;

public class SoccerFacade {

    private GameplayActivity gameplay;
    private ViewUpdater updater;
    private SoccerModel model;

    public SoccerFacade(GameplayActivity gameplay, SoccerModel model, ViewUpdater updater) {
        this.gameplay = gameplay;
        this.model = model;
        this.updater = updater;

        model.getBall().setFacade(this);
        darkenInactive();
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
                    if (model.score(player)) {
                        updater.updateScores();
                        darkenInactive();
                    }
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void respondOnSwipe(final float x1, final float y1, final float x2, final float y2) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( final Void ... params ) {
                if (model.push(x1, y1, x2, y2))
                    darkenInactive();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void respondOnTap(final float x, final float y) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( final Void ... params ) {
                model.select(x, y);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void respondOnDown(final float x, final float y) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( final Void ... params ) {
                model.selectIfNothingSelected(x, y);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
