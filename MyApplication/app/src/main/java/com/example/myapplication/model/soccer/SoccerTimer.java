package com.example.myapplication.model.soccer;

import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.Timer;

import java.io.Serializable;

public class SoccerTimer extends Timer implements Serializable {

    transient private SoccerGameplay soccerGameplay;

    public SoccerTimer(int timer, SoccerGameplay soccer) {
        super(timer);
        soccerGameplay = soccer;
    }

    @Override
    public void timerTicked() {
        /*final String s = toString();
        soccerGameplay.getFacade().gameplay.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textTime = soccerGameplay.getFacade().gameplay.findViewById(R.id.time_text);
                textTime.setText(s);
            }
        });*/
    }

    @Override
    public void finished() {
        soccerGameplay.timerFinished();
    }
}
