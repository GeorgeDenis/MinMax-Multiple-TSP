package com.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Chromosome {

    private int numberOfCities;
    private int numberOfSalesmen;
    private double[][] adjacencyMatrix;
    private List<List<Integer>> solution;
    private double cost;
    private double minmax;
    private double score;
    private Random random;

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
        int iteration = 0;

        while (improved && iteration < 15) {
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

            if (heaviestTour != null && lightestTour != null && (maxCost - minCost) > 0.15 * avgTourCost) {
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
            salesmanRoute.add(0);

            int end = start + baseSize + (i < extra ? 1 : 0);
            salesmanRoute.addAll(cities.subList(start, end));
            salesmanRoute.add(0);
            solution.add(salesmanRoute);

            start = end;
        }

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
            cost += salesmanCost;
            if (salesmanCost > minmax) {
                minmax = salesmanCost;
            }
        }

        double avgTourCost = totalTourCost / numberOfSalesmen;
        double imbalance = minmax - avgTourCost;

        score = cost + Math.pow(imbalance, 1.3);
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

    public void mutateLocal2Opt(int salesmanIndex) {
        List<Integer> route = solution.get(salesmanIndex);
        if (route.size() > salesmanIndex + 1) {
            int i = random.nextInt(route.size() - 3) + 1;
            int j = i + 1 + random.nextInt(route.size() - i - 2);

            while (i < j) {
                Collections.swap(route, i, j);
                i++;
                j--;
            }
            evaluateFitness();
        }
    }

    public void mutateLocal3Opt(int salesmanIndex) {
        List<Integer> route = solution.get(salesmanIndex);
        int size = route.size();
        if (size > 5) {
            int i = random.nextInt(size - 4) + 1;
            int j = i + 1 + random.nextInt(size - i - 3);
            int k = j + 1 + random.nextInt(size - j - 2);

            List<Integer> segment1 = new ArrayList<>(route.subList(0, i));
            List<Integer> segment2 = new ArrayList<>(route.subList(i, j));
            List<Integer> segment3 = new ArrayList<>(route.subList(j, k));
            List<Integer> segment4 = new ArrayList<>(route.subList(k, size));

            Collections.reverse(segment2);
            Collections.reverse(segment3);

            route.clear();
            route.addAll(segment1);
            route.addAll(segment3);
            route.addAll(segment2);
            route.addAll(segment4);
        }
        evaluateFitness();
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

    public void writeBestSolution(String dataPath) {
        for (int i = 0; i < solution.size(); i++) {
            List<Integer> route = solution.get(i);
            double routeCost = 0;
            for (int j = 0; j < route.size() - 1; j++) {
                int city1 = route.get(j);
                int city2 = route.get(j + 1);
                routeCost += adjacencyMatrix[city1][city2];
            }
            DataWriter.writeData(dataPath, (i + 1) + ": ");
            for (int city : route) {
                DataWriter.writeData(dataPath, (city + 1) + "-");
            }

            DataWriter.writeData(dataPath, String.format("   (# %d)   Cost: %.2f%n", route.size(), routeCost));
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

    public void setNumberOfCities(int numberOfCities) {
        this.numberOfCities = numberOfCities;
    }

    public void setNumberOfSalesmen(int numberOfSalesmen) {
        this.numberOfSalesmen = numberOfSalesmen;
    }

    public void setAdjacencyMatrix(double[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
    }

    public void setSolution(List<List<Integer>> solution) {
        this.solution = solution;
    }
}
