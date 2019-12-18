package urlshortener.service;

import java.io.IOException;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ua_parser.Parser;
import urlshortener.domain.ClickInfo;
import ua_parser.Client;

@Service
public class HTTPInfo {
    private final ClickService clickService;
    private final SimpMessagingTemplate template;

    public HTTPInfo(ClickService clickService, SimpMessagingTemplate template) {
        this.clickService = clickService;
        this.template = template;
    }

    /*
     * Obtiene el sistema operativo de la cabecera USER-AGENT
     */
    public String getOS(String uaHeader) throws IOException {
        Parser uaParser = new Parser();
        Client c = uaParser.parse(uaHeader);
        String os = c.os.family;
        return os;
    }

    /*
     * Obtiene el navegador de la cabecera USER-AGENT
     */
    public String getNav(String uaHeader) throws IOException {
        Parser uaParser = new Parser();
        Client c = uaParser.parse(uaHeader);
        String nav = c.userAgent.family;
        return nav;
    }

    /* 
     * Envía al cliente cada dos segundos información del sistema
     */
    @Scheduled(fixedDelay = 2000)
    public void info() {
        String browserMostUsed = new String();
        String osMostUsed = new String();
        String browserLastUsed = new String();
        String osLastUsed = new String();
        try {
            browserMostUsed = clickService.browserMostUsed();
            osMostUsed = clickService.osMostUsed();
            browserLastUsed = clickService.browserLastUsed();
            osLastUsed = clickService.osLastUsed();
            template.convertAndSend("/topic/info", new ClickInfo(osMostUsed, browserMostUsed, browserLastUsed, osLastUsed));
        } catch (Exception e) {
            template.convertAndSend("/topic/info", new ClickInfo(osMostUsed, browserMostUsed, browserLastUsed, osLastUsed));
        }
    }
}