package com.smsrz.url_shortener.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

public class UrlExistenceValidator {
    private static final Logger log = LoggerFactory.getLogger(UrlExistenceValidator.class);
    public static boolean isUrlExists(String urlString){
        try{
            log.debug("Checking if url exists {} ",urlString);
            URL url = new URI(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            return responseCode>=200 && responseCode<400;     //2xx and 3xx are valid
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
