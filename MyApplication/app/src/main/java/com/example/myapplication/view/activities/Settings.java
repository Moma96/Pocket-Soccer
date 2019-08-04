package com.example.myapplication.view.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

import static android.content.Context.MODE_PRIVATE;

public class Settings extends Fragment {

    private final static double BIGGEST_GAME_SPEED = 2;
    private final static double SMALLEST_GAME_SPEED = 0.5;
    private final static double GAME_SPEED_INCREMENT = 0.1;

    private final static double BIGGEST_FRICTION = 2.0;
    private final static double SMALLEST_FRICTION = 0.1;
    private final static double FRICTION_INCREMENT = 0.02;
    private final static double BIGGER_FRICTION_INCREMENT = 0.1;
    private final static double FRICTION_INCREMENT_TRANSITION = 0.4;

    private final static double BIGGEST_BALL_MASS = 4.0;
    private final static double SMALLEST_BALL_MASS = 0.1;
    private final static double BALL_MASS_INCREMENT = 0.1;

    private final static int FIELDS = 4;

    public final static double DEFAULT_FRICTION = 0.2;
    public final static double DEFAULT_GAME_SPEED = 1.0;
    public final static double DEFAULT_BALL_MASS = 0.4;
    public final static int DEFAULT_FIELD_IMG = 0;

    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;

    private View.OnClickListener changeGameSpeed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            double gamespeed = Double.longBitsToDouble(pref.getLong("game speed", Double.doubleToLongBits(DEFAULT_GAME_SPEED)));

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
            prefEditor.putLong("game speed", Double.doubleToLongBits(gamespeed));
            prefEditor.commit();

            updateGameSpeedValue();
        }
    };

    private View.OnClickListener changeFriction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            double friction = Double.longBitsToDouble(pref.getLong("friction", Double.doubleToLongBits(DEFAULT_FRICTION)));

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
            prefEditor.putLong("friction", Double.doubleToLongBits(friction));
            prefEditor.commit();

            updateFrictionValue();
        }
    };

    private View.OnClickListener changeBallMass = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            double ballMass = Double.longBitsToDouble(pref.getLong("ball mass", Double.doubleToLongBits(DEFAULT_FRICTION)));

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
            prefEditor.putLong("ball mass", Double.doubleToLongBits(ballMass));
            prefEditor.commit();

            updateBallMass();
        }
    };

    private View.OnClickListener changeField = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int fieldimg = pref.getInt("field img", DEFAULT_FIELD_IMG);

            switch(view.getId()) {
                case R.id.field_left:
                    fieldimg = (fieldimg - 1 + FIELDS) % FIELDS;
                    break;
                case R.id.field_right:
                    fieldimg = (fieldimg + 1) % FIELDS;
                    break;
            }
            prefEditor.putInt("field img", fieldimg);
            prefEditor.commit();

            updateFieldImg();
        }
    };

    private View.OnClickListener reset = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            resetPref();
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

        pref = getContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        prefEditor = pref.edit();

        initDefaultPref();
        setListener();
        resetPref();
    }

    private void initDefaultPref() {
        prefEditor.putLong("default friction", Double.doubleToLongBits(DEFAULT_FRICTION));
        prefEditor.putLong("default game speed", Double.doubleToLongBits(DEFAULT_GAME_SPEED));
        prefEditor.putLong("default ball mass", Double.doubleToLongBits(DEFAULT_BALL_MASS));
        prefEditor.putInt("default field img", DEFAULT_FIELD_IMG);

        prefEditor.commit();
    }

    private void resetPref() {
        prefEditor.putLong("friction", pref.getLong("default friction", Double.doubleToLongBits(DEFAULT_FRICTION)));
        prefEditor.putLong("game speed", pref.getLong("default game speed", Double.doubleToLongBits(DEFAULT_GAME_SPEED)));
        prefEditor.putLong("ball mass", pref.getLong("default ball mass", Double.doubleToLongBits(DEFAULT_BALL_MASS)));
        prefEditor.putInt("field img", pref.getInt("default field img", DEFAULT_FIELD_IMG));

        prefEditor.commit();

        updateGameSpeedValue();
        updateFrictionValue();
        updateBallMass();
        updateFieldImg();
    }

    private void updateGameSpeedValue() {
        double gamespeed = Double.longBitsToDouble(pref.getLong("game speed", Double.doubleToLongBits(DEFAULT_GAME_SPEED)));

        TextView fv = getActivity().findViewById(R.id.game_speed_value);
        fv.setText((int)(gamespeed*100) + "%");
    }

    private void updateFrictionValue() {
        double friction = Double.longBitsToDouble(pref.getLong("friction", Double.doubleToLongBits(DEFAULT_FRICTION)));

        TextView fv = getActivity().findViewById(R.id.friction_value);
        fv.setText((int)(friction*500) + "%");
    }

    private void updateBallMass() {
        double ballMass = Double.longBitsToDouble(pref.getLong("ball mass", Double.doubleToLongBits(DEFAULT_FRICTION)));

        TextView bm = getActivity().findViewById(R.id.ball_mass_value);
        bm.setText((int)(ballMass*250) + "%");
    }

    private void updateFieldImg() {
        int fieldimg = pref.getInt("field img", DEFAULT_FIELD_IMG);

        ImageView fimg = getActivity().findViewById(R.id.field_img);
        fimg.setImageResource(getResources().getIdentifier("field" + fieldimg, "drawable", getActivity().getPackageName()));
    }

    private void setListener() {
        getActivity().findViewById(R.id.game_speed_decrease).setOnClickListener(changeGameSpeed);
        getActivity().findViewById(R.id.game_speed_increase).setOnClickListener(changeGameSpeed);
        getActivity().findViewById(R.id.friction_decrease).setOnClickListener(changeFriction);
        getActivity().findViewById(R.id.friction_increase).setOnClickListener(changeFriction);
        getActivity().findViewById(R.id.ball_mass_decrease).setOnClickListener(changeBallMass);
        getActivity().findViewById(R.id.ball_mass_increase).setOnClickListener(changeBallMass);
        getActivity().findViewById(R.id.field_left).setOnClickListener(changeField);
        getActivity().findViewById(R.id.field_right).setOnClickListener(changeField);
        getActivity().findViewById(R.id.reset_text).setOnClickListener(reset);
    }
}
