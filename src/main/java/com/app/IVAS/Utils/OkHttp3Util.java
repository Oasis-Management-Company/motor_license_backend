package com.app.IVAS.Utils;


import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class OkHttp3Util {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client;

    public OkHttp3Util() {
        client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .readTimeout(1200, TimeUnit.SECONDS)
                .build();
        ;
    }

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println("hellloooooooooooo " + response.body());
            assert response.body() != null;
            return response.body().string();
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public String getWithInterSwitch(String url,
                                      String authToken,
                                      String merchantCode,
                                      String transactionReference,
                                      Long amount) throws IOException {
        String fullUrl = url + "?merchantcode=" + merchantCode + "&transactionreference=" + transactionReference + "&amount=" + amount + "/";
        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Authorization", "Bearer " + authToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .get().build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
    }

    public String post(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .get().build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
    }


    public String getWithAuthHeaderFor(String url, String baseUrl, String authGenKey) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + authGenKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .get().build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
    }


    public String getInterSwitchPaymentStatus(String url,
                                              String merchantCode,
                                              String transactionReference,
                                              Long amount) throws IOException {
        String fullUrl = url + "?merchantcode=" + merchantCode + "&transactionreference=" + transactionReference + "&amount=" + amount * 100;

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .get().build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "*/*")
                .get().build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
    }
}
