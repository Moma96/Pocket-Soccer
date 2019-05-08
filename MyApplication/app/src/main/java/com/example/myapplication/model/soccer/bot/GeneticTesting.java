package com.example.myapplication.model.soccer.bot;

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
            Unit max = get(0);
            for (Unit unit : this) {
                if (unit.getFitness() > max.getFitness()) {
                    max = unit;
                }
            }
            remove(max);
        }
    }

    public class Unit extends Thread {

        private TestingSoccerModel testingModel;
        private Vector genes;
        private int fitness;
        private boolean over = false;

        public Unit() {
            this(new Vector(Math.random(), Math.random()), Integer.MAX_VALUE);
            genes.scaleIntensity(SCALED_INTENSITY);
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

        public TestingSoccerModel getTestingModel() {
            return testingModel;
        }

        public GeneticTesting getGen() {
            return GeneticTesting.this;
        }

        public synchronized void finished(int scored, int time) {
            if (player == scored)
                fitness = time;
            else
                fitness = Integer.MAX_VALUE;
            selected.add(this);

            over = true;
            notify();
        }

        public synchronized void terminated() {
            over = true;
            notify();
        }

        @Override
        public void run() {
            try {
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

    private static final int POPULATION = 10;
    private static final int GENERATIONS = 10;
    private static final int TOP = POPULATION / 2;
    private static final double SCALED_INTENSITY = 100;

    private SoccerModel soccer;
    private int player;
    private int player_id;

    private int population;
    private int generations;
    private int top;

    private Unit[] generation;
    private Selected selected;

    public GeneticTesting(SoccerModel soccer, int player, int player_id) {
        this(soccer, player, player_id, POPULATION, GENERATIONS, TOP);
    }

    public GeneticTesting(@NotNull SoccerModel soccer, int player, int player_id, int population, int generations, int top) {
        this.soccer = soccer;
        this.player = player;
        this.player_id = player_id;

        this.population = population;
        this.generations = generations;
        this.top = top;

        generation = new Unit[population];
        for (int i = 0; i < population; i++) {
            generation[i] = new Unit();
        }
        selected = new Selected(top);
    }

    public Unit test() {
        for (int g = 0; g < generations; g++) {
            calculateFitness();
            crossBreed();
        }
        return fittest();
    }

    public int getPopulation() {
        return population;
    }

    public int getGenerations() {
        return generations;
    }

    public int getTop() {
        return top;
    }

    public Selected getSelected() {
        return selected;
    }

    public SoccerModel getSoccer() {
        return soccer;
    }

    @Nullable
    private Unit fittest() {
        if (selected == null) return null;

        Unit best = selected.get(0);
        for (Unit unit : selected) {
            if (unit.getFitness() < best.getFitness())
                best = unit;
        }
        return best;
    }

    private void crossBreed() {
        Unit[] new_gen = new Unit[population];
        int p = 0;
        for (int i = 0; i < population - 1; i++) {
            for (int j = i + 1; j < population; j++) {
                new_gen[p] = selected.get(i).crossBreed(selected.get(j));
                p++;
            }
        }
        generation = new_gen;
    }

    private void calculateFitness() {
        try {
            for (int i = 0; i < POPULATION; i++) {
                generation[i].start();
            }

            for (int i = 0; i < POPULATION; i++)
                generation[i].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
