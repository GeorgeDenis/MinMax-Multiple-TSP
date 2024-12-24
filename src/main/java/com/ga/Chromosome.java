package com.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Chromosome {

    private int numberOfCities; // Numărul de orașe
    private int numberOfSalesmen; // Numărul de salesmen
    private double[][] adjacencyMatrix; // Matricea de adiacență
    private List<List<Integer>> solution; // Soluția (rutele fiecărui salesman)
    private double cost; // Costul total al soluției
    private double minmax; // Cel mai lung cost al unui salesman
    private double score; // Fitness-ul total
    private Random random;

    // Constructor
    public Chromosome(int numberOfCities, int numberOfSalesmen, double[][] adjacencyMatrix) {
        this.numberOfCities = numberOfCities;
        this.numberOfSalesmen = numberOfSalesmen;
        this.adjacencyMatrix = adjacencyMatrix;
        this.solution = new ArrayList<>();
        this.random = new Random();

        generateRandomSolution();
        evaluateFitness();
    }

    public Chromosome(Chromosome other) {
        this.numberOfCities = other.numberOfCities;
        this.numberOfSalesmen = other.numberOfSalesmen;
        this.adjacencyMatrix = other.adjacencyMatrix;

        this.solution = new ArrayList<>();
        for (List<Integer> route : other.solution) {
            this.solution.add(new ArrayList<>(route));
        }

        this.cost = other.cost;
        this.minmax = other.minmax;
        this.score = other.score;
        this.random = new Random();
    }

    private void generateRandomSolution() {
        List<Integer> cities = new ArrayList<>();
        for (int i = 1; i < numberOfCities; i++) {
            cities.add(i);
        }
        Collections.shuffle(cities, random);

        int baseSize = cities.size() / numberOfSalesmen;
        int extra = cities.size() % numberOfSalesmen;


        int start = 0;
        for (int i = 0; i < numberOfSalesmen; i++) {
            List<Integer> salesmanRoute = new ArrayList<>();
            salesmanRoute.add(0);  // Start la depozit

            int end = start + baseSize + (i < extra ? 1 : 0);


            salesmanRoute.addAll(cities.subList(start, end));
            salesmanRoute.add(0);
            solution.add(salesmanRoute);

            start = end;
        }

        // Dacă există orașe rămase (deseori când `numberOfCities % numberOfSalesmen != 0`)
        while (start < cities.size()) {
            solution.get(random.nextInt(numberOfSalesmen)).add(1, cities.get(start++));
        }
    }


    // Funcția de fitness
    private void evaluateFitness() {
        cost = 0;
        minmax = 0;
        for (List<Integer> salesmanRoute : solution) {
            double salesmanCost = 0;
            for (int i = 0; i < salesmanRoute.size() - 1; i++) {
                int city1 = salesmanRoute.get(i);
                int city2 = salesmanRoute.get(i + 1);
                salesmanCost += adjacencyMatrix[city1][city2];
            }
            cost += salesmanCost;
            if (salesmanCost > minmax) {
                minmax = salesmanCost;
            }
        }
        score = cost + minmax;
    }


    public void mutateLocal() {
        int salesmanIndex = random.nextInt(numberOfSalesmen);
        List<Integer> salesmanRoute = solution.get(salesmanIndex);

        if (salesmanRoute.size() > 3) {
            int i = random.nextInt(salesmanRoute.size() - 2) + 1;
            int j = random.nextInt(salesmanRoute.size() - 2) + 1;
            Collections.swap(salesmanRoute, i, j);
            evaluateFitness();
        }
    }

    public void mutateGlobal() {
        int index1 = random.nextInt(numberOfSalesmen);
        int index2 = random.nextInt(numberOfSalesmen);
        while (index1 == index2) {
            index1 = random.nextInt(numberOfSalesmen);
            index2 = random.nextInt(numberOfSalesmen);
        }

        List<Integer> salesman1 = solution.get(index1);
        List<Integer> salesman2 = solution.get(index2);
        while (salesman1.size() < 4) {
            index1 = random.nextInt(numberOfSalesmen);
            index2 = random.nextInt(numberOfSalesmen);
            salesman1 = solution.get(index1);
            salesman2 = solution.get(index2);
        }
        int i = random.nextInt(salesman1.size() - 2) + 1;
        int city = salesman1.get(i);
        salesman1.remove(i);
        int j = random.nextInt(salesman2.size() - 1) + 1;
        salesman2.add(j, city);
        evaluateFitness();
    }

    public void crossover(Chromosome other) {
        for (int index = 0; index < numberOfSalesmen; index++) {
            List<Integer> salesman1 = solution.get(index);
            List<Integer> salesman2 = other.solution.get(index);

            int minLength = Math.min(salesman1.size(), salesman2.size());
            for (int i = 1; i < minLength - 1; i++) {
                int value = salesman1.get(i);
                if (salesman2.contains(value)) {
                    int idx = salesman1.indexOf(value);
                    if (idx != -1 && idx != i) {
                        Collections.swap(salesman1, i, idx);
                    }
                }
            }
        }
        evaluateFitness();
    }

    public void printBestSolution() {
        for (int i = 0; i < solution.size(); i++) {
            List<Integer> route = solution.get(i);
            double routeCost = 0;

            for (int j = 0; j < route.size() - 1; j++) {
                int city1 = route.get(j);
                int city2 = route.get(j + 1);
                routeCost += adjacencyMatrix[city1][city2];
            }

            System.out.print((i + 1) + ": ");
            for (int city : route) {
                System.out.print((city + 1) + "-");
            }
            System.out.printf("   (# %d)   Cost: %.2f%n", route.size(), routeCost);
        }
    }


    public double getScore() {
        return score;
    }

    public double getCost() {
        return cost;
    }

    public double getMinmax() {
        return minmax;
    }

    public List<List<Integer>> getSolution() {
        return solution;
    }
}
