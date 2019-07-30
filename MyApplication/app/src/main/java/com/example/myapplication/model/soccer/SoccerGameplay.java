package com.example.myapplication.model.soccer;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.model.Active;
import com.example.myapplication.model.Timer;
import com.example.myapplication.model.Vector;
import com.example.myapplication.model.soccer.bot.Bot;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.view.activities.GameplayActivity;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import static java.lang.Thread.sleep;

public class SoccerGameplay extends SoccerModel implements Serializable {

    public enum FinishCriteria { GOALS, TIME };
    public enum PlayingCriteria { MOTION, STATIC };

    public static final int DEFAULT_FIELD_IMG = 0;
    private static final int AFTER_GOAL_WAIT = 2; //s

    private FinishCriteria finishCriteria;
    private PlayingCriteria playingCriteria;
    int limit;
    private SoccerTimer timer = null;

    transient private Bot[] bots = new Bot[2];
    private boolean[] botplay;

    private Integer active = 0;
    transient private Player selected = null;
    private int[] scores = {0, 0};

    transient private SoccerFacade facade;

    transient private Boolean responsiveness = false;
    transient private Boolean botPlaying = false;

    public SoccerGameplay(double x, double y, double width, double height, double friction, double gamespeed, double ballMass, boolean[] botplay, FinishCriteria fc, int limit, PlayingCriteria pc) {
        super(x, y, width, height, friction, gamespeed, ballMass);

        initBots(botplay);
        initCriterias(fc, limit, pc);
    }

    public SoccerGameplay(@NotNull SoccerGameplay soccer) {
        super(soccer);
        this.scores = soccer.scores;
        this.active = soccer.active;

        initBots(soccer.botplay);
        initCriterias(soccer.finishCriteria, soccer.timer.getTime(), soccer.playingCriteria);
    }

    private void initBots(boolean[] botplay) {
        bots[0] = new Bot(this, 0);
        bots[1] = new Bot(this, 1);
        this.botplay = botplay;
    }

    private void initCriterias(FinishCriteria fc, int l, PlayingCriteria pc) {
        finishCriteria = fc;
        limit = l;
        playingCriteria = pc;
        if (finishCriteria == FinishCriteria.TIME)
            timer = new SoccerTimer(limit*60, this);
    }

    public synchronized void setFacade(@NotNull SoccerFacade facade) {
        this.facade = facade;
        notifyAll();
    }

    public synchronized SoccerFacade getFacade() {
        return facade;
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

        if (timer != null)
            timer.start();
        setResponsiveness();
    }

    @Override
    public synchronized void terminate() {
        resetResponsiveness();
        super.terminate();
        if (timer != null)
            timer.terminate();
    }

    @Override
    public synchronized void pause() {
        resetResponsiveness();
        super.pause();
        if (timer != null)
            timer.inactive();

    }

    @Override
    public synchronized void resume() {
        super.resume();
        if (timer != null)
            timer.active();
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

                if (finishCriteria == FinishCriteria.GOALS && scores[player] == limit) {
                     lastGoalScored(player);
                } else {
                    reset();
                    setActive((player + 1) % 2); //OVO PRAVI PROBLEM BOTU!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    facade.circlesReset();
                    setResponsiveness();
                }
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

    public SoccerTimer getTimer() {
        return timer;
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
        facade.refreshActive();
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

    public void lastGoalScored(int player) {
        facade.gameFinished(player);
    }

    public void timerFinished() {
        int winner = 0;
        if (scores[1] > scores[0])
            winner = 1;
        else if (scores[1] == scores[0])
            winner = -1;
        facade.gameFinished(winner);
    }

    @Override
    public void circlesMoved() {
        facade.circlesMoved();
    }
}
