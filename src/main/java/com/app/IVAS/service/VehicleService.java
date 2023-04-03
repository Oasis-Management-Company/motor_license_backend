package com.app.IVAS.service;

import com.app.IVAS.dto.InvoiceDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.Vehicle;

public interface VehicleService {
    InvoiceDto getUserVehicleDetails(Long id);

    VehicleDto getVehicleDetails(String chasis);

    Vehicle saveEditedVehicle(VehicleDto vehicleDto);
}
