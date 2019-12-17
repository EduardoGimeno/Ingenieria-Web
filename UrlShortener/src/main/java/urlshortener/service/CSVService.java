package urlshortener.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ResultSetColumnNameHelperService;
import com.opencsv.ResultSetHelperService;

import org.checkerframework.checker.units.qual.h;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CSVService {
    public static String[] getURLs(MultipartFile f) throws IOException{
        String urls= new String(f.getBytes());
        return urls.split("\n");
    }
    public static String write(OutputStreamWriter w, HashMap<String,String> h) throws IOException{
        ResultSetColumnNameHelperService s= new ResultSetColumnNameHelperService();
        StringWriter str = new StringWriter();
        CSVWriter csv=new CSVWriter(str);
        String[] names={"Long URL","Is correct?", "Shorted URL"};
        //s.setColumnNames(names, names);
        //String[] lines = {s.toString()};
        csv.writeNext(names);
        for (String key: h.keySet() ){
            String value= h.get(key);
            String state = "correcto";
            if(value.equals("ERROR")){
                state= "incorrect";
            }
            String[] col= {key,state,value};
            csv.writeNext(col);
        }
        csv.close();
        System.out.println(str);
        return str.toString();
    }
}