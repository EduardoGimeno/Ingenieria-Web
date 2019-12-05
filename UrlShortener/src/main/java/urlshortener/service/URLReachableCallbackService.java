package urlshortener.service;

import java.io.IOException;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.springframework.stereotype.Service;

@Service
public class URLReachableCallbackService implements Callback {
    private final ShortURLService shortURLService;

    public URLReachableCallbackService(ShortURLService shortURLService) {
        this.shortURLService = shortURLService;
    }

    /*
     *  La petición HTTP a la URL ha fallado por fallo de red, la URL no es
     *  alcanzable
     */
    @Override
    public void onFailure(Request request, IOException e) {
        
    }

    /*
     *  No se ha producido fallo de red, la URL puede ser alcanzable
     */
    @Override
    public void onResponse(Response response) {

    }
}