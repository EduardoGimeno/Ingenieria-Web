package urlshortener.web;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

import urlshortener.service.ShortURLService;
import urlshortener.domain.ShortURL;
import urlshortener.service.CSVService;
import urlshortener.service.ClickService;
import urlshortener.service.HTTPInfo;
import urlshortener.service.SafeBrowsingService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class UrlShortenerController {
    private final ShortURLService shortUrlService;

    private final HTTPInfo httpInfo;

    private final ClickService clickService;

    public UrlShortenerController(ShortURLService shortUrlService, ClickService clickService, HTTPInfo httpInfo) {
        this.shortUrlService = shortUrlService;
        this.clickService = clickService;
        this.httpInfo = httpInfo;
    }

    //******************************************************************************//
    //                                                                              //
    //                          REQUEST HANDLER METHODS                             //
    //                                                                              //
    //******************************************************************************//

    @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) throws RuntimeException,IOException {
        ShortURL l = shortUrlService.findByKey(id);
        String uaHeader = request.getHeader("User-Agent");
        String os = httpInfo.getOS(uaHeader);
        String brw = httpInfo.getNav(uaHeader);
        if (l != null) {
            clickService.saveClick(id, extractIP(request), os, brw);
            return createSuccessfulRedirectToResponse(l);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) throws IOException{
        UrlValidator urlValidator = new UrlValidator(new String[]{"http",
                "https"});
        if (urlValidator.isValid(url)) {
            ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
            HttpHeaders h = new HttpHeaders();
            h.setLocation(su.getUri());
            return new ResponseEntity<>(su, h, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/linkCSV", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortenerCSV(@RequestParam("path") String url,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              HttpServletRequest request) throws IOException{
        UrlValidator urlValidator = new UrlValidator(new String[]{"http",
                "https"});
        List<String[]> records= CSVService.readCSV(url);
        int size= url.indexOf(".csv");
        String newPath=url.substring(0, size)+"response.csv";
        String[] record;
        List<String[]> newRecord= new ArrayList<>();
        /*for(String[] record: records){
            if(urlValidator.isValid(record[0])){
                ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
                String[]newRecord={record[0],su.getHash()};
                CSVController.writeCSV(newPath, newRecord);
            }
            else{
                String[]newRecord={record[0],"ERROR"};
                CSVController.writeCSV(newPath, newRecord);
            }
        }
        if(records.isEmpty()){
            String[] record={"ERROR"};
                CSVController.writeCSV(newPath,record);
        }*/
        for(int i=0; i< records.size();i++){
            record= records.get(i);
            if(urlValidator.isValid(record[0]) && shortUrlService.findByKey(record[0])== null){
                ShortURL su = shortUrlService.save(url, sponsor, request.getRemoteAddr());
                String[] args={record[0],su.getHash()};
                newRecord.add(args);
            }
            else if(shortUrlService.findByKey(record[0])!= null){
                ShortURL req= shortUrlService.findByKey(record[0]);
                String[] args={record[0],req.getHash()};
                newRecord.add(args);
            }
            else{
                String[] args={record[0],"ERROR"};
                newRecord.add(args);
            }
        }
        CSVService.writeCSV(newPath, newRecord);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> check(HttpServletRequest request) {
        try {
            HttpHeaders h = new HttpHeaders();
            List<ShortURL> list = shortUrlService.listURLs();
            List<String> resp = new ArrayList<>();
            for(ShortURL url: list) {
                resp.add(url.getTarget() + ":" + url.getSafe());
            }
            return new ResponseEntity<>(resp.toString(), h, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, 
                reason="Error obtaining data of USER-AGENT header") 
    @ExceptionHandler(IOException.class)
    public void conflict() {
    }
}
