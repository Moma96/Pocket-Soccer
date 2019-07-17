package com.example.myapplication.model.soccer;

import android.os.AsyncTask;
import com.example.myapplication.view.activities.GameplayActivity;
import com.example.myapplication.view.updaters.ViewUpdater;

public class SoccerFacade {

  private GameplayActivity gameplay;
  private ViewUpdater updater;
  private SoccerGameplay soccer;

  public SoccerFacade(GameplayActivity gameplay, SoccerGameplay soccer, ViewUpdater updater) {
    this.gameplay = gameplay;
    this.soccer = soccer;
    this.updater = updater;

    soccer.setFacade(this);
  }

  public void refreshScores() {
    updater.updateScores();
    darkenInactive();
  }

  public void darkenInactive() {
    updater.darkenInactive();
  }

  public void respondOnSwipe(final float x1, final float y1, final float x2, final float y2) {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground( final Void ... params ) {
        if (!soccer.botPlaying()) {
          soccer.push(x1, y1, x2, y2);
        }
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  public void respondOnTap(final float x, final float y) {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground( final Void ... params ) {
        if (!soccer.botPlaying()) {
          soccer.select(x, y);
        }
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  public void respondOnDown(final float x, final float y) {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground( final Void ... params ) {
        if (!soccer.botPlaying()) {
          soccer.selectIfNothingSelected(x, y);
        }
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }
}