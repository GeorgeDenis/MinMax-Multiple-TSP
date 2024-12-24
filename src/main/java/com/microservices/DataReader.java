package com.microservices;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.ml.distance.EuclideanDistance;


public class DataReader {
    public static double[][] coordinatesToAdjacencyMatrix(String dataPath) {
        List<double[]> coordinates = new ArrayList<>();
        EuclideanDistance euclideanDistance = new EuclideanDistance();

        try {
            File file = new File(dataPath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    coordinates.add(new double[]{x, y});
                }
            }
            scanner.close();

            int n = coordinates.size();
            double[][] adjacencyMatrix = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        adjacencyMatrix[i][j] = euclideanDistance.compute(coordinates.get(i), coordinates.get(j));
                    } else {
                        adjacencyMatrix[i][j] = 0;
                    }
                }
            }

            return adjacencyMatrix;

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + e.getMessage());
        }

        return null;
    }

    public static double[][] readCoordinates(String dataPath) {
        List<double[]> coordinates = new ArrayList<>();

        try {
            File file = new File(dataPath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    coordinates.add(new double[]{x, y});
                }
            }
            scanner.close();

            return coordinates.toArray(new double[0][0]);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + e.getMessage());
        }

        return null;
    }

}
