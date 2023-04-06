package com.app.IVAS.serviceImpl;

import com.app.IVAS.configuration.AppConfigurationProperties;
import com.app.IVAS.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final AppConfigurationProperties appConfigurationProperties;

    @Override
    public void sendSms(String recipient, String message) throws URISyntaxException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.setBasicAuth(appConfigurationProperties.getVansoUsername(),appConfigurationProperties.getVansoPassword());
        HttpEntity requestEntity = new HttpEntity<>(headers);

        URIBuilder uriBuilder = new URIBuilder(appConfigurationProperties.getVansoUrl());
        if (recipient.startsWith("234")){
            uriBuilder.addParameter("dest", "+" + recipient);
        } else if (recipient.startsWith("0")){
            String phoneNumber = recipient.replaceFirst("0", "+234");
            uriBuilder.addParameter("dest", phoneNumber);
        } else {
            uriBuilder.addParameter("dest", recipient);
        }
        uriBuilder.addParameter("src", appConfigurationProperties.getVansoSender());
        uriBuilder.addParameter("text", message);


        ResponseEntity<Map> response = restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, requestEntity, Map.class);

        log.info(Objects.requireNonNull(response.getBody()).toString());

    }
}


