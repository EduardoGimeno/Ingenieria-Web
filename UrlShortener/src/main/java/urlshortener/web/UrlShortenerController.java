package urlshortener.web;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;
import urlshortener.utils.HTTPInfo;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URI;

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

    @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
        String uaHeader = request.getHeader("User-Agent");
        String os = new String();
        String brw = new String();
        try {
            os = httpInfo.getOS(uaHeader);
            brw = httpInfo.getNav(uaHeader);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ShortURL l = shortUrlService.findByKey(id);
        if (l != null) {
            clickService.saveClick(id, extractIP(request), os, brw);
            return createSuccessfulRedirectToResponse(l);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {
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

    private String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }
}
