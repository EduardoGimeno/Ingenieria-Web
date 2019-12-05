package urlshortener.service;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.springframework.stereotype.Service;

@Service
public class CSVService {
    public static List<String[]> readCSV(String path){
        List<String[]> records= new ArrayList<>();
        try{
            Reader reader= Files.newBufferedReader(Paths.get(path));
            CSVReader csvReader= new CSVReader(reader);
            records = csvReader.readAll();
            return records;
            }catch (Exception e){
                e.printStackTrace();
				return records;
            }
    }

    public static void writeCSV(String path,List<String[]> record){
        try{
            Writer writer= Files.newBufferedWriter(Paths.get(path));
            CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            for (int i=0; i< record.size();i++){
                csvWriter.writeNext(record.get(i));
            }
            csvWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}