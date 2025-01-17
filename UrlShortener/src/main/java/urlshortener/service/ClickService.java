package urlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import urlshortener.domain.Click;
import urlshortener.repository.ClickRepository;

import java.sql.Date;

@Service
public class ClickService {

    private static final Logger log = LoggerFactory
            .getLogger(ClickService.class);

    private final ClickRepository clickRepository;

    public ClickService(ClickRepository clickRepository) {
        this.clickRepository = clickRepository;
    }

    public void saveClick(String hash, String ip, String os, String brw) {
        //*************** Extracción información *****************//
        Click cl = ClickBuilder.newInstance().hash(hash).createdNow().ip(ip).browser(brw).platform(os).build();
        cl = clickRepository.save(cl);
        log.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
    }

    //*************** Limitar redirecciones *****************//

    public Long countRedirects(String hash, Date limit) {
        Click cl = ClickBuilder.newInstance()
                .hash(hash)
                .build();
        return clickRepository.countRedirects(cl, limit);
    }

    //*************** Extracción información *****************//

    public String browserMostUsed() {
        return clickRepository.getBrowserMostUsed();
    }

    public String osMostUsed() {
        return clickRepository.getOsMostUsed();
    }

    public String browserLastUsed() {
        return clickRepository.getBrowserLastUsed();
    }

    public String osLastUsed() {
        return clickRepository.getOsLastUsed();
    }
	
}
