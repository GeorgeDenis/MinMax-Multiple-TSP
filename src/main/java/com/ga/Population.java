package com.ga;

import java.util.*;

public class Population {

    private List<Chromosome> population;
    private int numberOfSalesman;
    private int populationSize;
    private int numberOfCities;
    private double[][] adjacencyMatrix;
    private Random random;

    // Constructor
    public Population(int populationSize,int numberOfCities, int numberOfSalesman, double[][] adjacencyMatrix) {
        this.populationSize = populationSize;
        this.numberOfCities = numberOfCities;
        this.adjacencyMatrix = adjacencyMatrix;
        this.population = new ArrayList<>();
        this.random = new Random();
        this.numberOfSalesman = numberOfSalesman;

        for (int i = 0; i < populationSize; i++) {
            population.add(new Chromosome(numberOfCities, numberOfSalesman, adjacencyMatrix));
        }
    }

    public void runGeneticAlgorithm(int numberOfIterations, double mutationProbability, double crossoverProbability) {
        for (int iteration = 0; iteration < numberOfIterations; iteration++) {

            performTournamentSelection();

            for (Chromosome chromosome : population) {
                if (random.nextDouble() < mutationProbability) {
                    Chromosome mutant = new Chromosome(chromosome);
                    mutant.mutateGlobal();
                    if (mutant.getScore() < chromosome.getScore()) {
                        population.set(population.indexOf(chromosome), mutant);
                    }
                }
            }

            // Mutare locală
            for (Chromosome chromosome : population) {
                if (random.nextDouble() < mutationProbability) {
                    Chromosome mutant = new Chromosome(chromosome);
                    mutant.mutateLocal();
                    if (mutant.getScore() < chromosome.getScore()) {
                        population.set(population.indexOf(chromosome), mutant);
                    }
                }
            }

            // Crossover
            for (int i = 0; i < population.size(); i++) {
                if (random.nextDouble() < crossoverProbability) {
                    int partnerIndex = random.nextInt(population.size());
                    if (i != partnerIndex) {
                        Chromosome child1 = new Chromosome(population.get(i));
                        Chromosome child2 = new Chromosome(population.get(partnerIndex));
                        child1.crossover(population.get(partnerIndex));
                        child2.crossover(population.get(i));
                        if (child1.getScore() < population.get(i).getScore()) {
                            population.set(i, child1);
                        }
                        if (child2.getScore() < population.get(partnerIndex).getScore()) {
                            population.set(partnerIndex, child2);
                        }
                    }
                }
            }
            Chromosome bestChromosome = getBestResult();
//            System.out.println("Iteration: " + iteration + ", Best Score: " + bestChromosome.getScore());

        }
    }

    private void performTournamentSelection() {
        int k = populationSize;
        int j = (int) (populationSize * 0.6);

        while (population.size() > k) {
            population.remove(random.nextInt(population.size()));
        }

        while (population.size() > j) {
            Chromosome worstChromosome = Collections.max(population, Comparator.comparingDouble(Chromosome::getScore));
            population.remove(worstChromosome);
        }

        while (population.size() < populationSize) {
            population.add(new Chromosome(numberOfCities, numberOfSalesman, adjacencyMatrix));
        }
    }

    public Chromosome getBestResult() {
        return Collections.min(population, Comparator.comparingDouble(Chromosome::getScore));
    }

    public int getNumberOfSalesman() {
        return numberOfSalesman;
    }
}
