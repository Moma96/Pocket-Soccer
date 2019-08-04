package com.example.myapplication.view.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

public class SelectPlayers extends Fragment {

    private final static int PLAYER_TEAMS = 35;

    public static final String PLAYER1_NAME = "PLAYER 1";
    public static final String PLAYER2_NAME = "PLAYER 2";
    public static final String BOT_NAME = "BOT";

    private int[] teamsimg = { 0, 1 };
    private boolean[] botplay = { false, false };

    private View.OnClickListener changeTeam = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
                onClick(view);
            updatePlayersImg();
        }
    };

    private View.OnClickListener changeBotplay = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.player1_human:
                    botplay[0] = false;
                    break;
                case R.id.player1_bot:
                    botplay[0] = true;
                    break;
                case R.id.player2_human:
                    botplay[1] = false;
                    break;
                case R.id.player2_bot:
                    botplay[1] = true;
                    break;
            }
            updateBotplay();
        }
    };

    private View.OnClickListener newGame = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity)getActivity();
                activity.newGame();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_players, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListener();
        updatePlayersImg();
        updatePlayersNames();
        updateBotplay();
    }

    public boolean[] getBotplay() {
        EditText p1n = getActivity().findViewById(R.id.player1_name);
        EditText p2n = getActivity().findViewById(R.id.player2_name);
        if (BOT_NAME.equals(p1n.getText().toString().toUpperCase()))
            botplay[0] = true;
        if (BOT_NAME.equals(p2n.getText().toString().toUpperCase()))
            botplay[1] = true;
        return botplay;
    }

    public int[] getTeamsimg() {
        return teamsimg;
    }

    public String getPlayerName(int id) {
        EditText et = null;
        switch (id) {
            case 0:
                et = getActivity().findViewById(R.id.player1_name);
                break;
            case 1:
                et = getActivity().findViewById(R.id.player2_name);
                break;
        }
        if (et == null) return null;
        return String.valueOf(et.getText());
    }

    private void setListener() {
        getActivity().findViewById(R.id.player1_left).setOnClickListener(changeTeam);
        getActivity().findViewById(R.id.player1_right).setOnClickListener(changeTeam);
        getActivity().findViewById(R.id.player2_left).setOnClickListener(changeTeam);
        getActivity().findViewById(R.id.player2_right).setOnClickListener(changeTeam);
        getActivity().findViewById(R.id.player1_human).setOnClickListener(changeBotplay);
        getActivity().findViewById(R.id.player1_bot).setOnClickListener(changeBotplay);
        getActivity().findViewById(R.id.player2_human).setOnClickListener(changeBotplay);
        getActivity().findViewById(R.id.player2_bot).setOnClickListener(changeBotplay);
        getActivity().findViewById(R.id.play_text).setOnClickListener(newGame);
    }

    private void updatePlayersImg() {
        ImageView player1img = getActivity().findViewById(R.id.player1_img);
        player1img.setImageResource(getResources().getIdentifier("t" + teamsimg[0], "drawable", getActivity().getPackageName()));
        ImageView player2img = getActivity().findViewById(R.id.player2_img);
        player2img.setImageResource(getResources().getIdentifier("t" + teamsimg[1], "drawable", getActivity().getPackageName()));
    }

    private void updatePlayersNames() {
        EditText p1n = getActivity().findViewById(R.id.player1_name);
        p1n.setText(PLAYER1_NAME);
        EditText p2n = getActivity().findViewById(R.id.player2_name);
        p2n.setText(PLAYER2_NAME);
    }

    private void updateBotplay() {
        TextView p1h = getActivity().findViewById(R.id.player1_human);
        TextView p1b = getActivity().findViewById(R.id.player1_bot);
        TextView p2h = getActivity().findViewById(R.id.player2_human);
        TextView p2b = getActivity().findViewById(R.id.player2_bot);
        EditText p1n = getActivity().findViewById(R.id.player1_name);
        EditText p2n = getActivity().findViewById(R.id.player2_name);

        if (!botplay[0]) {
            p1h.setTextColor(Color.WHITE);
            p1b.setTextColor(Color.GRAY);
            if (BOT_NAME.equals(p1n.getText().toString().toUpperCase())) {
                p1n.setText(PLAYER2_NAME);
                p1n.setEnabled(true);
                p1n.setClickable(true);
            }
        } else {
            p1h.setTextColor(Color.GRAY);
            p1b.setTextColor(Color.WHITE);
            p1n.setText(BOT_NAME);
            p1n.setEnabled(false);
            p1n.setClickable(false);
        }
        if (!botplay[1]) {
            p2h.setTextColor(Color.WHITE);
            p2b.setTextColor(Color.GRAY);
            if (BOT_NAME.equals(p2n.getText().toString().toUpperCase())) {
                p2n.setText(PLAYER2_NAME);
                p2n.setEnabled(true);
                p2n.setClickable(true);
            }
        } else {
            p2h.setTextColor(Color.GRAY);
            p2b.setTextColor(Color.WHITE);
            p2n.setText(BOT_NAME);
            p2n.setEnabled(false);
            p2n.setClickable(false);
        }
    }
}
