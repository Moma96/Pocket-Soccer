package com.example.myapplication.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.controller.SoccerGameplay;
import com.example.myapplication.model.soccer.database.entity.Match;
import com.example.myapplication.model.soccer.database.entity.Player;
import com.example.myapplication.model.soccer.database.repository.MatchRepository;
import com.example.myapplication.model.soccer.database.repository.PlayerRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

    private final static int PLAY = 1;
    public final static int GAME_FINISHED_CODE = 1;
    public final static int MAIN_MENU_CODE = 2;
    public final static String LAST_GAME_FILE = "lastgame.dat";

    private MainMenu mainMenu;
    private GameTypeSelection gameTypeSelection;
    private SelectPlayers selectPlayers;
    private History history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainMenu = new MainMenu();
        gameTypeSelection = new GameTypeSelection();
        selectPlayers = new SelectPlayers();
        history = new History();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment, mainMenu);
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case PLAY:
                switch (resultCode) {
                    case GAME_FINISHED_CODE:
                        SoccerGameplay soccer = (SoccerGameplay)data.getSerializableExtra("soccer");
                        saveMatch(soccer);
                        replaceFragment(new History(soccer.getPlayerNames()[0], soccer.getPlayerNames()[1]));
                        //replaceFragment(mainMenu);
                        break;
                    case MAIN_MENU_CODE:
                        saveLastGame((SoccerGameplay)data.getSerializableExtra("soccer"));
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

    public History getHistory() {
        return history;
    }

    public void newGame() {
        resetLastGame();

        SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        double ballMass = Double.longBitsToDouble(pref.getLong("ball mass", Double.doubleToLongBits(Settings.DEFAULT_FRICTION)));
        double friction = Double.longBitsToDouble(pref.getLong("friction", Double.doubleToLongBits(Settings.DEFAULT_FRICTION)));
        double gamespeed = Double.longBitsToDouble(pref.getLong("game speed", Double.doubleToLongBits(Settings.DEFAULT_GAME_SPEED)));
        int fieldimg = pref.getInt("field img", Settings.DEFAULT_FIELD_IMG);

        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtra("mode", "new game");
        intent.putExtra("playernames", selectPlayers.getPlayerNames());
        intent.putExtra("teamsimg", selectPlayers.getTeamsimg());
        intent.putExtra("fieldimg", fieldimg);
        intent.putExtra("gamespeed", gamespeed);
        intent.putExtra("friction", friction);
        intent.putExtra("ballmass", ballMass);
        intent.putExtra("finish criteria", gameTypeSelection.getFinishCriteria());
        intent.putExtra("limit", gameTypeSelection.getLimit());
        intent.putExtra("playing criteria", gameTypeSelection.getPlayingCriteria());
        intent.putExtra("botplay", selectPlayers.getBotplay());

        startActivityForResult(intent, PLAY);
    }

    public void continueLastGame() {
        SoccerGameplay soccer = loadLastGame();
        if (soccer != null) {
            Intent intent = new Intent(this, GameplayActivity.class);
            intent.putExtra("mode", "last game");
            intent.putExtra("soccer", soccer);

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

    public void saveLastGame(SoccerGameplay soccer) {
        try {
            File f = new File(getFilesDir(), LAST_GAME_FILE);
            f.createNewFile();
            ObjectOutputStream objectOutStream = new ObjectOutputStream(new FileOutputStream(f));

            objectOutStream.writeObject(soccer);
            objectOutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SoccerGameplay loadLastGame() {
        try {
            File f = new File(getFilesDir(), LAST_GAME_FILE);
            ObjectInputStream objectInStream = new ObjectInputStream(new FileInputStream(f));

            SoccerGameplay soccer = (SoccerGameplay)objectInStream.readObject();
            objectInStream.close();
            return soccer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void resetLastGame() {
        File f = new File(getFilesDir(), LAST_GAME_FILE);
        f.delete();
    }

    private void saveMatch(final SoccerGameplay soccer) {
        new AsyncTask<Match, Void, Void>() {
            @Override
            protected Void doInBackground(final Match... matches) {
                String[] playerNames = soccer.getPlayerNames();
                int[] scores = soccer.getScores();
                MatchRepository matchRes = new MatchRepository(getApplication());
                PlayerRepository playerRes = new PlayerRepository(getApplication());

                Match match = new Match(playerNames[0], soccer.getTeamsImg()[0], playerNames[1], soccer.getTeamsImg()[1], scores[0], scores[1]);
                Player player1 = playerRes.getPlayer(playerNames[0]);
                if (player1 == null) {
                    player1 = new Player(playerNames[0], 0);
                    playerRes.insert(player1);
                }
                Player player2 = playerRes.getPlayer(playerNames[1]);
                if (player2 == null) {
                    player2 = new Player(playerNames[1], 0);
                    playerRes.insert(player2);
                }

                if (scores[0] > scores[1]) {
                    player1.addVictory();
                    playerRes.update(player1);
                } else if (scores[1] > scores[0]) {
                    player2.addVictory();
                    playerRes.update(player2);
                }

                matchRes.insert(match);
                return null;
            }
        }.execute();
    }
}