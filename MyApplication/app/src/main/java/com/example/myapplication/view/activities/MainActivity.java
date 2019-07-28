package com.example.myapplication.view.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    private MainMenu mainMenu;
    private SelectPlayers selectPlayers;
    private Settings settings;
    private History history;

    private final static int PLAY = 1;

    private boolean[] botplay = { false, false };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainMenu = new MainMenu();
        selectPlayers = new SelectPlayers();
        settings = new Settings();
        history = new History();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment, mainMenu);
        ft.commit();
    }

    public MainMenu getMainMenu() {
        return mainMenu;
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
        intent.putExtra("teamsimg", selectPlayers.getTeamsimg());
        intent.putExtra("fieldimg", settings.getFieldimg());
        intent.putExtra("gamespeed", settings.getGamespeed());
        intent.putExtra("friction", settings.getFriction());
        intent.putExtra("ballmass", settings.getBallMass());
        intent.putExtra("botplay", botplay);

        startActivityForResult(intent, PLAY);
    }

    public void continueLastGame() {
        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtra("teamsimg", selectPlayers.getTeamsimg());
        intent.putExtra("fieldimg", settings.getFieldimg());
        intent.putExtra("gamespeed", settings.getGamespeed());
        intent.putExtra("friction", settings.getFriction());
        intent.putExtra("ballmass", settings.getBallMass());
        intent.putExtra("botplay", botplay);

        startActivityForResult(intent, PLAY);
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