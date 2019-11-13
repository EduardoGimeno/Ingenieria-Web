package urlshortener.utils;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class CSVTest {
    /*@Test
    public void readCSVTest() {
        String path = "/home/jorge/test.csv";
        try{
        Reader reader= Files.newBufferedReader(Paths.get(path));
        CSVReader csvReader= new CSVReader(reader);
        String[] record= csvReader.readNext();
        assertEquals("http://www.google.es",record[0]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
    @Test
    public void readAllCSVTest() {
        String path = "/home/jorge/test.csv";
        try{
        Reader reader= Files.newBufferedReader(Paths.get(path));
        CSVReader csvReader= new CSVReader(reader);
        List<String[]> record= csvReader.readAll();
        assertEquals("http://www.google.es",record.get(0)[0]);
        assertEquals("http://www.google.com",record.get(1)[0]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void writeCSVTest() {
        String path = "/home/jorge/test.csv";
        try{
        Writer writer= Files.newBufferedWriter(Paths.get(path));
        CSVWriter csvWriter= new CSVWriter(writer);
        String[] entries={"www.localhost.com"};
        csvWriter.writeNext(entries);
        csvWriter.close();
        Reader reader= Files.newBufferedReader(Paths.get(path));
        CSVReader csvReader= new CSVReader(reader);
        List<String[]> record= csvReader.readAll();
        assertEquals("www.localhost.com",record.get(2)[0]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}