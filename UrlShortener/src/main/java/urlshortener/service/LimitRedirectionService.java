package urlshortener.service;

import java.sql.Date;

import org.springframework.stereotype.Service;

@Service
public class LimitRedirectionService {
    private final ClickService clickService;
    private final Long limitTime = new Long(600000);

    public LimitRedirectionService(ClickService clickService) {
        this.clickService = clickService;
    }

    /*
     *  Comprueba si se ha superado el número de redirecciones en el periodo
     *  establecido
     */
    public Boolean limitReached(String hash) {
        Date currentDate = new Date(System.currentTimeMillis());
        Long time = currentDate.getTime();
        time = time - limitTime;
        Date limit = new Date(time);
        Long redirects = clickService.countRedirects(hash, limit);
        return redirects == 5;
    }
}