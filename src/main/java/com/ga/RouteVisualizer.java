package com.ga;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class RouteVisualizer extends JPanel {
    private Chromosome bestChromosome;
    private double[][] coordinates;

    public RouteVisualizer(Chromosome bestChromosome, double[][] coordinates) {
        this.bestChromosome = bestChromosome;
        this.coordinates = coordinates;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        double[][] normalizedCoords = normalizeCoordinates(coordinates, getWidth(), getHeight());

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawLine(50, getHeight() - 50, getWidth() - 50, getHeight() - 50);
        g2d.drawLine(50, 50, 50, getHeight() - 50);

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (double[] point : coordinates) {
            if (point[0] < minX) minX = point[0];
            if (point[1] < minY) minY = point[1];
            if (point[0] > maxX) maxX = point[0];
            if (point[1] > maxY) maxY = point[1];
        }

        int tickCountX = 10;
        for (int i = 0; i <= tickCountX; i++) {
            int xValue = (int) (minX + i * (maxX - minX) / tickCountX);
            int x = 50 + (int) ((xValue - minX) / (maxX - minX) * (getWidth() - 100));
            g2d.drawLine(x, getHeight() - 55, x, getHeight() - 45);
            g2d.drawString(String.format("%d", xValue), x - 10, getHeight() - 30);
        }

        int tickCountY = 10;
        for (int i = 0; i <= tickCountY; i++) {
            int yValue = (int) (minY + i * (maxY - minY) / tickCountY);
            int y = getHeight() - 50 - (int) ((yValue - minY) / (maxY - minY) * (getHeight() - 100));
            g2d.drawLine(45, y, 55, y);
            g2d.drawString(String.format("%d", yValue), 20, y + 5);
        }

        g2d.setColor(Color.BLACK);
        for (int i = 0; i < normalizedCoords.length; i++) {
            int x = (int) normalizedCoords[i][0];
            int y = (int) normalizedCoords[i][1];
            g2d.fillOval(x - 8, y - 8, 16, 16);
            g2d.drawString(String.valueOf(i + 1), x + 10, y - 10);
        }

        g2d.setStroke(new BasicStroke(3));
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.PINK};
        List<List<Integer>> solution = bestChromosome.getSolution();
        for (int i = 0; i < solution.size(); i++) {
            g2d.setColor(colors[i % colors.length]);
            List<Integer> route = solution.get(i);
            for (int j = 0; j < route.size() - 1; j++) {
                int city1 = route.get(j);
                int city2 = route.get(j + 1);

                int x1 = (int) normalizedCoords[city1][0];
                int y1 = (int) normalizedCoords[city1][1];
                int x2 = (int) normalizedCoords[city2][0];
                int y2 = (int) normalizedCoords[city2][1];

                g2d.drawLine(x1, y1, x2, y2);
            }
        }
    }


    private double[][] normalizeCoordinates(double[][] coordinates, int width, int height) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (double[] point : coordinates) {
            if (point[0] < minX) minX = point[0];
            if (point[1] < minY) minY = point[1];
            if (point[0] > maxX) maxX = point[0];
            if (point[1] > maxY) maxY = point[1];
        }

        double scaleX = (width - 100) / (maxX - minX);
        double scaleY = (height - 100) / (maxY - minY);

        double scale = Math.min(scaleX, scaleY);

        double[][] normalized = new double[coordinates.length][2];

        for (int i = 0; i < coordinates.length; i++) {
            normalized[i][0] = (coordinates[i][0] - minX) * scale + 50;
            normalized[i][1] = height - ((coordinates[i][1] - minY) * scale + 50);
        }

        return normalized;
    }

    private void saveAsImage(String fileName) {
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        this.paint(g2d);
        g2d.dispose();
        try {
            File file = new File(fileName + ".png");
            file.getParentFile().mkdirs();
            ImageIO.write(image, "png", file);
            System.out.println("Saved: " + fileName + ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void visualize(Chromosome bestChromosome, double[][] coordinates, String fileName) {
        JFrame frame = new JFrame("Salesman Route Visualization");
        RouteVisualizer visualizer = new RouteVisualizer(bestChromosome, coordinates);
        frame.add(visualizer);
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> visualizer.saveAsImage(fileName));
    }
}
