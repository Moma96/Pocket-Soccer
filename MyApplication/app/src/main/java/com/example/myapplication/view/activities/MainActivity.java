package com.example.myapplication.view.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.model.soccer.SoccerGameplay;

public class MainActivity extends AppCompatActivity {

    private final static int PLAY = 1;
    public final static int GAME_FINISHED_CODE = 1;
    public final static int MAIN_MENU_CODE = 2;

    private MainMenu mainMenu;
    private GameTypeSelection gameTypeSelection;
    private SelectPlayers selectPlayers;
    private Settings settings;
    private History history;

    ///////////////////////////////////////////
    public SoccerGameplay soccer = null;
    public int[] savedteams;
    public int savedfield;
    ////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainMenu = new MainMenu();
        gameTypeSelection = new GameTypeSelection();
        selectPlayers = new SelectPlayers();
        settings = new Settings();
        history = new History();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment, mainMenu);
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case PLAY:
                switch (resultCode) {
                    case GAME_FINISHED_CODE:



                        replaceFragment(mainMenu);
                        break;
                    case MAIN_MENU_CODE:
                        soccer = (SoccerGameplay)data.getSerializableExtra("soccer");
                        savedteams = data.getIntArrayExtra("teamsimg");
                        savedfield = data.getIntExtra("fieldimg", SoccerGameplay.DEFAULT_FIELD_IMG);
                        replaceFragment(mainMenu);
                        break;
                }
                break;
        }
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public GameTypeSelection getGameTypeSelection() {
        return gameTypeSelection;
    }

    public SelectPlayers getSelectPlayers() {
        return selectPlayers;
    }

    public Settings getSettings() {
        return settings;
    }

    public History getHistory() {
        return history;
    }

    public void newGame() {
        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtra("mode", "new game");
        intent.putExtra("teamsimg", selectPlayers.getTeamsimg());
        intent.putExtra("fieldimg", settings.getFieldimg());
        intent.putExtra("gamespeed", settings.getGamespeed());
        intent.putExtra("friction", settings.getFriction());
        intent.putExtra("ballmass", settings.getBallMass());
        intent.putExtra("finish criteria", gameTypeSelection.getFinishCriteria());
        intent.putExtra("limit", gameTypeSelection.getLimit());
        intent.putExtra("playing criteria", gameTypeSelection.getPlayingCriteria());
        intent.putExtra("botplay", selectPlayers.getBotplay());

        startActivityForResult(intent, PLAY);
    }

    public void continueLastGame() {
        if (soccer != null) {
            Intent intent = new Intent(this, GameplayActivity.class);
            intent.putExtra("mode", "last game");
            intent.putExtra("soccer", soccer);
            intent.putExtra("fieldimg", savedfield);
            intent.putExtra("teamsimg", savedteams);

            startActivityForResult(intent, PLAY);
        }
    }

    public void replaceFragment(Fragment frag) {
        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.fragment, frag);
            ft.commit();
        }
    }
}