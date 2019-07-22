package com.example.myapplication.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    private final static int PLAY = 1;
    private final static int PLAYER_TEAMS = 35;

    private int[] teamsimg = { 1, 3 };
    private int fieldimg = 4;

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

    public void play(View view) {

        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtra("teamsimg", teamsimg);
        intent.putExtra("fieldimg", fieldimg);
        intent.putExtra("friction", friction);
        intent.putExtra("gamespeed", gamespeed);
        intent.putExtra("botplay", botplay);

        startActivityForResult(intent, PLAY);
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
        updatePlayersImg();
    }

    public void replaceFrag(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.addToBackStack(null);
        transaction.commit();

        FrameLayout fl = findViewById(R.id.fragment);
        fl.removeAllViews();

        transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    public void selectPlayers(View view) {
        SelectPlayers frag = new SelectPlayers();
        replaceFrag(frag);
    }

    public void updatePlayersImg() {
        ImageView player1img = findViewById(R.id.player1_img);
        player1img.setImageResource(getResources().getIdentifier("t" + teamsimg[0], "drawable", getPackageName()));
        ImageView player2img = findViewById(R.id.player2_img);
        player2img.setImageResource(getResources().getIdentifier("t" + teamsimg[1], "drawable", getPackageName()));
    }

}