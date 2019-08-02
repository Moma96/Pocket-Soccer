package com.example.myapplication.view.updaters;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.example.myapplication.R;
import com.example.myapplication.view.activities.GameplayActivity;

public class SoundUpdater {

    private GameplayActivity gameplay;

    private MediaPlayer collision;
    private MediaPlayer goal;

    public SoundUpdater(GameplayActivity gameplay) {
        this.gameplay = gameplay;

        collision = MediaPlayer.create(gameplay, R.raw.collision_sound);
        goal = MediaPlayer.create(gameplay, R.raw.goal_sound);
    }

    public void goal() {
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

    public void collision() {
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
}
