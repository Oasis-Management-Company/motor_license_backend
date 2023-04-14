package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.*;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import com.app.IVAS.entity.ServiceType;
import com.app.IVAS.repository.InvoiceRepository;
import com.app.IVAS.repository.InvoiceServiceTypeRepository;
import com.app.IVAS.response.JsonResponse;
import com.app.IVAS.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {


    @Value("${payment-username}")
    private String username;

    @Value("${payment-password}")
    private String password;

    private final InvoiceRepository invoiceRepository;
    private final InvoiceServiceTypeRepository invoiceServiceTypeRepository;

    @Override
    public String sendPaymentTax(String invoice) {
        System.out.println("Steepped into this");
        try{

//            String baseUrl = "http://41.207.248.189:8084/api/external/authenticate";
            String baseUrl = "http://localhost:8787/api/external/authenticate";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");
            headers.add("Content-Type", "application/json");
            PaymentLoginDto paymentLoginDto = new PaymentLoginDto();

            paymentLoginDto.setUsername(username);
            paymentLoginDto.setPassword(password);

            String result = restTemplate.postForObject(baseUrl, paymentLoginDto, String.class);


            Gson gson = new Gson();
            PaymentRespondDto responseToken = gson.fromJson(result, PaymentRespondDto.class);

            MultiValueMap<String, String> headersAuth = new LinkedMultiValueMap<String, String>();
            Map map = new HashMap<String, String>();
            map.put("Content-Type", "application/json");
            map.put("Authorization", "Bearer "+responseToken.getToken());
            headersAuth.setAll(map);

//            String url ="http://41.207.248.189:8084/api/notification/amvas/handle-assessment-multiple";
            String url ="http://localhost:8787/api/informal/sector/public/createasin";

            ResponseEntity<Object> responseRC = null;
            PaymentDto paymentDto = new PaymentDto();
            List<ExternalPaymentDto> serviceTypes = new ArrayList<>();

            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy");
            Invoice invoice1 = invoiceRepository.findFirstByInvoiceNumberIgnoreCase(invoice);
            List<InvoiceServiceType> invoiceServiceTypes = invoiceServiceTypeRepository.findByInvoice(invoice1);
            for (InvoiceServiceType invoiceServiceType : invoiceServiceTypes) {
                ExternalPaymentDto dto = new ExternalPaymentDto();
                dto.setAmount(invoiceServiceType.getServiceType().getPrice());
                dto.setItemCode(invoiceServiceType.getRevenuecode());
                dto.setName(invoiceServiceType.getServiceType().getName());
                dto.setReferenceNumber(invoiceServiceType.getReference());
                dto.setDateBooked(invoiceServiceType.getInvoice().getCreatedAt().format(df));

                serviceTypes.add(dto);
            }

            paymentDto.setServiceTypeDtos(serviceTypes);
            paymentDto.setTotalamount(invoice1.getAmount());
            paymentDto.setFirstname(invoice1.getPayer().getFirstName());
            paymentDto.setLastname(invoice1.getPayer().getDisplayName());
            paymentDto.setEmail(invoice1.getPayer().getEmail());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            HttpEntity entity = new HttpEntity(new Gson().toJson(paymentDto), headersAuth);
            try {
                String personResultAsJsonStr = restTemplate.postForObject(builder.toUriString(), entity, String.class);
//                responseRC = restTemplate.exchange(builder.toUriString(),HttpMethod.POST,entity, Object.class);
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
