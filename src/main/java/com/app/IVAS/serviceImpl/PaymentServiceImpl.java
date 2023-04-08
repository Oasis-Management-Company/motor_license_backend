package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.PaymentLoginDto;
import com.app.IVAS.dto.PaymentRespondDto;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.response.JsonResponse;
import com.app.IVAS.service.PaymentService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {


    @Value("${payment-username}")
    private String username;

    @Value("${payment-password}")
    private String password;

    @Override
    public String sendPaymentTax(String invoice) {
        try{

            String baseUrl = "http://41.207.248.189:8084/api/external/authenticate";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");
            headers.add("Content-Type", "application/json");
            PaymentLoginDto paymentLoginDto = new PaymentLoginDto();

            paymentLoginDto.setUsername(username);
            paymentLoginDto.setPassword(password);

            String result = restTemplate.postForObject(baseUrl, paymentLoginDto, String.class);
            System.out.println(result);


            Gson gson = new Gson();
            PaymentRespondDto responseToken = gson.fromJson(result, PaymentRespondDto.class);

            System.out.println("here is the response: " + responseToken.getToken());

            MultiValueMap<String, String> headersAuth = new LinkedMultiValueMap<String, String>();
            Map map = new HashMap<String, String>();
            map.put("Content-Type", "application/json");
            map.put("Authorization", "Bearer "+responseToken.getToken());
            headersAuth.setAll(map);

            String url ="http://41.207.248.189:8084/api/notification/amvas/handle-assessment-multiple";

            ResponseEntity<Object> responseRC = null;
            Invoice invoice1 = new Invoice();
            invoice1.setAmount(2399.0);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(invoice1, headersAuth);
            System.out.println(entity);
            try {

                responseRC = restTemplate.exchange(builder.toUriString(),
                        HttpMethod.POST,
                        entity, Object.class);
                System.out.println("Here is the response:::" + responseRC.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;


        }catch(Exception e){
            return null;
        }
    }
}
