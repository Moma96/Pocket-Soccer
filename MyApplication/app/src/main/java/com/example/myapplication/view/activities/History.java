package com.example.myapplication.view.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.collidables.inactive.Line;
import com.example.myapplication.model.soccer.database.entity.Match;
import com.example.myapplication.model.soccer.database.entity.Player;
import com.example.myapplication.model.soccer.database.repository.MatchRepository;
import com.example.myapplication.model.soccer.database.repository.PlayerRepository;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class History extends Fragment {

    public class MatchView extends LinearLayout {

        private TextView player1Victories;
        private TextView player2Victories;
        private TextView player1Name;
        private TextView player2Name;
        private ImageView player1Team;
        private ImageView player2Team;
        private TextView score;

        @RequiresApi(api = Build.VERSION_CODES.M)
        public MatchView(Context context, Match match, int victories1, int victories2) {
            super(context);

            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER);

            player1Victories = new TextView(context);
            player1Victories.setText(String.valueOf(victories1));
            player1Victories.setPadding(30, 30, 30, 30);
            player1Victories.setTextAppearance(R.style.Text);
            addView(player1Victories);

            player1Name = new TextView(context);
            player1Name.setText(match.getPlayer1Name());
            player1Name.setPadding(30, 30, 30, 30);
            player1Name.setTextAppearance(R.style.Text);
            addView(player1Name);

            player1Team = new ImageView(context);
            player1Team.setBackgroundResource(getActivity().getResources().getIdentifier("t" + match.getPlayer1Team(), "drawable", getActivity().getPackageName()));
            player1Team.setPadding(30, 30, 30, 30);
            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(100, 100);
            addView(player1Team, params1);

            score = new TextView(context);
            score.setText(match.getPlayer1Score() + ":" + match.getPlayer2Score());
            score.setPadding(30, 30, 30, 30);
            score.setTextAppearance(R.style.Text);
            addView(score);

            player2Team = new ImageView(context);
            player2Team.setBackgroundResource(getActivity().getResources().getIdentifier("t" + match.getPlayer2Team(), "drawable", getActivity().getPackageName()));
            player2Team.setPadding(30, 30, 30, 30);
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(100, 100);
            addView(player2Team, params2);

            player2Name = new TextView(context);
            player2Name.setText(match.getPlayer2Name());
            player2Name.setTextAppearance(R.style.Text);
            player2Name.setPadding(30, 30, 30, 30);
            addView(player2Name);

            player2Victories = new TextView(context);
            player2Victories.setText(String.valueOf(victories2));
            player2Victories.setTextAppearance(R.style.Text);
            player2Victories.setPadding(30, 30, 30, 30);
            addView(player2Victories);
        }
    }

    private String strPlayer1Name;
    private String strPlayer2Name;

    private LinkedList<MatchView> matchViews;

    private View.OnClickListener showAllMatches = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            strPlayer1Name = null;
            strPlayer2Name = null;
            showMatches();
        }
    };

    private View.OnClickListener showMatchesBetween = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MatchView matchView = (MatchView)view;
            strPlayer1Name = matchView.player1Name.getText().toString();
            strPlayer2Name = matchView.player2Name.getText().toString();
            showMatches(matchView.player1Name.getText().toString(), matchView.player2Name.getText().toString());
        }
    };

    private View.OnClickListener deleteMatches = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (strPlayer1Name != null && strPlayer2Name != null) {
                delete(strPlayer1Name, strPlayer2Name);
            } else
                delete();
        }
    };

    private View.OnClickListener mainMenu = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity main = (MainActivity)getActivity();
            main.replaceFragment(main.getMainMenu());
        }
    };

    public History() {
        super();
    }

    @SuppressLint("ValidFragment")
    public History(String strPlayer1Name, String strPlayer2Name) {
        super();
        this.strPlayer1Name = strPlayer1Name;
        this.strPlayer2Name = strPlayer2Name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListeners();
        matchViews = new LinkedList<>();
        updateHistoryFragment();
    }

    private void setListeners() {
        getActivity().findViewById(R.id.all_matches).setOnClickListener(showAllMatches);
        getActivity().findViewById(R.id.delete).setOnClickListener(deleteMatches);
        getActivity().findViewById(R.id.main_menu).setOnClickListener(mainMenu);
    }

    public void updateHistoryFragment() {
        if (strPlayer1Name != null && strPlayer2Name != null) {
            showMatches(strPlayer1Name, strPlayer2Name);
        } else
            showMatches();
    }

    public void showMatches() {
        new AsyncTask<Match, Void, Void>() {
            @Override
            protected Void doInBackground(final Match... matches) {
                MatchRepository matchRep = new MatchRepository(getActivity().getApplication());
                showMatches(matchRep.getAll());
                return null;
            }
        }.execute();
    }

    public void showMatches(final String player1, final String player2) {
        new AsyncTask<Match, Void, Void>() {
            @Override
            protected Void doInBackground(final Match... matches) {
                MatchRepository matchRep = new MatchRepository(getActivity().getApplication());
                showMatches(matchRep.getAllBetween(player1, player2));
                return null;
            }
        }.execute();
    }

    public void delete() {
        new AsyncTask<Match, Void, Void>() {
            @Override
            protected Void doInBackground(final Match... matches) {
                MatchRepository matchRep = new MatchRepository(getActivity().getApplication());
                matchRep.deleteAll();
                showMatches();
                return null;
            }
        }.execute();
    }

    public void delete(final String player1, final String player2) {
        new AsyncTask<Match, Void, Void>() {
            @Override
            protected Void doInBackground(final Match... matches) {
                MatchRepository matchRep = new MatchRepository(getActivity().getApplication());
                matchRep.deleteAllBetween(player1, player2);
                showMatches(player1, player2);
                return null;
            }
        }.execute();
    }

    private void showMatches(final List<Match> matches) {
        PlayerRepository playerRes = new PlayerRepository(getActivity().getApplication());
        HashMap<String, Integer> players = new HashMap<>();
        if (matches != null) {
            for (Match match : matches) {
                if (!players.containsKey(match.getPlayer1Name())) {
                    Player player1 = playerRes.getPlayer(match.getPlayer1Name());
                    players.put(player1.getName(), player1.getVictories());
                }
                if (!players.containsKey(match.getPlayer2Name())) {
                    Player player2 = playerRes.getPlayer(match.getPlayer2Name());
                    players.put(player2.getName(), player2.getVictories());
                }
            }
        }

        final HashMap<String, Integer> finalPlayers = players;

        getActivity().runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                LinearLayout ll = getActivity().findViewById(R.id.match_list);
                ll.removeAllViewsInLayout();
                matchViews.clear();
                if (matches != null) {
                    for (Match match : matches) {
                        MatchView matchView = new MatchView(getActivity(), match, finalPlayers.get(match.getPlayer1Name()), finalPlayers.get(match.getPlayer2Name()));
                        matchView.setOnClickListener(showMatchesBetween);
                        matchViews.add(matchView);
                        ll.addView(matchView);
                    }
                }
            }
        });
    }
}
