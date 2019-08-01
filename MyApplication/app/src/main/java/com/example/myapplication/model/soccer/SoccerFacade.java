package com.example.myapplication.model.soccer;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.example.myapplication.R;
import com.example.myapplication.view.activities.GameplayActivity;
import com.example.myapplication.view.updaters.ViewUpdater;

public class SoccerFacade {

    public GameplayActivity gameplay;
    private ViewUpdater updater;
    private SoccerGameplay soccer;

    public SoccerFacade(GameplayActivity gameplay, SoccerGameplay soccer, ViewUpdater updater) {
        this.gameplay = gameplay;
        this.soccer = soccer;
        this.updater = updater;

        soccer.setFacade(this);

        collision = MediaPlayer.create(gameplay, R.raw.collision_sound);
        goal = MediaPlayer.create(gameplay, R.raw.goal_sound);
    }

    public void pause() {
        soccer.pause();
        updater.inactive();
    }

    public void resume() {
        updater.active();
        soccer.resume();
    }

    public void terminate() {
        soccer.terminate();
        updater.terminate();
    }

    public void refreshScores() {
        updater.updateScores();
        refreshActive();
    }

    public void refreshTime() {
        updater.updateTime();
    }

    public void refreshActive() {
        updater.darkenInactive();
    }

    public void circlesReset() {
        updater.refreshCircles();
    }

    public void circlesMoved() {
        updater.refreshMovingCircles();
    }

    public void gameFinished(int winner) {
        terminate();
        gameplay.gameFinished(winner);
    }


    private MediaPlayer collision;
    private MediaPlayer goal;

    public void collisionHappened() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                try {
                    if (collision.isPlaying()) {
                        collision.stop();
                        collision.release();
                        collision = MediaPlayer.create(gameplay, R.raw.collision_sound);
                    }
                    collision.start();
                } catch (Exception e) {
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void goalHappened() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                try {
                    if (goal.isPlaying()) {
                        goal.stop();
                        goal.release();
                        goal = MediaPlayer.create(gameplay, R.raw.goal_sound);
                    }
                    goal.start();
                } catch (Exception e) {
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void respondOnSwipe(final float x1, final float y1, final float x2, final float y2) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                if (!soccer.botPlaying()) {
                    soccer.push(x1, y1, x2, y2);
                }
                updater.refreshSelection();
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
                updater.refreshSelection();
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
                updater.refreshSelection();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}