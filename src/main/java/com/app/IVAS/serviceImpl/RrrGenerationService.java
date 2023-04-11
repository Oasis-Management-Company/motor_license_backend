package com.app.IVAS.serviceImpl;

import com.app.IVAS.entity.Invoice;
import com.app.IVAS.repository.InvoiceRepository;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Data
@Service
public class RrrGenerationService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public String generateNewRrrNumber() {
        while (true) {
            long numb = (long)(Math.random() * 100000000 * 1000000);
            if (String.valueOf(numb).length() == 13)
            return String.valueOf(numb);
        }
    }

    public String ConfirmRRR(String number){
        Optional<Invoice> rrr = invoiceRepository.findByInvoiceNumberIgnoreCase(number);
        if (rrr.isPresent()){
           return generateNewRrrNumber();
        }else{
            return number;
        }
    }

    public String generateNewReferenceNumber() {
        while (true) {
            long numb = (long)(Math.random() * 100000000 * 1000000);
            if (String.valueOf(numb).length() == 13)
                return  String.valueOf(numb);
        }
    }

    public String ConfirmReference(String number){
        Optional<Invoice> rrr = invoiceRepository.findByPaymentRefIgnoreCase(number);
        if (rrr.isPresent()){
            return generateNewReferenceNumber();
        }else{
            return number;
        }
    }

}
