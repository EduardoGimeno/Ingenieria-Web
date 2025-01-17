package urlshortener.web;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import urlshortener.service.ShortURLService;
import urlshortener.service.URLReachableService;
import urlshortener.domain.ShortURL;
import urlshortener.service.CSVService;
import urlshortener.service.ClickService;
import urlshortener.service.HTTPInfo;
import urlshortener.service.LimitRedirectionService;
import urlshortener.service.SafeBrowsingService;

import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;


@RestController
public class UrlShortenerController {
    private final ShortURLService shortUrlService;
    private final ClickService clickService;
    private final HTTPInfo httpInfo;
    private final URLReachableService urlReachableService;
    private final SafeBrowsingService safeBrowsingService;
    private final LimitRedirectionService limitRedirectionService;
    private final Long limitTime = new Long(600000);
    private final Long limitRedirects = new Long(5);

    public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService,
                                  HTTPInfo httpInfo, URLReachableService urlReachableService, SafeBrowsingService safeBrowsingService,
                                  LimitRedirectionService limitRedirectionService) {
        this.shortUrlService = shortUrlService;
        this.clickService = clickService;
        // ************************************************************************//
        this.httpInfo = httpInfo;
        this.urlReachableService = urlReachableService;
        this.safeBrowsingService = safeBrowsingService;
        this.limitRedirectionService = limitRedirectionService;
        this.limitRedirectionService.setLimitTime(limitTime);
        this.limitRedirectionService.setMaxRedirects(limitRedirects);
    }

    public Long getLimitRedirects() {
        return this.limitRedirects;
    }

    //******************************************************************************//
    //                                                                              //
    //                              REQUEST HANDLER METHODS                         //
    //                                                                              //
    //******************************************************************************//

    //******************************************************************************//
    //                                                                              //
    //                                  NORMAL METHODS                              //
    //                                                                              //
    //******************************************************************************//

    @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request)
            throws RuntimeException, IOException {
        ShortURL l = shortUrlService.findByKey(id);

        // ********************* Extracción información ***************************//
        String uaHeader = request.getHeader("User-Agent");
        String os = httpInfo.getOS(uaHeader);
        String brw = httpInfo.getNav(uaHeader);

        if (l == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // ********************* Limitar redirecciones ***************************//
        if (!limitRedirectionService.limitReached(l.getHash())) {
            clickService.saveClick(id, extractIP(request), os, brw);
            // ********************* Alcanzabilidad y Google Safe Browsing***************************//
            if (l.getReachable() && l.getSafe()) {
                return createSuccessfulRedirectToResponse(l);
            }
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }

    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                              @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request)
            throws IOException {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
        if (urlValidator.isValid(url)) {
            ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
            // ********************* Alcanzabilidad ***************************//
            String hash = su.getHash();
            urlReachableService.isReachableAsynchronous(url, hash);
            //********************* Google Safe Browsing ***************************//
            safeBrowsingService.asyncCheck(Collections.singletonList(su.getTarget()));
            HttpHeaders h = new HttpHeaders();
            h.setLocation(su.getUri());
            return new ResponseEntity<>(su, h, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //******************************************************************************//
    //                                                                              //
    //                               CSV METHODS                                    //
    //                                                                              //
    //******************************************************************************//

    @RequestMapping(value = "/linkCSV", method = RequestMethod.POST)
    public ResponseEntity<String> shortenerCSV(@RequestParam("path") MultipartFile url,
                                               @RequestParam(value = "sponsor", required = false) String sponsor,
                                               HttpServletRequest request) throws IOException {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http",
                "https"});
        String[] url_list = CSVService.getURLs(url);
        System.out.println(url_list.length);
        HashMap<String, String> shorted_urls = new HashMap<>();
        for (String long_url : url_list) {
            if (urlValidator.isValid(long_url)) {
                ShortURL su = shortUrlService.save(long_url, sponsor, request.getRemoteAddr());
                //********************* Alcanzabilidad ***************************//
                String hash = su.getHash();
                urlReachableService.isReachableAsynchronous(long_url, hash);
                //********************* Google Safe Browsing ***************************//
                safeBrowsingService.asyncCheck(Collections.singletonList(su.getTarget()));
                shorted_urls.put(long_url, su.getHash());
            } else {
                shorted_urls.put(long_url, "ERROR");
            }
        }
        HttpHeaders h = new HttpHeaders();
        OutputStream arg0 = new ByteArrayOutputStream();
        OutputStreamWriter w = new OutputStreamWriter(arg0);
        String result = CSVService.write(w, shorted_urls);
        System.out.println(result);
        h.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        h.add(HttpHeaders.CONTENT_LENGTH, Integer.toString(result.length()));
        h.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=response.csv");
        return new ResponseEntity<>(result, h, HttpStatus.CREATED);
    }

    //******************************************************************************//
    //                                                                              //
    //                                LIST METHODS                                  //
    //                                                                              //
    //******************************************************************************//

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> check(HttpServletRequest request) {
        try {
            HttpHeaders h = new HttpHeaders();
            List<ShortURL> list = shortUrlService.listURLs();
            List<String> resp = new ArrayList<>();
            for (ShortURL url : list) {
                resp.add(url.getTarget() + ":" + url.getSafe() + ":" + url.getReachable() + ":" + url.getHash());
            }
            return new ResponseEntity<>(resp.toString(), h, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //******************************************************************************//
    //                                                                              //
    //                               PRIVATE METHODS                                //
    //                                                                              //
    //******************************************************************************//

    private String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }

    //******************************************************************************//
    //                                                                              //
    //                          EXCEPTION HANDLING METHODS                          //
    //                                                                              //
    //******************************************************************************//

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR,
            reason = "Error obtaining data of USER-AGENT header")
    @ExceptionHandler(IOException.class)
    public void conflict() {
    }
}
