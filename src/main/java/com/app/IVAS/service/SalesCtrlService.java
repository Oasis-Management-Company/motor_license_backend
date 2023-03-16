package com.app.IVAS.service;

import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.entity.*;

import java.util.List;

public interface SalesCtrlService {
    Sales SaveSales(SalesDto sales);

    List<SalesDto> GetSales(List<Sales> results);

    AsinDto ValidateAsin(String asin);

    List<VehicleMake> getVehicleMake();

    List<VehicleModel> getVehicleModel(Long id);

    List<PlateNumber> getUserPlateNumbers(Long id);

    List<PlateNumberType> getUserPlateNumberTypes();

    List<VehicleCategory> getVehicleCategory();
}
