package urlshortener.service;

import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

// Prueba Google Safe Browsing
public class SafeBrowsingTest {

    @Test
    public void thataSingleCorrectURLisOK() throws IOException, GeneralSecurityException {
        String URL = "https://developers.google.com/safe-browsing/v4/lists";
        List<String> result = SafeBrowsingService.checkURLs(Collections.singletonList(URL));
        assertTrue(result.isEmpty());
    }

    @Test
    public void thatMultipleCorrectURLsisOK() throws IOException, GeneralSecurityException {
        List<String> URLs = new ArrayList<>();
        URLs.add("https://developers.google.com/safe-browsing/v4/lists");
        URLs.add("https://www.youtube.com/");
        List<String> result = SafeBrowsingService.checkURLs(URLs);
        assertTrue(result.isEmpty());
    }

    // TODO
    @Test
    public void thataSingleMaliciousURLisOK() throws IOException, GeneralSecurityException {
        String URL = "?????";
        //List<String> result = SafeBrowsing.checkURLs(Collections.singletonList(URL));
        //assertEquals(1, result.size());
        //assertEquals(URL, result.get(0));
    }

    // TODO
    @Test
    public void thatMultipleMaliciousURLsisOK() throws IOException, GeneralSecurityException {
        List<String> URLs = new ArrayList<>();
        URLs.add("?????");
        URLs.add("?????");
        //List<String> result = SafeBrowsing.checkURLs(URLs);
        //assertEquals(2, result.size());
        //...
    }
}