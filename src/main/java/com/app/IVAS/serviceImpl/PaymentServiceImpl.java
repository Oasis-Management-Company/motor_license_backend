package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.*;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import com.app.IVAS.repository.InvoiceRepository;
import com.app.IVAS.repository.InvoiceServiceTypeRepository;
import com.app.IVAS.service.PaymentService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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

            String baseUrl = "http://41.207.248.189:8084/api/external/authenticate";
//            String baseUrl = "http://localhost:8787/api/external/authenticate";
//            String baseUrl = "http://localhost:8787/api/informal/sector/public/createasin";

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

            String url ="http://41.207.248.189:8084/api/notification/handle-assessment-ivas";
//            String url ="http://localhost:8787/api/notification/handle-assessment-ivas";

            ResponseEntity<Object> responseRC = null;
            ParentRequest paymentDto = new ParentRequest();
            List<ChildRequest> childRequests = new ArrayList<>();

            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            Invoice invoice1 = invoiceRepository.findFirstByInvoiceNumberIgnoreCase(invoice);

            List<InvoiceServiceType> invoiceServiceTypes = invoiceServiceTypeRepository.findByInvoice(invoice1);

            for (InvoiceServiceType invoiceServiceType : invoiceServiceTypes) {
                ChildRequest dto = new ChildRequest();
                dto.setAmount(invoiceServiceType.getServiceType().getPrice());
                dto.setItemCode(invoiceServiceType.getRevenuecode());
                dto.setName(invoiceServiceType.getServiceType().getName());
                dto.setReferenceNumber(invoiceServiceType.getReference());
                dto.setDateBooked(invoiceServiceType.getInvoice().getCreatedAt().format(df));

                childRequests.add(dto);
            }

            paymentDto.setTotalAmount(invoice1.getAmount());
            paymentDto.setFirstName(invoice1.getPayer().getFirstName());
            paymentDto.setLastName(invoice1.getPayer().getDisplayName());
            paymentDto.setEmail(invoice1.getPayer().getEmail());
            paymentDto.setParentDescription("Vehicle Registration Licence and General Motor Registration");

            TopParentRequest topParentRequest = new TopParentRequest();
            topParentRequest.setParentRequest(paymentDto);
            topParentRequest.setChildRequestList(childRequests);


            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

            HttpEntity entity = new HttpEntity(new Gson().toJson(topParentRequest), headersAuth);
            System.out.println(entity.getBody());
            try {
                String personResultAsJsonStr = restTemplate.postForObject(url, entity, String.class);
                restTemplate.setErrorHandler(new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse response) throws IOException {
                        System.out.println(response);
                        return false;
                    }

                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        System.out.println(response);
                        // do nothing, or something
                    }
                });

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
