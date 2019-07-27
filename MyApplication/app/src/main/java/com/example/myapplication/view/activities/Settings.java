package com.example.myapplication.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

public class Settings extends Fragment {

    private final static double BIGGEST_GAME_SPEED = 2;
    private final static double SMALLEST_GAME_SPEED = 0.5;
    private final static double GAME_SPEED_INCREMENT = 0.1;

    private final static double BIGGEST_FRICTION = 2.0;
    private final static double SMALLEST_FRICTION = 0.1;
    private final static double FRICTION_INCREMENT = 0.02;
    private final static double BIGGER_FRICTION_INCREMENT = 0.1;
    private final static double FRICTION_INCREMENT_TRANSITION = 0.4;

    private final static double BIGGEST_BALL_MASS = 2.0;
    private final static double SMALLEST_BALL_MASS = 0.1;
    private final static double BALL_MASS_INCREMENT = 0.1;


    private final static int FIELDS = 4;

    private double friction = 0.2;
    private double gamespeed = 1;
    private double ballMass = 0.4;
    private int fieldimg = 0;

    private View.OnClickListener changeGameSpeed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.game_speed_decrease:
                    if (gamespeed > SMALLEST_GAME_SPEED)
                        gamespeed -= GAME_SPEED_INCREMENT;
                    break;
                case R.id.game_speed_increase:
                    if (gamespeed < BIGGEST_GAME_SPEED)
                        gamespeed += GAME_SPEED_INCREMENT;
                    break;
            }
            updateGameSpeedValue();
        }
    };

    private View.OnClickListener changeFriction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            double increment = FRICTION_INCREMENT;
            if (friction > FRICTION_INCREMENT_TRANSITION)
                increment = BIGGER_FRICTION_INCREMENT;
            switch(view.getId()) {
                case R.id.friction_decrease:
                    if (friction > SMALLEST_FRICTION)
                        friction -= increment;
                    break;
                case R.id.friction_increase:
                    if (friction < BIGGEST_FRICTION)
                        friction += increment;
                    break;
            }
            updateFrictionValue();
        }
    };

    /*private View.OnClickListener changeField = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.field_left:
                    fieldimg = (fieldimg - 1 + FIELDS) % FIELDS;
                    break;
                case R.id.field_right:
                    fieldimg = (fieldimg + 1) % FIELDS;
                    break;
            }
            updateFieldImg();
        }
    };*/

    private View.OnClickListener changeBallWeight = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.ball_mass_decrease:
                    if (ballMass > SMALLEST_BALL_MASS)
                        ballMass -= BALL_MASS_INCREMENT;
                    break;
                case R.id.ball_mass_increase:
                    if (ballMass < BIGGEST_BALL_MASS)
                        ballMass += BALL_MASS_INCREMENT;
                    break;
            }
            updateBallMass();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateGameSpeedValue();
        updateFrictionValue();
        updateBallMass();
        //updateFieldImg();
        setListener();
    }

    public double getFriction() {
        return friction;
    }

    public double getGamespeed() {
        return gamespeed;
    }

    public double getBallMass() {
        return ballMass;
    }

    public int getFieldimg() {
        return fieldimg;
    }

    private void updateGameSpeedValue() {
        TextView fv = getActivity().findViewById(R.id.game_speed_value);
        fv.setText((int)(gamespeed*100) + "%");
    }

    private void updateFrictionValue() {
        TextView fv = getActivity().findViewById(R.id.friction_value);
        fv.setText((int)(friction*500) + "%");
    }

    private void updateBallMass() {
        TextView bm = getActivity().findViewById(R.id.ball_mass_value);
        bm.setText((int)(ballMass*250) + "%");
    }

    /*private void updateFieldImg() {
        ImageView fimg = getActivity().findViewById(R.id.field_img);
        fimg.setImageResource(getResources().getIdentifier("field" + fieldimg, "drawable", getActivity().getPackageName()));
    }*/

    private void setListener() {
        getActivity().findViewById(R.id.game_speed_decrease).setOnClickListener(changeGameSpeed);
        getActivity().findViewById(R.id.game_speed_increase).setOnClickListener(changeGameSpeed);
        getActivity().findViewById(R.id.friction_decrease).setOnClickListener(changeFriction);
        getActivity().findViewById(R.id.friction_increase).setOnClickListener(changeFriction);
        //getActivity().findViewById(R.id.field_left).setOnClickListener(changeField);
        //getActivity().findViewById(R.id.field_right).setOnClickListener(changeField);
        getActivity().findViewById(R.id.ball_mass_decrease).setOnClickListener(changeBallWeight);
        getActivity().findViewById(R.id.ball_mass_increase).setOnClickListener(changeBallWeight);
    }
}
