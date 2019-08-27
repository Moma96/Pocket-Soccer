package com.example.myapplication.view.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.soccer.SoccerGameplay;

public class GameTypeSelection extends Fragment {

    private final static double BIGGEST_LIMIT = 10;
    private final static double SMALLEST_LIMIT = 1;
    private final static double LIMIT_INCREMENT = 1;

    private double limit = SoccerGameplay.DEFAULT_LIMIT;
    SoccerGameplay.FinishCriteria finishCriteria = SoccerGameplay.FinishCriteria.GOALS;
    SoccerGameplay.PlayingCriteria playingCriteria = SoccerGameplay.PlayingCriteria.MOTION;

    private View.OnClickListener changeLimit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.limit_decrease:
                    if (limit > SMALLEST_LIMIT)
                        limit -= LIMIT_INCREMENT;
                    break;
                case R.id.limit_increase:
                    if (limit < BIGGEST_LIMIT)
                        limit += LIMIT_INCREMENT;
                    break;
            }
            updateLimitValue();
        }
    };

    private View.OnClickListener changeFinishCriteria = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.minutes:
                    finishCriteria = SoccerGameplay.FinishCriteria.TIME;
                    break;
                case R.id.goals:
                    finishCriteria = SoccerGameplay.FinishCriteria.GOALS;
                    break;
            }
            updateFinishCriteria();
        }
    };

    private View.OnClickListener changePlayingCriteria = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.playing_static:
                    playingCriteria = SoccerGameplay.PlayingCriteria.STATIC;
                    break;
                case R.id.playing_motion:
                    playingCriteria = SoccerGameplay.PlayingCriteria.MOTION;
                    break;
            }
            updatePlayingCriteria();
        }
    };

    private View.OnClickListener next = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity)getActivity();
                activity.replaceFragment(activity.getSelectPlayers());
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_type_selection, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListener();
        updateLimitValue();
        updateFinishCriteria();
        updatePlayingCriteria();
    }

    public double getLimit() {
        return limit;
    }

    public SoccerGameplay.FinishCriteria getFinishCriteria() {
        return finishCriteria;
    }

    public SoccerGameplay.PlayingCriteria getPlayingCriteria() {
        return playingCriteria;
    }

    private void updateLimitValue() {
        TextView lv = getActivity().findViewById(R.id.limit_value);
        lv.setText("" + (int)limit);
    }

    private void updateFinishCriteria() {
        TextView ot = getActivity().findViewById(R.id.minutes);
        TextView g = getActivity().findViewById(R.id.goals);
        if (finishCriteria == SoccerGameplay.FinishCriteria.GOALS) {
            g.setTextColor(Color.WHITE);
            ot.setTextColor(Color.GRAY);
        } else {
            g.setTextColor(Color.GRAY);
            ot.setTextColor(Color.WHITE);
        }
    }

    private void updatePlayingCriteria() {
        TextView ps = getActivity().findViewById(R.id.playing_static);
        TextView pm = getActivity().findViewById(R.id.playing_motion);
        if (playingCriteria == SoccerGameplay.PlayingCriteria.STATIC) {
            ps.setTextColor(Color.WHITE);
            pm.setTextColor(Color.GRAY);
        } else {
            ps.setTextColor(Color.GRAY);
            pm.setTextColor(Color.WHITE);
        }
    }

    private void setListener() {
        View v = getActivity().findViewById(R.id.limit_decrease);
        v.setOnClickListener(changeLimit);
        getActivity().findViewById(R.id.limit_increase).setOnClickListener(changeLimit);
        getActivity().findViewById(R.id.playing_static).setOnClickListener(changePlayingCriteria);
        getActivity().findViewById(R.id.playing_motion).setOnClickListener(changePlayingCriteria);
        getActivity().findViewById(R.id.minutes).setOnClickListener(changeFinishCriteria);
        getActivity().findViewById(R.id.goals).setOnClickListener(changeFinishCriteria);
        getActivity().findViewById(R.id.next_text).setOnClickListener(next);
    }
}
