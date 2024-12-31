package com.ga;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RoutesLoader {
    public static List<List<Integer>> showRoutes(String cityInstance) {
        List<List<Integer>> routes = new ArrayList<>();
        try {
            File file = new File(cityInstance);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                List<Integer> current = new ArrayList<>();
                String[] parts = line.split("-");
                for (String part : parts) {
                    current.add(Integer.parseInt(part) - 1);
                }
                routes.add(new ArrayList<>(current));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return routes;
    }

    public static void visualizeRoutes() {
        String basePath = "K:\\Projects\\MIN\\MTSP\\Github-MTSP\\MinMax-Multiple-TSP\\src\\main\\java\\com\\ga\\data\\";
        String[][] inputs = {
                {"eil51", "51"},
                {"berlin52", "52"},
                {"eil76", "76"},
                {"rat99", "99"}
        };
        int[] salesmen = {2, 3, 5, 7};
        for (String[] input : inputs) {
            double[][] coordinates = DataReader.readCoordinates(basePath + input[0] + ".tsp.txt");
            double[][] adjacencyMatrix = DataReader.coordinatesToAdjacencyMatrix(basePath + input[0] + ".tsp.txt");
            for (int i = 0; i < salesmen.length; i++) {
                List<List<Integer>> routes = showRoutes(basePath + "visualizer\\" + input[0] + "-" + salesmen[i] + ".txt");
                Chromosome chromosome = new Chromosome(Integer.parseInt(input[1]), salesmen[i], adjacencyMatrix);
                chromosome.setSolution(routes);
                RouteVisualizer.visualize(chromosome, coordinates);
            }

        }
    }
}
