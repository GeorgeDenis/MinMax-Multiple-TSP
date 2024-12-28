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

    public void balanceRoutes() {
        boolean improved = true;
        int maxIterations = 5;
        int iteration = 0;

        while (improved && iteration < maxIterations) {
            improved = false;
            double avgTourCost = cost / numberOfSalesmen;

            List<Integer> heaviestTour = null;
            List<Integer> lightestTour = null;
            double maxCost = 0;
            double minCost = Double.MAX_VALUE;

            for (List<Integer> route : solution) {
                double routeCost = calculateRouteCost(route);
                if (routeCost > maxCost) {
                    maxCost = routeCost;
                    heaviestTour = route;
                }
                if (routeCost < minCost) {
                    minCost = routeCost;
                    lightestTour = route;
                }
            }

            // Mută doar dacă dezechilibrul este semnificativ (>20% față de medie)
            if (heaviestTour != null && lightestTour != null && (maxCost - minCost) > 0.2 * avgTourCost) {
                int bestCityIndex = -1;
                double bestSavings = 0;

                for (int i = 1; i < heaviestTour.size() - 1; i++) {
                    int city = heaviestTour.get(i);
                    heaviestTour.remove(i);
                    double newCost = calculateRouteCost(heaviestTour);
                    double savings = maxCost - newCost;

                    if (savings > bestSavings) {
                        bestSavings = savings;
                        bestCityIndex = i;
                    }
                    heaviestTour.add(i, city);
                }

                if (bestCityIndex != -1) {
                    int city = heaviestTour.remove(bestCityIndex);
                    lightestTour.add(random.nextInt(lightestTour.size() - 1) + 1, city);
                    improved = true;
                    evaluateFitness();
                }
            }
            iteration++;
        }
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
            salesmanRoute.add(0);  // Start de la depozit

            int end = start + baseSize + (i < extra ? 1 : 0);
            salesmanRoute.addAll(cities.subList(start, end));
            salesmanRoute.add(0);
            solution.add(salesmanRoute);

            start = end;
        }

        // Reechilibrare după generarea inițială
        balanceRoutes();
    }

    private void evaluateFitness() {
        cost = 0;
        minmax = 0;
        double totalTourCost = 0;
        int nonEmptyTours = 0;

        for (List<Integer> salesmanRoute : solution) {
            double salesmanCost = calculateRouteCost(salesmanRoute);
            totalTourCost += salesmanCost;
            if (salesmanCost > 0) {
                nonEmptyTours++;
            }
            cost += salesmanCost;
            if (salesmanCost > minmax) {
                minmax = salesmanCost;
            }
        }

        double avgTourCost = nonEmptyTours > 0 ? totalTourCost / nonEmptyTours : 0;
        double imbalance = minmax - avgTourCost;

        // Penalizare progresivă - mai blândă pentru dezechilibre mici
        double penaltyFactor = imbalance > 15 ? 8 : 3.5;
        score = cost + imbalance * penaltyFactor;
    }


    private double calculateRouteCost(List<Integer> route) {
        double cost = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            int city1 = route.get(i);
            int city2 = route.get(i + 1);
            cost += adjacencyMatrix[city1][city2];
        }
        return cost;
    }


    public void mutateLocal() {
        int salesmanIndex = random.nextInt(numberOfSalesmen);
        List<Integer> salesmanRoute = solution.get(salesmanIndex);

        if (salesmanRoute.size() > numberOfSalesmen + 1) {
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
        while (salesman1.size() < numberOfSalesmen + 1) {
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
        // Aplică reechilibrare doar dacă diferențele sunt mari
        if (minmax > 1.2 * (cost / numberOfSalesmen)) {
            balanceRoutes();
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

    public void hillClimbing(List<Integer> route) {
        boolean improved = true;
        while (improved) {
            improved = false;
            double currentCost = calculateRouteCost(route);
            for (int i = 1; i < route.size() - 2; i++) {
                for (int j = i + 1; j < route.size() - 1; j++) {
                    Collections.swap(route, i, j);
                    double newCost = calculateRouteCost(route);

                    if (newCost < currentCost) {
                        currentCost = newCost;
                        improved = true;
                    } else {
                        Collections.swap(route, i, j);
                    }
                }
            }
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
