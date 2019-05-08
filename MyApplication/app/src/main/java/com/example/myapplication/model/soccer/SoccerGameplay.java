package com.example.myapplication.model.soccer;

import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;

import org.jetbrains.annotations.NotNull;

import static java.lang.Thread.sleep;

public class SoccerGameplay extends SoccerModel {

    public static final int AFTER_GOAL_WAIT = 2; //s
    private static final String GOAL_TAG = "Goal";

    private int active = 0;
    private Player selected = null;
    private boolean responsiveness = false;
    private int[] scores = {0, 0};

    SoccerFacade facade;

    public SoccerGameplay(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public synchronized void setFacade(@NotNull SoccerFacade facade) {
        this.facade = facade;
        notifyAll();
    }

    public synchronized void waitFacade() {
        while (facade == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        waitFacade();
        super.start();
        setResponsiveness();
    }

    @Override
    public void terminate() {
        resetResponsiveness();
        super.terminate();
    }

    public void score(final int player) {
        if (!responsive()) return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground( final Void ... params ) {
                scores[player]++;
                Log.d(GOAL_TAG, "PLayer " + player + " scored! result: " + scores[0] + ":" + scores[1]);

                disableAndSleep(AFTER_GOAL_WAIT);

                reset();

                active = (player + 1) % 2;
                facade.refreshScores();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public boolean push(final float x1, final float y1, final float x2, final float y2) {
        if (!responsive()) return false;

        if (selected != null) {
            selected.push(new Vector(x2 - x1, y2 - y1));
            selected = null;
            changeActive();
        }
        return true;
    }

    public Player[] getActivePlayers() {
        return getPlayers(active);
    }

    public Player[] getNonActivePlayers() {
        return getPlayers((active + 1) % 2);
    }

    public Player getActivePlayer(Vector dot) {
        Player[] players = getActivePlayers();
        double min_distance = Double.MAX_VALUE;
        Player player = null;
        for (Player p : players) {
            double distance = p.getDistance(dot);
            if (distance < min_distance) {
                min_distance = distance;
                player = p;
            }
        }
        return player;
    }

    public int getActive() {
        return active;
    }

    public boolean select(final float x, final float y) {
        if (!responsive()) return false;

        selected = getActivePlayer(new Vector(x, y));
        return true;
    }

    public boolean selectIfNothingSelected(final float x, final float y) {
        if (selected != null) return false;

        return select(x, y);
    }

    public int[] getScores() {
        return scores;
    }

    public synchronized boolean responsive() {
        return responsiveness;
    }

    public synchronized void setResponsiveness() {
        responsiveness = true;
    }

    public synchronized void resetResponsiveness() {
        responsiveness = false;
    }

    public synchronized void resetSelection() { selected = null; }

    public void changeActive() {
        active = (active + 1) % 2;
    }

    public Player getSelected() {
        return selected;
    }

    private void disableAndSleep(int wait) {
        resetResponsiveness();
        resetSelection();
        try {
            sleep(wait * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setResponsiveness();
    }
}
