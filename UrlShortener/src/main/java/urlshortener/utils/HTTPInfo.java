package urlshortener.utils;

import java.io.IOException;

import ua_parser.Parser;
import ua_parser.Client;

public class HTTPInfo {
    /*
     * Muestra la información de la cabecera User-Agent de una petición HTTP
     */
    public String getInfo(String uaHeader) throws IOException {
        Parser uaParser = new Parser();
        Client c = uaParser.parse(uaHeader);
        String os = c.os.family;
        String nav = c.userAgent.family;
        String info = "OS: " + os + " NAV: " + nav;
        return info;
    }
}