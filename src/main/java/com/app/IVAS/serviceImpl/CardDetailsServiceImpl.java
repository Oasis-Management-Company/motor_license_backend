package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.CardDetailsDto;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.Vehicle;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.*;
import com.app.IVAS.service.CardDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardDetailsServiceImpl implements CardDetailsService {

    private final InvoiceRepository invoiceRepository;
    private final PortalUserRepository portalUserRepository;
    private final LgaRepository lgaRepository;
    private final AreaRepository areaRepository;
    private final VehicleRepository vehicleRepository;


    @Override
    public CardDetailsDto getCardDetails(String invoiceNumber) {

        CardDetailsDto cardDetailsDto = new CardDetailsDto();

        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumberIgnoreCase(invoiceNumber);
        cardDetailsDto.setInvoiceNumber(invoice.get().getInvoiceNumber());

        if (invoice.isPresent()){

            Vehicle vehicle = vehicleRepository.findById(invoice.get().getVehicle().getId()).get();

            PortalUser portalUser = portalUserRepository.findById(invoice.get().getPayer().getId()).get();

            /** Individual Details **/

            cardDetailsDto.setImage(portalUser.getImage());
            cardDetailsDto.setLga(lgaRepository.findById(portalUser.getLga().getId()).get().getName());
            cardDetailsDto.setArea(areaRepository.findById(portalUser.getArea().getId()).get().getName());
            cardDetailsDto.setFirstName(portalUser.getFirstName());
            cardDetailsDto.setLastName(portalUser.getLastName());
            cardDetailsDto.setAsin(portalUser.getAsin());
            cardDetailsDto.setEmail(portalUser.getEmail());
            cardDetailsDto.setPhoneNumber(portalUser.getPhoneNumber());


            /**   Vehicle Details **/

            cardDetailsDto.setChasisNumber(vehicle.getChasisNumber());
            cardDetailsDto.setEngineNumber(vehicle.getEngineNumber());
            cardDetailsDto.setPlateNumber(vehicle.getPlateNumber().getPlateNumber());
            cardDetailsDto.setVehicleModel(vehicle.getVehicleModel().getName());
            cardDetailsDto.setVehicleCategory(vehicle.getVehicleCategory().getName());
            cardDetailsDto.setPolicySector(vehicle.getPolicySector());
            cardDetailsDto.setPassengers(vehicle.getPassengers());
            cardDetailsDto.setColor(vehicle.getColor());
            cardDetailsDto.setVehicleYear(vehicle.getVehicleModel().getYear());
            cardDetailsDto.setWeight(vehicle.getVehicleCategory().getWeight());

        }else {
            return null;
        }


    return cardDetailsDto;
    }

}
