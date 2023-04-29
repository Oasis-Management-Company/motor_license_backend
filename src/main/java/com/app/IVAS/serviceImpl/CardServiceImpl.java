package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.*;
import com.app.IVAS.Utils.HtmlToPdfCreator;
import com.app.IVAS.Utils.PDFRenderToMultiplePages;
import com.app.IVAS.configuration.AppConfigurationProperties;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.security.QrCodeServices;
import com.app.IVAS.service.ActivityLogService;
import com.app.IVAS.service.CardService;
import com.itextpdf.text.DocumentException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final InvoiceRepository invoiceRepository;
    private final PortalUserRepository portalUserRepository;
    private final LgaRepository lgaRepository;
    private final AreaRepository areaRepository;
    private final HtmlToPdfCreator htmlToPdfCreator;
    private final PDFRenderToMultiplePages pdfRenderToMultiplePages;
    private final AppConfigurationProperties appConfigurationProperties;
    private final VehicleRepository vehicleRepository;
    private final JwtService jwtService;
    private final CardRepository cardRepository;
    private final ActivityLogService activityLogService;

    @Value("${asin_verify}")
    private String asin_verify;
    @Autowired
    QrCodeServices qrCodeServices;
    @Autowired
    InvoiceServiceTypeRepository invoiceServiceTypeRepository;


    @Override
    public CardDetailsDto getCardDetails(String invoiceNumber) {

        CardDetailsDto cardDetailsDto = new CardDetailsDto();

        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumberIgnoreCase(invoiceNumber);

        if (invoice.isPresent()) {
            cardDetailsDto.setInvoiceNumber(invoice.get().getInvoiceNumber());

            Vehicle vehicle = vehicleRepository.findById(invoice.get().getVehicle().getId()).get();

            PortalUser portalUser = portalUserRepository.findById(invoice.get().getPayer().getId()).get();

            /* Individual Details **/

            if (portalUser.getImage() != null) {
                cardDetailsDto.setImage(portalUser.getImage());
            }

            if (portalUser.getLga() != null) {
                cardDetailsDto.setLga(lgaRepository.findById(portalUser.getLga().getId()).get().getName());
            }

            if (portalUser.getArea() != null) {
                cardDetailsDto.setArea(areaRepository.findById(portalUser.getArea().getId()).get().getName());
            }

            if (portalUser.getFirstName() != null) {
                cardDetailsDto.setFirstName(portalUser.getFirstName());
            }

            if (portalUser.getLastName() != null) {
                cardDetailsDto.setLastName(portalUser.getLastName());
            }

            if (portalUser.getAsin() != null) {
                cardDetailsDto.setAsin(portalUser.getAsin());
            }

            if (portalUser.getEmail() != null) {
                cardDetailsDto.setEmail(portalUser.getEmail());
            }

            if (portalUser.getPhoneNumber() != null) {
                cardDetailsDto.setPhoneNumber(portalUser.getPhoneNumber());
            }

            /*   Vehicle Details **/

            if (vehicle.getChasisNumber() != null) {
                cardDetailsDto.setChasisNumber(vehicle.getChasisNumber());
            }

            if (vehicle.getEngineNumber() != null) {
                cardDetailsDto.setEngineNumber(vehicle.getEngineNumber());
            }

            if (vehicle.getPlateNumber() != null) {
                cardDetailsDto.setPlateNumber(vehicle.getPlateNumber().getPlateNumber());
            }

            if (vehicle.getVehicleModel() != null) {
                cardDetailsDto.setVehicleModel(vehicle.getVehicleModel().getName());
            }

            if (vehicle.getVehicleCategory() != null) {
                cardDetailsDto.setVehicleCategory(vehicle.getVehicleCategory().getName());
            }

            if (vehicle.getPolicySector() != null) {
                cardDetailsDto.setPolicySector(vehicle.getPolicySector());
            }

            if (vehicle.getPassengers() != null) {
                cardDetailsDto.setPassengers(vehicle.getPassengers());
            }

            if (vehicle.getColor() != null) {
                cardDetailsDto.setColor(vehicle.getColor());
            }

            if (vehicle.getVehicleModel().getYear() != null) {
                cardDetailsDto.setVehicleYear(vehicle.getVehicleModel().getYear());
            }

            if (vehicle.getVehicleCategory().getWeight() != null) {
                cardDetailsDto.setWeight(vehicle.getVehicleCategory().getWeight());
            }
        } else {
            return null;
        }


        return cardDetailsDto;
    }

    @Override
    public Card createCard(@NonNull Invoice invoice, @NonNull Vehicle vehicle, RegType regType) {
        Card card1 = new Card();
        Card card2 = new Card();

        card1.setLastUpdatedAt(LocalDateTime.now());
        card1.setCardType(CardTypeConstant.CARD);
        /* to be updated after payment **/
        card1.setStatus(GenericStatusConstant.ACTIVE);

        card1.setCardStatus(CardStatusConstant.NOT_PAID);
        card1.setCreatedBy(jwtService.user);
        card1.setLastUpdatedBy(jwtService.user);
        card1.setInvoice(invoice);
        card1.setVehicle(vehicle);
        card1.setRegType(regType);
        cardRepository.save(card1);
        activityLogService.createActivityLog(("Card for " + invoice.getPayer().getDisplayName()  + " was created"), ActivityStatusConstant.CREATE);


        card2.setCardType(CardTypeConstant.STICKER);
        card2.setLastUpdatedAt(LocalDateTime.now());
        card2.setStatus(GenericStatusConstant.INACTIVE);
        card2.setCardStatus(CardStatusConstant.NOT_PAID);
        card2.setCreatedBy(jwtService.user);
        card2.setLastUpdatedBy(jwtService.user);
        card2.setInvoice(invoice);
        card2.setVehicle(vehicle);
        card2.setRegType(regType);
        activityLogService.createActivityLog(("Card copy for " + invoice.getPayer().getDisplayName()  + " was created"), ActivityStatusConstant.CREATE);
        return cardRepository.save(card2);

    }

    @Override
    public List<Card> updateCardByPayment(@NonNull String invoiceNumber, @NonNull Double amount) {
        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumberIgnoreCase(invoiceNumber);


        if (invoice.isPresent()) {
            if (amount >= invoice.get().getAmount()) {
                Optional<List<Card>> cards = cardRepository.findAllByInvoiceInvoiceNumberIgnoreCase(invoice.get().getInvoiceNumber());
                System.out.println(cards);
    /*Update cards **/
                if (cards.isPresent()) {
                    for (Card card: cards.get()) {

                        card.setStatus(GenericStatusConstant.ACTIVE);
                        card.setCardStatus(CardStatusConstant.NOT_PRINTED);
                        if (card.getVehicle().getPlateNumber().getType().getName().contains("Commercial")){
                            card.setExpiryDate(LocalDateTime.now().plusMonths(6).minusDays(1));
                        }else{
                            card.setExpiryDate(LocalDateTime.now().plusYears(1).minusDays(1));
                        }
                        cardRepository.save(card);
                        activityLogService.createActivityLog(("Card for " + card.getInvoice().getPayer()  + " was updated"), ActivityStatusConstant.UPDATE);
                    }

                    return cards.get();
                }

            }

        }

        return null;
    }

    @Override
    public List<CardDto> get(List<Card> cards) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy/hh:mm:ss");
        return cards.stream().map(card -> {
            CardDto dto = new CardDto();
            dto.setId(card.getId());
            dto.setOwner(card.getVehicle().getPortalUser().getDisplayName());
            dto.setType(card.getCardType());
            dto.setStatusConstant(card.getCardStatus());
            if (card.getCardStatus() != CardStatusConstant.NOT_PAID){
                dto.setDateActivated(card.getLastUpdatedAt().format(df));
            }
            dto.setZonalOffice(card.getCreatedBy().getOffice().getName());
            dto.setExpiryDate(card.getExpiryDate().format(df));
            dto.setCreatedBy(card.getCreatedBy().getDisplayName());
            dto.setPlateNumber(card.getVehicle().getPlateNumber().getPlateNumber());

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Resource printCard(List<PrintDto> dtos) throws Exception {
        List<PdfDto> pdfDtos = dtos.stream().map(printDto -> {
            Card card = cardRepository.findById(printDto.getId()).orElseThrow(RuntimeException::new);

            Map<String, Object> extraParameter = new TreeMap<>();
            String templateName = getTemplate(printDto.getType());
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy");

            String dataValue = asin_verify+"="+card.getInvoice().getInvoiceNumber();
            String qrCode = qrCodeServices.base64CertificateQrCode(dataValue);

            extraParameter.put("photo", card.getVehicle().getPortalUser().getImage());
            extraParameter.put("asin", card.getVehicle().getPortalUser().getAsin());
            extraParameter.put("name", card.getVehicle().getPortalUser().getDisplayName());
            extraParameter.put("address", card.getVehicle().getPortalUser().getAddress());
            extraParameter.put("phoneNumber", card.getVehicle().getPortalUser().getPhoneNumber());
            extraParameter.put("zone", card.getVehicle().getCreatedBy().getOffice().getName());
            extraParameter.put("dateCreated", card.getCreatedAt().format(df));
            extraParameter.put("chasis", card.getVehicle().getChasisNumber());
            extraParameter.put("engine", card.getVehicle().getEngineNumber());
            extraParameter.put("plate", card.getVehicle().getPlateNumber().getPlateNumber());
            extraParameter.put("make", card.getVehicle().getVehicleModel().getVehicleMake().getName());
            extraParameter.put("model", card.getVehicle().getVehicleModel().getName());
            extraParameter.put("year", card.getVehicle().getYear());
            extraParameter.put("category", card.getVehicle().getVehicleCategory().getName());
            extraParameter.put("type", card.getVehicle().getPlateNumber().getType().getName());
            extraParameter.put("color", card.getVehicle().getColor());
            extraParameter.put("created", card.getCreatedAt().format(df));
            extraParameter.put("barcode", qrCode);
            extraParameter.put("capacity", card.getVehicle().getPassengers());
            extraParameter.put("weight", card.getVehicle().getLoad());
            extraParameter.put("policy", card.getVehicle().getInsurance().getName().substring(0, 20)+"...");
            if(card.getVehicle().getInsuranceNumber() != null){
                extraParameter.put("insurance", card.getVehicle().getInsuranceNumber());
            }else{
                extraParameter.put("insurance", "N/A");
            }
            if(card.getVehicle().getPermit() != null){
                extraParameter.put("permit", card.getVehicle().getPermit());
            }else{
                extraParameter.put("permit", "N/A");
            }
            extraParameter.put("invoice", card.getInvoice().getInvoiceNumber());
            extraParameter.put("expiry", card.getExpiryDate().format(df));
            extraParameter.put("regType", card.getRegType());
            extraParameter.put("phone", card.getVehicle().getPortalUser().getPhoneNumber());


            PdfDto pojo = new PdfDto();
            pojo.setTemplateName(templateName);
            pojo.setExtraParameter(extraParameter);
            pojo.setCard(card);

            if(card.getCardStatus() != CardStatusConstant.PRINTED){
                card.setCardStatus(CardStatusConstant.PRINTED);
                card.setLastUpdatedAt(LocalDateTime.now());
                card.setLastUpdatedBy(jwtService.user);
                cardRepository.save(card);
            }

            return pojo;

        }).collect(Collectors.toList());

        List<String> templates = new ArrayList();

        pdfDtos.forEach(pdfPojo -> {
            try {
                templates.add(htmlToPdfCreator.createPDFString(pdfPojo.getTemplateName(), htmlToPdfCreator.getContext(pdfPojo.getCard(), pdfPojo.getExtraParameter())));
            } catch (IllegalAccessException | IOException | DocumentException e) {
                e.printStackTrace();
            }
        });


        String fileName = pdfRenderToMultiplePages.multiPage(templates);

        if (StringUtils.isBlank(fileName)) {
            throw new Exception("file not found");
        }

        return pdfRenderToMultiplePages.loadFileAsResource(appConfigurationProperties.getPrintDirectory() +"/"+ fileName);
    }

    private String getTemplate(CardTypeConstant type){
        String templateName = "";

        switch (type) {
            case CARD:
                return templateName = "card";
            case STICKER:
                return templateName = "sticker";
            case COMPUTERIZED:
                return templateName = "npr";
            case LEARNERS:
                return templateName = "Referral_printout";
            case GENERAL:
                return templateName = "GMR";
            case HEAVY:
                return templateName = "heavy_duty";
            case HACKNEY:
                return templateName = "hackney_permit";
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    @Override
    public Resource printDocuments(List<PrintDto> dtos) throws Exception {
        List<PdfDtoInvoice> pdfDtos = dtos.stream().map(printDto -> {

            InvoiceServiceType invoiceServiceType = invoiceServiceTypeRepository.findById(printDto.getId()).orElseThrow(RuntimeException::new);

            Map<String, Object> extraParameter = new TreeMap<>();
            String templateName = getTemplate(printDto.getType());
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy");

            String dataValue = asin_verify+"="+invoiceServiceType.getInvoice().getInvoiceNumber();
            String qrCode = qrCodeServices.base64CertificateQrCode(dataValue);

            extraParameter.put("name", invoiceServiceType.getInvoice().getPayer().getDisplayName());
            extraParameter.put("address", invoiceServiceType.getInvoice().getPayer().getAddress());
            extraParameter.put("phoneNumber", invoiceServiceType.getInvoice().getPayer().getPhoneNumber());
            extraParameter.put("dateCreated", invoiceServiceType.getInvoice().getCreatedAt().format(df));
            extraParameter.put("chasis", invoiceServiceType.getInvoice().getVehicle().getChasisNumber());
            extraParameter.put("engine", invoiceServiceType.getInvoice().getVehicle().getEngineNumber());
            extraParameter.put("plate", invoiceServiceType.getInvoice().getVehicle().getPlateNumber().getPlateNumber());
            extraParameter.put("make", invoiceServiceType.getInvoice().getVehicle().getVehicleModel().getVehicleMake().getName());
            extraParameter.put("model", invoiceServiceType.getInvoice().getVehicle().getVehicleModel().getName());
            extraParameter.put("year", invoiceServiceType.getInvoice().getVehicle().getYear());
            extraParameter.put("category", invoiceServiceType.getInvoice().getVehicle().getVehicleCategory().getName());
            extraParameter.put("type", invoiceServiceType.getInvoice().getVehicle().getPlateNumber().getType().getName());
            extraParameter.put("color", invoiceServiceType.getInvoice().getVehicle().getColor());
            extraParameter.put("created", invoiceServiceType.getInvoice().getCreatedAt().format(df));
            extraParameter.put("barcode", qrCode);
            extraParameter.put("capacity", invoiceServiceType.getInvoice().getVehicle().getPassengers());
            extraParameter.put("weight", invoiceServiceType.getInvoice().getVehicle().getLoad());
            extraParameter.put("policy", invoiceServiceType.getInvoice().getVehicle().getInsurance().getName());
            extraParameter.put("insurance", invoiceServiceType.getInvoice().getVehicle().getInsuranceNumber());
            extraParameter.put("permit", invoiceServiceType.getInvoice().getVehicle().getPermit());
            extraParameter.put("invoice", invoiceServiceType.getInvoice().getInvoiceNumber());
//            extraParameter.put("expiry", invoiceServiceType.getExpiryDate());


            PdfDtoInvoice pojo = new PdfDtoInvoice();
            pojo.setTemplateName(templateName);
            pojo.setExtraParameter(extraParameter);
            pojo.setCard(invoiceServiceType);

            return pojo;

        }).collect(Collectors.toList());

        List<String> templates = new ArrayList();

        pdfDtos.forEach(pdfPojo -> {
            try {
                templates.add(htmlToPdfCreator.createPDFString(pdfPojo.getTemplateName(), htmlToPdfCreator.getContext(pdfPojo.getCard(), pdfPojo.getExtraParameter())));
            } catch (IllegalAccessException | IOException | DocumentException e) {
                e.printStackTrace();
            }
        });


        String fileName = pdfRenderToMultiplePages.multiPage(templates);

        if (StringUtils.isBlank(fileName)) {
            throw new Exception("file not found");
        }

        return pdfRenderToMultiplePages.loadFileAsResource(appConfigurationProperties.getPrintDirectory() +"/"+ fileName);
    }

}