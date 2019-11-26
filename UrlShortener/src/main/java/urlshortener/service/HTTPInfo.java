package urlshortener.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import ua_parser.Parser;
import ua_parser.Client;

@Service
public class HTTPInfo {
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
}