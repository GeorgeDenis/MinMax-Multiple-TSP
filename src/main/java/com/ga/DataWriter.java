package com.ga;

import java.io.FileWriter;
import java.io.IOException;

public class DataWriter {
    public static void writeData(String dataPath, String data) {
        try {
            FileWriter fileWriter = new FileWriter(dataPath,true);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error writing data: " + e.getMessage());
        }
    }
}
