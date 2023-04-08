package com.app.IVAS.service;

import com.app.IVAS.dto.InvoiceDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.Invoice;
import com.app.IVAS.entity.ServiceType;
import com.app.IVAS.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    InvoiceDto getUserVehicleDetails(Long id);

    VehicleDto getVehicleDetails(String chasis);

    Vehicle saveEditedVehicle(VehicleDto vehicleDto);

    VehicleDto getVehicleDetailsByPlate(String plate);

    List<ServiceType> getServiceTypeByPlate(String plate);

    Invoice saveServiceTypeByPlate(String myplate, List<Long> ids);

    InvoiceDto getTypeByInvoiceIdEdit(Long invoiceId);

    List<ServiceType> getTypeByInvoiceTaxpayer();
}
