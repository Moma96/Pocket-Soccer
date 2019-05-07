package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.SoccerField;

import org.jetbrains.annotations.NotNull;

public class TestingField extends SoccerField {

    GeneticTesting.Unit genUnit;

    public TestingField(double x, double y, double width, double height, @NotNull GeneticTesting.Unit unit) {
        super(x, y, width, height, 0, unit.getTestingModel());
        this.genUnit = unit;
    }

    @Override
    protected void checkTime() {
        if (genUnit.getGen().getSelected().surpassed(getTime())) {
            getSoccer().terminate();
            genUnit.terminated();
        }
    }
}
