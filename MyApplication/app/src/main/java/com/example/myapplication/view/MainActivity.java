package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.view.GameplayActivity;

public class MainActivity extends AppCompatActivity {

    private final static int PLAY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void play(View view) {
        int dimensions = 0;
        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtra("dimensions", dimensions);
        startActivityForResult(intent, PLAY);
    }

}
