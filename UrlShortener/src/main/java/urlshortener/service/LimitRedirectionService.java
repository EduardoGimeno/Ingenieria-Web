package urlshortener.service;

import java.sql.Date;

import org.springframework.stereotype.Service;

@Service
public class LimitRedirectionService {
    private final ClickService clickService;
    private Long limitTime;
    private Long maxRedirects;

    public LimitRedirectionService(ClickService clickService) {
        this.clickService = clickService;
        this.limitTime = new Long(600000);
        this.maxRedirects = new Long(5);
    }

    public void setLimitTime(Long limitTime) {
        this.limitTime = limitTime;
    }

    public void setMaxRedirects(Long maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

    /*
     *  Comprueba si se ha superado el número de redirecciones en el periodo
     *  establecido
     */
    public Boolean limitReached(String hash) {
        Date currentDate = new Date(System.currentTimeMillis());
        Long currentTime = currentDate.getTime();
        currentTime = currentTime - limitTime;
        Date limit = new Date(currentTime);
        Long redirects = clickService.countRedirects(hash, limit);
        return redirects.longValue() == maxRedirects.longValue();
    }
}