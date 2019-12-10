package urlshortener.service;

import java.util.ArrayList;
import java.util.List;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;

@Service
public class URLReachableService {
    private final ShortURLService shortURLService;

    private static final Logger log = LoggerFactory
            .getLogger(URLReachableService.class);

    private final ShortURLRepository shortURLRepository;

    public URLReachableService(ShortURLService shortUrlService, ShortURLRepository shortURLRepository) {
        this.shortURLService = shortUrlService;
        this.shortURLRepository = shortURLRepository;
    } 

    /*
     *  Comprueba si la URL proporcionada es alcanzable asíncronamente
     */
    public void isReachableAsynchronous(String url, String hash) {
        OkHttpClient client = new OkHttpClient();
        client.setFollowRedirects(false);
        client.setRetryOnConnectionFailure(false);


        URLReachableCallback callback = new URLReachableCallback(shortURLService, hash);

        Request request = new Request.Builder().url(url).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    /*
     *  Proceso que comprueba cada 5 segundos la alcanzabilidad de las 10 URLs
     *  que hace más tiempo que no se comprueban
     */
    @Scheduled(fixedDelay = 5000)
    public void doReachableChecks() {
        List<ShortURL> list = shortURLRepository.getURLsToCheckReachability();
        List<String> urls = new ArrayList<>();
        for(ShortURL url: list) {
            if(!urls.isEmpty()){
                try {
                    isReachableAsynchronous(url.getTarget(), url.getHash());
                }
                catch (Exception e) {
                    log.error(e.toString());
                }
            }
        }
    }
}