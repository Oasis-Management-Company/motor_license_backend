package com.app.IVAS.service;

import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.dto.VehicleDto;
import com.app.IVAS.entity.*;

import java.util.List;
import java.util.Optional;

public interface SalesCtrlService {
    SalesDto SaveSales(SalesDto sales);

    List<SalesDto> GetSales(List<Sales> results);

    AsinDto ValidateAsin(String asin);

    List<VehicleMake> getVehicleMake();

    List<VehicleModel> getVehicleModel(Long id);

    List<PlateNumber> getUserPlateNumbers(Long id);

    List<PlateNumberType> getUserPlateNumberTypes();

    List<VehicleCategory> getVehicleCategory();

    List<ServiceType> getServiceType(Long id);

    String getApprovalStatus(Long id, String action, Optional<String> reason);

    List<SalesDto> searchAllForVIO(List<Sales> results);

    List<VehicleDto> searchAllVehicles(List<Vehicle> results);

    SalesDto AddVehicle(SalesDto sales);
}
