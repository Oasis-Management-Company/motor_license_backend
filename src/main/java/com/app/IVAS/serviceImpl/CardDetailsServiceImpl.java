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

            if(portalUser.getImage() != null){
                cardDetailsDto.setImage(portalUser.getImage());
            }

            if(portalUser.getLga() != null) {
                cardDetailsDto.setLga(lgaRepository.findById(portalUser.getLga().getId()).get().getName());
            }

            if(portalUser.getArea() != null) {
                cardDetailsDto.setArea(areaRepository.findById(portalUser.getArea().getId()).get().getName());
            }

            if(portalUser.getFirstName()!= null) {
                cardDetailsDto.setFirstName(portalUser.getFirstName());
            }

            if(portalUser.getLastName() != null) {
                cardDetailsDto.setLastName(portalUser.getLastName());
            }

            if(portalUser.getAsin() != null) {
                cardDetailsDto.setAsin(portalUser.getAsin());
            }

            if(portalUser.getEmail() != null) {
                cardDetailsDto.setEmail(portalUser.getEmail());
            }

            if(portalUser.getPhoneNumber() != null) {
                cardDetailsDto.setPhoneNumber(portalUser.getPhoneNumber());
            }

            /**   Vehicle Details **/


            if(vehicle.getChasisNumber() != null) {
                cardDetailsDto.setChasisNumber(vehicle.getChasisNumber());
            }

            if(vehicle.getEngineNumber() != null) {
                cardDetailsDto.setEngineNumber(vehicle.getEngineNumber());
            }

            if(vehicle.getPlateNumber() != null) {
                cardDetailsDto.setPlateNumber(vehicle.getPlateNumber().getPlateNumber());
            }

            if(vehicle.getVehicleModel() != null) {
                cardDetailsDto.setVehicleModel(vehicle.getVehicleModel().getName());
            }

            if(vehicle.getVehicleCategory() != null) {
                cardDetailsDto.setVehicleCategory(vehicle.getVehicleCategory().getName());
            }

            if(vehicle.getPolicySector() != null) {
                cardDetailsDto.setPolicySector(vehicle.getPolicySector());
            }

            if(vehicle.getPassengers() != null) {
                cardDetailsDto.setPassengers(vehicle.getPassengers());
            }

            if(vehicle.getColor() != null) {
                cardDetailsDto.setColor(vehicle.getColor());
            }

            if(vehicle.getVehicleModel().getYear() != null) {
                cardDetailsDto.setVehicleYear(vehicle.getVehicleModel().getYear());
            }

            if(vehicle.getVehicleCategory().getWeight() != null) {
                cardDetailsDto.setWeight(vehicle.getVehicleCategory().getWeight());
            }

        }else {
            return null;
        }


    return cardDetailsDto;
    }

}
