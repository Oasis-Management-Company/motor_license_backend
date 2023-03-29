package com.app.IVAS.serviceImpl;

import com.app.IVAS.Enum.CardStatusConstant;
import com.app.IVAS.Enum.CardTypeConstant;
import com.app.IVAS.Enum.GenericStatusConstant;
import com.app.IVAS.dto.CardDetailsDto;
import com.app.IVAS.entity.Card;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.security.JwtService;
import com.app.IVAS.service.CardService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public Card createCard(@NonNull Invoice invoice, @NonNull Vehicle vehicle) {
        Card card1 = new Card();
        Card card2;

        card1.setLastUpdatedAt(LocalDateTime.now());
        card1.setCardType(CardTypeConstant.CARD);
        /* to be updated after payment **/
        card1.setStatus(GenericStatusConstant.INACTIVE);
        card1.setCardStatus(CardStatusConstant.NOT_PAID);
        /* **/
        card1.setCreatedBy(jwtService.user);
        card1.setLastUpdatedBy(jwtService.user);
        card1.setInvoice(invoice);
        card1.setVehicle(vehicle);

        card2 = card1;

        card2.setCardType(CardTypeConstant.STICKER);

        cardRepository.save(card1);
        cardRepository.save(card2);

        return card2;
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
}