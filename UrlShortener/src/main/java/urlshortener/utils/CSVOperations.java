package urlshortener.utils;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.util.List;

// TODO
public class CSVOperations {

    //https://www.callicoder.com/java-read-write-csv-file-opencsv/
    public static void readCSV(String path) {
        try {
            System.out.println("Working Directory = " +
                    System.getProperty("user.dir"));
            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(path);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            List<String[]> allData = csvReader.readAll();
            System.out.println(allData.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeCSV() {

    }
}
