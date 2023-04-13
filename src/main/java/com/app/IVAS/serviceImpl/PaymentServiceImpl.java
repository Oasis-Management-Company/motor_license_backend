package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.PaymentDto;
import com.app.IVAS.dto.PaymentLoginDto;
import com.app.IVAS.dto.PaymentRespondDto;
import com.app.IVAS.dto.ServiceTypeDto;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import com.app.IVAS.entity.ServiceType;
import com.app.IVAS.repository.InvoiceRepository;
import com.app.IVAS.repository.InvoiceServiceTypeRepository;
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
            PaymentDto paymentDto = new PaymentDto();
            List<ServiceTypeDto> serviceTypes = new ArrayList<>();

            Invoice invoice1 = invoiceRepository.findFirstByInvoiceNumberIgnoreCase(invoice);
            List<InvoiceServiceType> invoiceServiceTypes = invoiceServiceTypeRepository.findByInvoice(invoice1);
            for (InvoiceServiceType invoiceServiceType : invoiceServiceTypes) {
                ServiceTypeDto dto = new ServiceTypeDto();
                dto.setAmount(invoiceServiceType.getServiceType().getPrice());
                dto.setItemCode(invoiceServiceType.getRevenuecode());
                dto.setName(invoiceServiceType.getServiceType().getName());
                dto.setReferenceNumber(invoiceServiceType.getReference());
                dto.setDateBooked(invoiceServiceType.getInvoice().getCreatedAt());

                serviceTypes.add(dto);
            }


            paymentDto.setServiceTypeDtos(serviceTypes);
            paymentDto.setTotalamount(invoice1.getAmount());
            paymentDto.setFirstname(invoice1.getPayer().getFirstName());
            paymentDto.setLastname(invoice1.getPayer().getDisplayName());
            paymentDto.setEmail(invoice1.getPayer().getEmail());


            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(paymentDto, headersAuth);
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
