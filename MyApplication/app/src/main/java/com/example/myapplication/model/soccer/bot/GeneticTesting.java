package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.model.Vector;

public class GeneticTesting {

    private static final int POPULATION = 10;
    private static final int GENERATIONS = 10;

    private SoccerModel soccer;

    public GeneticTesting(SoccerModel soccer) {
        this.soccer = soccer;
    }

    public Vector test() {
        Vector[] generation = null;
        int[] fitness = null;
        for (int g = 0; g < GENERATIONS; g++) {
            if (g == 0)
                generation = initialize();
            else
                generation = crossBreed(generation);
            fitness = getFitness(generation);
            generation = select(generation, fitness);
        }
        return fittest(generation, fitness);
    }

    private Vector[] initialize() {
        Vector[] generation = new Vector[POPULATION];
        for (int i = 0; i < POPULATION; i++) {
            generation[i] = new Vector(Math.random(), Math.random());
            generation[i].scaleIntensity(1);
        }
        return generation;
    }

    private Vector[] crossBreed(Vector[] generation) {

        return generation;
    }

    private int[] getFitness(Vector[] generation) {

    }

    private Vector[] select(Vector[] generation, int[] fitness) {

    }

    private Vector fittest(Vector[] generation, int[] fitness) {
        int min_time = fitness[0];
        Vector best = generation[0];
        for (int i = 1; i < POPULATION; i++) {
            if (fitness[i] < min_time) {
                min_time = fitness[i];
                best = generation[i];
            }
        }
        return best;
    }
}
