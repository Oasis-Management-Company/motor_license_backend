package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.CardStatusConstant;
import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.dto.CardDetailsDto;
import com.app.IVAS.dto.CardDto;
import com.app.IVAS.dto.PrintDto;
import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final InvoiceRepository invoiceRepository;
    private final PortalUserRepository portalUserRepository;
    private final LgaRepository lgaRepository;
    private final AreaRepository areaRepository;
    private final VehicleRepository vehicleRepository;
    private final JwtService jwtService;
    private final CardRepository cardRepository;


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
    public Card createCard(Invoice invoice, Vehicle vehicle) {
        Card card = new Card();

        card.setCreatedAt(LocalDateTime.now());
        card.setLastUpdatedAt(LocalDateTime.now());
        /* to be updated after payment **/
        card.setStatus(GenericStatusConstant.INACTIVE);
        card.setCardStatus(CardStatusConstant.NOT_PAID);
        /* **/
        card.setCreatedBy(jwtService.user);
        card.setLastUpdatedBy(jwtService.user);
        card.setInvoice(invoice);
        card.setVehicle(vehicle);

        cardRepository.save(card);

        return card;
    }

    @Override
    public Card updateCardByPayment(String invoiceNumber, Double amount) {
        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumberIgnoreCase(invoiceNumber);


        if (invoice.isPresent()) {
            if (amount >= invoice.get().getAmount()) {
                Optional<Card> card = cardRepository.findByInvoiceInvoiceNumberIgnoreCase(invoice.get().getInvoiceNumber());
/*Update card **/
                if (card.isPresent()) {
                    card.get().setStatus(GenericStatusConstant.ACTIVE);
                    card.get().setCardStatus(CardStatusConstant.NOT_PRINTED);
                    card.get().setExpiryDate(LocalDateTime.now().plusYears(1));

                    cardRepository.save(card.get());

                    return card.get();
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
            dto.setOwner(card.getVehicle().getPortalUser().getDisplayName());
            dto.setType(card.getCardType());
            dto.setStatusConstant(card.getCardStatus());
            if (card.getCardStatus() != CardStatusConstant.NOT_PAID){
                dto.setDateActivated(card.getLastUpdatedAt().format(df));
            }
            dto.setZonalOffice(card.getCreatedBy().getOffice().getName());
            dto.setExpiryDate(card.getExpiryDate().format(df));
            dto.setCreatedBy(card.getCreatedBy().getDisplayName());
            dto.setInvoiceNumber(card.getInvoice().getInvoiceNumber());

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Resource printCard(List<PrintDto> dtos) throws Exception {
        return null;
    }
}