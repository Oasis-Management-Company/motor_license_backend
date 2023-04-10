package com.app.IVAS.nimc.others;

import com.app.IVAS.Utils.OkHttp3Util;
import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.entity.UserDemographicIndividual;
import com.app.IVAS.repository.CardRepository;
import com.app.IVAS.repository.PortalUserRepository;
import com.app.IVAS.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;

@Service
public class PublicServiceImpl implements PublicService{

    @Value("${asin_verification}")
    private String asinVerification;

    private final OkHttp3Util okHttp3Util;
    private final JwtService jwtService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private PortalUserRepository portalUserRepository;

    public PublicServiceImpl(OkHttp3Util okHttp3Util, JwtService jwtService) {
        this.okHttp3Util = okHttp3Util;
        this.jwtService = jwtService;
    }


    @Override
    public UserDemographicIndividual getInformalSectioFromTax(String asin) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        ResponseEntity<UserDemographicIndividual> responseRC = null;
        UserDemographicIndividual userinfo1 = null;

        String url = asinVerification + asin;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        try {
            responseRC = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,entity, UserDemographicIndividual.class);
            userinfo1 = responseRC.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  userinfo1;
    }
}
