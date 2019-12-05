package urlshortener.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class URLReachableServiceTests {

    private URLReachableService urlReachableService;

    @Before
    public void setUp() {
        this.urlReachableService = new URLReachableService();
    }

    @Test
    public void testURLReachableSync() throws IOException {
        String url = "https://www.google.es";
        assertEquals(true, urlReachableService.isReachableSynchronous(url));
    }

    @Test
    public void testUrlNoReachableSync() throws IOException {
        String url = "https://www.noexists.com";
        assertEquals(false, urlReachableService.isReachableSynchronous(url));
    }
}