package com.example.myapplication.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.view.activities.GameplayActivity;

public class MainActivity extends AppCompatActivity {

  private final static int PLAY = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void play(View view) {

    final int[] teamsimg = { 0, 1 };
    final int fieldimg = 4;

    final double friction = 0.1;
    final double gamespeed = 1;

    final boolean[] botplay = { false, false };

    Intent intent = new Intent(this, GameplayActivity.class);
    intent.putExtra("teamsimg", teamsimg);
    intent.putExtra("fieldimg", fieldimg);
    intent.putExtra("friction", friction);
    intent.putExtra("gamespeed", gamespeed);
    intent.putExtra("botplay", botplay);

    startActivityForResult(intent, PLAY);
  }

}