package urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import urlshortener.web.UrlShortenerController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.function.*;
import java.net.URI;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;


@Service
public class ShortURLService {
    private Function<String, URI> sam = (String hash) -> { try {
    return linkTo(methodOn(UrlShortenerController.class).redirectTo(hash, null)).toUri();
} catch(IOException e) {
    throw new RuntimeException(e);
}
    };

    private final ShortURLRepository shortURLRepository;

    public ShortURLService(ShortURLRepository shortURLRepository) {
        this.shortURLRepository = shortURLRepository;
    }

    public ShortURL findByKey(String id) {
        return shortURLRepository.findByKey(id);
    }

    public ShortURL save(String url, String sponsor, String ip) throws IOException{
        ShortURL su = ShortURLBuilder.newInstance()
                .target(url)
                .uri(sam)
                .sponsor(sponsor)
                .createdNow()
                .randomOwner()
                .temporaryRedirect()
                .treatAsSafe()
                .ip(ip)
                .unknownCountry()
                .build();
        return shortURLRepository.save(su);
    }
}
