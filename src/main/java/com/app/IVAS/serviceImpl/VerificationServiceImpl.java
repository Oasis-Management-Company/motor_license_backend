package com.app.IVAS.serviceImpl;

import com.app.IVAS.dto.InvoiceServiceTypeDto;
import com.app.IVAS.dto.VerificationDto;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import com.app.IVAS.entity.userManagement.PortalUser;
import com.app.IVAS.repository.InvoiceRepository;
import com.app.IVAS.repository.InvoiceServiceTypeRepository;
import com.app.IVAS.repository.PortalUserRepository;
import com.app.IVAS.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final InvoiceRepository invoiceRepository;
    private final PortalUserRepository portalUserRepository;
    private final InvoiceServiceTypeRepository invoiceServiceTypeRepository;

    @Override
    public VerificationDto getScannedInvoice(String invoiceNumber) {

        VerificationDto verificationDto = new VerificationDto();

        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumberIgnoreCase(invoiceNumber);
        if (invoice.isPresent()) {

            verificationDto.setInvoiceNumber(invoice.get().getInvoiceNumber());
            verificationDto.setPaymentDate(invoice.get().getPaymentDate());
            verificationDto.setAmount(invoice.get().getAmount());
            verificationDto.setPaymentStatus(invoice.get().getPaymentStatus().name());


            PortalUser portalUser = portalUserRepository.findById(invoice.get().getPayer().getId()).get();

            if (portalUser.getDisplayName() != null) {
                verificationDto.setName(portalUser.getDisplayName());
            }

            if (portalUser.getPhoneNumber() != null) {
                verificationDto.setPhoneNumber(portalUser.getPhoneNumber());
            }

            if (portalUser.getAddress() != null) {
                verificationDto.setAddress(portalUser.getAddress());
            }

            if (portalUser.getEmail() != null) {
                verificationDto.setEmail(portalUser.getEmail());
            }

            if (portalUser.getAsin() != null) {
                verificationDto.setAsin(portalUser.getAsin());
            }

            Optional<List<InvoiceServiceType>> invoiceServiceTypes = invoiceServiceTypeRepository.findAllByInvoice(invoice.get());

            List<InvoiceServiceTypeDto> invoiceServiceTypeDtos = new ArrayList<>();

            if (invoiceServiceTypes.isPresent()) {
                for (InvoiceServiceType invoiceServiceType : invoiceServiceTypes.get()) {

                    InvoiceServiceTypeDto invoiceServiceTypeDto = new InvoiceServiceTypeDto();

                    if(invoiceServiceType.getServiceType().getName() != null) {
                        invoiceServiceTypeDto.setName(invoiceServiceType.getServiceType().getName());
                    }

                    if(invoiceServiceType.getServiceType().getPrice() != null) {
                        invoiceServiceTypeDto.setPrice(invoiceServiceType.getServiceType().getPrice());
                    }

                    if(invoiceServiceType.getServiceType().getDurationInMonth() != null) {
                        invoiceServiceTypeDto.setDurationInMonth(invoiceServiceType.getServiceType().getDurationInMonth());
                    }

                    if(invoiceServiceType.getServiceType().getCategory().getName() != null) {
                        invoiceServiceTypeDto.setVehicleCategory(invoiceServiceType.getServiceType().getCategory().getName());
                    }

                    if(invoiceServiceType.getReference() != null) {
                        invoiceServiceTypeDto.setReference(invoiceServiceType.getReference());
                    }

                    if(invoiceServiceType.getPaymentDate() != null) {
                        invoiceServiceTypeDto.setPaymentDate(invoiceServiceType.getPaymentDate());
                    }

                    if(invoiceServiceType.getServiceType().getDurationInMonth() != null && invoiceServiceType.getPaymentDate() != null){
                        invoiceServiceTypeDto.setExpiryDate(invoiceServiceType.getPaymentDate().plusMonths(invoiceServiceType.getServiceType().getDurationInMonth()));
                    }

                    invoiceServiceTypeDtos.add(invoiceServiceTypeDto);
                }

                verificationDto.setInvoiceServices(invoiceServiceTypeDtos);
            }

            return verificationDto;

        }
        return null;
    }
}
