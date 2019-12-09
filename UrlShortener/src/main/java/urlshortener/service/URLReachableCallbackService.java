package urlshortener.service;

import java.io.IOException;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.springframework.stereotype.Service;

@Service
public class URLReachableCallbackService implements Callback {
    private ShortURLService shortURLService;
    private String hash;

    public URLReachableCallbackService() {
        this.shortURLService = null;
        this.hash = null;
    }

    public void setShortURLService(ShortURLService shortURLService) {
        this.shortURLService = shortURLService;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    /*
     *  La petición HTTP a la URL ha fallado por fallo de red, la URL no es
     *  alcanzable
     */
    @Override
    public void onFailure(Request request, IOException e) {
        shortURLService.updateReachable(hash, false);
    }

    /*
     *  No se ha producido fallo de red, la URL puede ser alcanzable
     */
    @Override
    public void onResponse(Response response) {
        if (!response.isSuccessful()) {
            shortURLService.updateReachable(hash, false);
        }
        else {
            shortURLService.updateReachable(hash, true);
        }
    }
}