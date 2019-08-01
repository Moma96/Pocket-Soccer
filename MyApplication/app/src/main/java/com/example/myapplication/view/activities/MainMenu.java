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

public class MainMenu extends Fragment {

    private View.OnClickListener changeFragment = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment frag = null;
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity)getActivity();
                switch (view.getId()) {
                    case R.id.new_game:
                        frag = activity.getGameTypeSelection();
                        break;
                    case R.id.settings:
                        frag = activity.getSettings();
                        break;
                    case R.id.history:
                        frag = activity.getHistory();
                        break;
                }

                activity.replaceFragment(frag);
            }
        }
    };

    private View.OnClickListener lastGame = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity)getActivity();
                activity.continueLastGame();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListener();
        updateMenuFragment();
    }

    public void updateMenuFragment() {
        MainActivity activity = (MainActivity)getActivity();
        if (activity.soccer == null) {
            TextView clg = getActivity().findViewById(R.id.last_game);
            clg.setClickable(false);
            clg.setTextColor(Color.GRAY);
        }
    }

    private void setListener() {
        getActivity().findViewById(R.id.new_game).setOnClickListener(changeFragment);
        getActivity().findViewById(R.id.last_game).setOnClickListener(lastGame);
        getActivity().findViewById(R.id.settings).setOnClickListener(changeFragment);
        getActivity().findViewById(R.id.history).setOnClickListener(changeFragment);
    }
}
