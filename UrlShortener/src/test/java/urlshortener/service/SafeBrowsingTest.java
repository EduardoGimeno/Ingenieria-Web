package urlshortener.service;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import urlshortener.repository.ShortURLRepository;
import urlshortener.repository.impl.ShortURLRepositoryImpl;

// Prueba Google Safe Browsing
public class SafeBrowsingTest {

    private SafeBrowsingService safeBrowsingService;

    @Before
    public void setup() {
        EmbeddedDatabase db = new EmbeddedDatabaseBuilder().setType(HSQL)
                .addScript("schema-hsqldb.sql").build();
        JdbcTemplate jdbc = new JdbcTemplate(db);
        ShortURLRepository repository = new ShortURLRepositoryImpl(jdbc);
        safeBrowsingService = new SafeBrowsingService(repository);
    }

    @Test
    public void thataSingleCorrectURLisOK() throws IOException, GeneralSecurityException {
        String URL = "http://example.com/";
        Set<String> result = safeBrowsingService.checkURLs(Collections.singletonList(URL));
        assertTrue(result.isEmpty());
    }

    @Test
    public void thatMultipleCorrectURLsisOK() throws IOException, GeneralSecurityException {
        List<String> URLs = new ArrayList<>();
        URLs.add("https://developers.google.com/safe-browsing/v4/lists");
        URLs.add("https://www.youtube.com/");
        Set<String> result = safeBrowsingService.checkURLs(URLs);
        assertTrue(result.isEmpty());
    }

    @Test
    public void thataSingleMaliciousURLisOK() throws IOException, GeneralSecurityException {
        String URL = "http://malware.testing.google.test/testing/malware/";
        Set<String> result = safeBrowsingService.checkURLs(Collections.singletonList(URL));
        assertEquals(1, result.size());
        Iterator iter = result.iterator();
        assertEquals(URL, iter.next());
    }

    @Test
    public void thatMultipleMaliciousURLsisOK() throws IOException, GeneralSecurityException {
        List<String> URLs = new ArrayList<>();
        URLs.add("https://the2pappsoftsubgroup.info/23po4ntogs/cba/index.php");
        URLs.add("http://malware.testing.google.test/testing/malware/");
        Set<String> result = safeBrowsingService.checkURLs(URLs);
        assertEquals(2, result.size());
    }
}