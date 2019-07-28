package com.example.myapplication.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication.R;

public class SelectPlayers extends Fragment {

    private final static int PLAYER_TEAMS = 35;

    private int[] teamsimg = { 0, 1 };

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
    }

    public int[] getTeamsimg() {
        return teamsimg;
    }

    private void setListener() {
        getActivity().findViewById(R.id.player1_left).setOnClickListener(changeTeam);
        getActivity().findViewById(R.id.player1_right).setOnClickListener(changeTeam);
        getActivity().findViewById(R.id.player2_left).setOnClickListener(changeTeam);
        getActivity().findViewById(R.id.player2_right).setOnClickListener(changeTeam);
        getActivity().findViewById(R.id.play_text).setOnClickListener(newGame);
    }

    private void updatePlayersImg() {
        ImageView player1img = getActivity().findViewById(R.id.player1_img);
        player1img.setImageResource(getResources().getIdentifier("t" + teamsimg[0], "drawable", getActivity().getPackageName()));
        ImageView player2img = getActivity().findViewById(R.id.player2_img);
        player2img.setImageResource(getResources().getIdentifier("t" + teamsimg[1], "drawable", getActivity().getPackageName()));
    }
}
