package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.CardStatusConstant;
import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Enum.PaymentStatus;
import com.app.IVAS.Enum.RegType;
import com.app.IVAS.Utils.OkHttp3Util;
import com.app.IVAS.Utils.Utils;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.*;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.CardService;
import com.app.IVAS.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
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
import java.io.IOException;
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

    @Value("${insurance-username}")
    private String insuranceusername;

    @Value("${insurance-password}")
    private String insurancepassword;

    private final InvoiceRepository invoiceRepository;
    private final InvoiceServiceTypeRepository invoiceServiceTypeRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CardRepository cardRepository;
    private final CardService cardService;
    private final VehicleRepository vehicleRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final InsuranceResponserepo insuranceResponserepo;

    @Value("${payment.domain}")
    private String paymentDomain;

    private final OkHttp3Util okHttp3Util;
    private final JwtService jwtService;



    @Override
    public String sendPaymentTax(String invoice) {
        Double OTHERS_AMOUNT = 0.0;

        try{

            System.out.println(invoice);
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
            List<ParentRequest> childRequests = new ArrayList<>();
            List<ChildRequest> licence = new ArrayList<>();

            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            Invoice invoice1 = invoiceRepository.findFirstByInvoiceNumberIgnoreCase(invoice);

            List<InvoiceServiceType> invoiceServiceTypes = invoiceServiceTypeRepository.findByInvoice(invoice1);

            for (InvoiceServiceType invoiceServiceType : invoiceServiceTypes) {
                ParentRequest dto = new ParentRequest();
                if (invoiceServiceType.getServiceType().getName().contains("PLATE NUMBER VEHICLE")){
                    dto.setAmount(invoiceServiceType.getAmount());
                    dto.setName("PLATE NUMBER");
                    dto.setItemCode("AIVDS001");
                    dto.setReferenceNumber(invoiceServiceType.getReference());
                    dto.setCustReference("167371977051");
                    dto.setDescription("PLATE NUMBER");
                    dto.setTotalAmount(invoiceServiceType.getAmount());
                    dto.setFirstName(invoice1.getPayer().getFirstName());
                    dto.setLastName(invoice1.getPayer().getDisplayName());
                    dto.setEmail(invoice1.getPayer().getEmail());

                    childRequests.add(dto);

                }else if(invoiceServiceType.getServiceType().getName().contains("INSURANCE")){
                    dto.setAmount(invoiceServiceType.getAmount());
                    dto.setName("INSURANCE");
                    dto.setItemCode("AIVDS003");
                    dto.setReferenceNumber(invoiceServiceType.getReference());
                    dto.setCustReference("167371977051");
                    dto.setDescription("INSURANCE");
                    dto.setTotalAmount(invoiceServiceType.getAmount());
                    dto.setFirstName(invoice1.getPayer().getFirstName());
                    dto.setLastName(invoice1.getPayer().getDisplayName());
                    dto.setEmail(invoice1.getPayer().getEmail());

                    childRequests.add(dto);
                }else if(invoiceServiceType.getServiceType().getName().contains("SMS")){
                    dto.setAmount(invoiceServiceType.getAmount());
                    dto.setName("SMS");
                    dto.setItemCode("AIVDS004");
                    dto.setReferenceNumber(invoiceServiceType.getReference());
                    dto.setCustReference("167371977051");
                    dto.setDescription("SMS");
                    dto.setTotalAmount(invoiceServiceType.getAmount());
                    dto.setFirstName(invoice1.getPayer().getFirstName());
                    dto.setLastName(invoice1.getPayer().getDisplayName());
                    dto.setEmail(invoice1.getPayer().getEmail());

                    childRequests.add(dto);
                }else if(invoiceServiceType.getServiceType().getName().contains("ROADWORTHINESS/COMPUTERIZED VEHICLE")){
                    dto.setAmount(invoiceServiceType.getAmount());
                    dto.setName("COMPUTERIZED TEST");
                    dto.setItemCode("AIVDS005");
                    dto.setReferenceNumber(invoiceServiceType.getReference());
                    dto.setCustReference("167371977051");
                    dto.setDescription("COMPUTERIZED TEST");
                    dto.setTotalAmount(invoiceServiceType.getAmount());
                    dto.setFirstName(invoice1.getPayer().getFirstName());
                    dto.setLastName(invoice1.getPayer().getDisplayName());
                    dto.setEmail(invoice1.getPayer().getEmail());

                    childRequests.add(dto);
                }else{
                    ChildRequest childRequest = new ChildRequest();
                    childRequest.setAmount(invoiceServiceType.getAmount());
                    childRequest.setName(invoiceServiceType.getServiceType().getName());
                    childRequest.setItemCode(invoiceServiceType.getServiceType().getCode());
                    childRequest.setDescription(invoiceServiceType.getServiceType().getName());
                    licence.add(childRequest);

                    OTHERS_AMOUNT += invoiceServiceType.getAmount();
                }
            }

            paymentDto.setAmount(OTHERS_AMOUNT);
            paymentDto.setName("LICENSES");
            paymentDto.setItemCode("AIVDS001");
            paymentDto.setReferenceNumber(invoice1.getInvoiceNumber());
            paymentDto.setCustReference("167371977051");
            paymentDto.setDescription("LICENSES");
            paymentDto.setFirstName(invoice1.getPayer().getFirstName());
            paymentDto.setLastName(invoice1.getPayer().getDisplayName());
            paymentDto.setEmail(invoice1.getPayer().getEmail());
            paymentDto.setExtendedData(licence);

            childRequests.add(paymentDto);

            System.out.println(childRequests);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

            HttpEntity entity = new HttpEntity(new Gson().toJson(childRequests), headersAuth);
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

                return personResultAsJsonStr;
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
        Boolean insurance = false;

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
            if (invoiceServiceType.getServiceType().getName().contains("INSURANCE")){
                insurance = true;
            }
            invoiceServiceType.setPaymentDate(dateTime);
            invoiceServiceType.setExpiryDate(dateTime.plusYears(1).minusDays(1));
            invoiceServiceTypeRepository.save(invoiceServiceType);
        }

        try{
            cardService.updateCardByPayment(respondDto.getCustReference(), Double.valueOf(respondDto.getAmount()), dateTime);
            if (insurance){
                sendInsuranceToVendor(invoice.getVehicle().getPlateNumber().getPlateNumber(), invoice.getInvoiceNumber());
            }
        }catch (Exception e){
            System.out.println(e);
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

    @Override
    public InsuranceResponse sendInsuranceToVendor(String plate, String invoiceNumber) {
        System.out.println(plate);
        try{

            String baseUrl = "https://ieiplcng.azurewebsites.net/api/Auth/Login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");
            headers.add("Content-Type", "application/json");
            PaymentLoginDto paymentLoginDto = new PaymentLoginDto();

            paymentLoginDto.setUsername(insuranceusername);
            paymentLoginDto.setPassword(insurancepassword);

            String result = restTemplate.postForObject(baseUrl, paymentLoginDto, String.class);

            Gson gson = new Gson();
            PaymentRespondDto responseToken = gson.fromJson(result, PaymentRespondDto.class);


            MultiValueMap<String, String> headersAuth = new LinkedMultiValueMap<String, String>();
            Map map = new HashMap<String, String>();
            map.put("Content-Type", "application/json");
            map.put("Authorization", "Bearer "+responseToken.getToken());
            headersAuth.setAll(map);

            String url ="https://ieiplcng.azurewebsites.net/api/Insurance/BuyInsurance";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            PlateNumber plateNumber = plateNumberRepository.findFirstByPlateNumberIgnoreCase(plate);
            Vehicle vehicle = vehicleRepository.findFirstByPlateNumber(plateNumber);
            Invoice invoice = invoiceRepository.findFirstByInvoiceNumberIgnoreCase(invoiceNumber);

            String PhoneNumber = Utils.removeFirstChar(vehicle.getPortalUser().getPhoneNumber());
            InsuranceDto dto = new InsuranceDto();
            dto.setAdddress(vehicle.getPortalUser().getAddress());
            dto.setChassisNumber(vehicle.getChasisNumber());
            dto.setEngineNumber(vehicle.getEngineNumber());
            dto.setEngine_capacity(vehicle.getCapacity());
            dto.setEmailAddress(vehicle.getPortalUser().getEmail());
            dto.setRegistration_center("AWKA");
            dto.setRegistrationNumber(vehicle.getPlateNumber().getPlateNumber());
            dto.setRegistration_date(vehicle.getCreatedAt().format(formatter));
            dto.setInsuranceType("MCO");
            dto.setCategory(vehicle.getLoad());
            dto.setFullname(vehicle.getPortalUser().getDisplayName());
            dto.setMobileNumber("234"+PhoneNumber);
            dto.setGender("Male");
            dto.setOccupation("NONE");
            dto.setType(vehicle.getPlateNumber().getType().getName());
            dto.setYearOfMake(vehicle.getYear());
            dto.setVehicleMake(vehicle.getVehicleModel().getVehicleMake().getName());
            dto.setVehicleModel(vehicle.getVehicleModel().getName());
            dto.setVehicleColour(vehicle.getColor());
            dto.setState("Anambra");
            dto.setPlate(vehicle.getPlateNumber().getPlateNumber());


            System.out.println(dto);

            HttpEntity entity = new HttpEntity(new Gson().toJson(dto), headersAuth);
            try {
                InsuranceResponse personResultAsJsonStr = restTemplate.postForObject(url, entity, InsuranceResponse.class);
                personResultAsJsonStr.setVehicle(vehicle);
                personResultAsJsonStr.setInvoice(invoice);

                insuranceResponserepo.save(personResultAsJsonStr);

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

                System.out.println("Here is the response:::" + personResultAsJsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }
}
