package com.example.myapplication.view.activities;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.myapplication.R;
import com.example.myapplication.model.soccer.database.entity.Match;
import com.example.myapplication.model.soccer.database.entity.Player;
import com.example.myapplication.model.soccer.database.repository.PlayerRepository;

public class History extends Fragment {

    public class MatchView extends LinearLayout {

        public MatchView(Context context, Match match) {
            super(context);
            Player player1 = new PlayerRepository(getActivity().getApplication()).getPlayer(match.getPlayer1Name()).getValue();
            Player player2 = new PlayerRepository(getActivity().getApplication()).getPlayer(match.getPlayer2Name()).getValue();
        }
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

        updateHistoryFragment();
    }

    public void updateHistoryFragment() {

    }
}
