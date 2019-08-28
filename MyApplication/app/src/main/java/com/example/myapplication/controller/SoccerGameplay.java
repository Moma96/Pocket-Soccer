package com.example.myapplication.controller;

import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.model.Vector;
import com.example.myapplication.model.soccer.bot.Bot;
import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class SoccerGameplay extends SoccerModel implements Serializable {

    public enum FinishCriteria { GOALS, TIME }
    public enum PlayingCriteria { MOTION, STATIC }

    public static final int DEFAULT_FIELD_IMG = 0;

    public static final int DEFAULT_LIMIT = 3; //(m)
    public static final int PLAYING_LIMIT = 10; // (s)
    private static final int AFTER_GOAL_WAIT = 2; // (s)
    private static final int TIMER_COEFFICIENT = 10; // (s/10)
    private static final int TIMER_PERIOD = 100; // (ms)

    private FinishCriteria finishCriteria;
    private PlayingCriteria playingCriteria;

    private double limit;
    transient private Timer timer = null;

    private int playingLimit = PLAYING_LIMIT * TIMER_COEFFICIENT;
    transient private Timer playingTimer = null;

    private int afterGoalLimit = 0;
    private int playerLastScored = 0;
    transient private Timer afterGoalReset = null;

    transient AsyncTask<Void, Void, Void> changeActive = null;

    transient private Bot[] bots = new Bot[2];
    private boolean[] botplay;

    private Integer active = 0;
    transient private Player selected = null;
    private int[] scores = {0, 0};

    transient private SoccerFacade facade;

    transient private boolean responsiveness = false;
    transient private boolean botPlaying = false;

    public String[] playerNames;
    public int[] teamsImg;
    public int fieldImg;

    public SoccerGameplay(double x, double y, double width, double height, double friction, double gamespeed, double ballMass, FinishCriteria fc, double limit, PlayingCriteria pc, boolean[] botplay, String[] playerNames, int[] teamsImg, int fieldImg) {
        super(x, y, width, height, friction, gamespeed, ballMass);

        this.playerNames = playerNames;
        this.teamsImg = teamsImg;
        this.fieldImg = fieldImg;

        initBots(botplay);
        initCriterias(fc, limit, pc, true);
    }

    public SoccerGameplay(@NotNull SoccerGameplay soccer) {
        super(soccer);
        this.scores = soccer.scores;
        this.active = soccer.active;

        this.playerNames = soccer.playerNames;
        this.teamsImg = soccer.teamsImg;
        this.fieldImg = soccer.fieldImg;

        initBots(soccer.botplay);
        initCriterias(soccer.finishCriteria, soccer.limit, soccer.playingCriteria, false);
        initPlayerTimer(soccer.playingLimit);
        initAfterGoalWait(soccer.afterGoalLimit, soccer.playerLastScored);
    }

    private void initBots(boolean[] botplay) {
        bots[0] = new Bot(this, 0);
        bots[1] = new Bot(this, 1);
        this.botplay = botplay;
    }

    private void initCriterias(FinishCriteria fc, double l, PlayingCriteria pc, boolean mul) {
        finishCriteria = fc;
        playingCriteria = pc;
        limit = l;

        if (fc == FinishCriteria.TIME && mul) {
            limit *= 60*TIMER_COEFFICIENT;
        }
    }

    private void initPlayerTimer(int pl) {
        playingLimit = pl;
    }

    private void initAfterGoalWait(int agl, int pls) {
        playerLastScored = pls;
        afterGoalLimit = agl;
    }

    public FinishCriteria getFinishCriteria() {
        return finishCriteria;
    }

    public PlayingCriteria getPlayingCriteria() {
        return playingCriteria;
    }

    public String[] getPlayerNames() {
        return playerNames;
    }

    public int[] getTeamsImg() {
        return teamsImg;
    }

    public int getFieldImg() {
        return fieldImg;
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

    private void setTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (limit%10 == 0)
                    facade.refreshTime();
                if (--limit < 0) {
                    timer.cancel();
                    facade.gameFinished();
                }
            }
        }, 0, TIMER_PERIOD);
    }

    private void setPlayingTimer() {
        playingTimer = new Timer();
        playingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (--playingLimit < 0) {
                    playingTimer.cancel();
                    changeActive();
                }
            }
        }, 0, TIMER_PERIOD);
    }

    private void setAfterGoalTimer() {
        afterGoalReset = new Timer();
        afterGoalReset.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (--afterGoalLimit < 0) {
                    afterGoalReset.cancel();
                    afterGoalReset = null;
                    if (finishCriteria == FinishCriteria.GOALS && scores[playerLastScored] == limit) {
                        facade.gameFinished();
                    } else {
                        reset();
                        //setActive((playerLastScored + 1) % 2); //OVO PRAVI PROBLEM BOTU!!!!!!!!!!
                        changeActive();
                        facade.circlesReset();
                        setResponsiveness();
                    }
                }
            }
        }, 0, TIMER_PERIOD);
    }

    public double getLimit() {
        return limit;
    }

    @Override
    public synchronized void start() {
        waitFacade();
        super.start();

        for (int i = 0; i < 2; i++) {
            if (botplay[i])
                bots[i].start();
        }

        if (finishCriteria == FinishCriteria.TIME)
            setTimer();

        setResponsiveness();

        if (afterGoalLimit > 0) {
            resetResponsiveness();
            resetSelection();
            setAfterGoalTimer();
        }

        if (playingCriteria == PlayingCriteria.STATIC && !allNotMoving()) {
            resetResponsiveness();
            changeActive();
        } else if (playingLimit > 0)
            setPlayingTimer();
    }

    @Override
    public synchronized void terminate() {
        resetResponsiveness();
        super.terminate();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public synchronized void pause() {
        resetResponsiveness();
        super.pause();

        if (timer != null)
            timer.cancel();

        if (afterGoalReset != null)
            afterGoalReset.cancel();

        if (playingTimer != null) {
            playingTimer.cancel();
        }

        if (changeActive != null)
            changeActive.cancel(true);
    }

    @Override
    public synchronized void resume() {
        if (timer != null)
            setTimer();

        if (afterGoalLimit > 0)
            setAfterGoalTimer();

        if (playingCriteria == PlayingCriteria.STATIC && !allNotMoving()) {
            resetResponsiveness();
            changeActive();
        } else if (playingLimit > 0)
            setPlayingTimer();

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
        if (afterGoalLimit > 0) return;

        afterGoalLimit = AFTER_GOAL_WAIT*TIMER_COEFFICIENT;
        playerLastScored = player;

        resetResponsiveness();
        resetSelection();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                scores[player]++;
                facade.refreshScores();
                facade.goalHappened();

                Log.d(GOAL_TAG, "PLayer " + player + " scored! result: " + scores[0] + ":" + scores[1]);

                setAfterGoalTimer();

                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized boolean push(final Vector speed) {
        if (!responsive()) return false;

        if (selected != null) {
            selected.push(speed);
            selected = null;

            if (playingTimer != null)
                playingTimer.cancel();
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
        changeActive = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                synchronized (field) {
                    try {
                        resetResponsiveness();
                        if (playingCriteria == PlayingCriteria.STATIC) {
                            while (!field.allNotMoving()) {
                                field.wait();
                            }
                        }
                        setActive((active + 1) % 2);
                        setResponsiveness();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public synchronized void setActive(int player) {
        selected = null;
        active = player;

        if (playingTimer != null)
            playingTimer.cancel();

        playingLimit = PLAYING_LIMIT * TIMER_COEFFICIENT;
        setPlayingTimer();

        notifyActiveBot();
        facade.refreshActive();
    }

    private void notifyActiveBot() {
        synchronized (bots[active]) {
            bots[active].notifyAll();
        }
    }

    @Override
    public void circlesMoved() {
        facade.circlesMoved();
    }

    @Override
    public void collisionHappened() {
        facade.collisionHappened();
    }

}
