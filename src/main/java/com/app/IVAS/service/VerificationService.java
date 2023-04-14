package com.app.IVAS.service;

import com.app.IVAS.dto.VerificationDto;

public interface VerificationService {

VerificationDto getScannedInvoice(String invoiceNumber);

    VerificationDto getInvoiceByPlateNumber(String plateNumber);
}
