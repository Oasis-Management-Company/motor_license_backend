package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.CardStatusConstant;
import com.app.IVAS.Enum.CardTypeConstant;
import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.Utils.HtmlToPdfCreator;
import com.app.IVAS.Utils.PDFRenderToMultiplePages;
import com.app.IVAS.configuration.AppConfigurationProperties;
import com.app.IVAS.dto.*;
import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.security.QrCodeServices;
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

    @Value("${asin_verify}")
    private String asin_verify;
    @Autowired
    QrCodeServices qrCodeServices;


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
    public Card createCard(@NonNull Invoice invoice, @NonNull Vehicle vehicle) {
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
        cardRepository.save(card1);


        card2.setCardType(CardTypeConstant.STICKER);
        card2.setLastUpdatedAt(LocalDateTime.now());
        card2.setStatus(GenericStatusConstant.INACTIVE);
        card2.setCardStatus(CardStatusConstant.NOT_PAID);
        card2.setCreatedBy(jwtService.user);
        card2.setLastUpdatedBy(jwtService.user);
        card2.setInvoice(invoice);
        card2.setVehicle(vehicle);
        return cardRepository.save(card2);

    }

    @Override
    public List<Card> updateCardByPayment(@NonNull String invoiceNumber, @NonNull Double amount) {
        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumberIgnoreCase(invoiceNumber);


        if (invoice.isPresent()) {
            if (amount >= invoice.get().getAmount()) {
                Optional<List<Card>> cards = cardRepository.findAllByInvoiceInvoiceNumberIgnoreCase(invoice.get().getInvoiceNumber());

    /*Update cards **/
                if (cards.isPresent()) {
                    for (Card card: cards.get()) {

                        card.setStatus(GenericStatusConstant.ACTIVE);
                        card.setCardStatus(CardStatusConstant.NOT_PRINTED);
                        card.setExpiryDate(LocalDateTime.now().plusYears(1));

                        cardRepository.save(card);
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
//            dto.setZonalOffice(card.getCreatedBy().getOffice().getName());
//            dto.setExpiryDate(card.getExpiryDate().format(df));
            dto.setCreatedBy(card.getCreatedBy().getDisplayName());
            dto.setPlateNumber(card.getVehicle().getPlateNumber().getPlateNumber());

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Resource printCard(List<PrintDto> dtos) throws Exception {
        List<PdfDto> pdfDtos = dtos.stream().map(printDto -> {
            Card card = cardRepository.findById(printDto.getId()).orElseThrow(RuntimeException::new);

            System.out.println(card);

            Map<String, Object> extraParameter = new TreeMap<>();
            String templateName = getTemplate(printDto.getType());
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd - MMM - yyyy");

            String dataValue = asin_verify+"="+card.getInvoice().getInvoiceNumber();
            String qrCode = qrCodeServices.base64CertificateQrCode(dataValue);




            PdfDto pojo = new PdfDto();
            pojo.setTemplateName(templateName);
            pojo.setExtraParameter(extraParameter);
            pojo.setCard(card);

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
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}