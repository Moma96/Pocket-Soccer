package com.example.myapplication.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.soccer.SoccerFacade;
import com.example.myapplication.model.soccer.SoccerGameplay;
import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.view.GestureListener;
import com.example.myapplication.view.updaters.ViewUpdater;

public class GameplayActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetectorCompat;
    private SoccerGameplay soccer;
    private int[] teams;
    private int field;

    private SoccerFacade soccerFacade;
    private ViewUpdater viewUpdater;

    private View.OnClickListener pause = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            soccerFacade.pause();
            TextView p = findViewById(R.id.pause_text);
            p.setClickable(false);
            setPauseOptions();
        }
    };

    private View.OnClickListener resume = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            resetPauseOptions();
            TextView p = findViewById(R.id.pause_text);
            p.setClickable(true);
            soccerFacade.resume();
        }
    };

    private View.OnClickListener mainMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent data = new Intent();
            data.putExtra("soccer", soccer);
            data.putExtra("teamsimg", teams);
            data.putExtra("fieldimg", field);

            soccerFacade.terminate();
            setResult(2, data);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        final FrameLayout background = findViewById(R.id.background);

        final Intent intent = getIntent();

        teams = intent.getIntArrayExtra("teamsimg");
        field = intent.getIntExtra("fieldimg", SoccerGameplay.DEFAULT_FIELD_IMG);
        int fieldimg = getResources().getIdentifier("field" + field, "drawable", getPackageName());
        background.setBackgroundResource(fieldimg);

        resetPauseOptions();
        setListener();

        background.post(new Runnable() {
            @Override
            public void run() {
                switch (intent.getStringExtra("mode")) {
                    case "new game":
                        setup(background,
                                teams,
                                intent.getDoubleExtra("friction", SoccerModel.DEFAULT_FRICTION),
                                intent.getDoubleExtra("gamespeed", SoccerModel.DEFAULT_GAME_SPEED),
                                intent.getDoubleExtra("ballmass", SoccerModel.DEFAULT_BALL_MASS),
                                intent.getBooleanArrayExtra("botplay"));
                        break;
                    case "last game":
                        setup((SoccerGameplay) intent.getSerializableExtra("soccer"),
                                teams);
                }
            }
        });
    }

    private void setup(FrameLayout background, int[] teams, double friction, double gamespeed, double ballMass, boolean[] botplay) {

        soccer = new SoccerGameplay(0, 0, background.getWidth(), background.getHeight(), friction, gamespeed, ballMass, botplay,
                SoccerGameplay.FinishCriteria.TIME, 2*60, SoccerGameplay.PlayingCriteria.STATIC);
        viewUpdater = new ViewUpdater(this, soccer, teams);
        soccerFacade = new SoccerFacade(this, soccer, viewUpdater);

        soccer.start();
        viewUpdater.start();

        GestureListener gestureListener = new GestureListener(soccerFacade);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);
    }

    private void setup(SoccerGameplay s, int[] teams) {

        soccer = new SoccerGameplay(s);
        viewUpdater = new ViewUpdater(this, soccer, teams);
        soccerFacade = new SoccerFacade(this, soccer, viewUpdater);

        soccer.start();
        viewUpdater.start();

        GestureListener gestureListener = new GestureListener(soccerFacade);
        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);
    }

    private void resetPauseOptions() {
        TextView ct = findViewById(R.id.resume_text);
        ct.setVisibility(View.INVISIBLE);
        ct.setClickable(false);
        TextView mmt = findViewById(R.id.main_menu_text);
        mmt.setVisibility(View.INVISIBLE);
        mmt.setClickable(false);
    }

    private void setPauseOptions() {
        LinearLayout ll = findViewById(R.id.pause_layout);
        ll.bringToFront();

        TextView ct = findViewById(R.id.resume_text);
        ct.setVisibility(View.VISIBLE);
        ct.setClickable(true);
        TextView mmt = findViewById(R.id.main_menu_text);
        mmt.setVisibility(View.VISIBLE);
        mmt.setClickable(true);
    }


    public SoccerModel getSoccer() {
        return soccer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetectorCompat != null) {
            gestureDetectorCompat.onTouchEvent(event);
        }
        return true;
    }

    private void setListener() {
        findViewById(R.id.pause_text).setOnClickListener(pause);
        findViewById(R.id.resume_text).setOnClickListener(resume);
        findViewById(R.id.main_menu_text).setOnClickListener(mainMenu);
    }

    public void gameFinished(int winner) {
        Intent data = new Intent();
        data.putExtra("soccer", soccer);
        data.putExtra("teamsimg", teams);
        data.putExtra("fieldimg", field);
        data.putExtra("winner", winner);

        setResult(2, data);
        finish();
    }

}
