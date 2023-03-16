package com.app.IVAS.service;

import com.app.IVAS.dto.AsinDto;
import com.app.IVAS.dto.SalesDto;
import com.app.IVAS.entity.Sales;
import com.app.IVAS.entity.VehicleMake;
import com.app.IVAS.entity.VehicleModel;

import java.util.List;

public interface SalesCtrlService {
    Sales SaveSales(SalesDto sales);

    List<SalesDto> GetSales(List<Sales> results);

    AsinDto ValidateAsin(String asin);

    List<VehicleMake> getVehicleMake();

    List<VehicleModel> getVehicleModel(Long id);
}
