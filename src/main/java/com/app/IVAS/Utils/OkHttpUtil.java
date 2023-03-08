package com.app.IVAS.Utils;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OkHttpUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType PLAIN = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response != null) {

                if (response.body() != null) {
                    return response.body().string();
                }
            }

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Response postWithASingleHeader(String url, String json, String headerKey, String headerValue) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader(headerKey, headerValue)
                .post(body)
                .build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Response postWithMultipleHeader(String url, String json, String headerKey, String headerValue, String headerTwoKey, String headerTwoValue) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader(headerKey, headerValue)
                .addHeader(headerTwoKey, headerTwoValue)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response != null) {
                return response;
            }

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



    public Response postForAfricaIsTalking(String url,
                                           RequestBody body,
                                           String apiKey) throws IOException {

        // RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("apiKey", apiKey)
                .post(body)
                .build();
        Response response = null;
        response = client.newCall(request).execute();
        return response;
    }

    public String postWithPlain(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response != null) {

                if (response.body() != null) {
                    return response.body().string();
                }
            }

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public JSONObject getWithJsonResponse(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        String resStr = response.body().string();
        JSONObject json = null;
        try {
            json = new JSONObject(resStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject getWithJsonResponseWithAuthorizationHeader(String url, String header) throws IOException {
        Request request = new Request.Builder()
                .url(url).addHeader("Authorization", header)
                .build();

        Response response = client.newCall(request).execute();
        String resStr = response.body().string();
        JSONObject json = null;
        try {
            json = new JSONObject(resStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject getWithJsonResponseWithMultipleHeader(String url, String header, String headerTwo) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", header)
                .addHeader("Dropbox-API-Select-Admin", headerTwo)
                .build();

        Response response = client.newCall(request).execute();
        String resStr = response.body().string();
        JSONObject json = null;
        try {
            json = new JSONObject(resStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public Response getWithJsonResponseWithMultipleHeader(String url, String headerKey, String headerValue, String headerTwoKey, String headerTwoValue) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader(headerKey, headerValue)
                .addHeader(headerTwoKey, headerTwoValue)
                .build();

        return client.newCall(request).execute();
        // return response;
    }

}
