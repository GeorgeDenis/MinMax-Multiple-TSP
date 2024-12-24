package com.microservices;


public class Main {
    public static void main(String[] args) {
        double[][] adjacencyMatrix = DataReader.coordinatesToAdjacencyMatrix("K:\\Projects\\MIN\\MTSP\\MTSP\\src\\main\\java\\com\\microservices\\data\\eil51.tsp.txt");
        double[][] coordinates = DataReader.readCoordinates("K:\\Projects\\MIN\\MTSP\\MTSP\\src\\main\\java\\com\\microservices\\data\\eil51.tsp.txt");


        Population population = new Population(100, 51, 5, adjacencyMatrix);
        population.runGeneticAlgorithm(10000, 0.7, 0.7);

        Chromosome bestChromosome = population.getBestResult();
        System.out.println("Best solution cost: " + bestChromosome.getCost());
        System.out.println("Best solution minmax: " + bestChromosome.getMinmax());
        System.out.println("Routes:");
        bestChromosome.printBestSolution();
        RouteVisualizer.visualize(bestChromosome, coordinates);

    }
}