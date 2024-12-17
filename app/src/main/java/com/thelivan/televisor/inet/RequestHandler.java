package com.thelivan.televisor.inet;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class RequestHandler {

    public String doRequest(String request) throws IOException {
        HttpURLConnection connection = getHttpURLConnection(request);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String request) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(request).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.setUseCaches(false);
        connection.connect();
        return connection;
    }

}
