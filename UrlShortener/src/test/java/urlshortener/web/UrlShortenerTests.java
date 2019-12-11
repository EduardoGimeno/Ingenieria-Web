package urlshortener.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener.fixtures.ShortURLFixture.someUrl;
import static urlshortener.fixtures.ShortURLFixture.urlNotReachable;
import static urlshortener.fixtures.ShortURLFixture.urlNotSafe;

import java.io.IOException;
import java.net.URI;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.service.ClickService;
import urlshortener.service.HTTPInfo;
import urlshortener.service.LimitRedirectionService;
import urlshortener.service.ShortURLService;
import urlshortener.service.URLReachableService;
import urlshortener.service.SafeBrowsingService;

public class UrlShortenerTests {

    private MockMvc mockMvc;

    @Mock
    private ClickService clickService;

    @Mock
    private ShortURLService shortUrlService;

    @Mock
    private HTTPInfo httpInfo;

    @Mock
    private URLReachableService urlReachableService;
	
    @Mock
    private SafeBrowsingService safeBrowsingService;

    @Mock
    private LimitRedirectionService LimitRedirectionService;

    @InjectMocks
    private UrlShortenerController urlShortener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
    }

    //********************* Extracción información ***************************//

    @Test
    public void thathttpHeaderInfoSavedOnDataBase() throws Exception {
        when(shortUrlService.findByKey("someKey")).thenReturn(someUrl());
        when(httpInfo.getNav("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0")).thenReturn("Firefox");
        when(httpInfo.getOS("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0")).thenReturn("Windows");

        mockMvc.perform(get("/{id}", "someKey").header(HttpHeaders.USER_AGENT, 
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0"))
                .andDo(print());
        
        verify(clickService).saveClick("someKey", "127.0.0.1", "Windows", "Firefox");
    }

    //********************* Alcanzabilidad ***************************//

    @Test
    @Ignore
    public void thatUrlReachabilitySavedOnDataBase() throws Exception {
        configureSave("http://sponsor.com/");

        mockMvc.perform(
                post("/link").param("url", "http://example.com/").param(
                        "sponsor", "http://sponsor.com/")).andDo(print())
                .andExpect(redirectedUrl("http://localhost/f684a3c4"))
                .andExpect(status().isCreated());

        verify(shortUrlService, timeout(5000)).updateReachable("f684a3c4", true);
    }

    @Test
    public void thatRedirectToFailsIfNoReachability() throws Exception {
        when(shortUrlService.findByKey("someKey")).thenReturn(urlNotReachable());

        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isBadGateway());
    }

    //********************* Google Safe Browsing ***************************//

    @Test
    @Ignore
    public void thatUrlSafenessSavedOnDataBase() throws Exception {
        configureSave("http://sponsor.com/");

        mockMvc.perform(
                post("/link").param("url", "http://example.com/").param(
                        "sponsor", "http://sponsor.com/")).andDo(print())
                .andExpect(redirectedUrl("http://localhost/f684a3c4"))
                .andExpect(status().isCreated());

        //verify(, timeout(5000));
    }

    @Test
    public void thatRedirectToFailsIfNoSafe() throws Exception {
        when(shortUrlService.findByKey("someKey")).thenReturn(urlNotSafe());

        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isBadGateway());
    }

    //********************* Limitar redirecciones ***************************//

    @Test
    @Ignore
    public void thatRedirectToFailsIfLimitReached() throws Exception {
        when(shortUrlService.findByKey("someKey")).thenReturn(someUrl());

        for (int i=0; i<urlShortener.getLimitRedirects(); ++i) {
            mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                  .andExpect(status().isTemporaryRedirect()); 
        }
  
        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isTooManyRequests());
    }

    @Test
    public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
            throws Exception {
        when(shortUrlService.findByKey("someKey")).thenReturn(someUrl());
  
        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isTemporaryRedirect())
                .andExpect(redirectedUrl("http://example.com/"));
    }

    @Test
    public void thatRedirecToReturnsNotFoundIdIfKeyDoesNotExist()
            throws Exception {
        when(shortUrlService.findByKey("someKey")).thenReturn(null);

        mockMvc.perform(get("/{id}", "someKey")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
        configureSave(null);

        mockMvc.perform(post("/link").param("url", "http://example.com/"))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/f684a3c4"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hash", is("f684a3c4")))
                .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
                .andExpect(jsonPath("$.target", is("http://example.com/")))
                .andExpect(jsonPath("$.sponsor", is(nullValue())));
    }

    @Test
    public void thatShortenerCreatesARedirectWithSponsor() throws Exception {
        configureSave("http://sponsor.com/");

        mockMvc.perform(
                post("/link").param("url", "http://example.com/").param(
                        "sponsor", "http://sponsor.com/")).andDo(print())
                .andExpect(redirectedUrl("http://localhost/f684a3c4"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hash", is("f684a3c4")))
                .andExpect(jsonPath("$.uri", is("http://localhost/f684a3c4")))
                .andExpect(jsonPath("$.target", is("http://example.com/")))
                .andExpect(jsonPath("$.sponsor", is("http://sponsor.com/")));
    }

    @Test
    public void thatShortenerFailsIfTheURLisWrong() throws Exception {
        configureSave(null);

        mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
        when(shortUrlService.save(any(String.class), any(String.class), any(String.class)))
                .thenReturn(null);

        mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
                .andExpect(status().isBadRequest());
    }

    private void configureSave(String sponsor) throws IOException{
        when(shortUrlService.save(any(), any(), any()))
                .then((Answer<ShortURL>) invocation -> new ShortURL(
                        "f684a3c4",
                        "http://example.com/",
                        URI.create("http://localhost/f684a3c4"),
                        sponsor,
                        null,
                        null,
                        0,
                        false,
                        null,
                        null,
                        null,
                        false,
                        null));
    }
}
