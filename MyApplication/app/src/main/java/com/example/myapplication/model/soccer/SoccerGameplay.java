package com.example.myapplication.model.soccer;

import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.soccer.bot.Bot;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;

import org.jetbrains.annotations.NotNull;

import static java.lang.Thread.sleep;

public class SoccerGameplay extends SoccerModel {

    private static final int AFTER_GOAL_WAIT = 2; //s

    private Bot[] bots = new Bot[2];
    private Integer active = 0;
    private Player selected = null;
    private int[] scores = {0, 0};

    SoccerFacade facade;

    private Boolean responsiveness = false;
    private Boolean botPlaying = false;

    public SoccerGameplay(double x, double y, double width, double height) {
        super(x, y, width, height);

        bots[0] = new Bot(this, 0);
        bots[1] = new Bot(this, 1);
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

        bots[0].start();
        bots[1].start();
    }

    @Override
    public void terminate() {
        resetResponsiveness();
        super.terminate();
    }

    @Override
    public void allStopped() {
        notifyActiveBot();
    }

    public boolean allNotMoving() {
        return field.allNotMoving();
    }

    public void score(final int player) {
        if (!responsive()) return;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                scores[player]++;
                Log.d(GOAL_TAG, "PLayer " + player + " scored! result: " + scores[0] + ":" + scores[1]);

                disableAndSleep(AFTER_GOAL_WAIT);

                reset();

                synchronized (active) {
                    active = (player + 1) % 2;
                    notifyActiveBot();
                }

                facade.refreshScores();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public Bot[] getBots() {
        return bots;
    }

    public boolean push(final Vector speed) {
        if (!responsive()) return false;

        if (selected != null) {
            selected.push(speed);
            selected = null;
            changeActive();
        }
        facade.darkenInactive();
        return true;
    }

    public boolean push(final float x1, final float y1, final float x2, final float y2) {
        return push(new Vector(x2 - x1, y2 - y1));
    }

    public Player[] getActivePlayers() {
        synchronized (active) {
            return getPlayers(active);
        }
    }

    public Player[] getNonActivePlayers() {
        synchronized (active) {
            return getPlayers((active + 1) % 2);
        }
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

    public Integer getActive() {
        synchronized (active) {
            return active;
        }
    }

    public int[] getScores() {
        return scores;
    }

    public synchronized boolean responsive() {
        synchronized (responsiveness) {
            return responsiveness;
        }
    }

    public synchronized void setResponsiveness() {
        synchronized (responsiveness) {
            responsiveness = true;
        }
    }

    public synchronized void resetResponsiveness() {
        synchronized (responsiveness) {
            responsiveness = false;
        }
    }

    public boolean botPlaying() {
        synchronized (botPlaying) {
            return botPlaying;
        }
    }

    public void botStarted() {
        synchronized(botPlaying) {
            botPlaying = true;
        }
    }

    public void botFinished() {
        synchronized (botPlaying) {
            botPlaying = false;
        }
    }

    public boolean select(Player player) {
        if (!responsive()) return false;

        selected = player;
        return true;
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

    public synchronized void resetSelection() {
        selected = null;
    }

    public Player getSelected() {
        return selected;
    }

    public void changeActive() {
        synchronized (active) {
            active = (active + 1) % 2;
            notifyActiveBot();
        }
    }

    private void notifyActiveBot() {
        synchronized (bots[active]) {
            bots[active].notifyAll();
        }
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
