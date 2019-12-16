package urlshortener.repository;

import urlshortener.domain.Click;

import java.sql.Date;
import java.util.List;

public interface ClickRepository {

    List<Click> findByHash(String hash);

    Long clicksByHash(String hash);

    Click save(Click cl);

    void update(Click cl);

    void delete(Long id);

    void deleteAll();

    Long count();

    List<Click> list(Long limit, Long offset);

    //*************** Limitar redirecciones *****************//

    Long countRedirects(Click cl, Date limit);

    //*************** Extracción información *****************//

    String getBrowserMostUsed();

    String getOsMostUsed();
 
    String getBrowserLastUsed();
 
    String getOsLastUsed();
}
