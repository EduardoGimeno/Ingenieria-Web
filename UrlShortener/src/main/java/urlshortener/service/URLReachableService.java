package urlshortener.service;

import java.io.IOException;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.springframework.stereotype.Service;

@Service
public class URLReachableService {
    /*
     * Comprueba si la URL proporcionada es alcanzable síncronamente
     */
    public Boolean isReachableSynchronous(String url) {
        OkHttpClient client = new OkHttpClient();
        client.setFollowRedirects(false);
        client.setRetryOnConnectionFailure(false);

        Request request = new Request.Builder().url(url).build();

        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            return false;
        }
        return response.isSuccessful();
    }

    /*
     *  Comprueba si la URL proporcionada es alcanzable asíncronamente
     */
    public void isReachableAsynchronous(String url, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        client.setFollowRedirects(false);
        client.setRetryOnConnectionFailure(false);

        Request request = new Request.Builder().url(url).build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}