package fr.cnes.sonar.report.providers;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/**
 * Provides issue items
 * @author begarco
 */
public class RequestManager {

    private static RequestManager ourInstance = new RequestManager();

    public static RequestManager getInstance() {
        return ourInstance;
    }

    private RequestManager() {
    }

    /**
     * Execute a get http request
     * @param url url to request
     * @return response as string
     * @throws IOException error on response
     */
    public String get(String url) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("content-type", "application/json");
        HttpResponse result = httpClient.execute(request);

        return EntityUtils.toString(result.getEntity(), "UTF-8");
    }

    /**
     * Execute a get http request
     * @param url url to request
     * @param data list of pairs containing data to post
     * @return response as string
     * @throws IOException error on response
     */
    public String post(String url, List<NameValuePair> data) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        request.addHeader("content-type", "application/json");
        request.setEntity(new UrlEncodedFormEntity(data));

        HttpResponse result = httpClient.execute(request);

        return EntityUtils.toString(result.getEntity(), "UTF-8");
    }
}
