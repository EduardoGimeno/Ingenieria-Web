package urlshortener.utils;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class HTTPInfoTests {

    private HTTPInfo httpInfo;

    @Before
    public void setUp() {
        this.httpInfo = new HTTPInfo();
    }

    @Test
    public void testUserAgentInfoExtraction1() throws IOException {
        String uaString = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";
        String info = httpInfo.getInfo(uaString);
        assertEquals("OS: Windows NAV: Firefox", info);
    }

    @Test
    public void testUserAgentInfoExtraction2() throws IOException {
        String uaString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        String info = httpInfo.getInfo(uaString);
        assertEquals("OS: Linux NAV: Chrome", info);
    }
}