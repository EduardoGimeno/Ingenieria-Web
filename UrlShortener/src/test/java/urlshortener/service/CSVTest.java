package urlshortener.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import com.squareup.okhttp.MultipartBuilder;

import org.apache.http.util.ByteArrayBuffer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.MultipartBodyBuilder.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

public class CSVTest {
    @Mock
    private CSVService CSVService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void thatReadisCorrect() throws IOException {
        String lines = "www.google.es";
        byte[] bytes= lines.getBytes();
        MockMultipartFile multipart= new MockMultipartFile("file", bytes);
        String[] result = CSVService.getURLs(multipart);
        assertEquals(lines, result[0]);
    }

    @Test
    public void thatWriteOfContentisCorrect() throws IOException {
        String lines = "www.google.es";
        OutputStream arg0= new ByteArrayOutputStream();
        OutputStreamWriter w= new OutputStreamWriter(arg0);
        HashMap<String,String> map = new HashMap<>();
        map.put(lines, "f1");
        String result = CSVService.write(w, map);
        String[] result_expected = result.split("\n");
        System.out.println(result_expected);
        assertEquals(result_expected.length, 2);
        assertEquals(result_expected[1].contains(lines),true);
    }
}