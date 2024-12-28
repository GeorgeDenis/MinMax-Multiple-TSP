package com.ga;


public class Main {
    public static void main(String[] args) {
        double[][] adjacencyMatrix = DataReader.coordinatesToAdjacencyMatrix("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\data\\berlin52.tsp.txt");
        double[][] coordinates = DataReader.readCoordinates("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\data\\berlin52.tsp.txt");


        Population population = new Population(100, 52, 3, adjacencyMatrix);
        population.runGeneticAlgorithm(40000, 0.3, 0.2);

        Chromosome bestChromosome = population.getBestResult();
        System.out.println("Best solution cost: " + bestChromosome.getCost());
        System.out.println("Best solution minmax: " + bestChromosome.getMinmax());
        System.out.println("Routes:");
        bestChromosome.printBestSolution();
        RouteVisualizer.visualize(bestChromosome, coordinates);

    }
}