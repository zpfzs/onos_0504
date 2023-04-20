package org.onosproject.roadm;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OutputUtils {

    public String readFromFile() {
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("output.txt"));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String output = stringBuilder.toString();
        //System.out.println(output);
        return output;
    }

}
