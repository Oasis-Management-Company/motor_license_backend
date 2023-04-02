package com.app.IVAS.service;

import com.app.IVAS.dto.InvoiceDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.ServiceType;

import java.util.List;

public interface VehicleService {
    InvoiceDto getUserVehicleDetails(Long id);

    VehicleDto getVehicleDetails(String chasis);

    VehicleDto getVehicleDetailsByPlate(String plate);

    List<ServiceType> getServiceTypeByPlate(String plate);
}
