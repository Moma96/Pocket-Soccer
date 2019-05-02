package com.example.myapplication.model.soccer.bot;

import com.example.myapplication.model.soccer.models.Player;
import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.model.Vector;

public class GeneticTesting {

    private class Unit extends Thread {

        private TestingSoccerModel testingModel;
        private Vector genes;
        private int fitness;
        private Unit[] resultArray;

        public Unit() {
            this(new Vector(Math.random(), Math.random()), Integer.MAX_VALUE);
            genes.scaleIntensity(SCALED_INTENSITY);
        }

        public Unit(Unit unit) {
            this(unit.genes, unit.fitness);
        }

        public Unit(Vector genes, int fitness) {
            testingModel = new TestingSoccerModel(soccer);
            this.genes = new Vector(genes);
            this.fitness = fitness;
        }

        public Unit crossBreed(Unit unit) {
            Unit result = new Unit(this);
            result.genes.setX((result.genes.getX() + unit.genes.getX()) / 2);
            result.genes.setY((result.genes.getY() + unit.genes.getY()) / 2);
            result.genes.scaleIntensity(SCALED_INTENSITY);
            return result;
        }

        public void abort() {
            testingModel.terminate();
        }

        public int getFitness() {
            return fitness;
        }

        public void setResultArray(Unit[] result) {
            synchronized (resultArray) {
                resultArray = result;
                notify();
            }
        }

        private synchronized void waitResultArray() {
            while (resultArray == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void checkResult() {

        }

        @Override
        public void run() {
            waitResultArray();
            /*if (player >= 2 || player_id >= testingModel.getPlayers(player).length)
                return -1; /////////////////throw exception*/
            notifyAll();
            testingModel.getPlayers()[player][player_id].push(genes);

            testingModel.waitData();

            synchronized(resultArray) {
                if (player == testingModel.getScored()) {
                    fitness = testingModel.getField().getTime();
                    checkResult();
                } else
                    fitness = Integer.MAX_VALUE;
            }
        }
    }

    private static final int POPULATION = 10;
    private static final int GENERATIONS = 10;
    private static final int TOP = POPULATION / 2;
    private static final double SCALED_INTENSITY = 100;

    private SoccerModel soccer;
    private int player;
    private int player_id;

    public GeneticTesting(SoccerModel soccer, int player, int player_id) {
        this.soccer = soccer;
        this.player = player;
        this.player_id = player_id;
    }

    private static Unit fittest(Unit[] generation) {
        if (generation == null) return null;

        Unit best = generation[0];
        for (int i = 1; i < POPULATION; i++) {
            if (generation[i].getFitness() < best.getFitness())
                best = generation[i];
        }
        return best;
    }

    public Unit test() {
        Unit[] generation = initialize();
        Unit[] selected = null;
        for (int g = 0; g < GENERATIONS; g++) {
            selected = calculateFitness(generation, selected);
            generation = crossBreed(selected);
        }
        return fittest(selected);
    }

    private Unit[] initialize() {
        Unit[] generation = new Unit[POPULATION];
        for (int i = 0; i < POPULATION; i++) {
            generation[i] = new Unit();
        }
        return generation;
    }

    private Unit[] crossBreed(Unit[] selected) {
        Unit[] generation = new Unit[POPULATION];
        int p = 0;
        for (int i = 0; i < POPULATION - 1; i++) {
            for (int j = i + 1; j < POPULATION; j++) {
                generation[p] = selected[i].crossBreed(selected[j]);
                p++;
            }
        }
        return generation;
    }

    private Unit[] calculateFitness(Unit[] generation, Unit[] selected) {
        try {
            Unit[] result = null;


            for (int i = 0; i < POPULATION; i++) {
                generation[i].setResultArray(result);
                generation[i].start();
            }

            for (int i = 0; i < POPULATION; i++)
                generation[i].join();

            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
