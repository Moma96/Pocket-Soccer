package com.example.myapplication.model.soccer;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.example.myapplication.R;
import com.example.myapplication.view.activities.GameplayActivity;
import com.example.myapplication.view.updaters.SoundUpdater;
import com.example.myapplication.view.updaters.ViewUpdater;

public class SoccerFacade {

    public GameplayActivity gameplay;
    private SoccerGameplay soccer;
    private ViewUpdater viewUpdater;
    private SoundUpdater soundUpdater;

    public SoccerFacade(GameplayActivity gameplay, SoccerGameplay soccer, ViewUpdater viewUpdater, SoundUpdater soundUpdater) {
        this.gameplay = gameplay;
        this.soccer = soccer;
        this.viewUpdater = viewUpdater;
        this.soundUpdater = soundUpdater;

        soccer.setFacade(this);
    }

    public void pause() {
        soccer.pause();
        viewUpdater.inactive();
    }

    public void resume() {
        viewUpdater.active();
        soccer.resume();
    }

    public void terminate() {
        soccer.terminate();
        viewUpdater.terminate();
    }

    public void refreshScores() {
        viewUpdater.updateScores();
        refreshActive();
    }

    public void refreshTime() {
        viewUpdater.updateTime();
    }

    public void refreshActive() {
        viewUpdater.darkenInactive();
    }

    public void circlesReset() {
        viewUpdater.refreshCircles();
    }

    public void circlesMoved() {
        viewUpdater.refreshMovingCircles();
    }

    public void gameFinished(int winner) {
        terminate();
        gameplay.gameFinished(winner);
    }

    public void collisionHappened() {
        soundUpdater.collision();
    }

    public void goalHappened() {
        soundUpdater.goal();
    }

    public void respondOnSwipe(final float x1, final float y1, final float x2, final float y2) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                if (!soccer.botPlaying()) {
                    soccer.push(x1, y1, x2, y2);
                }
                viewUpdater.refreshSelection();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void respondOnTap(final float x, final float y) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                if (!soccer.botPlaying()) {
                    soccer.select(x, y);
                }
                viewUpdater.refreshSelection();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void respondOnDown(final float x, final float y) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                if (!soccer.botPlaying()) {
                    soccer.selectIfNothingSelected(x, y);
                }
                viewUpdater.refreshSelection();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}