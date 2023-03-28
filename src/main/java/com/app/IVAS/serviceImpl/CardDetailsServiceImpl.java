package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.CardDetailsDto;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.AreaRepository;
import com.app.IVAS.repository.InvoiceRepository;
import com.app.IVAS.repository.LgaRepository;
import com.app.IVAS.repository.PortalUserRepository;
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


    @Override
    public CardDetailsDto getCardDetails(String invoiceNumber) {

        CardDetailsDto cardDetailsDto = new CardDetailsDto();

        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumberIgnoreCase(invoiceNumber);
        cardDetailsDto.setInvoiceNumber(invoice.get().getInvoiceNumber());

        if (invoice.isPresent()){

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




        }else {
            return null;
        }



    return null;
    }

}
