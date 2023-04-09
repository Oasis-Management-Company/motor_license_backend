package com.app.IVAS.service;

import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.dto.InvoiceDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.InvoiceServiceType;
import com.app.IVAS.entity.ServiceType;

import java.util.List;

public interface VehicleService {
    InvoiceDto getUserVehicleDetails(Long id);

    VehicleDto getVehicleDetails(String chasis);

    VehicleDto getVehicleDetailsByPlate(String plate);

    List<ServiceType> getServiceTypeByPlate(String plate);

    Invoice saveServiceTypeByPlate(String myplate, List<Long> ids);

    InvoiceDto getTypeByInvoiceIdEdit(Long invoiceId);

    List<ServiceType> getTypeByInvoiceTaxpayer();

    Invoice saveServiceTypeByPlateForTaxpayer(String phonenumber, List<Long> ids);

    AsinDto getTaxpayerByDetails(String phonenumber);
}
