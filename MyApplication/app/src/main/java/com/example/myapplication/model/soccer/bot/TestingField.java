package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.SoccerField;

import org.jetbrains.annotations.NotNull;

public class TestingField extends SoccerField {

    GeneticTesting.Unit genUnit;

    public TestingField(double x, double y, double width, double height, @NotNull GeneticTesting.Unit unit, @NotNull TestingSoccerModel soccer) {
        super(x, y, width, height, soccer);
        this.genUnit = unit;
    }

    @Override
    protected void checkTime() {
        if (getTime() >= genUnit.getGen().timeLimit()) {

            getSoccer().terminate();
            genUnit.terminated(getTime());
        }
    }
}
