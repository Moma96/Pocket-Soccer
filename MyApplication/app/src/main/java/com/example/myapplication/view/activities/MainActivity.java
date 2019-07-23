package com.example.myapplication.view.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    private final static int PLAY = 1;
    private final static int PLAYER_TEAMS = 35;
    private final static int FIELDS = 4;

    private final static double BIGGEST_GAME_SPEED = 2;
    private final static double SMALLEST_GAME_SPEED = 0.5;
    private final static double GAME_SPEED_INCREMENT = 0.1;

    private final static double BIGGEST_FRICTION = 0.2;
    private final static double SMALLEST_FRICTION = 0.05;
    private final static double FRICTION_INCREMENT = 0.01;

    private int[] teamsimg = { 0, 1 };
    private int fieldimg = 1;

    private double friction = 0.1;
    private double gamespeed = 1;

    private boolean[] botplay = { false, false };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment, new MainMenu());
        ft.commit();
    }

    public void newGame(View view) {

        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtra("teamsimg", teamsimg);
        intent.putExtra("fieldimg", fieldimg);
        intent.putExtra("friction", friction);
        intent.putExtra("gamespeed", gamespeed);
        intent.putExtra("botplay", botplay);

        startActivityForResult(intent, PLAY);
    }

    public void continueLastGame(View view) {


    }

    public void changeTeam(View view) {
        switch(view.getId()) {
            case R.id.player1_left:
                teamsimg[0] = (teamsimg[0] - 1 + PLAYER_TEAMS) % PLAYER_TEAMS;
                break;
            case R.id.player1_right:
                teamsimg[0] = (teamsimg[0] + 1) % PLAYER_TEAMS;
                break;
            case R.id.player2_left:
                teamsimg[1] = (teamsimg[1] - 1 + PLAYER_TEAMS) % PLAYER_TEAMS;
                break;
            case R.id.player2_right:
                teamsimg[1] = (teamsimg[1] + 1) % PLAYER_TEAMS;
                break;
        }
        if (teamsimg[0] == teamsimg[1])
            changeTeam(view);

        updatePlayersImg();
    }

    public void changeField(View view) {
        switch(view.getId()) {
            case R.id.field_left:
                fieldimg = (fieldimg - 1 + FIELDS) % FIELDS;
                break;
            case R.id.field_right:
                fieldimg = (fieldimg + 1) % FIELDS;
                break;
        }

        updateFieldImg();
    }

    private void updateGameSpeedValue() {
        TextView fv = findViewById(R.id.game_speed_value);
        fv.setText((int)(gamespeed*100) + "%");
    }

    public void changeGameSpeed(View view) {
        switch(view.getId()) {
            case R.id.game_speed_decrease:
                if (gamespeed > SMALLEST_GAME_SPEED)
                    gamespeed -= GAME_SPEED_INCREMENT;
                break;
            case R.id.game_speed_increase:
                if (gamespeed < BIGGEST_GAME_SPEED)
                    gamespeed += GAME_SPEED_INCREMENT;
                break;
        }

        updateGameSpeedValue();
    }

    private void updateFrictionValue() {
        TextView fv = findViewById(R.id.friction_value);
        fv.setText((int)(friction*1000) + "%");
    }

    public void changeFriction(View view) {
        switch(view.getId()) {
            case R.id.friction_decrease:
                if (friction > SMALLEST_FRICTION)
                    friction -= FRICTION_INCREMENT;
                break;
            case R.id.friction_increase:
                if (friction < BIGGEST_FRICTION)
                    friction += FRICTION_INCREMENT;
                break;
        }

        updateFrictionValue();
    }

    public void replaceFragment(View view) {
        Fragment frag = null;

        switch (view.getId()) {
            case R.id.new_game:
                frag = new SelectPlayers();
                break;
            case R.id.settings:
                frag = new Settings();
                break;
            case R.id.history:
                frag = new History();
                break;
        }

        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.fragment, frag);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        FrameLayout fl = findViewById(R.id.fragment);
        if (fl != null) {
            fl.removeAllViews();
            getFragmentManager().popBackStack();
        }
        super.onBackPressed();
    }

    public void updatePlayersFragment() {
        updatePlayersImg();
    }

    private void updatePlayersImg() {
        ImageView player1img = findViewById(R.id.player1_img);
        player1img.setImageResource(getResources().getIdentifier("t" + teamsimg[0], "drawable", getPackageName()));
        ImageView player2img = findViewById(R.id.player2_img);
        player2img.setImageResource(getResources().getIdentifier("t" + teamsimg[1], "drawable", getPackageName()));
    }

    private void updateFieldImg() {
        ImageView fimg = findViewById(R.id.field_img);
        fimg.setImageResource(getResources().getIdentifier("field" + fieldimg, "drawable", getPackageName()));
    }

    public void updateMenuFragment() {
        TextView clg = findViewById(R.id.continue_last_game);
        clg.setClickable(false);
        clg.setTextColor(Color.GRAY);
    }

    public void updateSettingsFragment() {
        updateGameSpeedValue();
        updateFrictionValue();
        updateFieldImg();
    }

    public void updateHistoryFragment() {

    }

}