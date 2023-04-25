package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.CardStatusConstant;
import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.Utils.OkHttp3Util;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import com.app.IVAS.entity.PaymentHistory;
import com.app.IVAS.repository.CardRepository;
import com.app.IVAS.repository.InvoiceRepository;
import com.app.IVAS.repository.InvoiceServiceTypeRepository;
import com.app.IVAS.repository.PaymentHistoryRepository;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.time.LocalDateTime;
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
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CardRepository cardRepository;

    @Value("${payment.domain}")
    private String paymentDomain;

    private final OkHttp3Util okHttp3Util;
    private final JwtService jwtService;



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

            Gson gson = new Gson();
            PaymentRespondDto responseToken = gson.fromJson(result, PaymentRespondDto.class);

            MultiValueMap<String, String> headersAuth = new LinkedMultiValueMap<String, String>();
            Map map = new HashMap<String, String>();
            map.put("Content-Type", "application/json");
            map.put("Authorization", "Bearer "+responseToken.getToken());
            headersAuth.setAll(map);

            String url ="http://41.207.248.189:8084/api/notification/handle-assessment-ivas";

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
            paymentDto.setTransactionId(invoice1.getInvoiceNumber());
            paymentDto.setCustReference("167371977051");

            TopParentRequest topParentRequest = new TopParentRequest();
            topParentRequest.setParentRequest(paymentDto);
            topParentRequest.setChildRequest(childRequests);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

            HttpEntity entity = new HttpEntity(new Gson().toJson(topParentRequest), headersAuth);
            try {
                String personResultAsJsonStr = restTemplate.postForObject(url, entity, String.class);
                restTemplate.setErrorHandler(new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse response) throws IOException {
                        return false;
                    }

                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        // do nothing, or something
                    }
                });

                invoice1.setSentToTax(true);
                invoiceRepository.save(invoice1);
                System.out.println("Here is the response:::" + personResultAsJsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }catch(Exception e){
            return null;
        }
    }

    @Override
    public List<PaymentHistoryDto> verifyPayment(String invoice) throws IOException {

        List<PaymentHistoryDto> historyList = new ArrayList<>();

        String result = this.okHttp3Util.get(paymentDomain+invoice);
        ObjectMapper objectMappe = new ObjectMapper();
        InformalResponsePojo nins= objectMappe.readValue(result, InformalResponsePojo.class);
        historyList.addAll(nins.getData());


        return historyList;
    }

    @Override
    public AssessmentResponse PaymentReturn(PaymentResponse respondDto) {
        AssessmentResponse assessmentResponse = new AssessmentResponse();
        PaymentHistory paymentHistory = new PaymentHistory();

        PaymentHistory history = paymentHistoryRepository.findFirstByPaymentReference(respondDto.getPaymentReference());

        if (history != null){
            assessmentResponse.setStatusCode("003");
            assessmentResponse.setMessage("Duplicate Transaction..");
            assessmentResponse.setPaymentReference(respondDto.getPaymentReference());
            return assessmentResponse;
        }



        Invoice invoice = invoiceRepository.findFirstByInvoiceNumberIgnoreCase(respondDto.getCustReference());
        if (invoice == null ){
            assessmentResponse.setStatusCode("002");
            assessmentResponse.setMessage("Not Sucessful");
            assessmentResponse.setPaymentReference(respondDto.getPaymentReference());
            return assessmentResponse;
        }


        List<Card> card = cardRepository.findAllByInvoiceInvoiceNumberIgnoreCase(invoice.getInvoiceNumber()).get();
        List<InvoiceServiceType> invoiceServiceTypes = invoiceServiceTypeRepository.findByInvoice(invoice);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(respondDto.getPaymentDate(), formatter);

        paymentHistory.setAmount(respondDto.getAmount());
        paymentHistory.setPaymentDate(dateTime.format(formatter));
        paymentHistory.setPaymentReference(respondDto.getPaymentReference());
        paymentHistory.setCustomerName(respondDto.getCustomerName());
        paymentHistory.setReceiptNo(respondDto.getReceiptNo());
        paymentHistory.setCustomerPhoneNumber(respondDto.getCustomerPhoneNumber());
        paymentHistory.setBankName(respondDto.getBankName());
        paymentHistory.setCollectionsAccount(respondDto.getCollectionsAccount());
        paymentHistory.setItemName(respondDto.getItemName());
        paymentHistory.setLog(respondDto.toString());

        paymentHistoryRepository.save(paymentHistory);


        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setPaymentDate(dateTime);
        invoice.setPaymentRef(respondDto.getPaymentReference());
        invoiceRepository.save(invoice);

        List<InvoiceServiceType> serviceType = invoiceServiceTypeRepository.findByInvoice(invoice);

        for (InvoiceServiceType invoiceServiceType : serviceType) {
            invoiceServiceType.setPaymentDate(dateTime);
            invoiceServiceType.setExpiryDate(dateTime.plusYears(1).minusDays(1));

            invoiceServiceTypeRepository.save(invoiceServiceType);
        }

        for (Card card1 : card) {
            card1.setCardStatus(CardStatusConstant.NOT_PRINTED);
            card1.setStatus(GenericStatusConstant.ACTIVE);
            if (card1.getVehicle().getPlateNumber().getType().getName().contains("Commercial")){
                card1.setExpiryDate(dateTime.plusMonths(6).minusDays(1));
            }else{
                card1.setExpiryDate(dateTime.plusYears(1).minusDays(1));
            }
            cardRepository.save(card1);
        }
        for (InvoiceServiceType invoiceServiceType : invoiceServiceTypes) {
            invoiceServiceType.setPaymentDate(dateTime);

            invoiceServiceTypeRepository.save(invoiceServiceType);
        }

        assessmentResponse.setStatusCode("000");
        assessmentResponse.setMessage("Success");
        assessmentResponse.setPaymentReference(respondDto.getPaymentReference());

        return assessmentResponse;
    }
}
