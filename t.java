package org.cloudbus.cloudsim.examples.RLcontainer;

import java.io.*;
import java.util.*;

public class t {
    public static void main(String[] args) {
        List<List<String>> data = readCSV("A:/c_meta.csv", false);
    }

    public static List<List<String>> readCSV(String filePath, boolean hasTitle){
        List<List<String>> data=new ArrayList<>();
        String line=null;
        try {
            //BufferedReader bufferedReader=new BufferedReader(new FileReader(filePath));
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"utf-8"));
            if (hasTitle){
                line = bufferedReader.readLine();
                String[] items=line.split(",");
                data.add(Arrays.asList(items));
            }

            int i=0;
            while((line=bufferedReader.readLine())!=null){
                String[] items=line.split(",");
                data.add(Arrays.asList(items));
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
