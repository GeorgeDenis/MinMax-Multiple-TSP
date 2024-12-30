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
    public Population(int populationSize, int numberOfCities, int numberOfSalesman, double[][] adjacencyMatrix) {
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
                    mutant.balanceRoutes();
                    if (mutant.getScore() < chromosome.getScore()) {
                        population.set(population.indexOf(chromosome), mutant);
                    }
                }
            }
            for (Chromosome chromosome : population) {
                if (random.nextDouble() < mutationProbability) {
                    Chromosome mutant = new Chromosome(chromosome);
                    int salesmanIndex = random.nextInt(mutant.getSolution().size());
                    mutant.mutateLocal3Opt(salesmanIndex);
                    mutant.balanceRoutes();
                    if (mutant.getScore() < chromosome.getScore()) {
                        population.set(population.indexOf(chromosome), mutant);
                    }
                }
            }

//            for (Chromosome chromosome : population) {
//                if (random.nextDouble() < mutationProbability) {
//                    Chromosome mutant = new Chromosome(chromosome);
//                    mutant.mutateLocal();
//                    mutant.balanceRoutes();
//                    if (mutant.getScore() < chromosome.getScore()) {
//                        population.set(population.indexOf(chromosome), mutant);
//                    }
//                }
//            }

            for (int i = 0; i < population.size(); i++) {
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
//            for (Chromosome chromosome : population) {
//                if (random.nextDouble() < 0.3) {
//                    for (List<Integer> route : chromosome.getSolution()) {
//                        chromosome.hillClimbing(route);
//                    }
//                }
//            }

            List<Chromosome> top10Percent = population.stream()
                    .sorted(Comparator.comparingDouble(Chromosome::getScore))
                    .limit((int) (populationSize * 0.5))
                    .toList();

            for (Chromosome chromosome : top10Percent) {
                for (List<Integer> route : chromosome.getSolution()) {
                    chromosome.hillClimbing(route);
                }
            }
//            for (List<Integer> route : bestChromosome.getSolution()) {
//                bestChromosome.hillClimbing(route);
//            }
//            System.out.println("Iteration: " + iteration + ", Best Score: " + bestChromosome.getScore());
        }
    }


    private void performElitismSelection() {
        population.sort(Comparator.comparingDouble(Chromosome::getScore));
        List<Chromosome> nextGeneration = new ArrayList<>(population.subList(0, (int) (populationSize * 0.3)));

        while (nextGeneration.size() < populationSize) {
            nextGeneration.add(new Chromosome(population.get(random.nextInt((int) (populationSize * 0.3)))));
        }
        population = nextGeneration;
    }


    private void performRouletteSelection() {
        double totalFitness = 0;
        List<Double> invertedFitness = new ArrayList<>();

        for (Chromosome chromosome : population) {
            double fitness = 1.0 / (chromosome.getScore() + 1e-6);
            invertedFitness.add(fitness);
            totalFitness += fitness;
        }

        List<Chromosome> selected = new ArrayList<>();

        while (selected.size() < (int) (populationSize * 0.6)) {
            double spin = random.nextDouble() * totalFitness;
            double partialSum = 0;

            for (int i = 0; i < population.size(); i++) {
                partialSum += invertedFitness.get(i);
                if (partialSum >= spin) {
                    selected.add(population.get(i));
                    break;
                }
            }
        }

        population = selected;

        while (population.size() < populationSize) {
            population.add(new Chromosome(numberOfCities, numberOfSalesman, adjacencyMatrix));
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
