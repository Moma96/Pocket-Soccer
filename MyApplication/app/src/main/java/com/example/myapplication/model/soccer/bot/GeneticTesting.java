package com.example.myapplication.model.soccer.bot;

import android.util.Log;

import com.example.myapplication.model.soccer.models.SoccerModel;
import com.example.myapplication.model.Vector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GeneticTesting {

    public class Selected extends ArrayList<Unit> {
        private int capacity;
        private int maximum = 0;

        public Selected(int capacity) {
            super();
            this.capacity = capacity;
        }

        @Override
        public synchronized boolean add(Unit unit) {
            if (full()) {
                if (unit.getFitness() >= maximum)
                    return false;
                else {
                    removeMax();
                    resetMax();
                }
            }

            Boolean result = super.add(unit);
            if (result) {
                if (unit.getFitness() > maximum) {
                    maximum = unit.getFitness();
                }
            }
            return result;
        }

        public synchronized boolean surpassed(int fitness) {
            if (full() && fitness > maximum) {
                return true;
            }
            return false;
        }

        public synchronized boolean full() {
            return size() == capacity;
        }

        public int getMaximum() {
            return maximum;
        }

        private synchronized void removeMax() {
            Unit max = get(0);
            for (Unit unit : this) {
                if (unit.getFitness() > max.getFitness()) {
                    max = unit;
                }
            }
            remove(max);
        }

        private synchronized void resetMax() {
            maximum = 0;
            for (Unit unit : this) {
                if (unit.getFitness() > maximum) {
                    maximum = unit.getFitness();
                }
            }
        }
    }

    public class Unit extends Thread {

        private TestingSoccerModel testingModel;
        private Vector genes;
        private int fitness;
        private boolean over = false;

        public Unit() {
            this(new Vector(Math.random()*2 - 1, Math.random()*2 - 1), Integer.MAX_VALUE);
            genes.scaleIntensity(SCALED_INTENSITY);
            fitness = Integer.MAX_VALUE;
        }

        public Unit(@NotNull Unit unit) {
            this(unit.genes, unit.fitness);
        }

        public Unit(Vector genes, int fitness) {
            testingModel = new TestingSoccerModel(this);
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

        public int getFitness() {
            return fitness;
        }

        public Vector getGenes() {
            return genes;
        }

        public TestingSoccerModel getTestingModel() {
            return testingModel;
        }

        public GeneticTesting getGen() {
            return GeneticTesting.this;
        }

        public synchronized void finished(int scored, int time) {
            if (player == scored) {
                fitness = time;
                selected.add(this);
                Log.d(GENETIC_TAG, "Unit finished with time " + time);
            }
            else {
                Log.d(GENETIC_TAG, "Bad score with time " + time);
            }
            over = true;
            notifyAll();
        }

        public synchronized void terminated(int time) {
            over = true;
            notifyAll();
            Log.d(GENETIC_TAG, "Unit terminated with time " + time);
        }

        @Override
        public void run() {
            try {
                testingModel.start();
                testingModel.getPlayers()[player][player_id].push(genes);
                synchronized (this) {
                    while (!over) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static final int INITIAL_POPULATION = 100;
    private static final int GENERATIONS = 10;
    private static final int TOP = 7;
    private static final int TIME_LIMIT = 300; //500;
    private static final int SATISFACTORY_TIME = 100;
    private static final double SCALED_INTENSITY = 1000; // 300 - cenim bar 700

    private static final String GENETIC_TAG = "Genetic testing";

    private SoccerModel soccer;
    private int player;
    private int player_id;

    private int initialPopulation;
    private int generations;
    private int top;
    private int timeLimit;

    private ArrayList<Unit> generation;
    private Selected selected;

    public GeneticTesting(SoccerModel soccer, int player, int player_id) {
        this(soccer, player, player_id, INITIAL_POPULATION, GENERATIONS, TOP, TIME_LIMIT);
    }

    public GeneticTesting(@NotNull SoccerModel soccer, int player, int player_id, int init_population, int generations, int top, int time_limit) {
        this.soccer = soccer;
        this.player = player;
        this.player_id = player_id;

        this.initialPopulation = init_population;
        this.generations = generations;
        this.top = top;
        this.timeLimit = time_limit;

        generation = new ArrayList<>();
        for (int i = 0; i < initialPopulation; i++) {
            generation.add(new Unit());
        }
        selected = new Selected(top);
    }

    public Unit test() {
        Unit fittest = null;
        for (int g = 0; g < generations; g++) {
            Log.d(GENETIC_TAG, "generation: " + g);

            calculateFitness();
            crossBreed();
            fittest = fittest();

            if (fittest().getFitness() < SATISFACTORY_TIME)
                break;
            if (selected.isEmpty())
                return new Unit();
            if (selected.size() == 1)
                break;
            Log.d(GENETIC_TAG, "selected: " + selected.size());
        }
        //Unit fittest = fittest();
        Log.d(GENETIC_TAG, "THE BEST TIME FOR PLAYER " + player_id + ": " + fittest.getFitness());
        return fittest;
    }

    public int getInitialPopulation() {
        return initialPopulation;
    }

    public int getGenerations() {
        return generations;
    }

    public int getTop() {
        return top;
    }

    public int timeLimit() {
        return timeLimit;
    }

    public Selected getSelected() {
        return selected;
    }

    public SoccerModel getSoccer() {
        return soccer;
    }

    public Unit fittest() {
        if (selected.isEmpty()) return new Unit();

        Unit best = selected.get(0);
        for (Unit unit : selected) {
            if (unit.getFitness() < best.getFitness())
                best = unit;
        }
        return best;
    }

    private void crossBreed() {
        ArrayList<Unit> new_gen = new ArrayList<>();
        for (int i = 0; i < selected.size() - 1; i++) {
            for (int j = i + 1; j < selected.size(); j++) {
                new_gen.add(selected.get(i).crossBreed(selected.get(j)));
            }
        }
        generation = new_gen;
    }

    private void calculateFitness() {
        try {
            for (Unit unit : generation)
                unit.start();

            for (Unit unit : generation)
                unit.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
