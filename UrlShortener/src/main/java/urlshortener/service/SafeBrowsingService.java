package urlshortener.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.safebrowsing.Safebrowsing;
import com.google.api.services.safebrowsing.model.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;


@Service
public class SafeBrowsingService {
    // Api Key obtenible desde: https://console.cloud.google.com/apis/credentials
    // Se debe activar el API de Google Safe Browsing: https://console.cloud.google.com/apis/api/safebrowsing.googleapis.com/
    // TODO: ¿Usar key comun cifrada como en practica 2?
    private static final String api_key = "AIzaSyDhWGqSKhAFV2-q0Jmm9EL65s2qQE3EkrQ";
    private static final String client_Name = "iwebtp6";
    private static final String client_Version = "0.1";
    private static final Logger log = LoggerFactory
            .getLogger(ClickService.class);
    private final ShortURLRepository shortURLRepository;

    public SafeBrowsingService(ShortURLRepository shortURLRepository) {
        this.shortURLRepository = shortURLRepository;
    }

    /*
     * Ver: https://developers.google.com/safe-browsing/v4/lists
     * Muestra las combinaciones de platformType, threatEntryType y threatType disponibles
     * Se podria usar para obtener dinamicamente esa informacion y usarla en la peticion
     */
    private void getThreats() {
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            final JacksonFactory GOOGLE_JSON_FACTORY = JacksonFactory.getDefaultInstance();
            Safebrowsing.Builder safebrowsingBuilder = new Safebrowsing.Builder(httpTransport, GOOGLE_JSON_FACTORY, null);
            safebrowsingBuilder.setApplicationName(client_Name);
            Safebrowsing safebrowsing = safebrowsingBuilder.build();
            ListThreatListsResponse response = safebrowsing.threatLists().list().setKey(api_key).execute();
            System.out.println(response.toPrettyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Crea ThreatInfo con todas las posibles combinaciones existentes de ThreatType, PlatformType=ANY_PLATFORM y threatEntryType=URL
    // TODO: ¿Nos interesa threatEntryType=IP_RANGE, PlatformType=IOS|ANDROID?
    private ThreatInfo createCompleteThreatInfo(List<String> URLs) {
        ThreatInfo threatInfo = new ThreatInfo();
        // Tipo de threat que buscamos
        threatInfo.setThreatTypes(Arrays.asList("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION"));
        // Plataforma en la que queremos comprobar (WINDOWS, LINUX, CHROME,...)
        threatInfo.setPlatformTypes(Collections.singletonList("ANY_PLATFORM"));
        // Tipo de entrada que queremos comprobar, a dia de hoy puede ser URL o IP_RANGE
        threatInfo.setThreatEntryTypes(Collections.singletonList("URL"));
        // Creacion de la lista de threats con las URLs
        List<ThreatEntry> threats = new ArrayList<>();
        for (String URL : URLs) {
            ThreatEntry threat = new ThreatEntry();
            threat.setUrl(URL);
            threats.add(threat);
        }
        threatInfo.setThreatEntries(threats);
        return threatInfo;
    }

    /*
     * Funcion principal del modulo, dada una lista de URLs devuelve las que no son seguras
     * Ver: https://developers.google.com/safe-browsing/v4/lookup-api
     */
    // TODO: ¿Cambiar comportamiento para que devuelva lista de seguras por ejemplo?
    public List<String> checkURLs(List<String> URLs) throws IOException, GeneralSecurityException {
        // Crear la peticion con los datos de cliente y threats
        final FindThreatMatchesRequest request = new FindThreatMatchesRequest();
        final ClientInfo clientInfo = new ClientInfo();
        clientInfo.setClientId(client_Name);
        clientInfo.setClientVersion(client_Version);
        request.setClient(clientInfo);
        ThreatInfo threatInfo = createCompleteThreatInfo(URLs);
        request.setThreatInfo(threatInfo);

        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final JacksonFactory GOOGLE_JSON_FACTORY = JacksonFactory.getDefaultInstance();

        Safebrowsing.Builder safebrowsingBuilder = new Safebrowsing.Builder(httpTransport, GOOGLE_JSON_FACTORY, null);
        safebrowsingBuilder.setApplicationName(client_Name);
        Safebrowsing safebrowsing = safebrowsingBuilder.build();
        // Enviar peticion y obtener respuesta
        FindThreatMatchesResponse findThreatMatchesResponse = safebrowsing.threatMatches().find(request).setKey(api_key).execute();
        //Obtener y mostrar la lista de coincidencias (urls maliciosas)
        List<ThreatMatch> threatMatches = findThreatMatchesResponse.getMatches();
        List<String> maliciousURLs = new ArrayList<>();
        if (threatMatches != null && threatMatches.size() > 0) {
            // Marcar como no seguras y eliminar de la lista de seguras
            for (ThreatMatch threatMatch : threatMatches) {
                maliciousURLs.add(threatMatch.getThreat().getUrl());
                List<ShortURL> ShortURLs = shortURLRepository.findByTarget(threatMatch.getThreat().getUrl());
                for (ShortURL shorturl : ShortURLs) {
                    shortURLRepository.mark(shorturl, false);
                }
                URLs.remove(threatMatch.getThreat().getUrl());
            }
        }
        // Marcar como seguras
        for (String url : URLs) {
            List<ShortURL> ShortURLs = shortURLRepository.findByTarget(url);
            for (ShortURL shorturl : ShortURLs) {
                shortURLRepository.mark(shorturl, true);
            }
        }
        return maliciousURLs;
    }

    @Scheduled(fixedDelay = 5000)
    public void doSafeChecks() {
        List<ShortURL> list = shortURLRepository.getURLsToCheck();
        List<String> urls = new ArrayList<>();
        for(ShortURL url: list) {
            urls.add(url.getTarget());
        }
		if(!urls.isEmpty()){
			try {
				checkURLs(urls);
			}
			catch (Exception e) {
				log.error(e.toString());
			}
		}
    }
}
