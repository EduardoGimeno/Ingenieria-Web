package urlshortener.utils;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.safebrowsing.Safebrowsing;
import com.google.api.services.safebrowsing.model.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SafeBrowsing {
    // Api Key obtenible desde: https://console.cloud.google.com/apis/credentials
    // Se debe activar el API de Google Safe Browsing: https://console.cloud.google.com/apis/api/safebrowsing.googleapis.com/
    // TODO: ¿Usar key comun cifrada como en practica 2?
    private static final String api_key = "";
    private static final String base_URL = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=xxx";
    private static final String threats_URL = "https://safebrowsing.googleapis.com/v4/threatLists?key=xxx";
    private static final String client_Name = "iwebtp6";
    private static final String client_Version = "0.1";

    /*
     * Ver: https://developers.google.com/safe-browsing/v4/lists
     * Muestra las combinaciones de platformType, threatEntryType y threatType disponibles
     * Se podria usar para obtener dinamicamente esa informacion y usarla en la peticion
     */
    private static void getThreats() {
        try {
            final URL url = new URL(threats_URL);
            final HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
            connect.setRequestProperty("Content-Type", "application/json");
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
    private static ThreatInfo createCompleteThreatInfo(List<String> URLs) {
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
     * Funcion principal, dada una lista de URLs muestra las que no son seguras
     * Ver: https://developers.google.com/safe-browsing/v4/lookup-api
     */
    // TODO: ¿Cambiar comportamiento para que devuelva lista de seguras|no seguras por ejemplo?
    public static void checkURLs(List<String> URLs) {
        try {
            final URL url = new URL(base_URL);
            // Get a URLConnection object, to write to POST method
            final HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setRequestProperty("Content-Type", "application/json");
            // Specify connection settings
            connect.setDoInput(true);
            connect.setDoOutput(true);
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
            if (threatMatches != null && threatMatches.size() > 0) {
                for (ThreatMatch threatMatch : threatMatches) {
                    System.out.println(threatMatch.getThreat().getUrl());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
