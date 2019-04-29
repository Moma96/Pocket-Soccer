package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.model.Vector;

public class GeneticTesting {

    private class Unit extends Thread {

        private TestingSoccerModel testingModel;
        private Vector genes;
        private int fitness;

        public Unit() {
            this(new Vector(Math.random(), Math.random()), 0);
            genes.scaleIntensity(1);
        }

        public Unit(Unit unit) {
            this(unit.genes, unit.fitness);
        }

        public Unit(Vector genes, int fitness) {
            testingModel = new TestingSoccerModel(soccer);
            this.genes = new Vector(genes);
            this.fitness = fitness;
        }

        public void crossBreed(Unit unit) {
            genes.setX((genes.getX() + unit.genes.getX()) / 2);
            genes.setY((genes.getY() + unit.genes.getY()) / 2);
            genes.scaleIntensity(1);
        }

        public void run() {
            fitness = testingModel.test(player, player_id, genes);
        }
    }

    private static final int POPULATION = 10;
    private static final int GENERATIONS = 10;

    private SoccerModel soccer;
    private int player;
    private int player_id;

    public GeneticTesting(SoccerModel soccer, int player, int player_id) {
        this.soccer = soccer;
        this.player = player;
        this.player_id = player_id;
    }

    /*public Vector test() {
        Unit[] generation = null;
        int[] fitness = null;
        for (int g = 0; g < GENERATIONS; g++) {
            if (g == 0)
                generation = initialize();
            else
                generation = crossBreed(generation);
            calculateFitness(generation);
            generation = select(generation);
        }
        return fittest(generation, fitness);
    }

    private void initialize() {
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

    private void calculateFitness(Unit[] generation) throws InterruptedException {
        int[] fitness = new int[POPULATION];
        for (int i = 0; i < POPULATION; i++) {
            generation[i].start();
        }
        for (int i = 0; i < POPULATION; i++) {
            generation[i].join();
        }
    }

    private void select(Unit[] generation) {

    }

    private Unit fittest(Unit[] generation) {
        int min_time = fitness[0];
        Vector best = generation[0];
        for (int i = 1; i < POPULATION; i++) {
            if (fitness[i] < min_time) {
                min_time = fitness[i];
                best = generation[i];
            }
        }
        return best;
    }*/
}
