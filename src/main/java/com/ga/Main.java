package com.ga;


public class Main {
    public static void main(String[] args) {
        double[][] adjacencyMatrix = DataReader.coordinatesToAdjacencyMatrix("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\data\\eil76.tsp.txt");
        double[][] coordinates = DataReader.readCoordinates("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\data\\eil76.tsp.txt");


//        Population population = new Population(100, 76, 2, adjacencyMatrix);
//        population.runGeneticAlgorithm(40000, 0.25, 0.2);

        double totalCost = 0;
        double totalMinmax = 0;
        double minCost = Double.MAX_VALUE;
        double minMinmax = Double.MAX_VALUE;
        double maxCost = Double.MIN_VALUE;
        double maxMinmax = Double.MIN_VALUE;

        for (int i = 0; i < 15; i++) {
            Population population = new Population(100, 76, 3, adjacencyMatrix);
            population.runGeneticAlgorithm(40000, 0.6, 0.2);
            System.out.println("Iteration: " + (i + 1) + "\n");
            DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\eil76-3.txt", "Iteration: " + (i + 1) + "\n");
            Chromosome bestChromosome = population.getBestResult();
            System.out.println("Best solution cost: " + bestChromosome.getCost());
            DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\eil76-3.txt", "Best solution cost: " + bestChromosome.getCost() + "\n");
            System.out.println("Best solution minmax: " + bestChromosome.getMinmax());
            DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\eil76-3.txt", "Best solution minmax: " + bestChromosome.getMinmax() + "\n");
            totalCost += bestChromosome.getCost();
            totalMinmax += bestChromosome.getMinmax();
            minCost = Math.min(minCost, bestChromosome.getCost());
            minMinmax = Math.min(minMinmax, bestChromosome.getMinmax());
            maxCost = Math.max(maxCost, bestChromosome.getCost());
            maxMinmax = Math.max(maxMinmax, bestChromosome.getMinmax());
            bestChromosome.writeBestSolution("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\eil76-3.txt");
        }

        System.out.println("Average cost: " + totalCost / 15);
        DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\eil76-3.txt", "Average cost: " + totalCost / 15 + "\n");
        System.out.println("Average minmax: " + totalMinmax / 15);
        DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\eil76-3.txt", "Average minmax: " + totalMinmax / 15 + "\n");
        System.out.println("Min cost: " + minCost);
        DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\eil76-3.txt", "Min cost: " + minCost + "\n");
        System.out.println("Min minmax: " + minMinmax);
        DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\eil76-3.txt", "Min minmax: " + minMinmax + "\n");


//        Chromosome bestChromosome = population.getBestResult();
//        System.out.println("Best solution cost: " + bestChromosome.getCost());
//        System.out.println("Best solution minmax: " + bestChromosome.getMinmax());
//        System.out.println("Routes:");
//        bestChromosome.printBestSolution();
//        RouteVisualizer.visualize(bestChromosome, coordinates);

//            runIterations(adjacencyMatrix);

    }

    public static void runIterations(double[][] adjacencyMatrix) {
        int[] salesman = {2, 3, 5, 7};
        for (int s : salesman) {
            double totalCost = 0;
            double totalMinmax = 0;
            double minCost = Double.MAX_VALUE;
            double minMinmax = Double.MAX_VALUE;
            double maxCost = Double.MIN_VALUE;
            double maxMinmax = Double.MIN_VALUE;

            for (int i = 0; i < 15; i++) {
                Population population = new Population(100, 99, s, adjacencyMatrix);
                population.runGeneticAlgorithm(40000, 0.4, 0.2);
                System.out.println("Iteration: " + (i + 1) + "\n");
                DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\rat99-" + s + ".txt", "Iteration: " + (i + 1) + "\n");
                Chromosome bestChromosome = population.getBestResult();
                System.out.println("Best solution cost: " + bestChromosome.getCost());
                DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\rat99-" + s + ".txt", "Best solution cost: " + bestChromosome.getCost() + "\n");
                System.out.println("Best solution minmax: " + bestChromosome.getMinmax());
                DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\rat99-" + s + ".txt", "Best solution minmax: " + bestChromosome.getMinmax() + "\n");
                totalCost += bestChromosome.getCost();
                totalMinmax += bestChromosome.getMinmax();
                minCost = Math.min(minCost, bestChromosome.getCost());
                minMinmax = Math.min(minMinmax, bestChromosome.getMinmax());
                maxCost = Math.max(maxCost, bestChromosome.getCost());
                maxMinmax = Math.max(maxMinmax, bestChromosome.getMinmax());
                bestChromosome.writeBestSolution("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\rat99-" + s + ".txt");
            }

            System.out.println("Average cost: " + totalCost / 15);
            DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\rat99-" + s + ".txt", "Average cost: " + totalCost / 15 + "\n");
            System.out.println("Average minmax: " + totalMinmax / 15);
            DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\rat99-" + s + ".txt", "Average minmax: " + totalMinmax / 15 + "\n");
            System.out.println("Min cost: " + minCost);
            DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\rat99-" + s + ".txt", "Min cost: " + minCost + "\n");
            System.out.println("Min minmax: " + minMinmax);
            DataWriter.writeData("K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\output\\rat99-" + s + ".txt", "Min minmax: " + minMinmax + "\n");
        }
    }
}