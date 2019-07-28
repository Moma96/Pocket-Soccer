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

    public static final int DEFAULT_FIELD_IMG = 0;

    private static final int AFTER_GOAL_WAIT = 2; //s

    private Bot[] bots = new Bot[2];
    final private boolean[] botplay;

    private Integer active = 0;
    private Player selected = null;
    private int[] scores = {0, 0};

    SoccerFacade facade;

    private Boolean responsiveness = false;
    private Boolean botPlaying = false;

    public SoccerGameplay(double x, double y, double width, double height, double friction, double gamespeed, double ballMass, boolean[] botplay) {
        super(x, y, width, height, friction, gamespeed, ballMass);

        bots[0] = new Bot(this, 0);
        bots[1] = new Bot(this, 1);

        this.botplay = botplay;
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
    public synchronized void start() {
        waitFacade();
        super.start();

        for (int i = 0; i < 2; i++) {
            if (botplay[i])
                bots[i].start();
        }

        setResponsiveness();
    }

    @Override
    public synchronized void terminate() {
        resetResponsiveness();
        super.terminate();
    }

    @Override
    public synchronized void pause() {
        resetResponsiveness();
        super.pause();

    }

    @Override
    public synchronized void resume() {
        super.resume();
        setResponsiveness();
    }

    @Override
    public void allStopped() {
        notifyActiveBot();
    }

    public boolean allNotMoving() {
        return field.allNotMoving();
    }

    public synchronized void score(final int player) {
        if (!responsive()) return;

        resetResponsiveness();
        resetSelection();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                scores[player]++;
                facade.refreshScores();

                Log.d(GOAL_TAG, "PLayer " + player + " scored! result: " + scores[0] + ":" + scores[1]);

                sleepFor(AFTER_GOAL_WAIT);
                reset();
                setActive((player + 1) % 2); //OVO PRAVI PROBLEM BOTU!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                setResponsiveness();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized boolean push(final Vector speed) {
        if (!responsive()) return false;

        if (selected != null) {
            selected.push(speed);
            selected = null;
            changeActive();
        }
        return true;
    }

    public synchronized boolean push(final float x1, final float y1, final float x2, final float y2) {
        return push(new Vector(x2 - x1, y2 - y1));
    }

    public synchronized Player[] getActivePlayers() {
        return getPlayers(active);
    }

    public synchronized Player[] getNonActivePlayers() {
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

    public synchronized Integer getActive() {
        return active;
    }

    public int[] getScores() {
        return scores;
    }

    public synchronized boolean responsive() {
        return responsiveness;
    }

    public synchronized void setResponsiveness() {
        responsiveness = true;
        notifyAll();
    }

    public synchronized void resetResponsiveness() {
        responsiveness = false;
    }

    public synchronized boolean botPlaying() {
        return botPlaying;
    }

    public synchronized void botStarted() {
        resetSelection();
        botPlaying = true;
    }

    public synchronized void botFinished() {
        botPlaying = false;
    }

    public synchronized boolean select(Player player) {
        if (!responsive()) return false;

        selected = player;
        return true;
    }

    public synchronized boolean select(final float x, final float y) {
        if (!responsive()) return false;

        selected = getActivePlayer(new Vector(x, y));
        return true;
    }

    public synchronized boolean selectIfNothingSelected(final float x, final float y) {
        if (selected != null) return false;

        return select(x, y);
    }

    public synchronized void resetSelection() {
        selected = null;
    }

    public synchronized Player getSelected() {
        return selected;
    }

    public synchronized void changeActive() {
        setActive((active + 1) % 2);
    }

    public synchronized void setActive(int player) {
        active = player;
        notifyActiveBot();
        facade.darkenInactive();
    }

    private void notifyActiveBot() {
        synchronized (bots[active]) {
            bots[active].notifyAll();
        }
    }

    private void sleepFor(int wait) {
        try {
            sleep(wait * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
