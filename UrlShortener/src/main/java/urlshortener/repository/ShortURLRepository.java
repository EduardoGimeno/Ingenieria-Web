package urlshortener.repository;

import urlshortener.domain.ShortURL;

import java.sql.Date;
import java.util.List;

public interface ShortURLRepository {

    ShortURL findByKey(String id);

    List<ShortURL> findByTarget(String target);

    ShortURL save(ShortURL su);

    ShortURL mark(ShortURL urlSafe, boolean safeness);

    void update(ShortURL su);

    void delete(String id);

    Long count();

    List<ShortURL> list(Long limit, Long offset);

    //*************** Safe browsing *****************//
	
	boolean isSafe(String target);

    List<ShortURL> getURLsToCheck();

    //*************** Alcanzabilidad *****************//
    
    void updateReachable(ShortURL su);

    List<ShortURL> getURLsToCheckReachability();
}
